plugins {
    id("org.jetbrains.kotlin.js") version "1.3.60"
    //id("org.jetbrains.kotlin.frontend") version "0.0.45"
    maven
    `maven-publish`
    id("kotlinx-serialization") version "1.3.60"
}

group = "com.rnett.kframe"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
    maven("https://dl.bintray.com/soywiz/soywiz")
    maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
    implementation(kotlin("stdlib-js"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.13.0")
}


kotlin.target.browser {}

kotlin.target {
    useCommonJs()

    configure(compilations) {
        kotlinOptions {
            metaInfo = true
            noStdlib = true
            sourceMap = true
            sourceMapEmbedSources = "always"
            freeCompilerArgs += "-Xuse-experimental=kotlin.contracts.ExperimentalContracts"
        }
    }
}


val sourcesJar = tasks.create<Jar>("sourcesJar") {
    classifier = "sources"
    from(kotlin.sourceSets["main"].kotlin.srcDirs)
}

publishing {
    publications{
        create("default", MavenPublication::class) {
            from(components["kotlin"])
            group = "com.rnett.kframe"
            artifactId = "kframe"
            version = "1.0.0"

            artifact(sourcesJar)
        }
    }
}
