/*
 * This file is generated by jOOQ.
 */
package co.petrin.ekristijan.db.tables.records;


import co.petrin.ekristijan.db.tables.Pupil;

import java.time.LocalTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * A pupil whose departures we are tracking
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PupilRecord extends UpdatableRecordImpl<PupilRecord> implements Record10<Integer, Integer, String, String, Boolean, LocalTime, LocalTime, LocalTime, LocalTime, LocalTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.pupil.pupil_id</code>.
     */
    public void setPupilId(@Nonnull Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.pupil.pupil_id</code>.
     */
    @Nonnull
    public Integer getPupilId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.pupil.school_id</code>. School this pupil is from
     */
    public void setSchoolId(@Nonnull Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.pupil.school_id</code>. School this pupil is from
     */
    @Nonnull
    public Integer getSchoolId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>public.pupil.name</code>. Pupil's name
     */
    public void setName(@Nonnull String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.pupil.name</code>. Pupil's name
     */
    @Nonnull
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.pupil.clazz</code>. The class this pupil belongs
     * to (class is a reserved word in languages, thus clazz)
     */
    public void setClazz(@Nonnull String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.pupil.clazz</code>. The class this pupil belongs
     * to (class is a reserved word in languages, thus clazz)
     */
    @Nonnull
    public String getClazz() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.pupil.leaves_alone</code>. Can this pupil leave
     * school on their own?
     */
    public void setLeavesAlone(@Nonnull Boolean value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.pupil.leaves_alone</code>. Can this pupil leave
     * school on their own?
     */
    @Nonnull
    public Boolean getLeavesAlone() {
        return (Boolean) get(4);
    }

    /**
     * Setter for <code>public.pupil.leave_mon</code>. The time at which this
     * pupil will leave school every monday
     */
    public void setLeaveMon(@Nullable LocalTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.pupil.leave_mon</code>. The time at which this
     * pupil will leave school every monday
     */
    @Nullable
    public LocalTime getLeaveMon() {
        return (LocalTime) get(5);
    }

    /**
     * Setter for <code>public.pupil.leave_tue</code>. The time at which this
     * pupil will leave school every tuesday
     */
    public void setLeaveTue(@Nullable LocalTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.pupil.leave_tue</code>. The time at which this
     * pupil will leave school every tuesday
     */
    @Nullable
    public LocalTime getLeaveTue() {
        return (LocalTime) get(6);
    }

    /**
     * Setter for <code>public.pupil.leave_wed</code>. The time at which this
     * pupil will leave school every wednesday
     */
    public void setLeaveWed(@Nullable LocalTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.pupil.leave_wed</code>. The time at which this
     * pupil will leave school every wednesday
     */
    @Nullable
    public LocalTime getLeaveWed() {
        return (LocalTime) get(7);
    }

    /**
     * Setter for <code>public.pupil.leave_thu</code>. The time at which this
     * pupil will leave school every thursday
     */
    public void setLeaveThu(@Nullable LocalTime value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.pupil.leave_thu</code>. The time at which this
     * pupil will leave school every thursday
     */
    @Nullable
    public LocalTime getLeaveThu() {
        return (LocalTime) get(8);
    }

    /**
     * Setter for <code>public.pupil.leave_fri</code>. The time at which this
     * pupil will leave school every friday
     */
    public void setLeaveFri(@Nullable LocalTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.pupil.leave_fri</code>. The time at which this
     * pupil will leave school every friday
     */
    @Nullable
    public LocalTime getLeaveFri() {
        return (LocalTime) get(9);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    @Nonnull
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    @Override
    @Nonnull
    public Row10<Integer, Integer, String, String, Boolean, LocalTime, LocalTime, LocalTime, LocalTime, LocalTime> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    @Override
    @Nonnull
    public Row10<Integer, Integer, String, String, Boolean, LocalTime, LocalTime, LocalTime, LocalTime, LocalTime> valuesRow() {
        return (Row10) super.valuesRow();
    }

    @Override
    @Nonnull
    public Field<Integer> field1() {
        return Pupil.PUPIL.PUPIL_ID;
    }

    @Override
    @Nonnull
    public Field<Integer> field2() {
        return Pupil.PUPIL.SCHOOL_ID;
    }

    @Override
    @Nonnull
    public Field<String> field3() {
        return Pupil.PUPIL.NAME;
    }

    @Override
    @Nonnull
    public Field<String> field4() {
        return Pupil.PUPIL.CLAZZ;
    }

    @Override
    @Nonnull
    public Field<Boolean> field5() {
        return Pupil.PUPIL.LEAVES_ALONE;
    }

    @Override
    @Nonnull
    public Field<LocalTime> field6() {
        return Pupil.PUPIL.LEAVE_MON;
    }

    @Override
    @Nonnull
    public Field<LocalTime> field7() {
        return Pupil.PUPIL.LEAVE_TUE;
    }

    @Override
    @Nonnull
    public Field<LocalTime> field8() {
        return Pupil.PUPIL.LEAVE_WED;
    }

    @Override
    @Nonnull
    public Field<LocalTime> field9() {
        return Pupil.PUPIL.LEAVE_THU;
    }

    @Override
    @Nonnull
    public Field<LocalTime> field10() {
        return Pupil.PUPIL.LEAVE_FRI;
    }

    @Override
    @Nonnull
    public Integer component1() {
        return getPupilId();
    }

    @Override
    @Nonnull
    public Integer component2() {
        return getSchoolId();
    }

    @Override
    @Nonnull
    public String component3() {
        return getName();
    }

    @Override
    @Nonnull
    public String component4() {
        return getClazz();
    }

    @Override
    @Nonnull
    public Boolean component5() {
        return getLeavesAlone();
    }

    @Override
    @Nullable
    public LocalTime component6() {
        return getLeaveMon();
    }

    @Override
    @Nullable
    public LocalTime component7() {
        return getLeaveTue();
    }

    @Override
    @Nullable
    public LocalTime component8() {
        return getLeaveWed();
    }

    @Override
    @Nullable
    public LocalTime component9() {
        return getLeaveThu();
    }

    @Override
    @Nullable
    public LocalTime component10() {
        return getLeaveFri();
    }

    @Override
    @Nonnull
    public Integer value1() {
        return getPupilId();
    }

    @Override
    @Nonnull
    public Integer value2() {
        return getSchoolId();
    }

    @Override
    @Nonnull
    public String value3() {
        return getName();
    }

    @Override
    @Nonnull
    public String value4() {
        return getClazz();
    }

    @Override
    @Nonnull
    public Boolean value5() {
        return getLeavesAlone();
    }

    @Override
    @Nullable
    public LocalTime value6() {
        return getLeaveMon();
    }

    @Override
    @Nullable
    public LocalTime value7() {
        return getLeaveTue();
    }

    @Override
    @Nullable
    public LocalTime value8() {
        return getLeaveWed();
    }

    @Override
    @Nullable
    public LocalTime value9() {
        return getLeaveThu();
    }

    @Override
    @Nullable
    public LocalTime value10() {
        return getLeaveFri();
    }

    @Override
    @Nonnull
    public PupilRecord value1(@Nonnull Integer value) {
        setPupilId(value);
        return this;
    }

    @Override
    @Nonnull
    public PupilRecord value2(@Nonnull Integer value) {
        setSchoolId(value);
        return this;
    }

    @Override
    @Nonnull
    public PupilRecord value3(@Nonnull String value) {
        setName(value);
        return this;
    }

    @Override
    @Nonnull
    public PupilRecord value4(@Nonnull String value) {
        setClazz(value);
        return this;
    }

    @Override
    @Nonnull
    public PupilRecord value5(@Nonnull Boolean value) {
        setLeavesAlone(value);
        return this;
    }

    @Override
    @Nonnull
    public PupilRecord value6(@Nullable LocalTime value) {
        setLeaveMon(value);
        return this;
    }

    @Override
    @Nonnull
    public PupilRecord value7(@Nullable LocalTime value) {
        setLeaveTue(value);
        return this;
    }

    @Override
    @Nonnull
    public PupilRecord value8(@Nullable LocalTime value) {
        setLeaveWed(value);
        return this;
    }

    @Override
    @Nonnull
    public PupilRecord value9(@Nullable LocalTime value) {
        setLeaveThu(value);
        return this;
    }

    @Override
    @Nonnull
    public PupilRecord value10(@Nullable LocalTime value) {
        setLeaveFri(value);
        return this;
    }

    @Override
    @Nonnull
    public PupilRecord values(@Nonnull Integer value1, @Nonnull Integer value2, @Nonnull String value3, @Nonnull String value4, @Nonnull Boolean value5, @Nullable LocalTime value6, @Nullable LocalTime value7, @Nullable LocalTime value8, @Nullable LocalTime value9, @Nullable LocalTime value10) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PupilRecord
     */
    public PupilRecord() {
        super(Pupil.PUPIL);
    }

    /**
     * Create a detached, initialised PupilRecord
     */
    public PupilRecord(@Nonnull Integer pupilId, @Nonnull Integer schoolId, @Nonnull String name, @Nonnull String clazz, @Nonnull Boolean leavesAlone, @Nullable LocalTime leaveMon, @Nullable LocalTime leaveTue, @Nullable LocalTime leaveWed, @Nullable LocalTime leaveThu, @Nullable LocalTime leaveFri) {
        super(Pupil.PUPIL);

        setPupilId(pupilId);
        setSchoolId(schoolId);
        setName(name);
        setClazz(clazz);
        setLeavesAlone(leavesAlone);
        setLeaveMon(leaveMon);
        setLeaveTue(leaveTue);
        setLeaveWed(leaveWed);
        setLeaveThu(leaveThu);
        setLeaveFri(leaveFri);
    }
}