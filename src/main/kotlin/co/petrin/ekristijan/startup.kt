package co.petrin.ekristijan

import co.petrin.ekristijan.security.createJwtProvider
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
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

            val jwtProvider = createJwtProvider(vertx, config.jwtSymetricPassword)

            val router = Router.router(vertx)
            router.route().handler(StaticHandler.create(config.frontendDistFolder ?: "src/frontend/dist"))

            DepartureVerticle(jooq, jwtProvider).let { verticle ->
                vertx.deployVerticle(verticle).await()
                router.route("/departures/*").subRouter(verticle.createSubrouter())
            }
            SecurityVerticle(jooq, jwtProvider).let { verticle ->
                vertx.deployVerticle(verticle).await()
                router.route("/security/*").subRouter(verticle.createSubrouter("/security"))
            }
            BackofficeVerticle(jooq, jwtProvider).let { verticle ->
                vertx.deployVerticle(verticle).await()
                router.route("/backoffice/*").subRouter(verticle.createSubrouter("/security"))
            }
            router.route("/log/*").subRouter(registerFrontendLoggingRouter(vertx))

            vertx
                .createHttpServer()
                .requestHandler(router::handle)
                .listen(config.port)
                .await()
            LOG.info("Server listening on ${config.port}")
        } catch (t: Throwable) {
            LOG.error("Error starting main verticle", t)
            vertx.close()
        }
    }
}