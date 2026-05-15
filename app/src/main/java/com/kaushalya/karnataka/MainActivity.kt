package com.kaushalya.karnataka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.kaushalya.karnataka.ui.navigation.Screen
import com.kaushalya.karnataka.ui.navigation.bottomNavItems
import com.kaushalya.karnataka.ui.screens.aibuilder.AiBuilderScreen
import com.kaushalya.karnataka.ui.screens.dashboard.DashboardScreen
import com.kaushalya.karnataka.ui.screens.home.HomeScreen
import com.kaushalya.karnataka.ui.screens.profile.ProfileScreen
import com.kaushalya.karnataka.ui.theme.KaushalyaKarnatakaTheme
import com.kaushalya.karnataka.ui.theme.ElectricBlue
import com.kaushalya.karnataka.ui.theme.PureWhite
import com.kaushalya.karnataka.ui.theme.RoyalBlue
import com.kaushalya.karnataka.ui.theme.WarmGold
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { KaushalyaKarnatakaTheme { MainNavHost() } }
    }
}

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val backStack     by navController.currentBackStackEntryAsState()
    val currentRoute  = backStack?.destination?.route

    val showBottomBar = currentRoute?.startsWith("profile/") == false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = RoyalBlue) {
                    bottomNavItems.forEachIndexed { index, item ->
                        val selected = when (index) {
                            0, 1 -> currentRoute == Screen.Home.route
                            2    -> currentRoute == Screen.AiBuilder.route
                            3    -> currentRoute == Screen.Dashboard.route
                            else -> false
                        }
                        NavigationBarItem(
                            selected = selected,
                            onClick  = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon   = { Icon(item.icon, item.label) },
                            label  = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = WarmGold,
                                selectedTextColor   = WarmGold,
                                unselectedIconColor = PureWhite.copy(alpha = 0.7f),
                                unselectedTextColor = PureWhite.copy(alpha = 0.7f),
                                indicatorColor      = ElectricBlue.copy(alpha = 0.3f),
                            ),
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onWorkerClick = { workerId ->
                        navController.navigate("profile/$workerId")
                    },
                    onHireMe = { workerId ->
                        navController.navigate("profile/$workerId")
                    },
                )
            }

            composable("profile/{workerId}") { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId") ?: ""
                ProfileScreen(
                    workerId = workerId,
                    onBack   = { navController.popBackStack() },
                )
            }

            composable(Screen.AiBuilder.route) { AiBuilderScreen() }
            composable(Screen.Dashboard.route) { DashboardScreen() }
        }
    }
}
