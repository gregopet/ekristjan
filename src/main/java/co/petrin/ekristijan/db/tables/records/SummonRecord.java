/*
 * This file is generated by jOOQ.
 */
package co.petrin.ekristijan.db.tables.records;


import co.petrin.ekristijan.db.tables.Summon;

import java.time.OffsetDateTime;

import javax.annotation.Nonnull;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * A request for the pupil to come to the door
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SummonRecord extends UpdatableRecordImpl<SummonRecord> implements Record4<Integer, Integer, Integer, OffsetDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.summon.summon_id</code>.
     */
    public void setSummonId(@Nonnull Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.summon.summon_id</code>.
     */
    @Nonnull
    public Integer getSummonId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.summon.pupil_id</code>. Pupil that was summoned
     */
    public void setPupilId(@Nonnull Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.summon.pupil_id</code>. Pupil that was summoned
     */
    @Nonnull
    public Integer getPupilId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>public.summon.teacher_id</code>. Teacher who issued the
     * summon
     */
    public void setTeacherId(@Nonnull Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.summon.teacher_id</code>. Teacher who issued the
     * summon
     */
    @Nonnull
    public Integer getTeacherId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>public.summon.time</code>. Time at which the summon was
     * triggered
     */
    public void setTime(@Nonnull OffsetDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.summon.time</code>. Time at which the summon was
     * triggered
     */
    @Nonnull
    public OffsetDateTime getTime() {
        return (OffsetDateTime) get(3);
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
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    @Nonnull
    public Row4<Integer, Integer, Integer, OffsetDateTime> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    @Nonnull
    public Row4<Integer, Integer, Integer, OffsetDateTime> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    @Nonnull
    public Field<Integer> field1() {
        return Summon.SUMMON.SUMMON_ID;
    }

    @Override
    @Nonnull
    public Field<Integer> field2() {
        return Summon.SUMMON.PUPIL_ID;
    }

    @Override
    @Nonnull
    public Field<Integer> field3() {
        return Summon.SUMMON.TEACHER_ID;
    }

    @Override
    @Nonnull
    public Field<OffsetDateTime> field4() {
        return Summon.SUMMON.TIME;
    }

    @Override
    @Nonnull
    public Integer component1() {
        return getSummonId();
    }

    @Override
    @Nonnull
    public Integer component2() {
        return getPupilId();
    }

    @Override
    @Nonnull
    public Integer component3() {
        return getTeacherId();
    }

    @Override
    @Nonnull
    public OffsetDateTime component4() {
        return getTime();
    }

    @Override
    @Nonnull
    public Integer value1() {
        return getSummonId();
    }

    @Override
    @Nonnull
    public Integer value2() {
        return getPupilId();
    }

    @Override
    @Nonnull
    public Integer value3() {
        return getTeacherId();
    }

    @Override
    @Nonnull
    public OffsetDateTime value4() {
        return getTime();
    }

    @Override
    @Nonnull
    public SummonRecord value1(@Nonnull Integer value) {
        setSummonId(value);
        return this;
    }

    @Override
    @Nonnull
    public SummonRecord value2(@Nonnull Integer value) {
        setPupilId(value);
        return this;
    }

    @Override
    @Nonnull
    public SummonRecord value3(@Nonnull Integer value) {
        setTeacherId(value);
        return this;
    }

    @Override
    @Nonnull
    public SummonRecord value4(@Nonnull OffsetDateTime value) {
        setTime(value);
        return this;
    }

    @Override
    @Nonnull
    public SummonRecord values(@Nonnull Integer value1, @Nonnull Integer value2, @Nonnull Integer value3, @Nonnull OffsetDateTime value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SummonRecord
     */
    public SummonRecord() {
        super(Summon.SUMMON);
    }

    /**
     * Create a detached, initialised SummonRecord
     */
    public SummonRecord(@Nonnull Integer summonId, @Nonnull Integer pupilId, @Nonnull Integer teacherId, @Nonnull OffsetDateTime time) {
        super(Summon.SUMMON);

        setSummonId(summonId);
        setPupilId(pupilId);
        setTeacherId(teacherId);
        setTime(time);
    }
}