package co.petrin.ekristijan.db

import co.petrin.ekristijan.TestSetup
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import si.razum.vertx.db.ConnectionPool
import java.time.LocalDate
import java.time.LocalDate.of as localDate
import java.time.LocalTime.of as localTime
import co.petrin.ekristijan.TextFixtures as fixture

private val monday = localDate(2022, 9, 5)
private val tuesday = localDate(2022, 9, 6)
private val wednesday = localDate(2022, 9, 7)
private val thursday = localDate(2022, 9, 8)
private val friday = localDate(2022, 9, 9)

class DepartureQueriesTest : FreeSpec({

    val postgres = TestSetup.postgresContainer
    val jooq = ConnectionPool.wrapWithJooq(postgres.openConnection(), true)

    /** Finds the departure status of [pupilId] on [day] */
    fun departureFor(pupilId: Int, day: LocalDate) = DepartureQueries.dailyDepartures(fixture.schoolId, day, fixture.allClasses, jooq).find { it.pupilId == pupilId }

    "Jana leaves at 16:20 every monday and has not left school yet" {
        departureFor(fixture.janaId, monday)!!.apply {
            usualDeparture shouldBe localTime(16, 20)
            actualDeparture shouldBe null
        }
    }

    "Gašper has an extraordinary departure planned for tuesday at 11:30" {
        preconditions {
            departureFor(fixture.gasperId, tuesday)!!.plannedDeparture shouldBe null
        }

        val elevenThirty = localTime(11, 30)
        DepartureQueries.declareExtraordinaryDeparture(fixture.gasperId, fixture.schoolId, tuesday, elevenThirty, "Zobozdravnik", true, jooq)
        departureFor(fixture.gasperId, tuesday)!!.apply {
            plannedDeparture shouldNotBe null
            plannedDeparture!!.time shouldBe elevenThirty
            plannedDeparture!!.date shouldBe tuesday
            plannedDeparture!!.remark shouldBe "Zobozdravnik"
            plannedDeparture!!.leavesAlone shouldBe true
        }
    }

    "Klemen left school at 14:00 on wednesday" {
        preconditions {
            departureFor(fixture.klemenId, wednesday)!!.actualDeparture shouldBe null
        }

        val atFourteenHours = wednesday.atStartOfDay(fixture.timezone).toOffsetDateTime().plusHours(14)
        DepartureQueries.recordDeparture(fixture.klemenId, fixture.teacherId, atFourteenHours, false, null, jooq) shouldBe true
        departureFor(fixture.klemenId, wednesday)!!.apply {
            actualDeparture shouldNotBe null
            actualDeparture!!.time shouldBe atFourteenHours
        }
    }

    "Anita has been summoned to the door" - {
        val fifteenHours = thursday.atStartOfDay(fixture.timezone).toOffsetDateTime().plusHours(15)
        DepartureQueries.summonPupil(fixture.anitaId, fixture.teacherId, fifteenHours, jooq)

        "The daily summary contains the summon for pupils that were called" {
            departureFor(fixture.gasperId, thursday)!!.apply { summon shouldBe null }
            departureFor(fixture.anitaId, thursday)!!.apply { summon shouldNotBe null }
        }

        "The summon can be acknowleded, thereby recording a departure as well" {
            preconditions {
                departureFor(fixture.anitaId, thursday)!!.apply {
                    summon shouldNotBe null
                    actualDeparture shouldBe null
                }
            }

            val tenPastFifteen = fifteenHours.plusMinutes(10)
            val id = departureFor(fixture.anitaId, thursday)!!.summon!!.summonId
            DepartureQueries.acknowledgePupilSummonAndRecordDeparture(id, fixture.teacherId, tenPastFifteen, jooq)
            departureFor(fixture.anitaId, thursday)!!.apply {
                summon shouldNotBe null
                actualDeparture shouldNotBe null
                actualDeparture!!.time
            }
        }
    }
})

/** A block to fence off verification of test preconditions. No functionality, syntax tool only. */
private suspend fun preconditions(block: () -> Any) {
    block()
}
