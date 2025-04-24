package ru.point.meal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.point.api.meal.data.ItemInMealDataModel
import ru.point.meal.databinding.ItemRecyclerViewMealProductsBinding
import ru.point.meal.ui.adapters.MealPickedAdapter.MealProductViewHolder

data class MealProductData(
    val itemId: String,
    val productName: String,
    val servingSize: String,
    val servingCcal: String
)

class MealPickedAdapter(
    private val onUpdateClick: (ItemInMealDataModel) -> Unit
) : ListAdapter<MealProductData, MealProductViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MealProductData>() {
            override fun areItemsTheSame(
                oldItem: MealProductData,
                newItem: MealProductData
            ): Boolean = oldItem.itemId == newItem.itemId

            override fun areContentsTheSame(
                oldItem: MealProductData,
                newItem: MealProductData
            ): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MealProductViewHolder {
        val binding = ItemRecyclerViewMealProductsBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MealProductViewHolder(binding, onUpdateClick)
    }

    override fun onBindViewHolder(holder: MealProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MealProductViewHolder(
        private val binding: ItemRecyclerViewMealProductsBinding,
        private val onUpdateClick: (ItemInMealDataModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mealItem: MealProductData) {
            binding.productName.text = mealItem.productName
            binding.productServingSize.text = mealItem.servingSize
            binding.productServingCcal.text = mealItem.servingCcal

            // парсим из строк нужные Double
            val parsedServing = mealItem.servingSize
                .substringBefore(" ")
                .toDoubleOrNull() ?: 0.0
            val parsedCalories = mealItem.servingCcal
                .substringBefore(" ")
                .toDoubleOrNull() ?: 0.0

            val item = ItemInMealDataModel(
                itemId = mealItem.itemId,
                currentServingSize = parsedServing,
                currentCalories = parsedCalories
            )

            binding.updateMealProductInformation.setOnClickListener {
                onUpdateClick(item)
            }
        }
    }
}
