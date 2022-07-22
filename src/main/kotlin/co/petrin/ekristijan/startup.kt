package co.petrin.ekristijan

import io.vertx.core.Vertx
import io.vertx.kotlin.core.deploymentOptionsOf
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.slf4j.LoggerFactory
import picocli.CommandLine
import si.razum.vertx.config.ConfigurableCoroutineVerticle
import si.razum.vertx.configureDataBinding
import java.security.Security

private val LOG = LoggerFactory.getLogger("ekristijan.main")

/**
 * Main entry point into application.
 */
fun main(args: Array<String>) {
    val options = CommandLine.populateCommand(StartupOptions(), *args)
    val vertx = Vertx.vertx()
    configureDataBinding()

    runBlocking {
        Security.addProvider(BouncyCastleProvider())

        val configRetriever = ConfigurableCoroutineVerticle.reloadableHoconConfig("config.hocon", options.configFile, vertx)
        val config = configRetriever.config.await()

        try {
            vertx.deployVerticle(MainVerticle(), deploymentOptionsOf(config = config)).await()
        } catch (t: Throwable) {
            LOG.error("Error starting main verticle", t)
            vertx.close()
        }
    }
}