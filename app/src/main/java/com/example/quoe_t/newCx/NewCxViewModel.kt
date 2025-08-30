package com.example.quoe_t.newCx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quoe_t.newCx.business.Line
import com.example.quoe_t.newCx.business.NewCxQuote
import com.example.quoe_t.newCx.business.RatePlan
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
    val lineCountEnabled: Boolean = false,
    val lineCount: String = "",
    val lineCountError: Boolean = false,
    val listOfLines: List<Line> = listOf(),
    val listOfLinesUiState: List<LineUiState> = listOf(),
    val saveNewCxButtonEnabled: Boolean = false,
    val newCxQuote: NewCxQuote? = null
)

class NewCxViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewCxUiState())
    val uiState: StateFlow<NewCxUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    private fun emitToast(text: String) {
        viewModelScope.launch { _toastMessage.emit(text) }
    }

    fun setRatePlan(newRatePlan: RatePlan) {
        if (_uiState.value.ratePlan != null) {
            _uiState.update { it.copy(ratePlan = newRatePlan) }
            updateLineCount(_uiState.value.lineCount)
        } else _uiState.update { it.copy(
            ratePlan = newRatePlan,
            lineCountEnabled = true
        )}
    }

    fun updateLineCount(newLineCount: String) {
        try {
            when {
                newLineCount.toInt() > 5 -> {
                    onLineCountValidationFailure(
                        newLineCount,
                        "Maximum of 5 lines."
                    )
                }

                newLineCount.toInt() < 1 -> {
                    onLineCountValidationFailure(
                        newLineCount,
                        "Minimum of 1 line"
                    )
                }

                _uiState.value.ratePlan == RatePlan.FourLineOffer && newLineCount.toInt() < 4 -> {
                    onLineCountValidationFailure(
                        newLineCount,
                        "Minimum of 4 lines for \"Four Line Offer.\""
                    )
                }

                else -> onLineCountValidationSuccess(newLineCount)
            }
        } catch (_: NumberFormatException) {
            if (newLineCount != "") onLineCountValidationFailure(
                newLineCount,
                "Must be only numbers."
            ) else onLineCountValidationFailure(newLineCount)
        }
    }

    private fun onLineCountValidationSuccess(newLineCount: String) {
        val listOfLines = mutableListOf<Line>()
        val listOfLineInputStates = mutableListOf<LineUiState>()

        for (i in 0..newLineCount.toInt() - 1) {
            listOfLines.add(Line(lineNumber = i))
            listOfLineInputStates.add(LineUiState())
        }
        _uiState.update { it.copy(
                lineCount = newLineCount,
                lineCountError = false,
                listOfLines = listOfLines,
                listOfLinesUiState = listOfLineInputStates
        )}
    }

    private fun onLineCountValidationFailure(newLineCount: String, toastMessage: String? = null) {
        if (toastMessage != null) emitToast(toastMessage)
        _uiState.update { it.copy(
                lineCount = newLineCount,
                lineCountError = true,
                listOfLines = listOf(),
                listOfLinesUiState = listOf()
        )}
    }

    fun updateLineUiStates(index: Int, currentLineUiState: LineUiState) {
        val mutableListOfLineUiStates = _uiState.value.listOfLinesUiState.toMutableList()
        var mutableLineUiState = mutableListOfLineUiStates[index]

        mutableLineUiState = mutableLineUiState.copy(
            fullDeviceCost = currentLineUiState.fullDeviceCost,
            promotionAmount = currentLineUiState.promotionAmount,
            fairMarketValue = currentLineUiState.fairMarketValue,
            downPayment = currentLineUiState.downPayment,
            hasP360 = currentLineUiState.hasP360,
            isByod = currentLineUiState.isByod
        )

        if (currentLineUiState.isByod) mutableLineUiState = mutableLineUiState.copy(
            fullDeviceCost = "",
            promotionAmount = "",
            fairMarketValue = "",
            downPayment = "",
            fullDeviceCostHasError = false,
            promotionAmountHasError = false,
            fairMarketValueHasError = false,
            downPaymentHasError = false
        ) else {
            if (currentLineUiState.fullDeviceCost.isNotEmpty()) validateFloatFromString(
                string = currentLineUiState.fullDeviceCost,
                onSuccess = { mutableLineUiState = mutableLineUiState.copy(
                    fullDeviceCostHasError = false
                )},
                onFail = { mutableLineUiState = mutableLineUiState.copy(
                    fullDeviceCostHasError = true
                )}
            ) else mutableLineUiState = mutableLineUiState.copy(
                fullDeviceCostHasError = true
            )

            if (currentLineUiState.promotionAmount.isNotEmpty()) validateFloatFromString(
                string = currentLineUiState.promotionAmount,
                onSuccess = { mutableLineUiState = mutableLineUiState.copy(
                    promotionAmountHasError = false
                )},
                onFail = { mutableLineUiState = mutableLineUiState.copy(
                    promotionAmountHasError = true
                )}
            ) else mutableLineUiState = mutableLineUiState.copy(
                promotionAmountHasError = false
            )

            if (currentLineUiState.fairMarketValue.isNotEmpty()) validateFloatFromString(
                string = currentLineUiState.fairMarketValue,
                onSuccess = { mutableLineUiState = mutableLineUiState.copy(
                    fairMarketValueHasError = false
                )},
                onFail = { mutableLineUiState = mutableLineUiState.copy(
                    fairMarketValueHasError = true
                )}
            ) else mutableLineUiState = mutableLineUiState.copy(
                fairMarketValueHasError = false
            )

            if (currentLineUiState.downPayment.isNotEmpty()) validateFloatFromString(
                string = currentLineUiState.downPayment,
                onSuccess = { mutableLineUiState = mutableLineUiState.copy(
                    downPaymentHasError = false
                )},
                onFail = { mutableLineUiState = mutableLineUiState.copy(
                    downPaymentHasError = true
                )}
            ) else mutableLineUiState = mutableLineUiState.copy(
                downPaymentHasError = false
            )
        }
        mutableListOfLineUiStates[index] = mutableLineUiState
        _uiState.update { it.copy(
                listOfLinesUiState = mutableListOfLineUiStates.toList()
        )}
        updateLines(index)
        enableSaveNewCxButton()
    }

    private fun validateFloatFromString(string: String, onSuccess: () -> Unit, onFail: () -> Unit) {
        try {
            string.toFloat()
            onSuccess()
        } catch (_: NumberFormatException) { onFail() }
    }

    private fun updateLines(index: Int) {
        if (checkForUiStateError()) {
            val mutableListOfLines = _uiState.value.listOfLines.toMutableList()
            var newLine = mutableListOfLines[index]
            val lineUiState = _uiState.value.listOfLinesUiState[index]

            if (lineUiState.isByod) newLine = newLine.copy(
                lineNumber = index,
                fullDeviceCost = 0f,
                promotionAmount = 0f,
                fairMarketValue = 0f,
                downPayment = 0f,
                hasP360 = lineUiState.hasP360,
                isByod = true
            ) else newLine = newLine.copy(
                lineNumber = index,
                fullDeviceCost = lineUiState.fullDeviceCost.toFloat(),

                promotionAmount = if (lineUiState.promotionAmount.isNotEmpty()) {
                    lineUiState.promotionAmount.toFloat()
                } else 0f,

                fairMarketValue = if (lineUiState.fairMarketValue.isNotEmpty()) {
                    lineUiState.fairMarketValue.toFloat()
                } else 0f,

                downPayment = if (lineUiState.downPayment.isNotEmpty()) {
                    lineUiState.downPayment.toFloat()
                } else 0f,

                hasP360 = lineUiState.hasP360,
                isByod = false
            )
            mutableListOfLines[index] = newLine
            _uiState.update { it.copy(
                    listOfLines = mutableListOfLines.toList()
            )}
        }
    }

    private fun checkForUiStateError(): Boolean {
        val hasError: MutableList<Boolean> = mutableListOf(_uiState.value.lineCountError)
        _uiState.value.listOfLinesUiState.forEach {
            hasError.add(it.fullDeviceCostHasError)
            hasError.add(it.promotionAmountHasError)
            hasError.add(it.fairMarketValueHasError)
            hasError.add(it.downPaymentHasError)
        }
        return hasError.all { !it }
    }

    private fun enableSaveNewCxButton() {
        val hasValue: MutableList<Boolean> = mutableListOf()
        _uiState.value.listOfLines.forEach {
            if (it.isByod || it.fullDeviceCost > 0f) hasValue.add(true) else hasValue.add(false)
        }
        if (hasValue.all { it }) _uiState.update {
            it.copy(saveNewCxButtonEnabled = checkForUiStateError())
        }
    }

    fun generateNewCxQuote() {
        _uiState.update { currentState ->
            currentState.copy(
                newCxQuote = currentState.ratePlan?.let { NewCxQuote(it, currentState.listOfLines) }
            )
        }
    }
}