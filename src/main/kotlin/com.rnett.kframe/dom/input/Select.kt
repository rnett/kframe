package com.rnett.kframe.dom.input

import com.rnett.kframe.structure.*
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

@KframeDSL
inline fun DisplayHost.option(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLOptionElement> = {}
): BasicDisplayElement<HTMLOptionElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicDisplayElement<HTMLOptionElement>("option")(klass, id, builder)
}

class Select<T>(
    val options: List<T>, val displayValue: BasicDisplayElement<HTMLOptionElement>.(T) -> Unit,
    val getValue: () -> T, val setValue: (T) -> Unit
) : DisplayElement<HTMLSelectElement, Select<T>>("select"), IDataElement {

    var disabled by attributes.flagValue()
    var readonly by attributes.flagValue()

    val optionElements = options.map {
        option { displayValue(it) }
    }

    var _value: T = getValue()
    val value get() = _value

    var _index: Int = -1
    val index get() = _index

    override fun update() {
        displayChanges(getValue())
    }

    fun displayChanges(newValue: T) {
        _value = newValue

        _index = options.indexOf(_value)

        underlying.selectedIndex = _index
    }

    fun saveChanges() {
        _index = underlying.selectedIndex
        if (_index >= 0) {
            _value = options[_index]
            setValue(_value)
        }
        displayChanges(_value)
    }

    init {
        update()
    }
}

@KframeDSL
inline fun <T> DisplayHost.select(
    options: List<T>, noinline displayValue: BasicDisplayElement<HTMLOptionElement>.(T) -> Unit,
    noinline getValue: () -> T, noinline setValue: (T) -> Unit,
    klass: String = "", id: String = "",
    builder: Builder<Select<T>> = {}
): Select<T> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Select(options, displayValue, getValue, setValue)(klass, id, builder)
}

@KframeDSL
inline fun <T> DisplayHost.select(
    property: KMutableProperty0<T>,
    options: List<T>, noinline displayValue: BasicDisplayElement<HTMLOptionElement>.(T) -> Unit,
    klass: String = "", id: String = "",
    builder: Builder<Select<T>> = {}
): Select<T> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return select(options, displayValue, property::get, property::set, klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.selectString(
    property: KMutableProperty0<String>,
    options: List<String>, noinline displayValue: BasicDisplayElement<HTMLOptionElement>.(String) -> Unit = { +it },
    klass: String = "", id: String = "",
    builder: Builder<Select<String>> = {}
): Select<String> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return select(options, displayValue, property::get, property::set, klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.selectInt(
    property: KMutableProperty0<Int>,
    options: List<Int>, noinline displayValue: BasicDisplayElement<HTMLOptionElement>.(Int) -> Unit = { +"$it" },
    klass: String = "", id: String = "",
    builder: Builder<Select<Int>> = {}
): Select<Int> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return select(options, displayValue, property::get, property::set, klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.selectDouble(
    property: KMutableProperty0<Double>,
    options: List<Double>, noinline displayValue: BasicDisplayElement<HTMLOptionElement>.(Double) -> Unit = { +"$it" },
    klass: String = "", id: String = "",
    builder: Builder<Select<Double>> = {}
): Select<Double> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return select(options, displayValue, property::get, property::set, klass, id, builder)
}

class SelectDelegate<T>(
    parent: DisplayHost,
    options: List<T>,
    initialValue: T = options.first(),
    displayValue: BasicDisplayElement<HTMLOptionElement>.(T) -> Unit,
    klass: String, id: String,
    builder: Builder<Select<T>>
) : ReadWriteProperty<Any?, T> {
    var value: T = initialValue
    val selectElement = parent.select(this::value, options, displayValue, klass, id, builder)

    override fun getValue(thisRef: Any?, property: KProperty<*>) = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
        selectElement.update()
    }
}

@KframeDSL
fun <T> DisplayHost.selectDelegate(
    initialValue: T,
    options: List<T>, displayValue: BasicDisplayElement<HTMLOptionElement>.(T) -> Unit,
    klass: String = "", id: String = "",
    builder: Builder<Select<T>> = {}
): SelectDelegate<T> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return SelectDelegate(this, options, initialValue, displayValue, klass, id, builder)
}

@KframeDSL
fun DisplayHost.selectStringDelegate(
    initialValue: String,
    options: List<String>, displayValue: BasicDisplayElement<HTMLOptionElement>.(String) -> Unit = { +it },
    klass: String = "", id: String = "",
    builder: Builder<Select<String>> = {}
): SelectDelegate<String> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return selectDelegate(initialValue, options, displayValue, klass, id, builder)
}

@KframeDSL
fun DisplayHost.selectIntDelegate(
    initialValue: Int,
    options: List<Int>, displayValue: BasicDisplayElement<HTMLOptionElement>.(Int) -> Unit = { +"$it" },
    klass: String = "", id: String = "",
    builder: Builder<Select<Int>> = {}
): SelectDelegate<Int> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return selectDelegate(initialValue, options, displayValue, klass, id, builder)
}

@KframeDSL
fun DisplayHost.selectDoubleDelegate(
    initialValue: Double,
    options: List<Double>, displayValue: BasicDisplayElement<HTMLOptionElement>.(Double) -> Unit = { +"$it" },
    klass: String = "", id: String = "",
    builder: Builder<Select<Double>> = {}
): SelectDelegate<Double> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return selectDelegate(initialValue, options, displayValue, klass, id, builder)
}

