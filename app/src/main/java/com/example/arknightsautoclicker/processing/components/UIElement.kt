package com.example.arknightsautoclicker.processing.components

/**
 * this interface does nothing and is just here for semantic purposes
 * all pieces of code assuming something
 * (ex: the position of a label, or the color / size of a button)
 * out of the UI should be in a UIElement
 */
interface UIElement

/**
 * a group of UI elements
 */
interface UIGroup: UIElement