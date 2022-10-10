package co.petrin.ekristijan.db

import co.petrin.ekristijan.TestSetup
import co.petrin.ekristijan.preconditions
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import si.razum.vertx.db.ConnectionPool
import java.time.LocalDate
import java.time.LocalTime
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
    fun departureFor(pupilId: Int, day: LocalDate) = DepartureQueries.dailyDepartures(fixture.schoolId, day, fixture.allClasses, jooq).find { it.pupil.id == pupilId }

    "Jana leaves at 16:20 every monday and has not left school yet" {
        departureFor(fixture.janaId, monday)!!.apply {
            departurePlan.time shouldBe localTime(16, 20)
            departure shouldBe null
        }
    }

    "Gašper has an extraordinary departure planned for tuesday at 11:30" {
        val elevenThirty = localTime(11, 30)
        preconditions {
            departureFor(fixture.gasperId, tuesday)!!.departurePlan.time shouldNotBe elevenThirty
        }

        DepartureQueries.declareExtraordinaryDeparture(fixture.gasperId, fixture.schoolId, tuesday, elevenThirty, "Zobozdravnik", true, jooq)
        departureFor(fixture.gasperId, tuesday)!!.apply {
            departurePlan.time shouldBe elevenThirty
            departurePlan.remark shouldBe "Zobozdravnik"
            departurePlan.leavesAlone shouldBe true
        }
    }

    "Klemen left school at 14:00 on wednesday" - {
        preconditions {
            departureFor(fixture.klemenId, wednesday)!!.departure shouldBe null
        }

        val atFourteenHours = wednesday.atStartOfDay(fixture.timezone).toOffsetDateTime().plusHours(14)
        DepartureQueries.recordDeparture(fixture.klemenId, fixture.teacher.id, atFourteenHours, false, null, jooq) shouldBe true
        departureFor(fixture.klemenId, wednesday)!!.apply {
            departure shouldNotBe null
            departure!!.time shouldBe atFourteenHours
        }

        "on thursday, that departure is no longer relevant" {
            departureFor(fixture.klemenId, thursday)!!.apply {
                departure shouldBe null
            }
        }
    }

    "Anita has been summoned to the door on thursday" - {
        val fifteenHours = thursday.atStartOfDay(fixture.timezone).toOffsetDateTime().plusHours(15)
        DepartureQueries.summonPupil(fixture.anitaId, fixture.teacher.id, fifteenHours, jooq)

        "The daily summary contains the summon for pupils that were called" {
            departureFor(fixture.gasperId, thursday)!!.apply { summon shouldBe null }
            departureFor(fixture.anitaId, thursday)!!.apply {
                summon shouldNotBe null
                summon!!.teacherName shouldBe fixture.teacher.name
            }
        }

        "On friday, that summon is no longer relevant" {
            departureFor(fixture.anitaId, friday)!!.apply { summon shouldBe null }
        }

        "The summon can be acknowleded, thereby recording a departure as well" {
            preconditions {
                departureFor(fixture.anitaId, thursday)!!.apply {
                    summon shouldNotBe null
                    departure shouldBe null
                }
            }

            val tenPastFifteen = fifteenHours.plusMinutes(10)
            val id = departureFor(fixture.anitaId, thursday)!!.summon!!.id
            DepartureQueries.acknowledgePupilSummonAndRecordDeparture(id, fixture.teacher.id, tenPastFifteen, jooq)
            departureFor(fixture.anitaId, thursday)!!.apply {
                summon shouldNotBe null
                departure shouldNotBe null
                departure!!.time
            }
        }
    }
})