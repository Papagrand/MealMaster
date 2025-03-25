package ru.point.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.point.core.ui.BaseFragment
import ru.point.profile.databinding.FragmentProfileBinding

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)



}