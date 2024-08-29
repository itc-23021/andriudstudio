package jp.ac.it_college.std.s23021.kadai

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import jp.ac.it_college.std.s23021.kadai.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
                            val intent = Intent(this@MainActivity, PokemonDetailActivity::class.java).apply {
                                putExtra("pokemon_name", pokemon.name)
                            }
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PokemonListResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}
