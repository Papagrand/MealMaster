package ru.point.fasting.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import ru.point.core.ui.BaseFragment
import ru.point.fasting.R
import ru.point.fasting.databinding.FragmentFastingBinding
import ru.point.fasting.ui.adapters.FastingModePagerAdapter
import ru.point.fasting.ui.adapters.MyItem

class FastingFragment : BaseFragment<FragmentFastingBinding>() {


    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFastingBinding = FragmentFastingBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.fastingModesViewPager

        val modItems = listOf(
            MyItem(
                R.drawable.icon16_8,
                getString(R.string.tracker_name_16_8),
                getString(R.string.tracker16_8_description)
            ),
            MyItem(
                R.drawable.icon14_10,
                getString(R.string.tracker_name_14_10),
                getString(R.string.tracker14_10_description)
            ),
            MyItem(
                R.drawable.icon18_6,
                getString(R.string.tracker_name_18_6),
                getString(R.string.tracker18_6_description)
            ),
            MyItem(
                R.drawable.icon20_4,
                getString(R.string.tracker_name_20_4),
                getString(R.string.tracker20_4_description)
            ),
            MyItem(
                R.drawable.icon3_1,
                getString(R.string.tracker_name_3_1),
                getString(R.string.tracker3_1_description)
            ),
            MyItem(
                R.drawable.icon1_1,
                getString(R.string.tracker_name_1_1),
                getString(R.string.tracker1_1_description)
            )

        )

        val fastingModePagerAdapter = FastingModePagerAdapter(modItems)
        viewPager.adapter = fastingModePagerAdapter
        PagerSnapHelper().attachToRecyclerView(binding.fastingModesViewPager)

    }
}