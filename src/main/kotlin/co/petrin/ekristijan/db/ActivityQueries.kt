package co.petrin.ekristijan.db

import co.petrin.ekristijan.db.Tables.ACTIVITY
import co.petrin.ekristijan.db.Tables.PUPIL_ACTIVITY
import org.jooq.DSLContext

object ActivityQueries {
    /**
     * Gets all activities for pupil, checking that they are indeed from the same school as teacher.
     */
    fun getForPupil(pupilId: Int, teacherId: Int, trans: DSLContext) = with(PUPIL_ACTIVITY) {
        trans.select(*activity().fields())
        .from(PUPIL_ACTIVITY)
        .where(
            PUPIL_ACTIVITY.PUPIL_ID.eq(pupilId),
            DepartureQueries.pupilBelongingToSameSchoolAsTeacher(pupilId, teacherId).isNotNull
        )
        .fetchInto(ACTIVITY)
    }
}