package com.example.quoe_t

import kotlinx.serialization.Serializable

class Routes {
    @Serializable
    object HomeScreen

    @Serializable
    object NewCxNavigation

    @Serializable
    object NewCxDataScreen

    @Serializable
    object NewCxQuoteScreen

    //TODO Add UpgradeScreen route.
}