package jp.ac.it_college.std.s23021.kadai

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PokemonDetailActivity : AppCompatActivity() {

    private lateinit var tvPokemonInfo: TextView
    private lateinit var ivPokemonSprite: ImageView

    private val pokemonNameMap = mapOf(
        "bulbasaur" to "フシギダネ",
        "ivysaur" to "フシギソウ",
        "venusaur" to "フシギバナ",
        "charmander" to "ヒトカゲ",
        "charmeleon" to "リザード",
        "charizard" to "リザードン"
        // 他のポケモンも追加
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_detail)

        tvPokemonInfo = findViewById(R.id.tvPokemonInfo)
        ivPokemonSprite = findViewById(R.id.ivPokemonSprite)

        val pokemonName = intent.getStringExtra("pokemon_name")
        if (pokemonName != null) {
            getPokemonData(pokemonName)
        }
    }

    private fun getPokemonData(name: String) {
        val service = RetrofitClient.instance.create(PokeApiService::class.java)
        service.getPokemonInfo(name).enqueue(object : Callback<PokemonResponse> {
            override fun onResponse(
                call: Call<PokemonResponse>,
                response: Response<PokemonResponse>
            ) {
                if (response.isSuccessful) {
                    val pokemon = response.body()
                    pokemon?.let {
                        val japaneseName = pokemonNameMap[name.toLowerCase()] ?: "日本語名不明"
                        val info = "名前: ${japaneseName}\n" +
                                "ID: ${it.id}\n" +
                                "高さ: ${it.height}\n" +
                                "体重: ${it.weight}"
                        tvPokemonInfo.text = info
                        Glide.with(this@PokemonDetailActivity)
                            .load(it.sprites.front_default)
                            .into(ivPokemonSprite)
                    }
                } else {
                    tvPokemonInfo.text = "データの取得に失敗しました"
                }
            }

            override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                tvPokemonInfo.text = t.message
            }
        })
    }
}
