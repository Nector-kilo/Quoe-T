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
    val fullDeviceCostHasError: Boolean = false,
    val promotionAmount: String = "",
    val promotionAmountHasError: Boolean = false,
    val fairMarketValue: String = "",
    val fairMarketValueHasError: Boolean = false,
    val downPayment: String = "",
    val downPaymentHasError: Boolean = false,
    val hasP360: Boolean = false,
    val isByod: Boolean = false
)

data class NewCxUiState(
    val ratePlan: RatePlan? = null,

    val lineCount: String = "",
    val lineCountError: Boolean = false,

    val listOfLines: List<Line> = listOf(),
    val listOfLinesUiState: List<LineUiState> = listOf(),

    val saveNewCxButtonEnabled: Boolean = false,

    val quote: Quote? = null
)

class NewCxViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewCxUiState())
    val uiState: StateFlow<NewCxUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    private fun emitToast(text: String) {
        viewModelScope.launch {
            _toastMessage.emit(text)
        }
    }

    fun setRatePlan(newRatePlan: RatePlan) {
        _uiState.update { currentState ->
            currentState.copy(ratePlan = newRatePlan)
        }
    }

    //TODO Consider reworking this. It can probably be done better.
    fun updateLineCount(newLineCount: String) {
        try {
            when {
                newLineCount.toInt() > 5 -> {
                    onLineCountValidationFailure(newLineCount, "Maximum of 5 lines.")
                }
                newLineCount.toInt() < 1 -> {
                    onLineCountValidationFailure(newLineCount, null)
                }
                else -> onLineCountValidationSuccess(newLineCount)
            }
        } catch (_: NumberFormatException) {
            if (newLineCount != "") {
                onLineCountValidationFailure(newLineCount, "Must be only numbers.")
            } else onLineCountValidationFailure(newLineCount, null)
        }
    }

    private fun onLineCountValidationSuccess(newLineCount: String) {
        val listOfLines = mutableListOf<Line>()
        val listOfLineInputStates = mutableListOf<LineUiState>()
        for (i in 1..newLineCount.toInt()) {
            listOfLines.add(Line(lineNumber = i))
            listOfLineInputStates.add(LineUiState())
        }
        _uiState.update { currentState ->
            currentState.copy(
                lineCount = newLineCount,
                lineCountError = false,
                listOfLines = listOfLines,
                listOfLinesUiState = listOfLineInputStates
            )
        }
    }

    private fun onLineCountValidationFailure(newLineCount: String, toastMessage: String?) {
        if (toastMessage != null) emitToast(toastMessage)
        _uiState.update { currentState ->
            currentState.copy(
                lineCount = newLineCount,
                lineCountError = true,
                listOfLines = listOf(),
                listOfLinesUiState = listOf()
            )
        }
    }

    fun updateLineUiStates(index: Int, currentLineUiState: LineUiState) {
        updateAndValidateLineUiStates(index, currentLineUiState)
        enableSaveNewCxButton()
    }

    //TODO Clean this up big time. Right now, error handling is very buggy.
    private fun updateAndValidateLineUiStates(index: Int, currentLineUiState: LineUiState) {
        val toastMessage = "Must be only numbers."
        val mutableListOfLineUiStates = _uiState.value.listOfLinesUiState.toMutableList()
        var updatedLineUiState: LineUiState
        when {
            currentLineUiState.isByod -> {
                emitToast("Saving line as BYOD.")
                updatedLineUiState = currentLineUiState.copy(
                    fullDeviceCost = "",
                    promotionAmount = "",
                    fairMarketValue = "",
                    downPayment = ""
                )
            } currentLineUiState.fullDeviceCost == "" -> {
                updatedLineUiState = currentLineUiState.copy(
                    fullDeviceCostHasError = true
                )
            } currentLineUiState.promotionAmount != "" -> {
                try {
                    currentLineUiState.promotionAmount.toFloat()
                    updatedLineUiState = currentLineUiState.copy(
                        promotionAmountHasError = false
                    )
                } catch (_: NumberFormatException) {
                    emitToast(toastMessage)
                    updatedLineUiState = currentLineUiState.copy(
                        promotionAmountHasError = true
                    )
                }
            } currentLineUiState.fairMarketValue != "" -> {
                try {
                    currentLineUiState.fairMarketValue.toFloat()
                    updatedLineUiState = currentLineUiState.copy(fairMarketValueHasError = false)
                } catch (_: NumberFormatException) {
                    emitToast(toastMessage)
                    updatedLineUiState = currentLineUiState.copy(fairMarketValueHasError = true)
                }
            } currentLineUiState.downPayment != "" -> {
                try {
                    currentLineUiState.downPayment.toFloat()
                    updatedLineUiState = currentLineUiState.copy(downPaymentHasError = false)
                } catch (_: NumberFormatException) {
                    emitToast(toastMessage)
                    updatedLineUiState = currentLineUiState.copy(downPaymentHasError = true)
                }
            } else -> {
                try {
                    currentLineUiState.fullDeviceCost.toFloat()
                    updatedLineUiState = currentLineUiState.copy(fullDeviceCostHasError = false)
                } catch (_: NumberFormatException) {
                    emitToast(toastMessage)
                    updatedLineUiState = currentLineUiState.copy(fullDeviceCostHasError = true)
                }
            }
        }
        mutableListOfLineUiStates[index] = updatedLineUiState
        _uiState.update { currentState ->
            currentState.copy(
                listOfLinesUiState = mutableListOfLineUiStates.toList()
            )
        }
    }

    //TODO Clean this code up. It's buggy and ugly.
    private fun updateAndValidateLines() {
        val mutableListOfLines = _uiState.value.listOfLines.toMutableList()
        mutableListOfLines.forEachIndexed{ i, line ->
            val lineUiState = _uiState.value.listOfLinesUiState[i]
            if (!lineUiState.isByod) {
                mutableListOfLines[i] = line.copy(
                    fullDeviceCost = lineUiState.fullDeviceCost.toFloat(),
                    promotionAmount = if (lineUiState.promotionAmount != "") {
                        lineUiState.promotionAmount.toFloat()
                    } else 0f,
                    fairMarketValue = if (lineUiState.fairMarketValue != "") {
                        lineUiState.fairMarketValue.toFloat()
                    } else 0f,
                    downPayment = if (lineUiState.downPayment != "") {
                        lineUiState.downPayment.toFloat()
                    } else 0f,
                    hasP360 = lineUiState.hasP360
                )
            }
        }
        _uiState.update { currentState ->
            currentState.copy(
                listOfLines = mutableListOfLines.toList(),
            )
        }
    }

    fun enableSaveNewCxButton() {
        val hasError: MutableList<Boolean?> = mutableListOf(_uiState.value.lineCountError)
        _uiState.value.listOfLinesUiState.forEach { lineUiState ->
            hasError.add(lineUiState.fullDeviceCostHasError)
            hasError.add(lineUiState.promotionAmountHasError)
            hasError.add(lineUiState.fairMarketValueHasError)
            hasError.add(lineUiState.downPaymentHasError)
        }
        if (hasError.all { it == false }) updateAndValidateLines()
        _uiState.update { currentState ->
            currentState.copy(saveNewCxButtonEnabled = hasError.all { it == false })
        }
    }

    fun generateQuote(){
        _uiState.update { currentState ->
            currentState.copy(
                quote = currentState.ratePlan?.let { Quote(it, currentState.listOfLines) }
            )
        }
    }
}