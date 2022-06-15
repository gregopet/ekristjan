package co.petrin.ekristijan

import co.petrin.ekristijan.dto.PushSubscription
import nl.martijndwars.webpush.Subscription

/** A client that has subscribed to receiving push notifications for certain classes */
data class SubscribedClient(val subscription: Subscription, var subscriptionData: PushSubscription)