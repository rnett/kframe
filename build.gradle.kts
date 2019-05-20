plugins {
    id("kotlin2js") version "1.3.31"
    //id("org.jetbrains.kotlin.frontend") version "0.0.45"
    `maven-publish`
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

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.11.0")
}

kotlin {
    target.compilations.all {
        kotlinOptions {
            sourceMap = true
            moduleKind = "commonjs"
            metaInfo = true
            //outputFile = "$projectDir/web/"
            noStdlib = true
            sourceMapEmbedSources = "always"
            freeCompilerArgs += "-Xuse-experimental=kotlin.contracts.ExperimentalContracts"
        }
    }
}

//kotlinFrontend {
//    sourceMaps = true
//    downloadNodeJsVersion = "latest"
//}

val sourcesJar = tasks.create<Jar>("sourcesJar") {
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

publishing {
    publications{
        create("default", MavenPublication::class) {
            from(components["java"])
            group = "com.rnett.kframe"
            artifactId = "kframe"
            version = "1.0.0"

            artifact(sourcesJar)
        }
    }
}
