package com.moksh.imposterai.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.moksh.imposterai.data.local.SharedPreferencesManager
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme
import com.moksh.imposterai.presentation.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sharedPref: SharedPreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImposterAiTheme {
                NavGraph(sharedPref)
            }
        }
    }
}
