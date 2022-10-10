package co.petrin.ekristijan

import java.time.ZoneId

/** Shortcut to data found in DevMigration fixtures */
object TextFixtures {

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

    val allClasses = arrayOf("1A", "1B", "2A", "2B")
    val schoolId = 1
    val janaId = 1
    val gasperId = 2
    val klemenId = 3
    val anitaId = 4
    val timezone = ZoneId.of("Europe/Ljubljana")

    val teacher = FixtureTeacher(id = 1, email = "ucitelj@example.org", password = "test", name = "Učiteljica Majda")
}

data class FixtureTeacher(val id: Int, val email: String, val password: String, val name: String)