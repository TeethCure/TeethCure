package com.teethcure.demo

data class CharacterInfo(
    val id: String,
    val name: String,
    val assetPath: String,
    val regionId: String,
    val mapX: Float,
    val mapY: Float,
)

data class RegionInfo(
    val id: String,
    val name: String,
    val assetPath: String,
    val characterIds: List<String>,
)

object GameCatalog {
    const val WORLD_MAP_ASSET = "world_map/World1_FoamWaterfallForest.png"
    const val STROKES_PER_ZONE = 50
    const val SESSION_DURATION_SEC = 120

    val regions = listOf(
        RegionInfo(
            id = "foam-waterfall-forest",
            name = "Foam Waterfall Forest",
            assetPath = "world_map/World1_FoamWaterfallForest.png",
            characterIds = listOf("aqua", "berry", "bubble", "guard", "sugar"),
        ),
        RegionInfo(
            id = "sweet-refreshing-fruit-garden",
            name = "Sweet Refreshing Fruit Garden",
            assetPath = "world_map/World2_SweetRefreshingFruitGarden.png",
            characterIds = listOf("brush", "peach", "cherry", "coco", "taffy"),
        ),
        RegionInfo(
            id = "sparkling-crystal-cave",
            name = "Sparkling Crystal Cave",
            assetPath = "world_map/World3_SparklingCrystalCave.png",
            characterIds = listOf("crystal", "dewy", "glimmer", "glow", "shining"),
        ),
        RegionInfo(
            id = "clouds-of-slight-night",
            name = "Clouds Of Slight Night",
            assetPath = "world_map/World4_CloudsOfSlightNight.png",
            characterIds = listOf("lully", "melody", "milky", "min", "plossy"),
        ),
        RegionInfo(
            id = "high-tech-dental-city",
            name = "High Tech Dental City",
            assetPath = "world_map/World5_HighTechDentalCity.png",
            characterIds = listOf("sal", "soda", "sparky", "sunny", "vivi"),
        ),
    )

    val characters = listOf(
        CharacterInfo("aqua", "AquaTeeth", "characters/AquaTeeth.png", "foam-waterfall-forest", 0.08f, 0.54f),
        CharacterInfo("berry", "BerryTeeth", "characters/BerryTeeth.png", "foam-waterfall-forest", 0.09f, 0.88f),
        CharacterInfo("bubble", "BubbleTeeth", "characters/BubbleTeeth.png", "foam-waterfall-forest", 0.05f, 0.33f),
        CharacterInfo("guard", "GuardTeeth", "characters/GuardTeeth.png", "foam-waterfall-forest", 0.16f, 0.22f),
        CharacterInfo("sugar", "SugarTeeth", "characters/SugarTeeth.png", "foam-waterfall-forest", 0.20f, 0.18f),

        CharacterInfo("brush", "BrushTeeth", "characters/BrushTeeth.png", "sweet-refreshing-fruit-garden", 0.28f, 0.42f),
        CharacterInfo("peach", "PeachTeeth", "characters/PeachTeeth.png", "sweet-refreshing-fruit-garden", 0.92f, 0.32f),
        CharacterInfo("cherry", "CherryTeeth", "characters/CherryTeeth.png", "sweet-refreshing-fruit-garden", 0.84f, 0.24f),
        CharacterInfo("coco", "CocoTeeth", "characters/CocoTeeth.png", "sweet-refreshing-fruit-garden", 0.68f, 0.88f),
        CharacterInfo("taffy", "TaffyTeeth", "characters/TaffyTeeth.png", "sweet-refreshing-fruit-garden", 0.94f, 0.87f),

        CharacterInfo("crystal", "CrystalTeeth", "characters/CrystalTeeth.png", "sparkling-crystal-cave", 0.47f, 0.88f),
        CharacterInfo("dewy", "DewyTeeth", "characters/DewyTeeth.png", "sparkling-crystal-cave", 0.76f, 0.11f),
        CharacterInfo("glimmer", "GlimmerTeeth", "characters/GlimmerTeeth.png", "sparkling-crystal-cave", 0.50f, 0.20f),
        CharacterInfo("glow", "GlowTeeth", "characters/GlowTeeth.png", "sparkling-crystal-cave", 0.88f, 0.55f),
        CharacterInfo("shining", "ShiningTeeth", "characters/ShiningTeeth.png", "sparkling-crystal-cave", 0.82f, 0.18f),

        CharacterInfo("lully", "LullyTeeth", "characters/LullyTeeth.png", "clouds-of-slight-night", 0.47f, 0.88f),
        CharacterInfo("melody", "Melodyis", "characters/Melodyis.png", "clouds-of-slight-night", 0.36f, 0.88f),
        CharacterInfo("milky", "MilkyTeeth", "characters/MilkyTeeth.png", "clouds-of-slight-night", 0.30f, 0.76f),
        CharacterInfo("min", "MinTeeth", "characters/MinTeeth.png", "clouds-of-slight-night", 0.20f, 0.18f),
        CharacterInfo("plossy", "Plossyis", "characters/Plossyis.png", "clouds-of-slight-night", 0.58f, 0.78f),

        CharacterInfo("sal", "SalTeeth", "characters/SalTeeth.png", "high-tech-dental-city", 0.18f, 0.72f),
        CharacterInfo("soda", "SodaTeeth", "characters/SodaTeeth.png", "high-tech-dental-city", 0.74f, 0.42f),
        CharacterInfo("sparky", "Sparkyis", "characters/Sparkyis.png", "high-tech-dental-city", 0.88f, 0.55f),
        CharacterInfo("sunny", "SunnyTeeth", "characters/SunnyTeeth.png", "high-tech-dental-city", 0.86f, 0.54f),
        CharacterInfo("vivi", "ViviTeeth", "characters/ViviTeeth.png", "high-tech-dental-city", 0.37f, 0.54f),
    )
}
