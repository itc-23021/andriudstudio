package jp.ac.it_college.std.s23021.kadai

data class PokemonResponse(
    val name: String,
    val id: Int,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val results: Any
)


data class Sprites(
    val front_default: String
)
data class PokemonListItem(
    val name: String,
    val url: String
)
data class PokemonSpeciesResponse(
    val names: List<NameInfo>
)

data class NameInfo(
    val name: String,
    val language: LanguageInfo
)

data class LanguageInfo(
    val name: String
)

