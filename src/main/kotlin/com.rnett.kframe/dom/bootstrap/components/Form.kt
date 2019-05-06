package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.div
import com.rnett.kframe.dom.input.*
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayElement
import com.rnett.kframe.structure.Element
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLDivElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KMutableProperty0

class FormGroup : DisplayElement<HTMLDivElement, FormGroup>("div") {
    override fun addChild(child: Element<*, *>) {
        when (child) {
            is BaseInputElement<*, *, *> -> child.classes += "form-control"
            is BaseTextArea<*> -> child.classes += "form-control"
            is Select<*> -> classes += "custom-select"
            is OptionalSelect<*> -> classes += "custom-select"
            is BaseCheckbox<*> -> classes += "form-check-input"
        }
    }

    init {
        classes += "form-group"
    }
}

fun BaseInputElement<*, *, *>.large() {
    classes += "form-control-lg"
}

fun BaseInputElement<*, *, *>.small() {
    classes += "form-control-sm"
}

fun BaseTextArea<*>.large() {
    classes += "form-control-lg"
}

fun BaseTextArea<*>.small() {
    classes += "form-control-sm"
}

fun Select<*>.large() {
    classes += "custom-select-lg"
}

fun Select<*>.small() {
    classes += "custom-select-sm"
}

fun BaseInputElement<Int, *, *>.asRange(min: Int = 0, max: Int = 100, step: Int = 1) {
    classes += "custom-range"
    type = "range"
    attributes["min"] = min
    attributes["max"] = max
    attributes["step"] = step
}

fun BaseInputElement<Int, *, *>.asRange(range: IntRange) = asRange(range.first, range.last, range.step)

fun BaseInputElement<Double, *, *>.asRange(min: Double = 0.0, max: Double = 100.0, step: Double = 1.0) {
    classes += "custom-range"
    type = "range"
    attributes["min"] = min
    attributes["max"] = max
    attributes["step"] = step
}

fun BaseInputElement<Double, *, *>.asRange(range: ClosedFloatingPointRange<Double>, step: Double = 1.0) =
    asRange(range.start, range.endInclusive, step)

@KframeDSL
fun FormGroup.bsCheckbox(
    property: KMutableProperty0<Boolean>,
    label: String,
    klass: String = "", id: String = "",
    builder: Builder<PropertyCheckbox> = {}
): PropertyCheckbox {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val result: PropertyCheckbox
    div("custom-control custom-checkbox") {
        result = checkbox(property, klass, id) {
            classes += "custom-control-input"
        }
        result.withLabel(label) {
            classes += "custom-control-label"
        }
    }
    return result
}

@KframeDSL
fun FormGroup.bsCheckboxDelegate(
    initialValue: Boolean,
    label: String,
    klass: String = "", id: String = "",
    builder: Builder<PropertyCheckbox> = {}
): CheckboxDelegate {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val result: CheckboxDelegate
    div("custom-control custom-checkbox") {
        result = checkboxDelegate(initialValue, klass, id) {
            classes += "custom-control-input"
        }
        result.checkboxElement.withLabel(label) {
            classes += "custom-control-label"
        }
    }
    return result
}

//TODO radio buttons