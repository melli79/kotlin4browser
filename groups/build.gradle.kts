plugins {
    kotlin("js") version "1.6.0"
}

group = "org.grutzmann"
version = "0.5-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react:17.0.2-pre.272-kotlin-1.6.0")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:17.0.2-pre.272-kotlin-1.6.0")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-styled:5.3.3-pre.272-kotlin-1.6.0")
}

kotlin {
    js(LEGACY) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
}
