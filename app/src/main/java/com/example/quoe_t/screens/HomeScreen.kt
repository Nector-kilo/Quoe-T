package com.example.quoe_t.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.quoe_t.QuoetViewModel
import com.example.quoe_t.business.RatePlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    quoetViewModel: QuoetViewModel,
    onAddDevicesClicked: () -> Unit
) {
    val uiState by quoetViewModel.uiState.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        quoetViewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val ratePlanName = uiState.ratePlan.data.rateName
    val lineCount = uiState.lineCount
    val addDevicesButtonEnabled = uiState.addDevicesButtonEnabled
    val isError = uiState.isError

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        enabled = addDevicesButtonEnabled,
                        onClick = {
                            onAddDevicesClicked()
                        }
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
            RatePlanSelectionMenu(
                ratePlanName = ratePlanName,
            ) { newRatePlan ->
                quoetViewModel.setRatePlan(newRatePlan)
            }

            LineCountInputField(lineCount, isError) { newLineCount ->
                quoetViewModel.setLineCount(newLineCount)
            }
            //TODO Implement text with instructions and credits.
        }
    }
}

@Composable
fun RatePlanSelectionMenu(
    ratePlanName: String,
    onRatePlanSelection: (RatePlan) -> Unit
) {
    val listOfRatePlans = RatePlan.getAllRatePlans()
    var expanded by remember { mutableStateOf(false) }
    var dropDownWidth by remember { mutableIntStateOf(0) }

    Column {
        OutlinedTextField(
            label = { Text("Rate Plan") },
            value = ratePlanName,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledContainerColor = OutlinedTextFieldDefaults.colors().unfocusedContainerColor,
                disabledTextColor = OutlinedTextFieldDefaults.colors().unfocusedTextColor,
                disabledLabelColor = OutlinedTextFieldDefaults.colors().unfocusedLabelColor,
                disabledTrailingIconColor = OutlinedTextFieldDefaults.colors().unfocusedTrailingIconColor,
                disabledIndicatorColor = OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor
            ),
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Rate Plan",
                    modifier = Modifier.clickable {
                        expanded = !expanded
                    }
                )
            },
            modifier = Modifier
                .clickable {
                    expanded = !expanded
                }
                .padding(8.dp)
                .onSizeChanged { newSize ->
                    dropDownWidth = newSize.width
                }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier
                .width(
                    with(LocalDensity.current) {
                        dropDownWidth.toDp()
                    }
                )
        ) {
            listOfRatePlans.forEach { ratePlan ->
                DropdownMenuItem(
                    text = { Text(ratePlan.data.rateName) },
                    onClick = {
                        expanded = false
                        onRatePlanSelection(ratePlan)
                    }
                )
            }
        }
    }
}

@Composable
fun LineCountInputField(lineCount: String, isError: Boolean, onLineCountInput: (String) -> Unit) {
    OutlinedTextField(
        label = { Text("Number of Lines") },
        value = lineCount,
        onValueChange = { text ->
            onLineCountInput(text)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        isError = isError,
        modifier = Modifier.padding(8.dp)
    )
}
