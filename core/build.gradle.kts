plugins {
    `java-library`
    `maven-publish`
}

fun DependencyHandlerScope.externalLib(libraryName: String) {
    api(files("${rootProject.rootDir}/libs/$libraryName.jar"))
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("commons-io:commons-io")

    implementation("org.apache.commons:commons-configuration2")
    implementation("commons-beanutils:commons-beanutils")

    api("org.ow2.asm:asm-tree")
    api("org.ow2.asm:asm")
    api("org.ow2.asm:asm-analysis")
    api("org.ow2.asm:asm-util")
    api("org.ow2.asm:asm-commons")

    implementation("com.github.leibnitz27:cfr") { isChanging = true }
    implementation("ch.qos.logback:logback-classic")

    externalLib("fernflower-15-05-20")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

val sourcesJar = tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

// Overwrite the default jar task to include all the dependencies and duplicate strategy as ignored
val fatJar = tasks.named<Jar>("jar") {
    archiveClassifier.set("")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map {
        if(it.isDirectory) it else zipTree(it)
    })
}

artifacts {
    add("archives", fatJar)
    add("archives", sourcesJar)
}

publishing {
    repositories {
        mavenLocal()
    }
    publications {
        create<MavenPublication>("mavenJava") {
            group = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
        }
    }
}
