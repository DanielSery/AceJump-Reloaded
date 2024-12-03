import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.25"
  id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "schoettker.acejump.reloaded"
version = "1.0.0-SNAPSHOT"

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
    id.set("schoettker.acejump.reloaded")
    // Plugin display name
    name.set("AceJump Reloaded")
    // Plugin description
    description.set("AceJump Reloaded is a plugin for IntelliJ IDEA that allows you to quickly navigate your code.")
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
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
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
