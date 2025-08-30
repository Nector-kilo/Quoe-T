package com.example.quoe_t.newCx.business

data class NewCxQuote(private val ratePlan: RatePlan, private val listOfLines: List<Line>) {
    val monthlyBill = {
        val lineCount = listOfLines.size

        // Calculate for 3rd line free promo. Edit if statement when 3rd line free promo ends.
        val serviceCost = if (!ratePlan.data.isDiscountedRate && lineCount > 2) {
            ratePlan.data.ratePricing[lineCount - 2]
        } else ratePlan.data.ratePricing[lineCount - 1]

        var deviceCosts = 0f
        var p360Cost = 0f
        listOfLines.forEach {
            deviceCosts += it.monthlyDevicePaymentAfterPromo
            p360Cost += it.p360MonthlyPayment
        }

        serviceCost + deviceCosts + p360Cost
    }

    val oneTimeBillCredit = {
        var total = 0f
        listOfLines.forEach { total += it.fairMarketValue }
        total
    }
}