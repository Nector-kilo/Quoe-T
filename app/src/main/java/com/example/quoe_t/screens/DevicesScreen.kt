package com.example.quoe_t.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.quoe_t.LineUiState
import com.example.quoe_t.QuoetViewModel
import com.example.quoe_t.business.Line
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    quoetViewModel: QuoetViewModel,
    onSaveAndCloseClicked: () -> Unit
) {
    val uiState by quoetViewModel.uiState.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        quoetViewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device Screen") }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            quoetViewModel.generateQuote()
                            onSaveAndCloseClicked()
                        },
                        modifier = Modifier
                    ) {
                        Text("Save & Close")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)
        ) {
            DevicesList(
                lines = uiState.listOfLines,
                linesUiState = uiState.listOfLineUiStates,
                onValueChanged = { index, lineUiState ->
                    quoetViewModel.updateLineUiState(index, lineUiState)
                }
            )
        }
    }
}

@Composable
fun DevicesList(
    lines: List<Line>,
    linesUiState: List<LineUiState>,
    onValueChanged: (index: Int, lineUiState: LineUiState) -> Unit
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(items = lines) { index, line ->
            DeviceItem(
                line = line,
                lineUiState = linesUiState[index],
                onValueChanged = { lineUiState ->
                    onValueChanged(index, lineUiState)
                }
            )
        }
    }
}

@Composable
fun DeviceItem(
    line: Line,
    lineUiState: LineUiState,
    onValueChanged: (LineUiState) -> Unit
) {
    Card (
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {

        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text (
                text = "Line\n   ${line.lineNumber}",
                modifier = Modifier
                    .padding(8.dp)
            )

            Column (
                modifier = Modifier.weight(1f)
            ) {
                Row {
                    DeviceInputTextField(
                        value = lineUiState.fullDeviceCost,
                        onValueChange = { newFullDeviceCost ->
                            onValueChanged(lineUiState.copy(fullDeviceCost = newFullDeviceCost))
                        },
                        enabled = !lineUiState.isByod,
                        label = { Text("Cost") }
                    )
                    DeviceInputTextField(
                        value = lineUiState.promotionAmount,
                        onValueChange = { newPromotionAmount ->
                            onValueChanged(lineUiState.copy(promotionAmount = newPromotionAmount))
                        },
                        enabled = !lineUiState.isByod,
                        label = { Text("Promo") }
                    )
                    DeviceInputTextField(
                        value = lineUiState.fairMarketValue,
                        onValueChange = { newFairMarketValue ->
                            onValueChanged(lineUiState.copy(fairMarketValue = newFairMarketValue))
                        },
                        enabled = !lineUiState.isByod,
                        label = { Text("Fmv") }
                    )
                }

                Row {
                    DeviceInputTextField(
                        value = lineUiState.downPayment,
                        onValueChange = { newDownPayment ->
                            onValueChanged(lineUiState.copy(downPayment = newDownPayment))
                        },
                        enabled = !lineUiState.isByod,
                        label = { Text("Down") },
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    ) {
                        Checkbox(
                            checked = lineUiState.isByod,
                            onCheckedChange = { newIsByod ->
                                onValueChanged(lineUiState.copy(isByod = newIsByod))
                            }
                        )
                        Text(
                            text = "BYOD",
                            modifier = Modifier.clickable(
                                onClick = {
                                    onValueChanged(lineUiState.copy(isByod = !lineUiState.isByod))
                                }
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    ) {
                        Checkbox(
                            checked = lineUiState.hasP360,
                            onCheckedChange = { newHasP360 ->
                                onValueChanged(lineUiState.copy(hasP360 = newHasP360))
                            }
                        )
                        Text(
                            text = "P360",
                            modifier = Modifier.clickable(
                                onClick = {
                                    onValueChanged(lineUiState.copy(hasP360 = !lineUiState.hasP360))
                                }
                            )
                        )
                    }
                }

                Row {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Text(text = "Balance after promo")
                        Text(
                            text = "$" + String.format(
                                Locale.US, "%.2f", line.deviceBalanceAfterPromo
                            )
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Text(text = "Total monthly cost")
                        Text(
                            text = "$" + String.format(
                                Locale.US, "%.2f", line.monthlyDevicePaymentAfterPromo + line.p360MonthlyPayment
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.DeviceInputTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = label,
        singleLine = true,
        enabled = enabled,
        modifier = modifier.weight(1f).padding(horizontal = 4.dp, vertical = 2.dp)
    )
}
