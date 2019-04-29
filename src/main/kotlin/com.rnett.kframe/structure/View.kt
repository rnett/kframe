package com.rnett.kframe.structure

interface View<in P : ElementHost, out E : AnyElement> {
    fun P.build(): E
    val watch: Boolean get() = false
}

interface DisplayView<out E : DisplayElement<*, *>> : View<DisplayHost, E>

interface DataView<in P : ElementHost, out E : AnyElement> : View<P, E> {
    override val watch get() = true
}

interface DataDisplayView<out E : DisplayElement<*, *>> : DataView<DisplayHost, E>

