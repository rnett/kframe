package com.rnett.kframe

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LazyInitDelegate<T>(val initialize: () -> T) : ReadWriteProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value == null)
            value = initialize()
        return value!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

fun <T> lazyInit(initialize: () -> T) = LazyInitDelegate(initialize)
