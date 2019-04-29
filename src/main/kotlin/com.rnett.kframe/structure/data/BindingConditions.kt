package com.rnett.kframe.structure.data

import kotlin.reflect.KProperty0

@DslMarker
annotation class BindingDSL

typealias AnyBinding<T> = () -> T
typealias BoolBinding = () -> Boolean

sealed class BindingCondition<T> {
    abstract fun check(): Pair<Boolean, T>
}

sealed class EqualityCheck {
    abstract fun <T> same(a: T, b: T): Boolean

    object HashCode : EqualityCheck() {
        override fun <T> same(a: T, b: T) = a.hashCode() == b.hashCode()
    }

    object ToString : EqualityCheck() {
        override fun <T> same(a: T, b: T) = a.toString() == b.toString()
    }

    object ReferenceEquality : EqualityCheck() {
        override fun <T> same(a: T, b: T) = a === b
    }

    data class CombinedCheck(val checks: Set<EqualityCheck>) : Set<EqualityCheck> by checks, EqualityCheck() {
        override fun <T> same(a: T, b: T) = checks.all { it.same(a, b) }
    }

    operator fun plus(other: EqualityCheck) =
        if (other is CombinedCheck)
            CombinedCheck(other.checks + this)
        else
            CombinedCheck(setOf(other, this))
}

data class BooleanBindingCondition(val needsUpdate: () -> Boolean) : BindingCondition<Unit>() {
    override fun check() = needsUpdate() to Unit

}

data class FunctionBindingCondition<T>(val func: AnyBinding<T>, val equalityCheck: EqualityCheck) :
    BindingCondition<T>() {

    private var lastValue: T? = null

    override fun check(): Pair<Boolean, T> {
        val value = func()

        if (lastValue == null) {
            lastValue = value
            return false to value
        }

        return if (equalityCheck.same(value, lastValue))
            false to value
        else {
            lastValue = value
            true to value
        }
    }
}

data class PropertiesBindingCondition(val props: List<KProperty0<*>>, val equalityCheck: EqualityCheck) :
    BindingCondition<List<*>>() {

    private var lastValues: List<*>? = null

    override fun check(): Pair<Boolean, List<*>> {
        val value = props.map { it.get() }

        if (lastValues == null) {
            lastValues = value
            return false to value
        }

        return if (value.zip(lastValues!!).all { equalityCheck.same(it.first, it.second) })
            false to value
        else {
            lastValues = value
            true to value
        }
    }
}

data class PropertyBindingCondition<T>(val prop: KProperty0<T>, val equalityCheck: EqualityCheck) :
    BindingCondition<T>() {

    private var lastValue: T? = null

    override fun check(): Pair<Boolean, T> {
        val value = prop.get()

        if (lastValue == null) {
            lastValue = value
            return false to value
        }

        return if (equalityCheck.same(value, lastValue))
            false to value
        else {
            lastValue = value
            true to value
        }
    }
}