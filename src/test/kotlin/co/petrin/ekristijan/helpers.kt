package co.petrin.ekristijan

import io.kotest.assertions.withClue
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** A block to fence off verification of test preconditions. No functionality, syntax tool only. */
internal suspend fun<T> preconditions(block: suspend () -> T): T =
    withClue("Failure in a precondition block") {
        block()
    }

/** Allows us to call Kotlin's standard "also" but also provide a clue for assertions */
@OptIn(ExperimentalContracts::class)
internal inline fun <T> T.assertWithClue(clue:String, block: (T) -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    withClue(clue) {
        block(this)
    }
    return this
}