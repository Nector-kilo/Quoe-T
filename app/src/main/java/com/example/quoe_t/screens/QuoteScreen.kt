package com.example.quoe_t.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quoe_t.QuoetViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteScreen(quoetViewModel: QuoetViewModel, onCloseClicked: () -> Unit) {
    val uiState by quoetViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quote") }
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
                            onCloseClicked()
                        },
                    ) { Text("New Quote") }
                    //TODO Implement share button.
                }
            }
        }
    ) { innerPadding ->
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                BillTotalsCard("Monthly Bill Est:", uiState.quote.monthlyBill())
                BillTotalsCard("One-Time Credit:", uiState.quote.oneTimeBillCredit())
            }
            Card (
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .padding(6.dp)
            ) {
                Text (
                    text = "Detailed Device Summary",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                LazyColumn {
                    itemsIndexed(uiState.listOfLines) { _, line ->
                        Surface (
                            shape = MaterialTheme.shapes.medium,
                            tonalElevation = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                        ) {
                            Column (
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text (
                                    text = "Line ${line.lineNumber}",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                ReceiptPrinter(
                                    item = "Total monthly cost estimate:",
                                    cost = line.monthlyDevicePaymentAfterPromo + line.p360MonthlyPayment
                                )
                                if (line.fullDeviceCost == 0f) {
                                    ReceiptPrinter(
                                        item = "Full device cost:",
                                        text = "BYOD"
                                    )
                                } else {
                                    ReceiptPrinter(
                                        item = "Full device cost:",
                                        cost = line.fullDeviceCost
                                    )
                                    if (line.downPayment != 0f) {
                                        ReceiptPrinter(
                                            item = "Down payment:",
                                            cost = line.downPayment
                                        )
                                        ReceiptPrinter(
                                            item = "Device balance after down payment:",
                                            cost = line.deviceBalance
                                        )
                                    }
                                    ReceiptPrinter(
                                        item = "Monthly device payment:",
                                        cost = line.deviceBalance / 24
                                    )
                                }
                                if (line.promotionAmount != 0f) {
                                    ReceiptPrinter(
                                        item = "Monthly device credit:",
                                        cost = line.promotionAmount / 24
                                    )
                                    ReceiptPrinter(
                                        item = "Monthly device payment after credit:",
                                        cost = line.monthlyDevicePaymentAfterPromo
                                    )
                                }
                                if (line.hasP360) {
                                    ReceiptPrinter(
                                        item = "P360 monthly cost estimate",
                                        cost = line.p360MonthlyPayment
                                    )
                                }
                                if (line.fairMarketValue != 0f) {
                                    ReceiptPrinter(
                                        item = "One time bill credit for trade in:",
                                        cost = line.fairMarketValue
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BillTotalsCard(text: String, value: Float) {
    Card (
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .padding(6.dp)
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 32.dp, horizontal = 8.dp)
        ) {
            Text(
                text = text,
                fontSize = 22.sp
            )
            Text(
                text = "$" +
                        String.format(Locale.US, "%.2f", value),
                fontSize = 30.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun ReceiptPrinter(item: String, cost: Float = 0f, text: String = "") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = item)
        Text (
            text = ".".repeat(150),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Clip,
            maxLines = 1
        )
        if (text == "") {
            Text(text = "$" + String.format(Locale.US, "%.2f", cost))
        } else {
            Text(text = text)
        }
    }
}
