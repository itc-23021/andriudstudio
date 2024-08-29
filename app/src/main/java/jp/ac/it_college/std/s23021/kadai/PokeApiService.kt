package jp.ac.it_college.std.s23021.kadai

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon/{name}")
    fun getPokemonInfo(@Path("name") name: String): Call<PokemonResponse>

    @GET("pokemon")
    fun getPokemonList(@Query("limit") limit: Int, @Query("offset") offset: Int): Call<PokemonListResponse>
}
