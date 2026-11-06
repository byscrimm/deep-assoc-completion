import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    java
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "lv.midiana.misc.phpstorm-plugins"
version = providers.gradleProperty("pluginVersion").get()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

java {
    toolchain {
        // PhpStorm 2026.2 runs on JBR 25 and its class files are bytecode 69
        languageVersion = JavaLanguageVersion.of(25)
    }
}

sourceSets {
    main {
        java.srcDir("src")
        resources.srcDir("resources")
    }
    test {
        java.srcDir("tests-src")
    }
}

tasks.processResources {
    from("src") {
        include("**/*.png", "**/*.js")
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    testRuntimeOnly("org.opentest4j:opentest4j:1.3.0")

    intellijPlatform {
        // -PlocalIdePath=/Applications/PhpStorm.app builds against an installed IDE
        if (project.hasProperty("localIdePath")) {
            local(project.property("localIdePath").toString())
        } else {
            phpstorm(providers.gradleProperty("platformVersion").get())
        }
        bundledPlugin("com.jetbrains.php")
        bundledPlugin("com.intellij.database")
        bundledPlugin("com.jetbrains.twig")
        bundledModule("com.intellij.modules.json")
        pluginVerifier()
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    pluginConfiguration {
        version = providers.gradleProperty("pluginVersion")
        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.gradleProperty("pluginUntilBuild")
        }
    }
    pluginVerification {
        ides {
            current()
        }
    }
}

tasks.test {
    systemProperty("idea.platform.prefix", "PhpStorm")
}
