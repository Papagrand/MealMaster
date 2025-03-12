package ru.point.auth.ui.profile_create.first_step

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.point.auth.R
import ru.point.auth.databinding.FragmentCreateProfileFirstStepBinding
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment

class CreateProfileFirstStepFragment : BaseFragment<FragmentCreateProfileFirstStepBinding>() {

    override fun createView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateProfileFirstStepBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

        binding.goToStepTwo.setOnClickListener {
            navigator.fromCreateProfileFirstStepFragmentToCreateProfileSecondStepFragment()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        (requireActivity() as BottomBarManager).hide()
    }
}