package com.rnett.kframe.dom.input

import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayElement
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLInputElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

abstract class BaseCheckbox<S : BaseCheckbox<S>>(
    val getValue: () -> Boolean,
    val setValue: (Boolean) -> Unit,
    val doSet: Boolean = true
) : IDataElement, DisplayElement<HTMLInputElement, S>("input") {

    private var lastValue: Boolean? = null

    private var _value: Boolean
    val value get() = _value

    var type by attributes.boxedValue<String>()

    init {
        type = "checkbox"
    }

    var disabled by attributes.flagValue()
    var readonly by attributes.flagValue()

    final override fun update() {
        val value = getValue()

        if (lastValue == null) {
            lastValue = value
            displayChanges(value)
            return
        }

        if (value != lastValue) {
            lastValue = value
            displayChanges(value)
        }
    }

    fun displayChanges(newValue: Boolean) {
        _value = newValue
        underlying.checked = newValue
    }

    init {
        _value = getValue()
        displayChanges(_value)
    }

    fun saveChanges() {

        if (!doSet)
            return

        val newValue = try {
            underlying.checked
        } catch (e: Exception) {
            underlying.setCustomValidity(e.toValidationError())
            underlying.reportValidity()
            return
        }

        underlying.setCustomValidity("")

        setValue(newValue)
        displayChanges(newValue)
    }

    init {
        if (doSet) {
            on.change {
                saveChanges()
            }
        }
        update()
    }
}

class Checkbox(getValue: () -> Boolean, setValue: (Boolean) -> Unit) : BaseCheckbox<Checkbox>(getValue, setValue)

@KframeDSL
fun DisplayHost.checkbox(
    getValue: () -> Boolean, setValue: (Boolean) -> Unit,
    klass: String = "", id: String = "",
    builder: Builder<Checkbox> = {}
): Checkbox {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Checkbox(getValue, setValue)(klass, id, builder)
}

class PropertyCheckbox(val property: KMutableProperty0<Boolean>) :
    BaseCheckbox<PropertyCheckbox>(property::get, property::set)

@KframeDSL
inline fun DisplayHost.checkbox(
    property: KMutableProperty0<Boolean>,
    klass: String = "", id: String = "",
    builder: Builder<PropertyCheckbox> = {}
): PropertyCheckbox {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +PropertyCheckbox(property)(klass, id, builder)
}


class CheckboxDelegate(
    parent: DisplayHost,
    initialValue: Boolean,
    klass: String,
    id: String,
    builder: Builder<PropertyCheckbox>
) : ReadWriteProperty<Any?, Boolean> {

    var value: Boolean = initialValue
    val checkboxElement = parent.checkbox(this::value, klass, id, builder)

    override fun getValue(thisRef: Any?, property: KProperty<*>) = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        this.value = value
    }
}

@KframeDSL
fun DisplayHost.checkboxDelegate(
    initialValue: Boolean,
    klass: String = "", id: String = "",
    builder: Builder<PropertyCheckbox> = {}
): CheckboxDelegate {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return CheckboxDelegate(this, initialValue, klass, id, builder)
}