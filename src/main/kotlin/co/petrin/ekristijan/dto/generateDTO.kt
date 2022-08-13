package co.petrin.ekristijan.dto

import co.petrin.ekristijan.db.DailyDeparture
import co.petrin.ekristijan.dto.event.SendPupilEvent
import co.petrin.ekristijan.security.LoginDTO
import me.ntrrgc.tsGenerator.TypeScriptGenerator
import java.nio.file.Paths
import kotlin.io.path.writeText

/**
 * Generates typescript definitions for the DTO files and stores them in the file indicated by the first parameter.
 * This code is not really required during runtime, on the other hand it doesn't hurt if it's here.
 **/
fun main(args: Array<String>) {
    val (destination) = args
    val definitions = TypeScriptGenerator(
        rootClasses = setOf(
            PushSubscription::class,
            SendPupilEvent::class,
            Pupil::class,
            LoginDTO::class,
            DailyDeparture::class,
            LoginDTO::class,
        )
    ).definitionsText
    val namespacedDefinition = listOf("namespace dto {", definitions, "}").joinToString("\n")

    println("Saving following definitions to $destination:")
    println(namespacedDefinition)

    Paths.get(destination).writeText(namespacedDefinition)
}