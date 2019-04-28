package com.rnett.kframe.structure

import org.w3c.dom.get
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Attributes(private val attributes: MutableMap<String, Value>, val element: Element<*, *>){
    abstract class Value {
        abstract val raw: String

        data class Raw(override val raw: String) : Value()
        data class Box<T>(val value: T): Value(){
            override val raw = value.toString()
        }
    }

    operator fun get(key: String) = attributes[key]
    operator fun set(key: String, value: Value?) {
        if(value == null) {
            attributes.remove(key)
            element.underlying.attributes.removeNamedItem(key)
        } else {
            attributes[key] = value
            element.underlying.attributes[key]?.value = value.raw
        }


    }

    fun <T : Value> getValue(key: String) =
        this[key] as? T

    inner class ValueDelegate<T: Value>(val key: String?): ReadWriteProperty<Any?, T?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return getValue(key ?: property.name)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            this@Attributes[key ?: property.name] = value
        }
    }

    inner class BoxValueDelegate<T>(val key: String?): ReadWriteProperty<Any?, T?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return getValue<Value.Box<T>>(key ?: property.name)?.value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {

            if(value == null)
                this@Attributes[key ?: property.name] = null
            else
                this@Attributes[key ?: property.name] = Value.Box(value)
        }
    }

    operator fun <T: Value> provideDelegate(
        thisRef: Any?,
        prop: KProperty<*>
    ) = ValueDelegate<T>(prop.name)

    fun <T: Value> value(key: String) = ValueDelegate<T>(key)
    fun <T: Value> value() = ValueDelegate<T>(null)

    fun <T> boxedValue(key: String) = BoxValueDelegate<T>(key)
    fun <T> boxedValue() = BoxValueDelegate<T>(null)

    val style: Style
    val classes: Classes

    init{
        style = Style(mutableMapOf(), element)
        this["style"] = style

        classes = Classes()
        this["class"] = classes
    }
}

class Classes(val classes: MutableSet<String> = mutableSetOf()): MutableSet<String> by classes, Attributes.Value() {
    override val raw get() = classes.joinToString(" ")
}