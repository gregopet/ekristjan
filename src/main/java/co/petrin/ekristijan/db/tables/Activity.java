/*
 * This file is generated by jOOQ.
 */
package co.petrin.ekristijan.db.tables;


import co.petrin.ekristijan.db.Keys;
import co.petrin.ekristijan.db.Public;
import co.petrin.ekristijan.db.tables.records.ActivityRecord;

import java.util.function.Function;

import javax.annotation.Nonnull;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function2;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * An after-school activity pupils may have on their schedule
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Activity extends TableImpl<ActivityRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.activity</code>
     */
    public static final Activity ACTIVITY = new Activity();

    /**
     * The class holding records for this type
     */
    @Override
    @Nonnull
    public Class<ActivityRecord> getRecordType() {
        return ActivityRecord.class;
    }

    /**
     * The column <code>public.activity.activity_id</code>.
     */
    public final TableField<ActivityRecord, Integer> ACTIVITY_ID = createField(DSL.name("activity_id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.activity.name</code>. Name of the activity
     */
    public final TableField<ActivityRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB.nullable(false), this, "Name of the activity");

    private Activity(Name alias, Table<ActivityRecord> aliased) {
        this(alias, aliased, null);
    }

    private Activity(Name alias, Table<ActivityRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("An after-school activity pupils may have on their schedule"), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.activity</code> table reference
     */
    public Activity(String alias) {
        this(DSL.name(alias), ACTIVITY);
    }

    /**
     * Create an aliased <code>public.activity</code> table reference
     */
    public Activity(Name alias) {
        this(alias, ACTIVITY);
    }

    /**
     * Create a <code>public.activity</code> table reference
     */
    public Activity() {
        this(DSL.name("activity"), null);
    }

    public <O extends Record> Activity(Table<O> child, ForeignKey<O, ActivityRecord> key) {
        super(child, key, ACTIVITY);
    }

    @Override
    @Nonnull
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    @Nonnull
    public Identity<ActivityRecord, Integer> getIdentity() {
        return (Identity<ActivityRecord, Integer>) super.getIdentity();
    }

    @Override
    @Nonnull
    public UniqueKey<ActivityRecord> getPrimaryKey() {
        return Keys.ACTIVITY_PKEY;
    }

    @Override
    @Nonnull
    public Activity as(String alias) {
        return new Activity(DSL.name(alias), this);
    }

    @Override
    @Nonnull
    public Activity as(Name alias) {
        return new Activity(alias, this);
    }

    @Override
    @Nonnull
    public Activity as(Table<?> alias) {
        return new Activity(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    @Nonnull
    public Activity rename(String name) {
        return new Activity(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    @Nonnull
    public Activity rename(Name name) {
        return new Activity(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    @Nonnull
    public Activity rename(Table<?> name) {
        return new Activity(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    @Nonnull
    public Row2<Integer, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function2<? super Integer, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function2<? super Integer, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
