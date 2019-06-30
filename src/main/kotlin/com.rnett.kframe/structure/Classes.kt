package com.rnett.kframe.structure

import com.rnett.kframe.dom.bootstrap.core.IHasClass
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Classes(val classes: MutableSet<String> = mutableSetOf(), val element: Element<*, *>) :
    MutableSet<String> by classes {

    override fun add(element: String): Boolean {
        val klass = element.trim()
        return if (" " in klass)
            addAll(klass.split(" ").filter { it.isNotBlank() })
        else {
            if (classes.add(klass)) {
                this.element.underlying.classList.add(klass)
                true
            } else
                false
        }
    }

    override fun addAll(elements: Collection<String>): Boolean {
        return elements.all { add(it) }
    }

    override fun clear() {
        classes.clear()
        element.underlying.className = ""
    }

    override fun removeAll(elements: Collection<String>): Boolean = elements.all { remove(it) }

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

        init {
            this@Classes.add(toClass(this.value))
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
        add(klass)
    }

    operator fun minusAssign(klass: String) {
        remove(klass)
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