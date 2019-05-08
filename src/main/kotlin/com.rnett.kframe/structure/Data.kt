package com.rnett.kframe.structure

import org.w3c.dom.DOMStringMap
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun DOMStringMap.removeKey(key: String) {
    this.asDynamic().removeDataAttr(key)
}

class Data(val element: Element<*, *>) {
    operator fun provideDelegate(
        thisRef: Any?,
        prop: KProperty<*>
    ) = DataDelegate(prop.name)

    inner class DataDelegate(val name: String?) : ReadWriteProperty<Any?, String?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>) = this@Data[name ?: property.name]

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            if (value == null)
                this@Data.element.underlying.dataset.removeKey(name ?: property.name)
            else
                this@Data[name ?: property.name] = value
        }

    }

    operator fun get(key: String) = element.underlying.dataset[key]
    operator fun set(key: String, value: String) {
        element.underlying.dataset[key] = value
    }

    var target by this
    var dismiss by this
    var toggle by this

}