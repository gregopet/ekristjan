package co.petrin.ekristijan

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.kotlin.core.deploymentOptionsOf
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.security.Security

val CONFIG_CHANGE_HANDLER_ADDRESS = "ekristijan.configChange"
private val LOG = LoggerFactory.getLogger("ekristijan.main")

/**
 * Main entry point into application.
 */
fun main(args: Array<String>) {
    val options = CommandLine.populateCommand(StartupOptions(), *args)
    val vertx = Vertx.vertx()
    runBlocking {
        Security.addProvider(BouncyCastleProvider())
        DatabindCodec.mapper().registerModule(KotlinModule.Builder().build())

        val configurator = createConfigRetriever(options.configFile, vertx)
        val config = configurator.config.await()


        try {
            vertx.deployVerticle(MainVerticle(), deploymentOptionsOf(config = config)).await()
        } catch (t: Throwable) {
            LOG.error("Error starting main verticle", t)
            vertx.close()
        }
    }
}

private fun createConfigRetriever(configFile: String?, vertx: Vertx): ConfigRetriever {
    val configRetrieverOptions = ConfigRetrieverOptions()
    configRetrieverOptions.addStore(
        ConfigStoreOptions().setType("file").setFormat("hocon").setConfig(JsonObject().put("path", "config.hocon"))
    )

    if (configFile != null) {
        LOG.info("Overlaying default config with options from $configFile")
        configRetrieverOptions.addStore(ConfigStoreOptions().apply {
            type = "file"
            isOptional = false
            format = "hocon"
            config = JsonObject().put("path", configFile)
        })
    }

    val configRetriever = ConfigRetriever.create(vertx, configRetrieverOptions)
    configRetriever.listen { change ->
        vertx.eventBus().publish(CONFIG_CHANGE_HANDLER_ADDRESS, change.newConfiguration)
    }
    return configRetriever
}