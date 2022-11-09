rootProject.name = "ekristijan"
sourceControl {
    gitRepository(java.net.URI("https://github.com/Razum-OSS/vertx-common")) {
        producesModule("si.razum:vertx-common")
    }
}
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }
}
