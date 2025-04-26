package ru.point.recipes.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.point.api.recipes.domain.models.RecipeItemModel
import ru.point.recipes.databinding.ItemSearchedRecipeBinding

class SearchedRecipesAdapter(
    private val onItemClick: (recipeId: String) -> Unit
) : ListAdapter<RecipeItemModel, SearchedRecipesAdapter.SearchedRecipeViewHolder>(
    DIFF_CALLBACK
) {

    fun clear() {
        submitList(emptyList())
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecipeItemModel>() {
            override fun areItemsTheSame(
                oldItem: RecipeItemModel,
                newItem: RecipeItemModel
            ): Boolean {
                return oldItem.recipeId == newItem.recipeId
            }

            override fun areContentsTheSame(
                oldItem: RecipeItemModel,
                newItem: RecipeItemModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedRecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchedRecipeBinding.inflate(inflater, parent, false)
        return SearchedRecipeViewHolder(binding)
    }


    override fun onBindViewHolder(holder: SearchedRecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchedRecipeViewHolder(
        private val binding: ItemSearchedRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemModel: RecipeItemModel) {
            binding.recipeNameTextView.text = itemModel.recipeName
            binding.recipeCaloriesValue.text = "${itemModel.recipeCalories.toInt()} ккал"
            binding.recipeProteinValueTextView.text = "${itemModel.recipeProtein} г"
            binding.recipeFatValueTextView.text = "${itemModel.recipeFat} г"
            binding.recipeCarbohydratesValueTextView.text = "${itemModel.recipeCarbohydrate} г"
            binding.veganImage.isVisible = itemModel.recipeIsVegan
            binding.cookingTimeValue.text = "${itemModel.recipeCookingTime} мин"
            binding.difficultyValue.text = "${itemModel.recipeDifficulty} уровень"

            binding.root.setOnClickListener {
                onItemClick(itemModel.recipeId)
            }

            val recipeItemPictureString = itemModel.recipeBackdrop

            if (recipeItemPictureString.startsWith("data:image", ignoreCase = true)) {
                val base64Part = recipeItemPictureString.substringAfter("base64,")
                val decodedBytes =
                    android.util.Base64.decode(base64Part, android.util.Base64.DEFAULT)
                binding.overlayImage.load(decodedBytes) {
                    crossfade(true)
                    allowHardware(false)
                }
            } else {
                binding.overlayImage.load(recipeItemPictureString) {
                    crossfade(true)
                    allowHardware(false)
                }
            }
        }
    }
}