package jp.ac.it_college.std.s23021.kadai

import PokemonListItem
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.ac.it_college.std.s23021.kadai.databinding.ItemPokemonBinding

class PokemonListAdapter(
    private val pokemonList: List<PokemonListItem>,
    private val onItemClick: (PokemonListItem) -> Unit
) : RecyclerView.Adapter<PokemonListAdapter.PokemonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemonList[position]
        holder.bind(pokemon)
        holder.itemView.setOnClickListener { onItemClick(pokemon) }
    }

    override fun getItemCount(): Int = pokemonList.size

    class PokemonViewHolder(private val binding: ItemPokemonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pokemon: PokemonListItem) {
            binding.pokemonName.text = pokemon.japaneseName
        }
    }
}
