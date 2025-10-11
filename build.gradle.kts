import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.25"
  id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "serydaniel.aceactions.AceActions"
version = "1.0.2"

repositories {
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
  intellijPlatform {
    pluginVerifier()
    intellijIdeaUltimate("2024.3")
    instrumentationTools()
  }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellijPlatform {
  pluginConfiguration {
    // Plugin ID
    id.set("serydaniel.aceactions.AceActions")
    // Plugin display name
    name.set("AceActions")
    // Plugin description
    description.set("A plugin to easily jump and perform actions inside code inspired by emacs. Fork from https://github.com/schoettker")
    ideaVersion {
      untilBuild = provider { null }
    }
  }
  pluginVerification {
    ides {
      ide(IntelliJPlatformType.IntellijIdeaUltimate, "2024.3")
      select {
        types = listOf(IntelliJPlatformType.IntellijIdeaUltimate)
      }
    }
  }
}


tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
  }

  patchPluginXml {
    sinceBuild.set("232")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}
