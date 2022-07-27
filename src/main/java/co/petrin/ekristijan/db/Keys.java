/*
 * This file is generated by jOOQ.
 */
package co.petrin.ekristijan.db;


import co.petrin.ekristijan.db.tables.Departure;
import co.petrin.ekristijan.db.tables.ExtraordinaryDeparture;
import co.petrin.ekristijan.db.tables.PasswordReset;
import co.petrin.ekristijan.db.tables.Pupil;
import co.petrin.ekristijan.db.tables.School;
import co.petrin.ekristijan.db.tables.Summon;
import co.petrin.ekristijan.db.tables.SummonAck;
import co.petrin.ekristijan.db.tables.Teacher;
import co.petrin.ekristijan.db.tables.records.DepartureRecord;
import co.petrin.ekristijan.db.tables.records.ExtraordinaryDepartureRecord;
import co.petrin.ekristijan.db.tables.records.PasswordResetRecord;
import co.petrin.ekristijan.db.tables.records.PupilRecord;
import co.petrin.ekristijan.db.tables.records.SchoolRecord;
import co.petrin.ekristijan.db.tables.records.SummonAckRecord;
import co.petrin.ekristijan.db.tables.records.SummonRecord;
import co.petrin.ekristijan.db.tables.records.TeacherRecord;

