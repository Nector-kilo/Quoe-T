package com.example.quoe_t.newCx.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quoe_t.newCx.LineUiState
import com.example.quoe_t.newCx.NewCxViewModel
import com.example.quoe_t.newCx.business.Line
import com.example.quoe_t.newCx.business.RatePlan
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCxDataScreen(newCxViewModel: NewCxViewModel, onSaveAndCloseClicked: () -> Unit) {
    val uiState by newCxViewModel.uiState.collectAsState()

    val ratePlanName = uiState.ratePlan?.data?.rateName
    val lineCount = uiState.lineCount

    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        newCxViewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("New Customer Data") })},
        bottomBar = { BottomAppBar(containerColor = MaterialTheme.colorScheme.background) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        newCxViewModel.generateNewCxQuote()
                        onSaveAndCloseClicked()
                    },
                    enabled = uiState.saveNewCxButtonEnabled,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) { Text("Save & Close") }
            }
        }}
    ) { innerPadding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)
        ) {
            item {
                OutlinedCard(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.padding(2.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RatePlanSelectionMenu(
                            ratePlanName = ratePlanName,
                            onRatePlanChange = { newCxViewModel.setRatePlan(it) }
                        )

                        LineCountInputField(
                            lineCount = lineCount,
                            isError = uiState.lineCountError,
                            enabled = uiState.lineCountEnabled,
                            onLineCountChange = { newCxViewModel.updateLineCount(it) }
                        )
                    }
                }
            }
            item {
                DevicesDataList(
                    lines = uiState.listOfLines,
                    linesUiState = uiState.listOfLinesUiState,
                    onLineUiChange = { index, lineUiState ->
                        newCxViewModel.updateLineUiStates(index, lineUiState)
                    }
                )
            }
        }
    }
}

@Composable
fun RatePlanSelectionMenu(ratePlanName: String?, onRatePlanChange: (RatePlan) -> Unit) {
    val listOfRatePlans = RatePlan.getAllRatePlans()
    var expanded by remember { mutableStateOf(false) }
    var dropDownWidth by remember { mutableIntStateOf(0) }

    Column {
        OutlinedTextField(
            label = { Text("Rate Plan") },
            value = ratePlanName ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledContainerColor =
                    OutlinedTextFieldDefaults.colors().unfocusedContainerColor,
                disabledTextColor =
                    OutlinedTextFieldDefaults.colors().unfocusedTextColor,
                disabledLabelColor =
                    OutlinedTextFieldDefaults.colors().unfocusedLabelColor,
                disabledTrailingIconColor =
                    OutlinedTextFieldDefaults.colors().unfocusedTrailingIconColor,
                disabledIndicatorColor =
                    OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor
            ),
            trailingIcon = { Icon(
                imageVector =  Icons.Default.ArrowDropDown,
                contentDescription = "Rate Plan",
                modifier = Modifier.clickable { expanded = !expanded }
            )},
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(8.dp)
                .onSizeChanged { dropDownWidth = it.width }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { dropDownWidth.toDp() })
        ) {
            listOfRatePlans.forEach { DropdownMenuItem(
                text = { Text(it.data.rateName) },
                onClick = {
                    expanded = false
                    onRatePlanChange(it)
                }
            )}
        }
    }
}

@Composable
fun LineCountInputField(
    lineCount: String,
    isError: Boolean,
    enabled: Boolean,
    onLineCountChange: (String) -> Unit
) {
    OutlinedTextField(
        label = { Text("Number of Lines") },
        value = lineCount,
        onValueChange = { onLineCountChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        isError = isError,
        enabled = enabled,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun DevicesDataList(
    lines: List<Line>,
    linesUiState: List<LineUiState>,
    onLineUiChange: (index: Int, lineUiState: LineUiState) -> Unit,
) {
    OutlinedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        if (lines.isEmpty()) Text(
            text = "Device data will show here.",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize().padding(vertical = 64.dp)
        ) else {
            Text(
                text = "Input Device Data",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Column {
                lines.forEachIndexed { i, line ->
                    DeviceDataItem(
                        line = line,
                        lineUiState = linesUiState[i],
                        onLineUiChanged = { onLineUiChange(i, it) }
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceDataItem(line: Line, lineUiState: LineUiState, onLineUiChanged: (LineUiState) -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.padding(6.dp)
    ) {
        Row (verticalAlignment = Alignment.CenterVertically) {
            Text (
                text = "Line\n   ${line.lineNumber + 1}",
                modifier = Modifier.padding(8.dp)
            )
            Column (modifier = Modifier.weight(1f)) {

                Row {
                    DeviceInputTextField(
                        value = lineUiState.fullDeviceCost,
                        onValueChange = { onLineUiChanged(lineUiState.copy(fullDeviceCost = it)) },
                        enabled = !lineUiState.isByod,
                        isError = lineUiState.fullDeviceCostHasError,
                        label = { Text("Cost") }
                    )
                    DeviceInputTextField(
                        value = lineUiState.promotionAmount,
                        onValueChange = { onLineUiChanged(lineUiState.copy(promotionAmount = it)) },
                        enabled = !lineUiState.isByod,
                        isError = lineUiState.promotionAmountHasError,
                        label = { Text("Promo") }
                    )
                    DeviceInputTextField(
                        value = lineUiState.fairMarketValue,
                        onValueChange = { onLineUiChanged(lineUiState.copy(fairMarketValue = it))},
                        enabled = !lineUiState.isByod,
                        isError = lineUiState.fairMarketValueHasError,
                        label = { Text("Fmv") }
                    )
                }

                Row {
                    DeviceInputTextField(
                        value = lineUiState.downPayment,
                        onValueChange = { onLineUiChanged(lineUiState.copy(downPayment = it)) },
                        enabled = !lineUiState.isByod,
                        isError = lineUiState.downPaymentHasError,
                        label = { Text("Down") },
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                    ) {
                        Checkbox(
                            checked = lineUiState.isByod,
                            onCheckedChange = { onLineUiChanged(lineUiState.copy(isByod = it)) }
                        )
                        Text(
                            text = "BYOD",
                            modifier = Modifier.clickable(
                                onClick = { onLineUiChanged(lineUiState.copy(
                                    isByod = !lineUiState.isByod
                                ))}
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                    ) {
                        Checkbox(
                            checked = lineUiState.hasP360,
                            onCheckedChange = { onLineUiChanged(lineUiState.copy(hasP360 = it)) }
                        )
                        Text(
                            text = "P360",
                            modifier = Modifier.clickable(onClick = {
                                onLineUiChanged(lineUiState.copy(hasP360 = !lineUiState.hasP360))
                            })
                        )
                    }
                }

                Row {
                    Column(modifier = Modifier.weight(1f).padding(4.dp)) {
                        Text(text = "Balance after promo")
                        Text(text = "$" + String.format(
                            Locale.US, "%.2f",
                            line.deviceBalanceAfterPromo
                        ))
                    }

                    Column(modifier = Modifier.weight(1f).padding(4.dp)) {
                        Text(text = "Total monthly cost")
                        Text(text = "$" + String.format(
                            Locale.US, "%.2f",
                            line.monthlyDevicePaymentAfterPromo + line.p360MonthlyPayment
                        ))
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.DeviceInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = label,
        singleLine = true,
        enabled = enabled,
        isError = isError,
        modifier = Modifier.weight(1f).padding(horizontal = 4.dp, vertical = 2.dp)
    )
}
