package co.petrin.ekristijan.db

import co.petrin.ekristijan.db.Tables.REGISTERED_DEVICE
import co.petrin.ekristijan.dto.PushSubscription
import nl.martijndwars.webpush.Subscription
import org.jooq.DSLContext
import org.jooq.impl.DSL.`val`
import org.jooq.impl.DSL.any


object DeviceQueries {

    /**
     * Registers a new device for receiving push notifications or updates an existing one.
     * @param pushEndpoint The push endpoint via which this device can be contacted
     * @param classes The classes this endpoint wishes to be contacted for
     * @param keys The push notification authentication data
     * @param userAgent The user agent this device sent, for diagnostic purposes
     * @param teacherId The teacher who was logged into the device
     * @param trans A jooq connection/transaction
     */
    fun registerOrUpdateDevice(teacherId: Int, subscription: PushSubscription, userAgent: String?, classes: List<String>, trans: DSLContext) = with(REGISTERED_DEVICE) {
        trans
            .insertInto(REGISTERED_DEVICE)
                .set(PUSH_ENDPOINT, subscription.endpoint)
                .set(USER_AGENT, userAgent)
                .set(LAST_TEACHER_ID, teacherId)
                .set(PUSH_AUTH, subscription.keys.auth)
                .set(PUSH_P256DH, subscription.keys.p256dh)
                .set(CLASSES, classes.toTypedArray())
            .onConflict(PUSH_ENDPOINT).doUpdate()
                .set(USER_AGENT, userAgent)
                .set(PUSH_AUTH, subscription.keys.auth)
                .set(PUSH_P256DH, subscription.keys.p256dh)
                .set(CLASSES, classes.toTypedArray())
                .set(LAST_TEACHER_ID, teacherId)
            .execute()
    }

    /**
     * Unregisters a device from sending push notifications.
     * @param pushEndpoint The push endpoint via which this device can be contacted
     * @param trans A jooq connection/transaction
     */
    fun unregisterDevice(endpoint: String, trans: DSLContext) = with(REGISTERED_DEVICE) {
        trans.deleteFrom(REGISTERED_DEVICE).where(PUSH_ENDPOINT.eq(endpoint)).execute()
    }

    /**
     * Finds all devices that need to be notified when a pupil from [clazz] leaves school.
     * @param schoolId The school to which the pupil belongs
     * @param clazz The class from which this pupil originates
     * @param trans A jooq connection/transaction
     */
    fun devicesToNotify(schoolId: Int, clazz: String, trans: DSLContext): List<PushSubscription> = with(REGISTERED_DEVICE) {
        trans
            .select(PUSH_ENDPOINT, PUSH_AUTH, PUSH_P256DH, CLASSES)
            .from(REGISTERED_DEVICE)
            .where(
                teacher().SCHOOL_ID.eq(schoolId),
                `val`(clazz).eq(any(CLASSES)),
            )
            .fetch { rec ->
                PushSubscription(
                    endpoint = rec.get(PUSH_ENDPOINT),
                    keys = Subscription.Keys(rec.get(PUSH_P256DH), rec.get(PUSH_AUTH)),
                    fromClasses = rec.get(CLASSES).toSet()
                )
            }
    }
}