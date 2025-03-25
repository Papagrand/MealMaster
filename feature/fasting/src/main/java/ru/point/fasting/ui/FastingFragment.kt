package ru.point.fasting.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.point.core.ui.BaseFragment
import ru.point.fasting.R
import ru.point.fasting.databinding.FragmentFastingBinding

class FastingFragment : BaseFragment<FragmentFastingBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFastingBinding = FragmentFastingBinding.inflate(inflater, container, false)



}