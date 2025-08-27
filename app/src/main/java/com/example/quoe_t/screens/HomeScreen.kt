package com.example.quoe_t.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quoe_t.QuoetViewModel

//TODO Move all code to DevicesScreen, refactor DevicesScreen to NewBanScreen, change HomeScreen to choose between upgrading device or new account.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    quoetViewModel: QuoetViewModel,
    onAddDevicesClicked: () -> Unit
) {
    val uiState by quoetViewModel.uiState.collectAsState()

    val addDevicesButtonEnabled = uiState.addDevicesButtonEnabled

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.background) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onAddDevicesClicked()
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Add Devices")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            //TODO Implement text with instructions and credits.
        }
    }
}