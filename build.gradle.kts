plugins {
    id("kotlin2js") version "1.3.31"
    id("org.jetbrains.kotlin.frontend") version "0.0.45"
    `maven-publish`
}

group = "com.rnett.kframe"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))
}

kotlin {
    target.compilations.all {
        kotlinOptions {
            sourceMap = true
            moduleKind = "amd"
            metaInfo = true
            //outputFile = "$projectDir/web/"
            noStdlib = true
            sourceMapEmbedSources = "always"
        }
    }
}

kotlinFrontend {

    sourceMaps = true
    downloadNodeJsVersion = "latest"

}

tasks.create<Jar>("sourcesJar"){
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
            
            artifact("sourcesJar")
        }
    }
}
