package co.petrin.ekristijan.db

import co.petrin.ekristijan.TestSetup
import co.petrin.ekristijan.TextFixtures
import co.petrin.ekristijan.db.Tables.REGISTERED_DEVICE
import co.petrin.ekristijan.db.tables.records.RegisteredDeviceRecord
import co.petrin.ekristijan.dto.PushSubscription
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.*
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import nl.martijndwars.webpush.Subscription
import si.razum.vertx.db.ConnectionPool

class DeviceQueriesSpec : FreeSpec({

    val postgres = TestSetup.postgresContainer
    val jooq = ConnectionPool.wrapWithJooq(postgres.openConnection(), true)


    "Given a device registered for class 1A events" - {
        val devicePushEndpoint = "https://push"
        val subscription = PushSubscription(devicePushEndpoint, Subscription.Keys("key", "auth"), setOf("1A"))
        fun refreshDevice(): RegisteredDeviceRecord = jooq.selectFrom(REGISTERED_DEVICE).where(REGISTERED_DEVICE.PUSH_ENDPOINT.eq(devicePushEndpoint)).fetchOne()!!
        DeviceQueries.registerOrUpdateDevice(
            teacherId = TextFixtures.teacher.id,
            subscription = subscription.copy(fromClasses = setOf("1A")),
            userAgent = "Android",
            trans = jooq
        )
        refreshDevice().apply {
            this shouldNotBe null
            classes shouldBe listOf("1A")
        }

        "device info (such as list of classes) can be upserted" {
            DeviceQueries.registerOrUpdateDevice(
                teacherId = TextFixtures.teacher.id,
                subscription = subscription.copy(fromClasses = setOf("1A", "1B")),
                userAgent = "Android",
                trans = jooq
            )
            refreshDevice().apply {
                classes shouldBe listOf("1A", "1B")
            }
        }

        "device will be returned when querying via classes" {
            DeviceQueries.devicesToNotify(TextFixtures.schoolId, "1A", jooq) should exist { it.subscription().endpoint == devicePushEndpoint }
            DeviceQueries.devicesToNotify(TextFixtures.schoolId, "2A", jooq) shouldNot exist { it.subscription().endpoint == devicePushEndpoint }
        }

        "device can be removed" {
            DeviceQueries.registerOrUpdateDevice(
                teacherId = TextFixtures.teacher.id,
                subscription = PushSubscription("delete me", Subscription.Keys("key", "auth"), setOf("1A")),
                userAgent = "Android",
                trans = jooq
            )
            DeviceQueries.devicesToNotify(TextFixtures.schoolId, "1A", jooq) should exist { it.subscription().endpoint == "delete me" }
            DeviceQueries.unregisterDevice("delete me", jooq)
            DeviceQueries.devicesToNotify(TextFixtures.schoolId, "1A", jooq) shouldNot exist { it.subscription().endpoint == "delete me" }
        }


    }

})
