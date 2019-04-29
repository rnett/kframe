package com.rnett.kframe.structure.data

import com.rnett.kframe.structure.AnyElement

//TODO finish binding
class Binding<T, E : AnyElement>(
    val buildElement: (T) -> E,
    val condition: BindingCondition<T>,
    var currentElement: E? = null
) {
    fun update() {

        val (needsUpdate, value) = condition.check()

        if (needsUpdate) {
            currentElement?.remove()
            currentElement = buildElement(value)
        }
    }
}