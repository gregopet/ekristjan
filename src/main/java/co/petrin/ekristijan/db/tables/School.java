/*
 * This file is generated by jOOQ.
 */
package co.petrin.ekristijan.db.tables;


import co.petrin.ekristijan.db.Keys;
import co.petrin.ekristijan.db.Public;
import co.petrin.ekristijan.db.tables.records.SchoolRecord;

import java.util.Arrays;
import java.util.List;
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
 * A school with pupils &amp; teachers
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class School extends TableImpl<SchoolRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.school</code>
     */
    public static final School SCHOOL = new School();

    /**
     * The class holding records for this type
     */
    @Override
    @Nonnull
    public Class<SchoolRecord> getRecordType() {
        return SchoolRecord.class;
    }

    /**
     * The column <code>public.school.school_id</code>.
     */
    public final TableField<SchoolRecord, Integer> SCHOOL_ID = createField(DSL.name("school_id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.school.name</code>. Name of the school
     */
    public final TableField<SchoolRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB, this, "Name of the school");

    private School(Name alias, Table<SchoolRecord> aliased) {
        this(alias, aliased, null);
    }

    private School(Name alias, Table<SchoolRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("A school with pupils & teachers"), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.school</code> table reference
     */
    public School(String alias) {
        this(DSL.name(alias), SCHOOL);
    }

    /**
     * Create an aliased <code>public.school</code> table reference
     */
    public School(Name alias) {
        this(alias, SCHOOL);
    }

    /**
     * Create a <code>public.school</code> table reference
     */
    public School() {
        this(DSL.name("school"), null);
    }

    public <O extends Record> School(Table<O> child, ForeignKey<O, SchoolRecord> key) {
        super(child, key, SCHOOL);
    }

    @Override
    @Nonnull
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    @Nonnull
    public Identity<SchoolRecord, Integer> getIdentity() {
        return (Identity<SchoolRecord, Integer>) super.getIdentity();
    }

    @Override
    @Nonnull
    public UniqueKey<SchoolRecord> getPrimaryKey() {
        return Keys.SCHOOL_PKEY;
    }

    @Override
    @Nonnull
    public List<UniqueKey<SchoolRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.SCHOOL_NAME_KEY);
    }

    @Override
    @Nonnull
    public School as(String alias) {
        return new School(DSL.name(alias), this);
    }

    @Override
    @Nonnull
    public School as(Name alias) {
        return new School(alias, this);
    }

    @Override
    @Nonnull
    public School as(Table<?> alias) {
        return new School(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    @Nonnull
    public School rename(String name) {
        return new School(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    @Nonnull
    public School rename(Name name) {
        return new School(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    @Nonnull
    public School rename(Table<?> name) {
        return new School(name.getQualifiedName(), null);
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
