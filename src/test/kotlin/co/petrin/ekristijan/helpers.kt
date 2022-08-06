package co.petrin.ekristijan

/** A block to fence off verification of test preconditions. No functionality, syntax tool only. */
internal fun preconditions(block: () -> Any) {
    block()
}