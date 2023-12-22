import net.fabricmc.loom.task.RemapJarTask

val fabricLoaderVersion: String by extra

repositories {
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/releases/")
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    // Bukkit libs
    implementation("org.yaml:snakeyaml:2.2")
    include("org.yaml:snakeyaml:2.2")
    implementation("org.apache.maven:maven-resolver-provider:3.9.6")
    include("org.apache.maven:maven-resolver-provider:3.9.6")
    implementation("org.apache.maven.resolver:maven-resolver-connector-basic:1.9.18")
    include("org.apache.maven.resolver:maven-resolver-connector-basic:1.9.18")
    implementation("org.apache.maven.resolver:maven-resolver-transport-http:1.9.18")
    include("org.apache.maven.resolver:maven-resolver-transport-http:1.9.18")

    // CraftBukkit libs
    implementation("jline:jline:2.12.1")
    include("jline:jline:2.12.1")
    implementation("org.apache.logging.log4j:log4j-iostreams:2.19.0")
    include("org.apache.logging.log4j:log4j-iostreams:2.19.0")
    implementation("commons-lang:commons-lang:2.6")
    include("commons-lang:commons-lang:2.6")
    implementation("com.googlecode.json-simple:json-simple:1.1.1") { exclude("junit") }
    include("com.googlecode.json-simple:json-simple:1.1.1") { exclude("junit") }
    implementation("org.xerial:sqlite-jdbc:3.42.0.1")
    include("org.xerial:sqlite-jdbc:3.42.0.1")
    implementation("com.mysql:mysql-connector-j:8.2.0")
    include("com.mysql:mysql-connector-j:8.2.0")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    include("com.google.code.findbugs:jsr305:3.0.2")
}

architectury {
    common("fabric", "forge", "neoforge")
}

loom {
    accessWidenerPath.set(file("src/main/resources/cerium.accesswidener"))
}

tasks.withType<RemapJarTask> {
    enabled = false
}