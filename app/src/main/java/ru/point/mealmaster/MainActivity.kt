package ru.point.mealmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import ru.point.api.login.data.createLoginService
import ru.point.core.ContentLoadListener
import ru.point.core.navigation.BottomBarManager
import ru.point.core.navigation.Navigator
import ru.point.core.navigation.NavigatorProvider
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.core.secure_prefs.toNightMode
import ru.point.core_data.SessionRepository
import ru.point.mealmaster.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), BottomBarManager, NavigatorProvider, ContentLoadListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SecurePrefs.init(this)

        val mode = SecurePrefs.getTheme().toNightMode()
        AppCompatDelegate.setDefaultNightMode(mode)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val splashScreen = installSplashScreen()
        setContentView(binding.root)

        val repo = SessionRepository(application)
        val loginService = createLoginService()
        val factory = MainViewModelFactory(this, repo, loginService)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading || !viewModel.contentLoaded.value
        }

        viewModel.resolve { destination ->
            val navController =
                (supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                    .navController

            val graph = navController.navInflater.inflate(R.navigation.nav_graph)
            graph.setStartDestination(
                when (destination) {
                    MainViewModel.Destination.LOGIN -> R.id.loginFragment
                    MainViewModel.Destination.ONBOARD -> R.id.onboardingFragment
                    MainViewModel.Destination.MAIN -> R.id.homeProgressFragment
                }
            )
            navController.graph = graph

            binding.navHostFragment.isVisible = true
            binding.bottomNavigation.isVisible = true
        }


        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.navHostFragment) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right
            )
            WindowInsetsCompat.CONSUMED
        }

    }

    override fun onStart() {
        super.onStart()
        setupWithNavController(
            binding.bottomNavigation,
            findNavController(this, R.id.nav_host_fragment)
        )
    }

    override fun hide() {
        binding.bottomNavigation.isVisible = false
    }

    override fun show() {
        binding.bottomNavigation.isVisible = true
    }

    override fun getNavigator(navController: NavController): Navigator {
        return NavigatorImpl(navController)
    }

    override fun onContentLoaded() {
        viewModel.markContentLoaded()
    }
}