package jp.ac.it_college.std.s23021.kadai

import PokemonListItem
import PokemonResponse
import PokemonSpeciesResponse
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
    private lateinit var btnNextPage: Button
    private lateinit var btnPreviousPage: Button
    private lateinit var btnSearch: Button
    private lateinit var etSearch: EditText

    private var currentOffset = 0
    private val limit = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ivPokemonSprite = findViewById(R.id.ivPokemonSprite)
        tvPokemonInfo = findViewById(R.id.tvPokemonInfo)
        btnNextPage = findViewById(R.id.btnNextPage)
        btnPreviousPage = findViewById(R.id.btnPreviousPage)
        btnSearch = findViewById(R.id.btnSearch)
        etSearch = findViewById(R.id.etSearch)

        // RecyclerView の設定
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // 初期ページのポケモンリストを取得して表示
        fetchPokemonList()

        // 「次へ」ボタンの設定
        btnNextPage.setOnClickListener {
            currentOffset += limit
            fetchPokemonList()
        }

        // 「戻る」ボタンの設定
        btnPreviousPage.setOnClickListener {
            if (currentOffset >= limit) {
                currentOffset -= limit
                fetchPokemonList()
            }
        }

        // 検索ボタンのクリックリスナーを設定
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchPokemon(query)
            } else {
                tvPokemonInfo.text = "ポケモン名を入力してください"
            }
        }

        // 「戻る」ボタンの無効化を管理
        updateButtonStates()
    }

    private fun fetchPokemonList() {
        val service = RetrofitClient.instance.create(PokeApiService::class.java)
        service.getPokemonList(limit = limit, offset = currentOffset).enqueue(object : Callback<PokemonListResponse> {
            override fun onResponse(
                call: Call<PokemonListResponse>,
                response: Response<PokemonListResponse>
            ) {
                if (response.isSuccessful) {
                    val pokemonListResponse = response.body()
                    pokemonListResponse?.let {
                        val pokemonListItems = it.results.map { result ->
                            // ここで日本語名を取得する
                            val item = PokemonListItem(name = result.name, url = result.url)
                            service.getPokemonSpecies(result.name).enqueue(object : Callback<PokemonSpeciesResponse> {
                                override fun onResponse(
                                    call: Call<PokemonSpeciesResponse>,
                                    response: Response<PokemonSpeciesResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        val species = response.body()
                                        species?.let {
                                            val japaneseName = it.names.find { nameInfo ->
                                                nameInfo.language.name == "ja-Hrkt"
                                            }?.name
                                            item.japaneseName = japaneseName ?: result.name.capitalize()
                                        }
                                        // アダプタに通知して表示を更新
                                        binding.recyclerView.adapter?.notifyDataSetChanged()
                                    }
                                }

                                override fun onFailure(call: Call<PokemonSpeciesResponse>, t: Throwable) {
                                    t.printStackTrace()
                                }
                            })
                            item
                        }

                        binding.recyclerView.adapter = PokemonListAdapter(pokemonListItems) { pokemon ->
                            showPokemonDetails(pokemon.name)
                        }

                        // ボタンの状態を更新
                        updateButtonStates()
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

        // ポケモン基本情報の取得
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

                        // ポケモンの種別情報を取得して日本語の名前とフレーバーテキストを表示
                        fetchPokemonSpecies(it.id)

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

    private fun fetchPokemonSpecies(id: Int) {
        val service = RetrofitClient.instance.create(PokeApiService::class.java)
        service.getPokemonSpecies(id.toString()).enqueue(object : Callback<PokemonSpeciesResponse> {
            override fun onResponse(
                call: Call<PokemonSpeciesResponse>,
                response: Response<PokemonSpeciesResponse>
            ) {
                if (response.isSuccessful) {
                    val species = response.body()
                    species?.let {
                        // 日本語の名前を取得
                        val japaneseName = it.names.find { nameInfo ->
                            nameInfo.language.name == "ja-Hrkt"
                        }?.name

                        // 日本語のフレーバーテキストを取得
                        val flavorText = it.flavor_text_entries.find { entry ->
                            entry.language.name == "ja-Hrkt"
                        }?.flavor_text

                        // 画面に表示
                        val info = "${tvPokemonInfo.text}\n日本語名: $japaneseName\n説明: $flavorText"
                        tvPokemonInfo.text = info
                    }
                } else {
                    tvPokemonInfo.text = "種別データの取得に失敗しました"
                }
            }

            override fun onFailure(call: Call<PokemonSpeciesResponse>, t: Throwable) {
                tvPokemonInfo.text = t.message
            }
        })
    }

    private fun searchPokemon(name: String) {
        showPokemonDetails(name)
    }

    private fun updateButtonStates() {
        btnPreviousPage.isEnabled = currentOffset > 0
    }
}
