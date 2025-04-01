package ru.point.mealmaster

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import ru.point.core.navigation.BottomBarManager
import ru.point.core.navigation.Navigator
import ru.point.core.navigation.NavigatorProvider
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.mealmaster.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), BottomBarManager, NavigatorProvider {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
}