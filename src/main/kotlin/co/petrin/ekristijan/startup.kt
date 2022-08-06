package co.petrin.ekristijan

import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.slf4j.LoggerFactory
import picocli.CommandLine
import si.razum.vertx.config.ConfigurableCoroutineVerticle
import si.razum.vertx.configureDataBinding
import si.razum.vertx.db.ConnectionPool
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
        try {
            Security.addProvider(BouncyCastleProvider())

            val configRetriever =
                ConfigurableCoroutineVerticle.reloadableHoconConfig(vertx, overrideFileLocation = options.configFile)
            val configJson = configRetriever.config.await()
            val config = configJson.mapTo(Config::class.java)

            ConnectionPool.migrateDatabase(config.db, false, false, true)
            val jooq = ConnectionPool.wrapWithJooq(config.db, true)
            jooq.selectOne().fetch()

            vertx.deployVerticle(DepartureVerticle()).await()
        } catch (t: Throwable) {
            LOG.error("Error starting main verticle", t)
            vertx.close()
        }
    }
}