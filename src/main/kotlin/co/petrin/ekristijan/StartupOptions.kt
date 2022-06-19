package co.petrin.ekristijan
import picocli.CommandLine

/** Command line options that can be provided when spinning up the app */
class StartupOptions (
    @field:CommandLine.Option(names= ["-c", "--config"], description = ["Path to the external config file (if not given, defaults will be used)"])
    var configFile: String? = null,
)