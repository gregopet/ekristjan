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

    fun departureFor(pupilId: Int, day: LocalDate) = DepartureQueries.dailyDepartures(fixture.schoolId, day, fixture.allClasses, jooq).find { it.pupilId == pupilId }

    "Jana leaves at 16:20 every monday and has not left school yet" {
        departureFor(fixture.janaId, monday)!!.apply {
            usualDeparture shouldBe localTime(16, 20)
            actualDeparture shouldBe null
        }
    }

    "Gašper has an extraordinary departure planned for tuesday at 11:30" {
        departureFor(fixture.gasperId, tuesday)!!.plannedDeparture shouldBe null

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
        departureFor(fixture.klemenId, wednesday)!!.actualDeparture shouldBe null

        val atFourteenHours = wednesday.atStartOfDay(fixture.timezone).toOffsetDateTime().plusHours(14)
        DepartureQueries.recordDeparture(fixture.klemenId, fixture.teacherId, atFourteenHours, false, null, jooq) shouldBe true

        departureFor(fixture.klemenId, wednesday)!!.apply {
            actualDeparture shouldNotBe null
            actualDeparture!!.time shouldBe atFourteenHours
        }
    }

})
