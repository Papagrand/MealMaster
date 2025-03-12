package ru.point.auth.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.point.auth.databinding.FragmentLoginBinding
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    override fun createView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLoginBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

        binding.buttonRegistrationInAuth.setOnClickListener {
            navigator.fromLoginFragmentToRegisterFragment()
        }

        binding.buttonAuth.setOnClickListener{
            navigator.fromLoginFragmentToCreateProfileFirstStepFragment()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        (requireActivity() as BottomBarManager).hide()
    }
}