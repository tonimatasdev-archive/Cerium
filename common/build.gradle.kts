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
}

architectury {
    common("fabric", "forge", "neoforge")
}
