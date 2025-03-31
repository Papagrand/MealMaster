package ru.point.profile.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.point.core.ui.BaseFragment
import ru.point.profile.R
import ru.point.profile.databinding.FragmentProfileBinding

class UpdateProfileInformationFragment : BaseFragment<FragmentProfileBinding>() {

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)

}