package com.rnett.kframe.dom.input

import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayElement
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import com.rnett.kframe.structure.data.EqualityCheck
import org.w3c.dom.HTMLInputElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

interface IDataElement {
    fun update()
}

class ValidationException(val customMessage: String) : RuntimeException(customMessage)

fun Exception.toValidationError(): String =
    if (this is ValidationException)
        this.customMessage
    else
        "${this::class}: $message"

open class BaseInputElement<T, R, S : BaseInputElement<T, R, S>>(
    val getValue: () -> T,
    val setValue: (T) -> Unit,
    val equalityCheck: EqualityCheck<R>,
    val parseValue: (String) -> T,
    val displayValue: (T) -> String,
    val doSet: Boolean = true
) : IDataElement, DisplayElement<HTMLInputElement, S>("input") {

    private var lastValue: R? = null

    private var _value: T
    val value get() = _value

    var rawValue by attributes.boxedValue<String>("value")

    var type by attributes.boxedValue<String>()

    var disabled by attributes.flagValue()
    var readonly by attributes.flagValue()

    final override fun update() {
        val value = getValue()

        if (lastValue == null) {
            lastValue = equalityCheck.same(lastValue, value).second
            displayChanges(value)
            return
        }

        val (same, newValue) = equalityCheck.same(lastValue!!, value)

        if (!same) {
            lastValue = newValue
            displayChanges(value)
        }
    }

    fun displayChanges(newValue: T) {
        _value = newValue
        rawValue = displayValue(_value)
    }

    init {
        _value = getValue()
        displayChanges(_value)
    }

    fun saveChanges() {

        if (!doSet)
            return

        val newValue = try {
            parseValue(rawValue!!)
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

class InputElement<T, R>(
    getValue: () -> T,
    setValue: (T) -> Unit,
    equalityCheck: EqualityCheck<R>,
    parseValue: (String) -> T,
    displayValue: (T) -> String
) : BaseInputElement<T, R, InputElement<T, R>>(
    getValue, setValue, equalityCheck, parseValue, displayValue
)

@KframeDSL
inline fun <T> DisplayHost.input(
    noinline getValue: () -> T, noinline setValue: (T) -> Unit,
    noinline parseValue: (String) -> T,
    noinline displayValue: (T) -> String = { it.toString() },
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<InputElement<T, *>> = {}
): InputElement<T, *> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +InputElement(
        getValue, setValue,
        equalityCheck,
        parseValue, displayValue
    )(klass, id, builder)
}

open class PropertyInputElement<T, R>(
    val property: KMutableProperty0<T>,
    equalityCheck: EqualityCheck<R>,
    parseValue: (String) -> T,
    displayValue: (T) -> String
) : BaseInputElement<T, R, PropertyInputElement<T, R>>(
    property::get, property::set,
    equalityCheck, parseValue, displayValue
)

@KframeDSL
inline fun <T> DisplayHost.input(
    property: KMutableProperty0<T>,
    noinline parseValue: (String) -> T,
    noinline displayValue: (T) -> String = { it.toString() },
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<T, *>> = {}
): PropertyInputElement<T, *> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +PropertyInputElement(
        property,
        equalityCheck,
        parseValue, displayValue
    )(klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.stringInput(
    property: KMutableProperty0<String>,
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<String, *>> = {}
): PropertyInputElement<String, *> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return input(property, { it }, { it }, equalityCheck, klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.intInput(
    property: KMutableProperty0<Int>,
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<Int, *>> = {}
): PropertyInputElement<Int, *> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return input(property, { it.toInt() }, { it.toString() }, equalityCheck, klass, id) {
        type = "number"
        builder()
    }
}

@KframeDSL
inline fun DisplayHost.doubleInput(
    property: KMutableProperty0<Double>,
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<Double, *>> = {}
): PropertyInputElement<Double, *> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return input(property, { it.toDouble() }, { it.toString() }, equalityCheck, klass, id) {
        type = "number"
        builder()
    }
}

@KframeDSL
inline fun <T> DisplayHost.optionalInput(
    property: KMutableProperty0<T?>,
    noinline parseValue: (String) -> T,
    noinline displayValue: (T) -> String = { it.toString() },
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<T?, *>> = {}
): PropertyInputElement<T?, *> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return input(
        property,
        { if (it.isBlank()) null else parseValue(it) },
        { it?.let(displayValue) ?: "" },
        equalityCheck,
        klass,
        id,
        builder
    )
}

@KframeDSL
inline fun DisplayHost.optionalStringInput(
    property: KMutableProperty0<String?>,
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<String?, *>> = {}
): PropertyInputElement<String?, *> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return input(property, { if (it.isBlank()) null else it }, { it ?: "" }, equalityCheck, klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.optionalIntInput(
    property: KMutableProperty0<Int?>,
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<Int?, *>> = {}
): PropertyInputElement<Int?, *> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return input(
        property,
        { if (it.isBlank()) null else it.toInt() },
        { it?.toString() ?: "" },
        equalityCheck,
        klass,
        id
    ) {
        type = "number"
        builder()
    }
}

@KframeDSL
inline fun DisplayHost.optionalDoubleInput(
    property: KMutableProperty0<Double?>,
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<Double?, *>> = {}
): PropertyInputElement<Double?, *> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return input(
        property,
        { if (it.isBlank()) null else it.toDouble() },
        { it?.toString() ?: "" },
        equalityCheck,
        klass,
        id
    ) {
        type = "number"
        builder()
    }
}

