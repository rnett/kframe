package com.rnett.kframe.structure

import kotlin.collections.set
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Attributes(private val attributes: MutableMap<String, Value>, val element: Element<*, *>) {
    abstract class Value {
        abstract val raw: String

        data class Raw(override val raw: String) : Value()
        data class Box<T>(val value: T) : Value() {
            override val raw = value.toString()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class.js != other::class.js) return false

            other as Value

            if (raw != other.raw) return false

            return true
        }

        override fun hashCode(): Int {
            return raw.hashCode()
        }
    }

    object Present : Value() {
        override val raw = ""
    }

    operator fun get(key: String) = attributes[key]
    operator fun set(key: String, value: Value?) { //TODO make sure this adds attributes
        if (value == null) {
            attributes.remove(key)
            element.underlying.removeAttribute(key)
        } else {
            attributes[key] = value

            if (value == Present)
                element.underlying.setAttribute(key, key)
            else
                element.underlying.setAttribute(key, value.raw)
        }


    }

    fun remove(key: String) {
        attributes.remove(key)
        element.underlying.removeAttribute(key)
    }

    operator fun set(key: String, value: String?) = set(key, value?.let { Value.Box(it) })
    operator fun set(key: String, value: Int?) = set(key, value?.let { Value.Box(it) })
    operator fun set(key: String, value: Double?) = set(key, value?.let { Value.Box(it) })

    fun <T : Value> getValue(key: String) =
        this[key] as? T

    inner class ValueDelegate<T : Value>(val key: String?) : ReadWriteProperty<Any?, T?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return getValue(key ?: property.name)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            this@Attributes[key ?: property.name] = value
        }
    }

    inner class BoxValueDelegate<T>(val key: String?) : ReadWriteProperty<Any?, T?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return getValue<Value.Box<T>>(key ?: property.name)?.value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {

            if (value == null)
                this@Attributes.remove(key ?: property.name)
            else
                this@Attributes[key ?: property.name] = Value.Box(value)
        }
    }

    inner class FlagValueDelegate(val key: String?) : ReadWriteProperty<Any?, Boolean> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return (key ?: property.name) in attributes
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {

            this@Attributes[key ?: property.name] = if (value) Present else null
        }
    }

    fun <T : Value> value(key: String) = ValueDelegate<T>(key)
    fun <T : Value> value() = ValueDelegate<T>(null)

    fun <T> boxedValue(key: String) = BoxValueDelegate<T>(key)
    fun <T> boxedValue() = BoxValueDelegate<T>(null)

    fun flagValue(key: String? = null) = FlagValueDelegate(key)

    operator fun invoke(builder: Attributes.() -> Unit) = builder()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as Attributes

        if (attributes != other.attributes) return false

        return true
    }

    override fun hashCode(): Int {
        return attributes.hashCode()
    }

    operator fun contains(s: String): Boolean = s in attributes

}

