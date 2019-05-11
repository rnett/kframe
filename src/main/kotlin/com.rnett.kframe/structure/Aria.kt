package com.rnett.kframe.structure

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Aria(val element: AnyElement) {
    var srOnly
        get() = element.srOnly
        set(v) {
            element.srOnly = v
        }

    var role
        get() = element.role
        set(v) {
            element.role = v
        }

    inner class StringDelegate(val key: String?) : ReadWriteProperty<Any?, String?> {

        fun name(property: KProperty<*>) = "aria-${key ?: property.name}"

        override fun getValue(thisRef: Any?, property: KProperty<*>) = element.attributes[name(property)]?.raw

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            element.attributes[name(property)] = value
        }

    }

    inner class BooleanDelegate(val key: String?) : ReadWriteProperty<Any?, Boolean?> {

        fun name(property: KProperty<*>) = "aria-${key ?: property.name}"

        override fun getValue(thisRef: Any?, property: KProperty<*>) =
            element.attributes[name(property)]?.raw?.toBoolean()

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean?) {
            element.attributes[name(property)] = value?.toString()
        }

    }

    inner class IntDelegate(val key: String?) : ReadWriteProperty<Any?, Int?> {

        fun name(property: KProperty<*>) = "aria-${key ?: property.name}"

        override fun getValue(thisRef: Any?, property: KProperty<*>) =
            element.attributes[name(property)]?.raw?.toInt()

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int?) {
            element.attributes[name(property)] = value?.toString()
        }

    }

    inner class DoubleDelegate(val key: String?) : ReadWriteProperty<Any?, Double?> {

        fun name(property: KProperty<*>) = "aria-${key ?: property.name}"

        override fun getValue(thisRef: Any?, property: KProperty<*>) =
            element.attributes[name(property)]?.raw?.toDouble()

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double?) {
            element.attributes[name(property)] = value?.toString()
        }

    }

    val str get() = StringDelegate(null)
    fun str(key: String?) = StringDelegate(key)

    val bool get() = BooleanDelegate(null)
    fun bool(key: String?) = BooleanDelegate(key)

    val int get() = IntDelegate(null)
    fun int(key: String?) = IntDelegate(key)

    val double get() = DoubleDelegate(null)
    fun double(key: String?) = DoubleDelegate(key)


    var activedescendant by str
    var atomic by bool
    var autocomplete by str

    var busy by bool
    var checked by bool

    var colcount by int
    var colindex by int
    var colspan by int

    var controls by str
    var current by str
    var describedby by str
    var details by str

    var disabled by bool

    var dropeffect by str
    var errormessage by str

    var expanded by bool

    var flowto by str
    var grabbed by bool
    var haspopup by bool

    var hidden by bool

    var invalid by str

    var keyshortcuts by str

    var label by str

    var labelledby by str

    var level by int

    var live by str

    var modal by bool

    var multiline by bool

    var multiselectable by bool

    var orientation by str

    var owns by str

    var placeholder by str

    var posinset by int

    var pressed by bool

    var readonly by bool

    var relevant by str

    var required by bool

    var roledescription by str

    var rowcount by int
    var rowindex by int
    var rowspan by int

    var selected by bool

    var setsize by int

    var sort by str

    var valuemax by double

    var valuemin by double

    var valuenow by double

    var valuetext by str
}

//TODO set in all Bootstrap elements