class InputDelegate<T>(
    initialValue: T,
    parent: DisplayHost,
    equalityCheck: EqualityCheck<*>,
    parseValue: (String) -> T,
    displayValue: (T) -> String,
    klass: String, id: String,
    builder: Builder<PropertyInputElement<T, *>>
) : ReadWriteProperty<Any?, T> {
    var value: T = initialValue
    val inputElement = parent.input(this::value, parseValue, displayValue, equalityCheck, klass, id, builder)

    override fun getValue(thisRef: Any?, property: KProperty<*>) = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
        inputElement.update()
    }
}

@KframeDSL
fun <T> DisplayHost.inputDelegate(
    initialValue: T,
    parseValue: (String) -> T,
    displayValue: (T) -> String = { it.toString() },
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<T, *>> = {}
): InputDelegate<T> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return InputDelegate(initialValue, this, equalityCheck, parseValue, displayValue, klass, id, builder)
}

@KframeDSL
fun DisplayHost.stringInputDelegate(
    initialValue: String = "",
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<String, *>> = {}
): InputDelegate<String> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return inputDelegate(initialValue, { it }, { it }, equalityCheck, klass, id, builder)
}

@KframeDSL
fun DisplayHost.intInputDelegate(
    initialValue: Int = 0,
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<Int, *>> = {}
): InputDelegate<Int> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return inputDelegate(initialValue, { it.toInt() }, { it.toString() }, equalityCheck, klass, id) {
        type = "number"
        builder()
    }
}

@KframeDSL
fun DisplayHost.doubleInputDelegate(
    initialValue: Double = 0.0,
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<Double, *>> = {}
): InputDelegate<Double> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return inputDelegate(initialValue, { it.toDouble() }, { it.toString() }, equalityCheck, klass, id) {
        type = "number"
        builder()
    }
}

@KframeDSL
fun <T> DisplayHost.optionalInputDelegate(
    parseValue: (String) -> T,
    displayValue: (T) -> String,
    initialValue: T? = null,
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<T?, *>> = {}
): InputDelegate<T?> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return inputDelegate(
        initialValue,
        { if (it.isBlank()) null else parseValue(it) },
        { it?.let(displayValue) ?: "" },
        equalityCheck,
        klass,
        id,
        builder
    )
}

@KframeDSL
fun DisplayHost.optionalStringInputDelegate(
    initialValue: String? = null,
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<String?, *>> = {}
): InputDelegate<String?> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return optionalInputDelegate({ it }, { it }, initialValue, equalityCheck, klass, id, builder)
}

@KframeDSL
fun DisplayHost.optionalIntInputDelegate(
    initialValue: Int? = null,
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<Int?, *>> = {}
): InputDelegate<Int?> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return optionalInputDelegate({ it.toInt() }, { it.toString() }, initialValue, equalityCheck, klass, id) {
        type = "number"
        builder()
    }
}

@KframeDSL
fun DisplayHost.optionalDoubleInputDelegate(
    initialValue: Double? = null,
    equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
    klass: String = "", id: String = "",
    builder: Builder<PropertyInputElement<Double?, *>> = {}
): InputDelegate<Double?> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return optionalInputDelegate({ it.toDouble() }, { it.toString() }, initialValue, equalityCheck, klass, id) {
        type = "number"
        builder()
    }
}