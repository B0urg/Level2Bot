plugins{
    id("com.github.johnrengelman.shadow") version "7.1.2"
    java
    `maven-publish`
}

group = "dev.bourg"
version = "1.0"

repositories{
    mavenCentral()
}

dependencies{
    implementation("ch.qos.logback:logback-classic:1.2.8")
    implementation("net.dv8tion:JDA:5.0.0-alpha.18")
    implementation ("com.google.code.gson:gson:2.9.1")
    implementation("mysql:mysql-connector-java:8.0.30")
    implementation("org.apache.logging.log4j:log4j-api:2.18.0")
    implementation("org.apache.logging.log4j:log4j-core:2.18.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
    withSourcesJar()
    withJavadocJar()
}

tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("version") {
                expand(
                    "version" to project.version
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    compileJava {
        options.encoding = "UTF-8"
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    shadowJar {
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "dev.bourg.level2bot.Level2Bot"))
        }
    }

    build {
        dependsOn(shadowJar)
    }

}