package co.petrin.ekristijan

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.Route
import io.vertx.ext.web.handler.BodyHandler
import org.slf4j.LoggerFactory

/**
 * Registers a client routing handler on some subroute (to be used as a subRouter).
 * @see Route.subRouter
 */
fun registerFrontendLoggingRouter(vertx: Vertx, logPrefix: String = "frontend.") = Router.router(vertx).let { router ->
    router.apply {
        postWithRegex("""/(?<logger>[^/]+)/(?<level>(trace|debug|info|warn|error))""")
        .handler(BodyHandler.create(false).setBodyLimit(2000L))
        .handler { ctx ->
            val level = ctx.pathParam("level")
            val logger = ctx.pathParam("logger")
            val msg = ctx.body().asString()
            val log = LoggerFactory.getLogger(logPrefix + logger) // this is probably not optimal & could be cached or something

            when (level) {
                "trace" -> log.trace(msg)
                "debug" -> log.debug(msg)
                "info" -> log.info(msg)
                "warn" -> log.warn(msg)
                "error" -> log.error(msg)
            }
            ctx.response().setStatusCode(204).end()
        }
    }
}