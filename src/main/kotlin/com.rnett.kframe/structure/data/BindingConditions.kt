package com.rnett.kframe.structure.data

import kotlin.reflect.KProperty0

@DslMarker
annotation class BindingDSL

typealias AnyBinding<T> = () -> T
typealias BoolBinding = () -> Boolean

sealed class BindingCondition<T> {
    abstract fun check(): Pair<Boolean, T>
}

sealed class EqualityCheck<R> {
    abstract fun <T> same(saved: R?, new: T): Pair<Boolean, R>

    object Equality : EqualityCheck<Any>() {
        override fun <T> same(saved: Any?, new: T) = (saved == new) to new as Any
    }

    object HashCode : EqualityCheck<Int>() {
        override fun <T> same(saved: Int?, new: T): Pair<Boolean, Int> {
            val bh = new.hashCode()
            return (saved == bh) to bh
        }
    }

    object ToString : EqualityCheck<String>() {
        override fun <T> same(saved: String?, new: T): Pair<Boolean, String> {
            val str = new.toString()
            return (saved == str) to str
        }
    }

    object ReferenceEquality : EqualityCheck<Any>() {
        override fun <T> same(saved: Any?, new: T) = (saved == new) to new as Any
    }

    data class CombinedCheck(val checks: List<EqualityCheck<Any>>) : List<EqualityCheck<Any>> by checks,
        EqualityCheck<List<Any>>() {
        override fun <T> same(saved: List<Any>?, new: T): Pair<Boolean, List<Any>> {

            if (saved == null)
                return false to checks.map { it.same(null, new).second }

            val res = saved.zip(checks).map { (s, c) -> c.same(s, new) }
            return res.all { it.first } to res.map { it.second }
        }
    }

    operator fun <T : Any> plus(other: EqualityCheck<T>) =
        if (other is CombinedCheck)
            CombinedCheck(other.checks + (this as EqualityCheck<Any>))
        else
            CombinedCheck(listOf(other as EqualityCheck<Any>, (this as EqualityCheck<Any>)))
}

data class BooleanBindingCondition(val needsUpdate: () -> Boolean) : BindingCondition<Unit>() {
    override fun check() = needsUpdate() to Unit
}

@BindingDSL
fun watch(needsUpdate: () -> Boolean) = BooleanBindingCondition(needsUpdate)

//TODO not sure if this will work with references, may need to store the hashcode/tostring when using w/ the same value
data class FunctionBindingCondition<T, R>(val func: AnyBinding<T>, val equalityCheck: EqualityCheck<R>) :
    BindingCondition<T>() {

    private var lastValue: R? = null

    override fun check(): Pair<Boolean, T> {
        val value = func()

        if (lastValue == null) {
            lastValue = equalityCheck.same(null, value).second
            return false to value
        }

        val (same, newValue) = equalityCheck.same(lastValue!!, value)

        return if (same)
            false to value
        else {
            lastValue = newValue
            true to value
        }
    }
}

@BindingDSL
fun <T> watch(condition: () -> T, equalityCheck: EqualityCheck<*> = EqualityCheck.HashCode) =
    FunctionBindingCondition(condition, equalityCheck)

data class PropertiesBindingCondition<R>(val props: List<KProperty0<*>>, val equalityCheck: EqualityCheck<R>) :
    BindingCondition<List<*>>() {

    private var lastValues: List<R>? = null

    override fun check(): Pair<Boolean, List<*>> {
        val value = props.map { it.get() }

        if (lastValues == null) {
            lastValues = value.map { equalityCheck.same(null, it).second }
            return false to value
        }

        val values = value.zip(lastValues!!).map { equalityCheck.same(it.second, it.first) }

        val same = values.all { it.first }
        val newValues = values.map { it.second }

        return if (same)
            false to value
        else {
            lastValues = newValues
            true to value
        }
    }
}

@BindingDSL
fun <T> watch(equalityCheck: EqualityCheck<*> = EqualityCheck.Equality, vararg properties: KProperty0<*>) =
    PropertiesBindingCondition(properties.toList(), equalityCheck)

data class PropertyBindingCondition<T, R>(val prop: KProperty0<T>, val equalityCheck: EqualityCheck<R>) :
    BindingCondition<T>() {

    private var lastValue: R? = null

    override fun check(): Pair<Boolean, T> {
        val value = prop.get()

        if (lastValue == null) {
            lastValue = equalityCheck.same(null, value).second
            return false to value
        }

        val (same, newValue) = equalityCheck.same(lastValue, value)

        return if (same)
            false to value
        else {
            lastValue = newValue
            true to value
        }
    }
}

@BindingDSL
fun <T> watch(property: KProperty0<T>, equalityCheck: EqualityCheck<*> = EqualityCheck.Equality) =
    PropertyBindingCondition(property, equalityCheck)

@BindingDSL
fun <T> KProperty0<T>.watch(equalityCheck: EqualityCheck<*> = EqualityCheck.Equality) =
    PropertyBindingCondition(this, equalityCheck)

data class ValueBindingCondition<T, R>(val value: T, val equalityCheck: EqualityCheck<R>) : BindingCondition<T>() {

    override fun check(): Pair<Boolean, T> {
        if (lastValue == null) {
            lastValue = equalityCheck.same(null, value).second
            return false to value
        }

        val (same, newValue) = equalityCheck.same(lastValue!!, value)

        return if (same)
            false to value
        else {
            lastValue = newValue
            true to value
        }
    }

    private var lastValue: R? = null
}

@BindingDSL
fun <T> watch(value: T, equalityCheck: EqualityCheck<*> = EqualityCheck.HashCode) =
    ValueBindingCondition(value, equalityCheck)

@BindingDSL
fun <T> T.watch(equalityCheck: EqualityCheck<*> = EqualityCheck.HashCode) =
    ValueBindingCondition(this, equalityCheck)
