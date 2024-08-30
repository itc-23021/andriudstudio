data class PokemonResponse(
    val name: String,
    val id: Int,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val stats: List<PokemonStat>
)

data class PokemonStat(
    val base_stat: Int,
    val stat: Stat
)

data class Stat(
    val name: String
)

data class Sprites(
    val front_default: String
)

data class PokemonListItem(
    val name: String,
    val url: String
)

data class PokemonSpeciesResponse(
    val names: List<NameInfo>,
    val flavor_text_entries: List<FlavorTextEntry>
)

data class NameInfo(
    val name: String,
    val language: LanguageInfo
)

data class LanguageInfo(
    val name: String
)

data class FlavorTextEntry(
    val flavor_text: String,
    val language: LanguageInfo
)
