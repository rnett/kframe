package com.rnett.kframe.structure

interface View<in P : AnyElementHost, out E : AnyElement> {
    fun P.build(): E
}

interface DisplayView<out E : DisplayElement<*, *>> : View<DisplayHost, E>

