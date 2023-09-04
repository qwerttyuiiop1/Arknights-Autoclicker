package com.example.arknightsautoclicker.processing.tasks.base

import com.example.arknightsautoclicker.processing.components.UIGroup
import com.example.arknightsautoclicker.processing.io.Clicker

class DormUIBinding(
    val clicker: Clicker,
) {
    fun setPos(x: Int, y: Int) {
        TODO()
    }
    inner class Summary: UIGroup {

    }
    inner class Select: UIGroup {

    }
    val summary = Summary()
    val select = Select()
}