package co.petrin.ekristijan

import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.AutoScan
import si.razum.vertx.configureDataBinding
import si.razum.vertx.PostgresContainer

@AutoScan
class TestSetup : ProjectListener {
    override suspend fun beforeProject() {
        configureDataBinding()
    }

    companion object {
        /** An once-per-test initiated postgres container */
        val postgresContainer by lazy {
            PostgresContainer("14")
        }
    }
}