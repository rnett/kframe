package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.bootstrap.core.ClassElement
import com.rnett.kframe.structure.percent
import org.w3c.dom.HTMLDivElement

class Progress : ClassElement<HTMLDivElement, Progress>("div", "progress") {
    val bar = +ProgressBar()

    var allStriped: Boolean
        get() = children.filterIsInstance<ProgressBar>().all { it.striped }
        set(v) {
            children.filterIsInstance<ProgressBar>().forEach { it.striped = v }
        }

    var allAnimated: Boolean
        get() = children.filterIsInstance<ProgressBar>().all { it.animated }
        set(v) {
            children.filterIsInstance<ProgressBar>().forEach { it.animated = v }
        }

}

class ProgressBar : ClassElement<HTMLDivElement, ProgressBar>("div", "progress-bar") {

    var useLabel: Boolean = false

    private var text = +""

    var ariaMultiplier: Double = 1.0

    init {
        aria.valuemin = 0.0
        aria.valuemax = 100.0 * ariaMultiplier
        aria.valuenow = 0.0
    }

    private var _width: Int = 0
    var width
        get() = _width
        set(v) {
            _width = v
            style.width = v.percent
            aria.valuenow = v * ariaMultiplier
            if (useLabel)
                text.value = "$v%"
        }

    var striped by classes.presentDelegate.withClass
    var animated by classes.presentDelegate.withClass

    init {
        width = 0
    }
}