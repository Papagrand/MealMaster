package ru.point.recipe_information.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.point.api.recipes.domain.models.RecipeStepData
import ru.point.api.recipes.domain.models.StepImageData
import ru.point.recipes.databinding.ItemRecipeStepBinding

data class StepItem(
    val stepNumber: Int,
    val stepDescription: String,
    val stepImageUrl: String?
)

class RecipeStepsAdapter : RecyclerView.Adapter<RecipeStepsAdapter.StepViewHolder>() {

    private val items = mutableListOf<StepItem>()

    fun submitLists(steps: List<RecipeStepData>, images: List<StepImageData>) {
        items.clear()

        val imagesMap = images.associateBy { it.stepNumber }

        steps.forEach { stepData ->
            val matchedImage = imagesMap[stepData.stepNumber]
            val imageUrl = matchedImage?.stepImage
            items.add(
                StepItem(
                    stepNumber = stepData.stepNumber,
                    stepDescription = stepData.stepDescription,
                    stepImageUrl = imageUrl
                )
            )
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecipeStepBinding.inflate(inflater, parent, false)
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class StepViewHolder(
        private val binding: ItemRecipeStepBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StepItem) {
            binding.stepNumberText.text = "Шаг ${item.stepNumber}"
            binding.stepDescriptionText.text = item.stepDescription

            val imageString = item.stepImageUrl
            if (imageString != null) {
                binding.stepImageView.isVisible = true
                if (imageString.startsWith("data:image", ignoreCase = true)) {
                    val base64Part = imageString.substringAfter("base64,")
                    val decodedBytes = android.util.Base64.decode(base64Part, android.util.Base64.DEFAULT)
                    binding.stepImageView.load(decodedBytes) {
                        crossfade(true)
                    }
                } else {
                    binding.stepImageView.load(imageString) {
                        crossfade(true)
                    }
                }
            } else {
                binding.stepImageView.setImageDrawable(null)
            }
        }
    }
}

