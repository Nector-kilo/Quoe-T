package com.example.quoe_t.newCx.business

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

    init { promotionAmount = promotionAmount - fairMarketValue }

    val totalDeviceBalance = fullDeviceCost - downPayment

    private val deviceBalanceAfterPromoMath: Float = round(
        (totalDeviceBalance - promotionAmount) * 100) / 100
    val deviceBalanceAfterPromo: Float = if (deviceBalanceAfterPromoMath == -0.01f) {
        0f
    } else deviceBalanceAfterPromoMath

    private val monthlyDevicePaymentAfterPromoMath: Float = round(
        ((totalDeviceBalance - promotionAmount) / 24) * 100) / 100
    val monthlyDevicePaymentAfterPromo: Float = if (monthlyDevicePaymentAfterPromoMath == -0f) {
        0f
    } else monthlyDevicePaymentAfterPromoMath

    // Very rough estimate of monthly Protection 360 cost.
    val p360MonthlyPayment = if (hasP360) when {
        fullDeviceCost == 0f -> 18f
        fullDeviceCost in (0f..300f) -> 9f
        fullDeviceCost in (300f..600f) -> 16f
        fullDeviceCost in (600f..1600f) -> 18f
        fullDeviceCost > 1600f -> 25f
        else -> 0f
    } else 0f
}