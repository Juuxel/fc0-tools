plugins {
    `java-library`
    `maven-publish`
    id("net.minecrell.licenser") version "0.4.1"
}

group = "io.github.juuxel"
version = "1.1.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation("blue.endless", "jankson", "1.2.0")
    implementation("org.ow2.asm", "asm", "8.0.1")
    compileOnly("org.jetbrains", "annotations", "19.0.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

license {
    header = file("HEADER.txt")
    include("**/*.java") // Only Java files, no resources
}

tasks.jar {
    from("LICENSE")
}

val sourcesJar by tasks.register<Jar>("sourcesJar") {
    group = "build"
    description = "Assembles a jar archive containing the main sources."
    archiveClassifier.set("sources")

    from(sourceSets["main"].allSource)
    from("LICENSE")
}

tasks.assemble {
    dependsOn(sourcesJar)
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])

        artifact(sourcesJar)
    }
}
