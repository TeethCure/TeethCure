package com.teethcure.demo

data class CharacterInfo(
    val id: String,
    val name: String,
    val assetPath: String,
    val mapX: Float,
    val mapY: Float,
)

object GameCatalog {
    const val WORLD_MAP_ASSET = "world_map/teethkeeth_kingdom.png"
    const val STROKES_PER_ZONE = 50
    const val SESSION_DURATION_SEC = 120

    val characters = listOf(
        CharacterInfo("aqua", "Aqua", "characters/aqua.png", 0.08f, 0.54f),
        CharacterInfo("berryteeth", "Berryteeth", "characters/berryteeth.png", 0.09f, 0.88f),
        CharacterInfo("bubbleeth", "Bubbleeth", "characters/bubbleeth.png", 0.05f, 0.33f),
        CharacterInfo("cocoteeth", "Cocoteeth", "characters/cocoteeth.png", 0.68f, 0.88f),
        CharacterInfo("crystalteeth", "Crystalteeth", "characters/crystalteeth.png", 0.47f, 0.88f),
        CharacterInfo("dewyteeth", "Dewyteeth", "characters/dewyteeth.png", 0.76f, 0.11f),
        CharacterInfo("flossyeeth", "Flossyeeth", "characters/flossyeeth.png", 0.93f, 0.11f),
        CharacterInfo("glimmereeth", "Glimmereeth", "characters/glimmereeth.png", 0.50f, 0.20f),
        CharacterInfo("gloweeth", "Gloweeth", "characters/gloweeth.png", 0.88f, 0.55f),
        CharacterInfo("lullyeeth", "Lullyeeth", "characters/lullyeeth.png", 0.47f, 0.88f),
        CharacterInfo("melodyeeth", "Melodyeeth", "characters/melodyeeth.png", 0.36f, 0.88f),
        CharacterInfo("minteeth", "Minteeth", "characters/minteeth.png", 0.20f, 0.18f),
        CharacterInfo("peachyeeth", "Peachyeeth", "characters/peachyeeth.png", 0.92f, 0.32f),
        CharacterInfo("sparkeeth", "Sparkeeth", "characters/sparkeeth.png", 0.88f, 0.55f),
        CharacterInfo("sugarteeth", "Sugarteeth", "characters/sugarteeth.png", 0.20f, 0.18f),
        CharacterInfo("sunnyteeth", "Sunnyteeth", "characters/sunnyteeth.png", 0.86f, 0.54f),
        CharacterInfo("taffyteeth", "Taffyteeth", "characters/taffyteeth.png", 0.94f, 0.87f),
        CharacterInfo("viviteeth", "Viviteeth", "characters/viviteeth.png", 0.37f, 0.54f),
        CharacterInfo("zappyeeth", "Zappyeeth", "characters/zappyeeth.png", 0.20f, 0.88f),
    )
}
