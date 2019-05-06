package com.rnett.kframe.dom.input

import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayElement
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLTextAreaElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

open class BaseTextArea<S : BaseTextArea<S>>(
    val getValue: () -> String,
    val setValue: (String) -> Unit,
    val doSet: Boolean = true
) : IDataElement, DisplayElement<HTMLTextAreaElement, S>("textarea") {

    private var lastValue: String? = null

    private var _value: String
    val value get() = _value

    var rawValue by attributes.boxedValue<String>("value")

    var rows by attributes.boxedValue<Int>()
    var cols by attributes.boxedValue<Int>()

    var disabled by attributes.flagValue()
    var readonly by attributes.flagValue()

    final override fun update() {
        val value = getValue()

        if (lastValue == null) {
            lastValue = value
            displayChanges(value)
            return
        }

        if (lastValue != value) {
            lastValue = value
            displayChanges(value)
        }
    }

    fun displayChanges(newValue: String) {
        _value = newValue
        rawValue = _value
    }

    init {
        _value = getValue()
        displayChanges(_value)
    }

    open fun saveChanges() {

        if (!doSet)
            return

        val newValue = try {
            rawValue!!
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

class TextArea(
    getValue: () -> String,
    setValue: (String) -> Unit
) : BaseTextArea<TextArea>(
    getValue, setValue
)

class PropertyTextArea(
    val property: KMutableProperty0<String>
) : BaseTextArea<PropertyTextArea>(property::get, property::set)

@KframeDSL
inline fun DisplayHost.textArea(
    noinline getValue: () -> String, noinline setValue: (String) -> Unit,
    klass: String = "", id: String = "",
    builder: Builder<TextArea> = {}
): TextArea {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +TextArea(getValue, setValue)(klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.textArea(
    property: KMutableProperty0<String>,
    klass: String = "", id: String = "",
    builder: Builder<PropertyTextArea> = {}
): PropertyTextArea {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +PropertyTextArea(property)(klass, id, builder)
}

class TextAreaDelegate(
    parent: DisplayHost,
    initialValue: String,
    klass: String,
    id: String,
    builder: Builder<PropertyTextArea>
) : ReadWriteProperty<Any?, String> {
    var value: String = initialValue
    val textAreaElement = parent.textArea(this::value, klass, id, builder)

    override fun getValue(thisRef: Any?, property: KProperty<*>) = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.value = value
        textAreaElement.update()
    }
}

@KframeDSL
fun DisplayHost.textAreaDelegate(
    initialValue: String,
    klass: String = "", id: String = "",
    builder: Builder<PropertyTextArea> = {}
): TextAreaDelegate {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return TextAreaDelegate(this, initialValue, klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.optionalTextArea(
    noinline getValue: () -> String?, noinline setValue: (String?) -> Unit,
    klass: String = "", id: String = "",
    builder: Builder<TextArea> = {}
): TextArea {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +TextArea({ getValue() ?: "" }, { setValue(if (it.isBlank()) null else it) })(klass, id, builder)
}

class OptionalPropertyTextArea(
    val property: KMutableProperty0<String?>
) : BaseTextArea<OptionalPropertyTextArea>({ property.get() ?: "" }, property::set) {
    override fun saveChanges() {

        if (!doSet)
            return

        val newValue = try {
            if (rawValue.isNullOrBlank()) null else ""
        } catch (e: Exception) {
            underlying.setCustomValidity(e.toValidationError())
            underlying.reportValidity()
            return
        }

        underlying.setCustomValidity("")

        property.set(newValue)
        displayChanges(newValue ?: "")
    }
}

@KframeDSL
inline fun DisplayHost.optionalTextArea(
    property: KMutableProperty0<String?>,
    klass: String = "", id: String = "",
    builder: Builder<OptionalPropertyTextArea> = {}
): OptionalPropertyTextArea {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +OptionalPropertyTextArea(property)(klass, id, builder)
}

class OptionalTextAreaDelegate(
    parent: DisplayHost,
    initialValue: String? = null,
    klass: String,
    id: String,
    builder: Builder<OptionalPropertyTextArea>
) : ReadWriteProperty<Any?, String?> {
    var value: String? = initialValue
    val textAreaElement = parent.optionalTextArea(this::value, klass, id, builder)

    override fun getValue(thisRef: Any?, property: KProperty<*>) = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        this.value = value
        textAreaElement.update()
    }
}

@KframeDSL
fun DisplayHost.optionalTextAreaDelegate(
    initialValue: String? = null,
    klass: String = "", id: String = "",
    builder: Builder<OptionalPropertyTextArea> = {}
): OptionalTextAreaDelegate {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return OptionalTextAreaDelegate(this, initialValue, klass, id, builder)
}