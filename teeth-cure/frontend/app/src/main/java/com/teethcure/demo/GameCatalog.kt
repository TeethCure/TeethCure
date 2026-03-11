package com.teethcure.demo

data class CharacterInfo(
    val id: String,
    val name: String,
    val assetPath: String,
    val regionId: String,
    val roleSummary: String,
)

data class RegionInfo(
    val id: String,
    val name: String,
    val assetPath: String,
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
        ),
        RegionInfo(
            id = "sweet-refreshing-fruit-garden",
            name = "Sweet Refreshing Fruit Garden",
            assetPath = "world_map/World2_SweetRefreshingFruitGarden.png",
        ),
        RegionInfo(
            id = "sparkling-crystal-cave",
            name = "Sparkling Crystal Cave",
            assetPath = "world_map/World3_SparklingCrystalCave.png",
        ),
        RegionInfo(
            id = "clouds-of-slight-night",
            name = "Clouds Of Slight Night",
            assetPath = "world_map/World4_CloudsOfSlightNight.png",
        ),
        RegionInfo(
            id = "high-tech-dental-city",
            name = "High Tech Dental City",
            assetPath = "world_map/World5_HighTechDentalCity.png",
        ),
    )

    val characters = listOf(
        CharacterInfo("aqua", "AquaTeeth", "characters/AquaTeeth.png", "foam-waterfall-forest", "A rinsing fairy who freshens the mouth with cool water."),
        CharacterInfo("bubbleeth", "BubbleTeeth", "characters/BubbleTeeth.png", "foam-waterfall-forest", "A fizzy bubble fairy that pops away debris."),
        CharacterInfo("minteeth", "MinTeeth", "characters/MinTeeth.png", "foam-waterfall-forest", "A mint herb fairy that leaves a crisp, clean feeling."),
        CharacterInfo("soda", "SodaTeeth", "characters/SodaTeeth.png", "foam-waterfall-forest", "A powdery whitening fairy who brightens teeth."),
        CharacterInfo("sparkeeth", "Sparkyis", "characters/Sparkyis.png", "foam-waterfall-forest", "A bubbly foam mage who whips up cleansing fizz."),
        CharacterInfo("sunnyteeth", "SunnyTeeth", "characters/SunnyTeeth.png", "foam-waterfall-forest", "A morning sunshine fairy for refreshing first-brush energy."),

        CharacterInfo("berryteeth", "BerryTeeth", "characters/BerryTeeth.png", "sweet-refreshing-fruit-garden", "A vitamin-rich berry fairy with lively fruit magic."),
        CharacterInfo("cherryteeth", "CherryTeeth", "characters/CherryTeeth.png", "sweet-refreshing-fruit-garden", "A bright cherry fairy that brings playful freshness."),
        CharacterInfo("peachyeeth", "PeachTeeth", "characters/PeachTeeth.png", "sweet-refreshing-fruit-garden", "A peach-scented fairy who makes brushing feel sweet and easy."),
        CharacterInfo("sugarteeth", "SugarTeeth", "characters/SugarTeeth.png", "sweet-refreshing-fruit-garden", "A quick-response fairy who appears right after sweets."),
        CharacterInfo("taffyteeth", "TaffyTeeth", "characters/TaffyTeeth.png", "sweet-refreshing-fruit-garden", "A sticky-candy cleanup fairy that peels residue away."),

        CharacterInfo("crystalteeth", "CrystalTeeth", "characters/CrystalTeeth.png", "sparkling-crystal-cave", "A crystal coating fairy with strong, jewel-clear protection."),
        CharacterInfo("glimmereeth", "GlimmerTeeth", "characters/GlimmerTeeth.png", "sparkling-crystal-cave", "A stardust fairy who makes teeth shine bright."),
        CharacterInfo("coolteeth", "SalTeeth", "characters/SalTeeth.png", "sparkling-crystal-cave", "A sturdy salt guardian who helps sanitize the mouth."),
        CharacterInfo("shiningteeth", "ShiningTeeth", "characters/ShiningTeeth.png", "sparkling-crystal-cave", "A gloss-check fairy who watches tooth sparkle closely."),

        CharacterInfo("dewyteeth", "DewyTeeth", "characters/DewyTeeth.png", "clouds-of-slight-night", "A moisture fairy who keeps the mouth from drying out."),
        CharacterInfo("guardteeth", "GuardTeeth", "characters/GuardTeeth.png", "clouds-of-slight-night", "A protector fairy who guards teeth during rest and activity."),
        CharacterInfo("lullyeeth", "LullyTeeth", "characters/LullyTeeth.png", "clouds-of-slight-night", "A bedtime fairy who helps with calm night brushing."),
        CharacterInfo("melodyeeth", "Melodyis", "characters/Melodyis.png", "clouds-of-slight-night", "A singing timer fairy who keeps the three-minute rhythm."),
        CharacterInfo("milkyteeth", "MilkyTeeth", "characters/MilkyTeeth.png", "clouds-of-slight-night", "A calcium fairy carrying a bottle of strength."),

        CharacterInfo("brushteeth", "BrushTeeth", "characters/BrushTeeth.png", "high-tech-dental-city", "The leader fairy who carries a crown-tipped brush staff."),
        CharacterInfo("cocoteeth", "CocoTeeth", "characters/CocoTeeth.png", "high-tech-dental-city", "A tough-shell defender fairy for strong enamel care."),
        CharacterInfo("gloweeth", "GlowTeeth", "characters/GlowTeeth.png", "high-tech-dental-city", "A light fairy that spots hidden plaque."),
        CharacterInfo("flossyeeth", "Plossyis", "characters/Plossyis.png", "high-tech-dental-city", "A flossing fairy that cleans tight gaps precisely."),
        CharacterInfo("viviteeth", "ViviTeeth", "characters/ViviTeeth.png", "high-tech-dental-city", "A gum-care fairy focused on healthy, lively tissue."),
    )

    fun charactersInRegion(regionId: String): List<CharacterInfo> =
        characters.filter { it.regionId == regionId }
}
