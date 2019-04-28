package com.rnett.kframe.structure

import org.w3c.dom.get
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Style(private val rules: MutableMap<String, Value>, val element: Element<*, *>) : Attributes.Value() {
    abstract class Value {
        abstract val raw: String

        data class Raw(override val raw: String) : Value()
    }

    operator fun get(key: String) = rules[key]
    operator fun set(key: String, value: Value?) {
        if (value == null)
            rules.remove(key)
        else
            rules[key] = value

        element.underlying.attributes["style"]?.value = raw
    }

    fun <T : Value> getValue(key: String) =
        this[key] as? T

    inner class ValueDelegate<T : Value>(val key: String?) : ReadWriteProperty<Any?, T?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return getValue(key ?: property.name)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            this@Style[key ?: property.name] = value
        }
    }

    operator fun <T : Value> provideDelegate(
        thisRef: Any?,
        prop: KProperty<*>
    ) = ValueDelegate<T>(prop.name)

    fun <T : Value> value(key: String) = ValueDelegate<T>(key)
    fun <T: Value> value() = ValueDelegate<T>(null)

    fun toRaw(): String = rules.entries.joinToString(";") { "${it.key}: ${it.value}" }
    override val raw get() = toRaw()
}

enum class SizeUnit {
    percent, cm, em, rem, ex, `in`, mm, pc, pt, px, vh, vw, vmin, raw;


    val unit = if (name == "raw") "" else if (name == "percent") "%" else name.trim('`')

    companion object {
        val inch = `in`

        operator fun get(value: String) = when (value.trim('s')) {
            "%" -> percent
            "in" -> `in`
            "percent" -> percent
            "inches" -> inch
            "" -> raw
            else -> if (value.isBlank()) raw else values().find { it.unit == value.trim('s') } ?: raw
        }

        operator fun invoke(value: String) = get(value)
    }
}


data class Size(val length: Number, val units: SizeUnit) : Style.Value() {
    override val raw: String
        get() = length.toString() + units.toString()

    companion object {
        fun Char.isLetterOrPercent() = this == '%' || (this.toString().match("[a-zA-z]")?.isNotEmpty() ?: false)
    }

    constructor(sizeAndUnits: String) : this(
        sizeAndUnits.dropLastWhile { it.isLetterOrPercent() }.toDouble(),
        SizeUnit[sizeAndUnits.takeLastWhile { it.isLetterOrPercent() }]
    )

}

val Number.px get() = Size(this, SizeUnit.px)
val Number.rem get() = Size(this, SizeUnit.rem)
val Number.percent get() = Size(this, SizeUnit.percent)
val Number.cm get() = Size(this, SizeUnit.cm)
val Number.em get() = Size(this, SizeUnit.em)
val Number.ex get() = Size(this, SizeUnit.ex)
val Number.inch get() = Size(this, SizeUnit.inch)
val Number.mm get() = Size(this, SizeUnit.mm)
val Number.pc get() = Size(this, SizeUnit.pc)
val Number.pt get() = Size(this, SizeUnit.pt)
val Number.vh get() = Size(this, SizeUnit.vh)
val Number.vw get() = Size(this, SizeUnit.vw)
val Number.vmin get() = Size(this, SizeUnit.vmin)