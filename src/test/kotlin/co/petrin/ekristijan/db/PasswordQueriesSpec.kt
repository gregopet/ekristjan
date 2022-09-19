package co.petrin.ekristijan.db

import co.petrin.ekristijan.TestSetup
import co.petrin.ekristijan.db.Tables.TEACHER
import co.petrin.ekristijan.db.tables.records.TeacherRecord
import co.petrin.ekristijan.preconditions
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import si.razum.vertx.db.ConnectionPool
import java.time.*
import co.petrin.ekristijan.TextFixtures as fixture

class PasswordQueriesSpec : FreeSpec({

    val postgres = TestSetup.postgresContainer
    val jooq = ConnectionPool.wrapWithJooq(postgres.openConnection(), true)

    fun refreshTeacher() =
        jooq.selectFrom(TEACHER).where(TEACHER.TEACHER_ID.eq(fixture.teacherId)).fetchOne()!!

    "A teacher making 3 password attempts" - {
        val attemptAt = OffsetDateTime.of(LocalDate.now(), LocalTime.of(8, 0), fixture.timezone.rules.getOffset(Instant.now()))
        val attemptMinutes = 10

        PasswordQueries.passwordAttempt(fixture.teacherEmail, attemptMinutes, attemptAt.plusSeconds(0), jooq)
        PasswordQueries.passwordAttempt(fixture.teacherEmail, attemptMinutes, attemptAt.plusSeconds(20), jooq)
        PasswordQueries.passwordAttempt(fixture.teacherEmail, attemptMinutes, attemptAt.plusSeconds(30), jooq)

        "successive attempts should be recorded" {
            refreshTeacher().apply {
                passwordLastAttemptCount shouldBe 3
                passwordLastAttempt shouldBe attemptAt
            }
        }

        "an attempt at a later time should reset the timer" {
            val dayAfter = attemptAt.plusDays(1)
            PasswordQueries.passwordAttempt(fixture.teacherEmail, attemptMinutes, dayAfter, jooq)
            refreshTeacher().apply {
                passwordLastAttemptCount shouldBe 1
                passwordLastAttempt shouldBe dayAfter
            }
        }

        "a password reset should update the hash & reset the fail timer" {
            preconditions {
                refreshTeacher().apply {
                    passwordLastAttemptCount shouldBeGreaterThan 0
                    passwordHash shouldNotBe "hash"
                }
            }

            PasswordQueries.passwordReset(fixture.teacherEmail, "hash", 1L, jooq)
            refreshTeacher().apply {
                passwordLastAttemptCount shouldBe 0
                passwordHash shouldBe "hash"
            }
        }

        "a password reset should not work with the same unique identifier twice" {
            val uniqueIdentifier = 2L
            PasswordQueries.passwordReset(fixture.teacherEmail, "random", uniqueIdentifier, jooq) shouldNotBe null
            PasswordQueries.passwordReset(fixture.teacherEmail, "random", uniqueIdentifier, jooq) shouldBe null
            PasswordQueries.passwordReset(fixture.teacherEmail, "random", uniqueIdentifier + 1, jooq) shouldNotBe null
        }

    }
})
