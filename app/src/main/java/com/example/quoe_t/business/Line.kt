package com.example.quoe_t.business

import kotlin.math.round


data class Line (
    val lineNumber: Int,
    val fullDeviceCost: Float = 0f,
    var promotionAmount: Float = 0f,
    val fairMarketValue: Float = 0f,
    val downPayment: Float = 0f,
    val hasP360: Boolean = false,
    val isByod: Boolean = false
) {

    init {
        promotionAmount -= fairMarketValue
    }

    val deviceBalance = fullDeviceCost - downPayment

    private val deviceBalanceAfterPromoMath: Float = round((deviceBalance - promotionAmount) * 100) / 100
    val deviceBalanceAfterPromo: Float = if (deviceBalanceAfterPromoMath == -0.01f) 0f else deviceBalanceAfterPromoMath

    private val monthlyDevicePaymentAfterPromoMath: Float = round(((deviceBalance - promotionAmount) / 24) * 100) / 100
    val monthlyDevicePaymentAfterPromo: Float = if (monthlyDevicePaymentAfterPromoMath == -0f) 0f else monthlyDevicePaymentAfterPromoMath

    // Very rough estimate of monthly Protection 360 cost. Update if true pricing is ever made public.
    val p360MonthlyPayment = if (hasP360) {
        when {
            fullDeviceCost == 0f -> 18f
            fullDeviceCost <= 300f && fullDeviceCost > 0f -> 9f
            fullDeviceCost <= 600f && fullDeviceCost > 300f -> 16f
            fullDeviceCost <= 1600f && fullDeviceCost > 600f -> 18f
            fullDeviceCost > 1600f -> 25f
            else -> 0f
        }
    } else {
        0f
    }
}