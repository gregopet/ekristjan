package co.petrin.ekristijan

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions
import io.vertx.ext.web.handler.sockjs.SockJSSocket
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import java.nio.charset.Charset


class MainVerticle : CoroutineVerticle() {

  val handlers = mutableSetOf<SockJSSocket>()

  override suspend fun start() {

    val socketHandler = SockJSHandler.create(vertx, SockJSHandlerOptions()
      .setRegisterWriteHandler(true)
    ).socketHandler { socket ->
      println("Handler connecting")
      handlers.add(socket)

      socket.handler { buff ->
          // Relay message to everyone else
          handlers.forEach { anotherSocket ->
            if (anotherSocket !== socket) {
              anotherSocket.write(buff)
            }
          }
      }
      socket.closeHandler {
        handlers.remove(socket)
        println("Handler disconnected")
      }
    }

    val router = Router.router(vertx)
    router.route().handler(StaticHandler.create("src/frontend/dist"))
      router.route("/sock/*").subRouter(socketHandler)

    vertx
      .createHttpServer()
      .requestHandler(router::handle)
      .listen(8888)
      .await()
  }
}
