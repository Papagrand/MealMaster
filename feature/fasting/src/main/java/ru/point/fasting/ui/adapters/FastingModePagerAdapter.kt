package ru.point.fasting.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.point.fasting.databinding.ItemFastingModeBinding

class FastingModePagerAdapter (
    private val items: List<MyItem>,
    private val onItemClick: (String) -> Unit

) : RecyclerView.Adapter<FastingModePagerAdapter.PagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding = ItemFastingModeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class PagerViewHolder(
        private val binding: ItemFastingModeBinding,
        private val onItemClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyItem) {
            binding.imageItem.setImageResource(item.imageRes)
            binding.trackerName.text = item.title
            binding.trackerDescription.text = item.description

            binding.root.setOnClickListener {
                onItemClick(item.title)
            }
        }
    }
}

data class MyItem(
    val imageRes: Int,
    val title: String,
    val description: String
)