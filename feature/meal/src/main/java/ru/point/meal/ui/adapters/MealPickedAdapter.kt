package ru.point.meal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.point.api.meal.domain.models.FoodItemModel
import ru.point.meal.databinding.ItemRecyclerViewMealProductsBinding

class MealPickedAdapter(
) : RecyclerView.Adapter<MealPickedAdapter.MealProductViewHolder>() {

    private val items = mutableListOf<MealProductData>()

    fun submitList(newItems: List<FoodItemModel>) {
        items.clear()

        newItems.forEach { itemData ->
            items.add(
                MealProductData(
                    productName = itemData.itemName,
                    servingSize = "${itemData.itemServingSize.toInt()} гр.",
                    servingCcal = "${itemData.itemCalories.toInt()} ккал"
                )
            )

        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecyclerViewMealProductsBinding.inflate(inflater, parent, false)
        return MealProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealProductViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class MealProductViewHolder(
        private val binding: ItemRecyclerViewMealProductsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MealProductData) {
            binding.productName.text = item.productName
            binding.productServingSize.text = item.servingSize
            binding.productServingCcal.text = item.servingCcal
        }
    }
}

data class MealProductData(
    val productName: String,
    val servingSize: String,
    val servingCcal: String
)