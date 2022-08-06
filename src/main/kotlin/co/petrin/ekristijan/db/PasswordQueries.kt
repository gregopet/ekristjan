package co.petrin.ekristijan.db

import co.petrin.ekristijan.db.Tables.TEACHER
import co.petrin.ekristijan.db.tables.records.TeacherRecord
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.impl.DSL.*
import java.time.OffsetDateTime

object PasswordQueries {

    /**
     * Attempts to fetch a teacher row for login purposes.
     * It increments the password attempt counter, unless [attemptDurationMinutes] have passed since the last password
     * attempt - in this case, the counter is reset to 1 and the attempt window is reset to now.
     * @param time time at which the user had made the password attempt. Should be now() most of the time, only exposed as param for unit testing.
     */
    fun passwordAttempt(email: String, attemptDurationMinutes: Int, time: OffsetDateTime, trans: DSLContext): TeacherRecord? = with(TEACHER) {
        val attemptWindowStart = time.minusMinutes(attemptDurationMinutes.toLong())

        // do we have a new attempt (since not enough time had passed since the last one) or not
        fun <T> windowCheck(sameWindow: Field<T>, newWindow: T): Field<T> =
            `when`(`val`(attemptWindowStart).lt(PASSWORD_LAST_ATTEMPT), sameWindow)
            .otherwise(newWindow)

        trans
        .update(TEACHER)
        .set(PASSWORD_LAST_ATTEMPT, windowCheck(sameWindow = PASSWORD_LAST_ATTEMPT, newWindow = time))
        .set(PASSWORD_LAST_ATTEMPT_COUNT, windowCheck(sameWindow = PASSWORD_LAST_ATTEMPT_COUNT.plus(1), newWindow = 1))
        .where(EMAIL.eq(email))
        .returning()
        .fetchOne()
    }

    /**
     * Updates the teacher's password hash and resets the password fail counters.
     */
    fun passwordReset(teacherId: Int, newPassHash: String, trans: DSLContext) = with(TEACHER) {
        trans.update(TEACHER)
        .set(PASSWORD_HASH, newPassHash)
        .set(PASSWORD_LAST_ATTEMPT_COUNT, 0)
        .where(TEACHER_ID.eq(teacherId))
        .execute()
    }
}