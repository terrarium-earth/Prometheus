architectury {
    val enabledPlatforms: String by rootProject
    common(enabledPlatforms.split(","))
}

loom {
    accessWidenerPath.set(file("src/main/resources/prometheus.accesswidener"))
}