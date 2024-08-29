package jp.ac.it_college.std.s23021.kadai

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import jp.ac.it_college.std.s23021.kadai.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var ivPokemonSprite: ImageView
    private lateinit var tvPokemonInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ivPokemonSprite = findViewById(R.id.ivPokemonSprite)
        tvPokemonInfo = findViewById(R.id.tvPokemonInfo)

        // RecyclerView の設定
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // ポケモンリストを取得して表示
        fetchPokemonList()
    }

    private fun fetchPokemonList() {
        val service = RetrofitClient.instance.create(PokeApiService::class.java)
        service.getPokemonList(limit = 10, offset = 0).enqueue(object : Callback<PokemonListResponse> {
            override fun onResponse(
                call: Call<PokemonListResponse>,
                response: Response<PokemonListResponse>
            ) {
                if (response.isSuccessful) {
                    val pokemonListResponse = response.body()
                    pokemonListResponse?.let {
                        val pokemonListItems = it.results.map { result ->
                            PokemonListItem(name = result.name, url = result.url)
                        }
                        binding.recyclerView.adapter = PokemonListAdapter(pokemonListItems) { pokemon ->
                            showPokemonDetails(pokemon.name)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PokemonListResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun showPokemonDetails(name: String) {
        val service = RetrofitClient.instance.create(PokeApiService::class.java)
        service.getPokemonInfo(name).enqueue(object : Callback<PokemonResponse> {
            override fun onResponse(
                call: Call<PokemonResponse>,
                response: Response<PokemonResponse>
            ) {
                if (response.isSuccessful) {
                    val pokemon = response.body()
                    pokemon?.let {
                        val info = "名前: ${it.name.capitalize()}\n" +
                                "ID: ${it.id}\n" +
                                "高さ: ${it.height}\n" +
                                "体重: ${it.weight}"
                        tvPokemonInfo.text = info
                        Glide.with(this@MainActivity)
                            .load(it.sprites.front_default)
                            .into(ivPokemonSprite)

                        // ポケモンの詳細セクションを表示
                        binding.pokemonDetailSection.visibility = android.view.View.VISIBLE
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
