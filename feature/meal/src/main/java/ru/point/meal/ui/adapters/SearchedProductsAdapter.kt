package ru.point.meal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.point.api.meal.domain.models.ProductItemModel
import ru.point.meal.databinding.ItemSearchedProductBinding

class SearchedProductsAdapter(
    private val onItemClick: (ProductItemModel) -> Unit  // если нужно обрабатывать клики
) : ListAdapter<ProductItemModel, SearchedProductsAdapter.SearchedProductViewHolder>(
    DIFF_CALLBACK
) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProductItemModel>() {
            override fun areItemsTheSame(
                oldItem: ProductItemModel,
                newItem: ProductItemModel
            ): Boolean {
                return oldItem.productId == newItem.productId
            }

            override fun areContentsTheSame(
                oldItem: ProductItemModel,
                newItem: ProductItemModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchedProductBinding.inflate(inflater, parent, false)
        return SearchedProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchedProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchedProductViewHolder(
        private val binding: ItemSearchedProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductItemModel) {
            binding.productNameTextView.text = item.productName
            binding.productServingCcalValue.text = "${item.productCalories.toInt()} ккал"
            binding.productServingSizeValue.text = "${item.productServingSizeDefault.toInt()} г"
            binding.productProteinValueTextView.text = "${item.productProtein} г"
            binding.productFatValueTextView.text = "${item.productFat} г"
            binding.productCarbohydratesValueTextView.text = "${item.productCarbohydrates} г"
            binding.veganImage.isVisible = item.productIsVegan

            val productItemPictureString = item.productBackdrop

            if (productItemPictureString.startsWith("data:image", ignoreCase = true)) {
                val base64Part = productItemPictureString.substringAfter("base64,")
                val decodedBytes = android.util.Base64.decode(base64Part, android.util.Base64.DEFAULT)
                binding.overlayImage.load(decodedBytes) {
                    crossfade(true)
                    allowHardware(false)
                }
            } else {
                binding.overlayImage.load(productItemPictureString) {
                    crossfade(true)
                    allowHardware(false)
                }
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
