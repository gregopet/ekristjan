/*
 * This file is generated by jOOQ.
 */
package co.petrin.ekristijan.db;


import co.petrin.ekristijan.db.tables.Departure;
import co.petrin.ekristijan.db.tables.ExtraordinaryDeparture;
import co.petrin.ekristijan.db.tables.Summon;
import co.petrin.ekristijan.db.tables.SummonAck;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index IDX_DEPARTURE_PUPIL = Internal.createIndex(DSL.name("idx_departure_pupil"), Departure.DEPARTURE, new OrderField[] { Departure.DEPARTURE.PUPIL_ID, Departure.DEPARTURE.TIME }, false);
    public static final Index IDX_EXTRAORDINARY_DEPARTURE_PUPIL = Internal.createIndex(DSL.name("idx_extraordinary_departure_pupil"), ExtraordinaryDeparture.EXTRAORDINARY_DEPARTURE, new OrderField[] { ExtraordinaryDeparture.EXTRAORDINARY_DEPARTURE.PUPIL_ID, ExtraordinaryDeparture.EXTRAORDINARY_DEPARTURE.DATE }, false);
    public static final Index IDX_SUMMON_ACK_SUMMON = Internal.createIndex(DSL.name("idx_summon_ack_summon"), SummonAck.SUMMON_ACK, new OrderField[] { SummonAck.SUMMON_ACK.SUMMON_ID }, false);
    public static final Index IDX_SUMMON_PUPIL = Internal.createIndex(DSL.name("idx_summon_pupil"), Summon.SUMMON, new OrderField[] { Summon.SUMMON.PUPIL_ID, Summon.SUMMON.CREATED_AT }, false);
}
