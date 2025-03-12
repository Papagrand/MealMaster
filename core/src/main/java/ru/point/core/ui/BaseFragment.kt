package ru.point.core.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import ru.point.core.navigation.Navigator
import ru.point.core.navigation.NavigatorProvider

abstract class BaseFragment<T : ViewBinding> : Fragment() {

    private var _binding: T? = null
    protected val binding get() = requireNotNull(_binding)

    protected lateinit var navigator: Navigator

    abstract fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createView(inflater, container).also {
        _binding = it
        navigator = (requireActivity() as NavigatorProvider).getNavigator(findNavController())
    }.root

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}