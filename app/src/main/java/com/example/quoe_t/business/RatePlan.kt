package com.example.quoe_t.business

data class RatePlanData(
    val rateName: String,
    val ratePricing: List<Int>,
    val isDiscountedRate: Boolean
)

sealed class RatePlan {
    data object EmptyRatePlan : RatePlan()
    data object ExperienceMore : RatePlan()
    data object ExperienceBeyond : RatePlan()
    data object ExperienceMoreMilRes : RatePlan()
    data object ExperienceBeyondMilRes : RatePlan()

    val data: RatePlanData
        get() = when (this) {
            EmptyRatePlan -> RatePlanData(
                rateName = "",
                ratePricing = listOf(0),
                isDiscountedRate = false
            )

            ExperienceMore -> RatePlanData(
                rateName = "Experience More",
                ratePricing = listOf(85, 140, 170, 200, 230),
                isDiscountedRate = false
            )

            ExperienceBeyond -> RatePlanData(
                rateName = "Experience Beyond",
                ratePricing = listOf(100, 170, 215, 260, 305),
                isDiscountedRate = false
            )

            ExperienceMoreMilRes -> RatePlanData(
                rateName = "Experience More Military",
                ratePricing = listOf(70, 100, 120, 140, 160),
                isDiscountedRate = true
            )

            ExperienceBeyondMilRes -> RatePlanData(
                rateName = "Experience Beyond Military",
                ratePricing = listOf(85, 130, 165, 200, 235),
                isDiscountedRate = true
            )
        }

    companion object {
        fun getAllRatePlans(): List<RatePlan> {
            return listOf(
                ExperienceMore,
                ExperienceBeyond,
                ExperienceMoreMilRes,
                ExperienceBeyondMilRes
            )
        }
    }
}