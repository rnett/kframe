package com.rnett.kframe.structure

import com.rnett.kframe.dom.bootstrap.core.IHasClass
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

    val style: Style
    val classes: Classes

    init {
        style = Style(mutableMapOf(), element)
        this["style"] = style

        classes = Classes(element = element)
        this["class"] = classes
    }

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

class Classes(val classes: MutableSet<String> = mutableSetOf(), val element: Element<*, *>) :
    MutableSet<String> by classes, Attributes.Value() {
    override val raw get() = classes.joinToString(" ")

    override fun add(element: String) =
        if (" " in element)
            addAll(element.split(" "))
        else {
            if (classes.add(element)) {
                this.element.underlying.classList.add(element)
                true
            } else
                false
        }

    override fun clear() {
        classes.clear()
        element.underlying.className = ""
    }

    override fun remove(element: String) =
        if (classes.remove(element)) {
            this.element.underlying.classList.remove(element)
            true
        } else
            false

    inner class PresentDelegate internal constructor(val klass: String?, val prefix: String? = null) :
        ReadWriteProperty<Any?, Boolean> {


        val KProperty<*>.key get() = if (prefix == null) (klass ?: name) else "$prefix-${(klass ?: name)}"

        override fun getValue(thisRef: Any?, property: KProperty<*>) = property.key in this@Classes

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            if (value)
                this@Classes.add(property.key)
            else
                this@Classes.remove(property.key)
        }
    }

    val presentDelegate get() = PresentDelegate(null)
    fun presentDelegate(klass: String?, prefix: String? = null) = PresentDelegate(klass, prefix)

    inner class ClassDelegate<T> internal constructor(val toClass: (T) -> String, initialValue: T) :
        ReadWriteProperty<Any?, T> {

        var value: T = initialValue

        override fun getValue(thisRef: Any?, property: KProperty<*>) = value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this@Classes.remove(toClass(this.value))
            this@Classes.add(toClass(value))
            this.value = value
        }
    }

    fun <T> classDelegate(initialValue: T, toClass: (T) -> String) = ClassDelegate(toClass, initialValue)

    inner class OptionalClassDelegate<T> internal constructor(val toClass: (T) -> String?, initialValue: T?) :
        ReadWriteProperty<Any?, T?> {

        var value: T? = initialValue

        override fun getValue(thisRef: Any?, property: KProperty<*>) = value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            if (this.value != null)
                this@Classes.remove(toClass(this.value!!))

            if (value != null)
                toClass(value)?.let(this@Classes::add)
            this.value = value
        }
    }

    fun <T> optionalClassDelegate(initialValue: T? = null, toClass: (T) -> String?) =
        OptionalClassDelegate(toClass, initialValue)

    fun <T : IHasClass> classDelegate(initialValue: T) =
        classDelegate(initialValue, { it.klass })

    fun <T : IHasClass> optionalClassDelegate(initialValue: T? = null) =
        optionalClassDelegate(initialValue, { it.klass })

    operator fun plusAssign(klass: String) {
        classes += klass
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as Classes

        if (classes != other.classes) return false

        return true
    }

    override fun hashCode(): Int {
        return classes.hashCode()
    }
}