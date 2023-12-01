architectury {
    neoForge()
}

val common: Configuration by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentNeoForge"].extendsFrom(this)
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    neoForge { // TODO: Not sure if this is still needed since it doesn't exist anymore
//        convertAccessWideners.set(true)
//        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
    }
}

dependencies {
    val minecraftVersion: String by project
    val neoforgeVersion: String by project

    neoForge(group = "net.neoforged", name = "neoforge", version = neoforgeVersion)

    common(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    shadowCommon(project(path = ":common", configuration = "transformProductionNeoForge")) {
        isTransitive = false
    }

    forgeRuntimeLibrary("com.teamresourceful:yabn:1.0.3")
}