import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<DepartureRecord> DEPARTURE_PKEY = Internal.createUniqueKey(Departure.DEPARTURE, DSL.name("departure_pkey"), new TableField[] { Departure.DEPARTURE.DEPARTURE_ID }, true);
    public static final UniqueKey<ExtraordinaryDepartureRecord> EXTRAORDINARY_DEPARTURE_DATE_PUPIL_ID_KEY = Internal.createUniqueKey(ExtraordinaryDeparture.EXTRAORDINARY_DEPARTURE, DSL.name("extraordinary_departure_date_pupil_id_key"), new TableField[] { ExtraordinaryDeparture.EXTRAORDINARY_DEPARTURE.DATE, ExtraordinaryDeparture.EXTRAORDINARY_DEPARTURE.PUPIL_ID }, true);
    public static final UniqueKey<ExtraordinaryDepartureRecord> EXTRAORDINARY_DEPARTURE_PKEY = Internal.createUniqueKey(ExtraordinaryDeparture.EXTRAORDINARY_DEPARTURE, DSL.name("extraordinary_departure_pkey"), new TableField[] { ExtraordinaryDeparture.EXTRAORDINARY_DEPARTURE.EXTRAORDINARY_DEPARTURE_ID }, true);
    public static final UniqueKey<PasswordResetRecord> PASSWORD_RESET_PKEY = Internal.createUniqueKey(PasswordReset.PASSWORD_RESET, DSL.name("password_reset_pkey"), new TableField[] { PasswordReset.PASSWORD_RESET.PASSWORD_RESET_ID }, true);
    public static final UniqueKey<PupilRecord> PUPIL_PKEY = Internal.createUniqueKey(Pupil.PUPIL, DSL.name("pupil_pkey"), new TableField[] { Pupil.PUPIL.PUPIL_ID }, true);
    public static final UniqueKey<SchoolRecord> SCHOOL_NAME_KEY = Internal.createUniqueKey(School.SCHOOL, DSL.name("school_name_key"), new TableField[] { School.SCHOOL.NAME }, true);
    public static final UniqueKey<SchoolRecord> SCHOOL_PKEY = Internal.createUniqueKey(School.SCHOOL, DSL.name("school_pkey"), new TableField[] { School.SCHOOL.SCHOOL_ID }, true);
    public static final UniqueKey<SummonRecord> SUMMON_PKEY = Internal.createUniqueKey(Summon.SUMMON, DSL.name("summon_pkey"), new TableField[] { Summon.SUMMON.SUMMON_ID }, true);
    public static final UniqueKey<SummonAckRecord> SUMMON_ACK_PKEY = Internal.createUniqueKey(SummonAck.SUMMON_ACK, DSL.name("summon_ack_pkey"), new TableField[] { SummonAck.SUMMON_ACK.SUMMON_ACK_ID }, true);
    public static final UniqueKey<TeacherRecord> TEACHER_PKEY = Internal.createUniqueKey(Teacher.TEACHER, DSL.name("teacher_pkey"), new TableField[] { Teacher.TEACHER.TEACHER_ID }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<DepartureRecord, PupilRecord> DEPARTURE__DEPARTURE_PUPIL_ID_FKEY = Internal.createForeignKey(Departure.DEPARTURE, DSL.name("departure_pupil_id_fkey"), new TableField[] { Departure.DEPARTURE.PUPIL_ID }, Keys.PUPIL_PKEY, new TableField[] { Pupil.PUPIL.PUPIL_ID }, true);
    public static final ForeignKey<DepartureRecord, TeacherRecord> DEPARTURE__DEPARTURE_TEACHER_ID_FKEY = Internal.createForeignKey(Departure.DEPARTURE, DSL.name("departure_teacher_id_fkey"), new TableField[] { Departure.DEPARTURE.TEACHER_ID }, Keys.TEACHER_PKEY, new TableField[] { Teacher.TEACHER.TEACHER_ID }, true);
    public static final ForeignKey<ExtraordinaryDepartureRecord, PupilRecord> EXTRAORDINARY_DEPARTURE__EXTRAORDINARY_DEPARTURE_PUPIL_ID_FKEY = Internal.createForeignKey(ExtraordinaryDeparture.EXTRAORDINARY_DEPARTURE, DSL.name("extraordinary_departure_pupil_id_fkey"), new TableField[] { ExtraordinaryDeparture.EXTRAORDINARY_DEPARTURE.PUPIL_ID }, Keys.PUPIL_PKEY, new TableField[] { Pupil.PUPIL.PUPIL_ID }, true);
    public static final ForeignKey<ExtraordinaryDepartureRecord, TeacherRecord> EXTRAORDINARY_DEPARTURE__EXTRAORDINARY_DEPARTURE_TEACHER_ID_FKEY = Internal.createForeignKey(ExtraordinaryDeparture.EXTRAORDINARY_DEPARTURE, DSL.name("extraordinary_departure_teacher_id_fkey"), new TableField[] { ExtraordinaryDeparture.EXTRAORDINARY_DEPARTURE.TEACHER_ID }, Keys.TEACHER_PKEY, new TableField[] { Teacher.TEACHER.TEACHER_ID }, true);
    public static final ForeignKey<PasswordResetRecord, TeacherRecord> PASSWORD_RESET__PASSWORD_RESET_TEACHER_ID_FKEY = Internal.createForeignKey(PasswordReset.PASSWORD_RESET, DSL.name("password_reset_teacher_id_fkey"), new TableField[] { PasswordReset.PASSWORD_RESET.TEACHER_ID }, Keys.TEACHER_PKEY, new TableField[] { Teacher.TEACHER.TEACHER_ID }, true);
    public static final ForeignKey<PupilRecord, SchoolRecord> PUPIL__PUPIL_SCHOOL_ID_FKEY = Internal.createForeignKey(Pupil.PUPIL, DSL.name("pupil_school_id_fkey"), new TableField[] { Pupil.PUPIL.SCHOOL_ID }, Keys.SCHOOL_PKEY, new TableField[] { School.SCHOOL.SCHOOL_ID }, true);
    public static final ForeignKey<SummonRecord, PupilRecord> SUMMON__SUMMON_PUPIL_ID_FKEY = Internal.createForeignKey(Summon.SUMMON, DSL.name("summon_pupil_id_fkey"), new TableField[] { Summon.SUMMON.PUPIL_ID }, Keys.PUPIL_PKEY, new TableField[] { Pupil.PUPIL.PUPIL_ID }, true);
    public static final ForeignKey<SummonRecord, TeacherRecord> SUMMON__SUMMON_TEACHER_ID_FKEY = Internal.createForeignKey(Summon.SUMMON, DSL.name("summon_teacher_id_fkey"), new TableField[] { Summon.SUMMON.TEACHER_ID }, Keys.TEACHER_PKEY, new TableField[] { Teacher.TEACHER.TEACHER_ID }, true);
    public static final ForeignKey<SummonAckRecord, SummonRecord> SUMMON_ACK__SUMMON_ACK_SUMMON_ID_FKEY = Internal.createForeignKey(SummonAck.SUMMON_ACK, DSL.name("summon_ack_summon_id_fkey"), new TableField[] { SummonAck.SUMMON_ACK.SUMMON_ID }, Keys.SUMMON_PKEY, new TableField[] { Summon.SUMMON.SUMMON_ID }, true);
    public static final ForeignKey<SummonAckRecord, TeacherRecord> SUMMON_ACK__SUMMON_ACK_TEACHER_ID_FKEY = Internal.createForeignKey(SummonAck.SUMMON_ACK, DSL.name("summon_ack_teacher_id_fkey"), new TableField[] { SummonAck.SUMMON_ACK.TEACHER_ID }, Keys.TEACHER_PKEY, new TableField[] { Teacher.TEACHER.TEACHER_ID }, true);
    public static final ForeignKey<TeacherRecord, SchoolRecord> TEACHER__TEACHER_SCHOOL_ID_FKEY = Internal.createForeignKey(Teacher.TEACHER, DSL.name("teacher_school_id_fkey"), new TableField[] { Teacher.TEACHER.SCHOOL_ID }, Keys.SCHOOL_PKEY, new TableField[] { School.SCHOOL.SCHOOL_ID }, true);
}
