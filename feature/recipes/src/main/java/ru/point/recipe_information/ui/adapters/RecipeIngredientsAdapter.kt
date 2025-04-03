package ru.point.recipe_information.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.point.api.recipes.domain.models.IngredientData
import ru.point.recipes.databinding.ItemIngredientBinding

class RecipeIngredientsAdapter : RecyclerView.Adapter<RecipeIngredientsAdapter.IngredientViewHolder>() {

    private val items = mutableListOf<IngredientData>()

    fun submitList(newItems: List<IngredientData>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemIngredientBinding.inflate(inflater, parent, false)
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class IngredientViewHolder(
        private val binding: ItemIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: IngredientData) {
            binding.ingredientNameText.text = item.ingredientName
            binding.ingredientGramsText.text = " ${item.ingredientGrams} гр"
        }
    }
}