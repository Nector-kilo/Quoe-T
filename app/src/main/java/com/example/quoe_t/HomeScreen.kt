package com.example.quoe_t

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//TODO Update HomeScreen to choose between upgrading device or new account.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNewCxClicked: () -> Unit, onExistingCxClicked: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Quoe-T") }) },
        bottomBar = { BottomAppBar(containerColor = MaterialTheme.colorScheme.background) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { onExistingCxClicked() },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f).padding(4.dp)
                ) { Text("Existing Customer") }

                Button(
                    onClick = { onNewCxClicked() },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f).padding(4.dp)
                ) { Text("New Customer") }
            }
        }}
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            OutlinedCard (
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.padding(6.dp).weight(1f)
            ) {
                Card (
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxSize().padding(6.dp)
                ) {
                    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                        item {
                            Text(
                                textAlign = TextAlign.Center,
                                fontSize = 32.sp,
                                fontStyle = FontStyle.Italic,
                                text = "\nWelcome to Quoe-T\n"
                            )
                            Text(
                                fontSize = 22.sp,
                                modifier = Modifier.fillMaxWidth(),
                                text = "Introduction:"
                            )
                            Text(
                                fontSize = 16.sp,
                                text = "This is my little pet project to help generate quotes for " +
                                        "new and existing T-Mobile customers.\n\nThis application " +
                                        "was written in Kotlin for the Android operating system, " +
                                        "following a Model-View-ViewModel (MVVM) architectural " +
                                        "design and utilizing the JetPack Compose framework.\n\nIf " +
                                        "you're interested in seeing the code, it's public on my " +
                                        "GitHub:\nhttps://GitHub.com/NicholasGeraci\n\nAll code is " +
                                        "licensed under the GNU General Public Licence 3.0, so feel " +
                                        "free to change the code however you like, just make sure " +
                                        "you follow the GNU GPL guidelines.\n"
                            )
                            Text(
                                fontSize = 22.sp,
                                modifier = Modifier.fillMaxWidth(),
                                text = "Disclaimer:"
                            )
                            Text(
                                fontSize = 16.sp,
                                text = "This program can make mistakes! I am not responsible for " +
                                        "any misquotes due to the usage of this application within " +
                                        "a retail environment.\n"
                            )
                            Text(
                                fontSize = 22.sp,
                                modifier = Modifier.fillMaxWidth(),
                                text = "Instructions:"
                            )
                            Text(
                                fontSize = 16.sp,
                                text = "Select either Existing Customer or New Customer.\n"
                            )
                            Text(
                                fontSize = 18.sp,
                                modifier = Modifier.fillMaxWidth(),
                                text = "New Customer:"
                            )
                            Text(
                                fontSize = 16.sp,
                                text = "1) Choose the Rate Plan.\n2) Input the number of " +
                                        "lines (Maximum is 5).\n3) Fill in the data for the " +
                                        "new device(s). You will be notified of any errors. " +
                                        "Note: Not every field needs to have a value, just the " +
                                        "Number of Lines, and either Cost or BYOD needs to be " +
                                        "checked.\n4) Select Save & Close.\n5) Present the " +
                                        "quote to the customer.\n"
                            )
                            Text(
                                fontSize = 18.sp,
                                modifier = Modifier.fillMaxWidth(),
                                text = "Existing Customer:"
                            )
                            //TODO Implement this documentation and remove the modifier param.
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 16.sp,
                                text = "TODO\n"
                            )
                            Text(
                                fontSize = 22.sp,
                                modifier = Modifier.fillMaxWidth(),
                                text = "Found a bug?:"
                            )
                            Text(
                                fontSize = 16.sp,
                                text = "If you find any bugs, please email me directly at:\n" +
                                        "NicholasAGeraci00@gmail.com\n"
                            )
                            Text(
                                fontSize = 22.sp,
                                modifier = Modifier.fillMaxWidth(),
                                text = "Bug Tracker:"
                            )
                            Text(
                                fontSize = 16.sp,
                                text = "1) Since Existing Customer isn't yet implemented, if the " +
                                        "user presses the Existing Customer button, they will be " +
                                        "locked to an empty screen and be forced to restart the " +
                                        "application."
                            )
                        }
                    }
                }
            }
            OutlinedCard (
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.padding(6.dp).weight(0.1f)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.padding(6.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        maxLines = 1,
                        text = "Choose an option below to get started!"
                    )
                }
            }
        }
    }
}