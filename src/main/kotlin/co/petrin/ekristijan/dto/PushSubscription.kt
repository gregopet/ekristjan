package co.petrin.ekristijan.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import nl.martijndwars.webpush.Subscription

/**
 * A command coming in to subscribe to push notifications.
 * @param endpoint The endpoint to which notifications must be sent
 * @param keys The subscription keys generated by the browser & its push server
 * @param fromClasses The classes we want to be notified about
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class PushSubscription(val endpoint: String, val keys: Subscription.Keys, val fromClasses: Set<String>) {
    fun subscription() = Subscription(endpoint, keys)
}