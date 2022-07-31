package co.petrin.ekristijan.db

import co.petrin.ekristijan.TestSetup
import co.petrin.ekristijan.db.Tables.EXTRAORDINARY_DEPARTURE
import co.petrin.ekristijan.db.tables.ExtraordinaryDeparture
import co.petrin.ekristijan.db.tables.records.ExtraordinaryDepartureRecord
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import si.razum.vertx.db.ConnectionPool
import java.time.LocalDate
import java.time.LocalTime

/*
    The initial data model for the test is inserted by the development migrations and the data looks like this:

     pupil_id | school_id |      name      | clazz | leaves_alone | leave_mon | leave_tue | leave_wed | leave_thu | leave_fri
    ----------+-----------+----------------+-------+--------------+-----------+-----------+-----------+-----------+-----------
            1 |         1 | Jana Vanič     | 1A    | f            | 16:20:00  | 15:30:00  | 16:20:00  | 17:30:00  | 13:00:00
            2 |         1 | Gašper Žbogar  | 1A    | f            | 15:30:00  | 15:30:00  | 16:20:00  | 17:30:00  | 13:00:00
            3 |         1 | Klemen Kotnik  | 1B    | f            | 16:20:00  | 15:30:00  | 16:20:00  | 17:30:00  | 13:00:00
            4 |         1 | Anita Borštnar | 1B    | f            | 13:10:00  | 15:30:00  | 16:20:00  | 17:30:00  | 13:00:00
            5 |         1 | Klara Cetkin   | 2A    | t            |           | 15:30:00  | 16:20:00  | 17:30:00  | 13:00:00
            6 |         1 | Karel Maks     | 2A    | f            | 17:00:00  | 15:30:00  | 16:20:00  | 17:30:00  | 13:00:00
            7 |         1 | Benito Harez   | 2B    | t            | 16:20:00  | 15:30:00  | 16:20:00  | 17:30:00  | 13:00:00
            8 |         1 | Ivar Svenson   | 2B    | t            |           | 15:30:00  | 16:20:00  | 17:30:00  | 13:00:00
*/

private val allClasses = arrayOf("1A", "1B", "2A", "2B")
private val schoolId = 1
private val janaId = 1
private val gasperId = 2

private val monday = LocalDate.of(2022, 9, 5)
private val tuesday = LocalDate.of(2022, 9, 6)
private val wednesday = LocalDate.of(2022, 9, 7)
private val thursday = LocalDate.of(2022, 9, 8)
private val friday = LocalDate.of(2022, 9, 9)

class DepartureQueriesTest : FreeSpec({

    val postgres = TestSetup.postgresContainer
    val jooq = ConnectionPool.wrapWithJooq(postgres.openConnection(), true)

    "Jana leaves at 16:20 every monday and has not left school yet" {
        val janaMonday = DepartureQueries.dailyDepartures(schoolId, monday, allClasses, jooq).find { it.pupilId == janaId }
        janaMonday shouldNotBe null
        janaMonday!!
        janaMonday.usualDeparture shouldBe LocalTime.of(16, 20)
        janaMonday.actualDeparture shouldBe null
    }

    "Gašper has an extraordinary departure planned for tuesday at 11:30" {
        val elevenThirty = LocalTime.of(11, 30)
        ExtraordinaryDepartureRecord().apply {
            pupilId = gasperId
            date = tuesday
            time = elevenThirty

            attach(jooq.configuration())
            insert()
        }

        val gasperTuesday = DepartureQueries.dailyDepartures(schoolId, tuesday, allClasses, jooq).find { it.pupilId == gasperId }
        gasperTuesday shouldNotBe null
        gasperTuesday!!.plannedDeparture shouldNotBe null
        gasperTuesday.plannedDeparture!!.time shouldBe elevenThirty
        gasperTuesday.plannedDeparture!!.date shouldBe tuesday
    }

})
