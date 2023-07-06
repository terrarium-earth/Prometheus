architectury {
    forge()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    forge {
        mixinConfig("prometheus-common.mixins.json")

        convertAccessWideners.set(true)

        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
    }
}

dependencies {
    val minecraftVersion: String by project
    val forgeVersion: String by project

    forge(group = "net.minecraftforge", name = "forge", version = "$minecraftVersion-$forgeVersion")
}