class OptionalSelect<T>(
    val options: List<T>, val displayValue: BasicDisplayElement<HTMLOptionElement>.(T) -> Unit,
    val getValue: () -> T?, val setValue: (T) -> Unit
) : DisplayElement<HTMLSelectElement, OptionalSelect<T>>("select"), IDataElement {

    var disabled by attributes.flagValue()
    var readonly by attributes.flagValue()

    val optionElements = options.map {
        option { displayValue(it) }
    }

    var _value: T? = getValue()
    val value get() = _value

    var _index: Int = -1
    val index get() = _index

    override fun update() {
        displayChanges(getValue())
    }

    fun displayChanges(newValue: T?) {
        _value = newValue

        _index = _value?.let { options.indexOf(it) } ?: -1

        underlying.selectedIndex = _index
    }

    fun saveChanges() {
        _index = underlying.selectedIndex

        if (_index >= 0)
            _value = options[_index]
        else
            _value = null

        _value?.let(setValue)

        displayChanges(_value)
    }

    init {
        update()
    }
}

@KframeDSL
inline fun <T> DisplayHost.optionalSelect(
    options: List<T>, noinline displayValue: BasicDisplayElement<HTMLOptionElement>.(T) -> Unit,
    noinline getValue: () -> T?, noinline setValue: (T) -> Unit,
    klass: String = "", id: String = "",
    builder: Builder<OptionalSelect<T>> = {}
): OptionalSelect<T> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +OptionalSelect(options, displayValue, getValue, setValue)(klass, id, builder)
}

@KframeDSL
inline fun <T> DisplayHost.optionalSelect(
    property: KMutableProperty0<T?>,
    options: List<T>, noinline displayValue: BasicDisplayElement<HTMLOptionElement>.(T) -> Unit,
    klass: String = "", id: String = "",
    builder: Builder<OptionalSelect<T>> = {}
): OptionalSelect<T> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return optionalSelect(options, displayValue, property::get, property::set, klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.optionalSelectString(
    property: KMutableProperty0<String?>,
    options: List<String>, noinline displayValue: BasicDisplayElement<HTMLOptionElement>.(String) -> Unit = { +it },
    klass: String = "", id: String = "",
    builder: Builder<OptionalSelect<String>> = {}
): OptionalSelect<String> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return optionalSelect(options, displayValue, property::get, property::set, klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.optionalSelectInt(
    property: KMutableProperty0<Int?>,
    options: List<Int>, noinline displayValue: BasicDisplayElement<HTMLOptionElement>.(Int) -> Unit = { +"$it" },
    klass: String = "", id: String = "",
    builder: Builder<OptionalSelect<Int>> = {}
): OptionalSelect<Int> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return optionalSelect(options, displayValue, property::get, property::set, klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.optionalSelectDouble(
    property: KMutableProperty0<Double?>,
    options: List<Double>, noinline displayValue: BasicDisplayElement<HTMLOptionElement>.(Double) -> Unit = { +"$it" },
    klass: String = "", id: String = "",
    builder: Builder<OptionalSelect<Double>> = {}
): OptionalSelect<Double> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return optionalSelect(options, displayValue, property::get, property::set, klass, id, builder)
}


class OptionalSelectDelegate<T>(
    parent: DisplayHost,
    options: List<T>,
    initialValue: T? = null,
    displayValue: BasicDisplayElement<HTMLOptionElement>.(T) -> Unit,
    klass: String, id: String,
    builder: Builder<OptionalSelect<T>>
) : ReadWriteProperty<Any?, T?> {
    var value: T? = initialValue
    val selectElement = parent.optionalSelect(this::value, options, displayValue, klass, id, builder)

    override fun getValue(thisRef: Any?, property: KProperty<*>) = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.value = value
        selectElement.update()
    }
}

@KframeDSL
fun <T> DisplayHost.optionalSelectDelegate(
    vararg options: T,
    initialValue: T? = null, displayValue: BasicDisplayElement<HTMLOptionElement>.(T) -> Unit,
    klass: String = "", id: String = "",
    builder: Builder<OptionalSelect<T>> = {}
): OptionalSelectDelegate<T> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return OptionalSelectDelegate(this, options.toList(), initialValue, displayValue, klass, id, builder)
}

@KframeDSL
fun DisplayHost.optionalSelectStringDelegate(
    vararg options: String,
    initialValue: String? = null, displayValue: BasicDisplayElement<HTMLOptionElement>.(String) -> Unit = { +it },
    klass: String = "", id: String = "",
    builder: Builder<OptionalSelect<String>> = {}
): OptionalSelectDelegate<String> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return optionalSelectDelegate(
        options = *options,
        initialValue = initialValue,
        displayValue = displayValue,
        klass = klass,
        id = id,
        builder = builder
    )
}

@KframeDSL
fun DisplayHost.optionalSelectIntDelegate(
    vararg options: Int,
    initialValue: Int? = null, displayValue: BasicDisplayElement<HTMLOptionElement>.(Int) -> Unit = { +"$it" },
    klass: String = "", id: String = "",
    builder: Builder<OptionalSelect<Int>> = {}
): OptionalSelectDelegate<Int> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return optionalSelectDelegate(
        options = *options.toTypedArray(),
        initialValue = initialValue,
        displayValue = displayValue,
        klass = klass,
        id = id,
        builder = builder
    )
}

@KframeDSL
fun DisplayHost.optionalSelectDoubleDelegate(
    vararg options: Double,
    initialValue: Double? = null, displayValue: BasicDisplayElement<HTMLOptionElement>.(Double) -> Unit = { +"$it" },
    klass: String = "", id: String = "",
    builder: Builder<OptionalSelect<Double>> = {}
): OptionalSelectDelegate<Double> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return optionalSelectDelegate(
        options = *options.toTypedArray(),
        initialValue = initialValue,
        displayValue = displayValue,
        klass = klass,
        id = id,
        builder = builder
    )
}