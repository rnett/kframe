package com.rnett.kframe.structure.data

import com.rnett.kframe.structure.AnyElement
import com.rnett.kframe.structure.AnyElementHost
import com.rnett.kframe.structure.View
import kotlin.reflect.KProperty0

open class Binding<T, E : AnyElement>(
    val buildElement: (T) -> E,
    val condition: BindingCondition<T>,
    currentElement: E? = null
) {

    private var currentElement = currentElement ?: buildElement(condition.check().second)

    fun refresh() {

        val (needsUpdate, value) = condition.check()

        if (needsUpdate) {
            currentElement.remove()
            currentElement = buildElement(value)
        }
    }
}

class ViewBinding<V : View<P, E>, E : AnyElement, P : AnyElementHost>(
    val parent: P,
    val view: V,
    val equalityCheck: EqualityCheck<*> = EqualityCheck.HashCode
) : Binding<V, E>(
    {
        view.run {
            parent.build()
        }
    },
    FunctionBindingCondition({ view }, equalityCheck)
)

class ViewPropertyBinding<V : View<P, E>, E : AnyElement, P : AnyElementHost>(
    val parent: P,
    val view: KProperty0<V>,
    val equalityCheck: EqualityCheck<*> = EqualityCheck.Equality
) : Binding<V, E>(
    {
        view.get().run {
            parent.build()
        }
    },
    PropertyBindingCondition(view, equalityCheck)
)
