package ru.point.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.point.core.ui.BaseFragment
import ru.point.home.databinding.FragmentHomeProgressBinding

class HomeProgressFragment : BaseFragment<FragmentHomeProgressBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeProgressBinding =
        FragmentHomeProgressBinding.inflate(inflater, container, false)


}