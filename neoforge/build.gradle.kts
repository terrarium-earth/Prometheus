architectury {
    neoForge()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

//loom {
//    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
//
//    neoForge {
//        convertAccessWideners.set(true)
//        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
//    }
//}

dependencies {
    val minecraftVersion: String by project
    val neoforgeVersion: String by project

    neoForge(group = "net.neoforged", name = "neoforge", version = neoforgeVersion)
}
