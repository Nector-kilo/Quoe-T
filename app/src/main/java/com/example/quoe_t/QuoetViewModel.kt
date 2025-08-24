package com.example.quoe_t

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quoe_t.business.Line
import com.example.quoe_t.business.Quote
import com.example.quoe_t.business.RatePlan
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LineUiState(
    val fullDeviceCost: String = "",
    val promotionAmount: String = "",
    val fairMarketValue: String = "",
    val downPayment: String = "",
    val hasP360: Boolean = false,
    val isByod: Boolean = false
)

data class QuoetUiState(
    val ratePlan: RatePlan = RatePlan.EmptyRatePlan,
    val lineCount: String = "",
    val listOfLines: List<Line> = listOf(),
    val listOfLineUiStates: List<LineUiState> = listOf(),
    val quote: Quote = Quote(ratePlan, listOfLines),
    val addDevicesButtonEnabled: Boolean = false,
    val isError: Boolean = false
)

class QuoetViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(QuoetUiState())
    val uiState: StateFlow<QuoetUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    fun emitToast(text: String) {
        viewModelScope.launch {
            _toastMessage.emit(text)
        }
    }

    fun clearViewModel() {
        _uiState.update { currentState ->
            currentState.copy(
                ratePlan = RatePlan.EmptyRatePlan,
                lineCount = "",
                listOfLines = listOf(),
                addDevicesButtonEnabled = false
            )
        }
    }

    fun setRatePlan(newRatePlan: RatePlan) {
        _uiState.update { currentState ->
            currentState.copy(ratePlan = newRatePlan)
        }
        enableAddDevicesButton()
    }

    fun setLineCount(newLineCount: String) {
        try {
            when {
                newLineCount.toInt() > 5 -> {
                    emitToast("Maximum of 5 lines.")
                    _uiState.update { currentState ->
                        currentState.copy(
                            lineCount = newLineCount,
                            isError = true
                        )
                    }
                }
                newLineCount.toInt() < 1 -> {
                    emitToast("Minimum of 1 line.")
                    _uiState.update { currentState ->
                        currentState.copy(
                            lineCount = newLineCount,
                            isError = true
                        )
                    }
                }
                else -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            lineCount = newLineCount,
                            isError = false
                        )
                    }
                    createListOfLines()
                }
            }
        } catch (_: NumberFormatException) {
            if (newLineCount != "") {
                emitToast("Must only be numbers.")
            }
            _uiState.update { currentState ->
                currentState.copy(
                    lineCount = newLineCount,
                    isError = true
                )
            }
        }
        enableAddDevicesButton()
    }

    private fun createListOfLines() {
        val listOfLines = mutableListOf<Line>()
        val listOfLineInputStates = mutableListOf<LineUiState>()
        for (i in 1..uiState.value.lineCount.toInt()) {
            listOfLines.add(Line(lineNumber = i))
            listOfLineInputStates.add(LineUiState())
        }
        _uiState.update { currentState ->
            currentState.copy(
                listOfLines = listOfLines,
                listOfLineUiStates = listOfLineInputStates
            )
        }
    }

    fun enableAddDevicesButton() {
        _uiState.update { currentState ->
            currentState.copy(addDevicesButtonEnabled =
                uiState.value.ratePlan != RatePlan.EmptyRatePlan && !uiState.value.isError
            )
        }
    }

    fun updateLineUiState(index: Int, lineUiState: LineUiState) {
        _uiState.update { currentState ->
            val mutableListOfLineUiStates = currentState.listOfLineUiStates.toMutableList()
            if (lineUiState.isByod) {
                mutableListOfLineUiStates[index] = lineUiState.copy(
                    fullDeviceCost = "",
                    promotionAmount = "",
                    fairMarketValue = "",
                    downPayment = ""
                )
            } else {
                mutableListOfLineUiStates[index] = lineUiState
            }
            currentState.copy(
                listOfLineUiStates = mutableListOfLineUiStates.toList()
            )
        }
        updateLine(index)
    }

    fun updateLine(index: Int) { //TODO Implement input error handling.
        try {
            _uiState.update { currentState ->
                val lineInputState = currentState.listOfLineUiStates[index]

                val mutableListOfLines = currentState.listOfLines.toMutableList()
                mutableListOfLines[index] = mutableListOfLines[index].copy(
                    fullDeviceCost = if (lineInputState.fullDeviceCost != "") {
                        lineInputState.fullDeviceCost.toFloat()
                    } else 0f,
                    promotionAmount = if (lineInputState.promotionAmount != "") {
                        lineInputState.promotionAmount.toFloat()
                    } else 0f,
                    fairMarketValue = if (lineInputState.fairMarketValue != "") {
                        lineInputState.fairMarketValue.toFloat()
                    } else 0f,
                    downPayment = if (lineInputState.downPayment != "") {
                        lineInputState.downPayment.toFloat()
                    } else 0f,
                    hasP360 = lineInputState.hasP360
                )

                currentState.copy(
                    isError = false, //TODO Implement this feature.
                    listOfLines = mutableListOfLines.toList(),
                )
            }
        } catch (_: NumberFormatException) {
            emitToast("Must only be numbers.")
            _uiState.update { currentState ->
                currentState.copy(isError = true)
            }
        }
    }

    fun generateQuote(){
        _uiState.update {
            it.copy(quote = Quote(uiState.value.ratePlan, uiState.value.listOfLines))
        }
    }
}
