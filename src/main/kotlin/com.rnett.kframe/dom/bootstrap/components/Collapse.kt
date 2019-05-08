package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.Element
import com.rnett.kframe.structure.KframeDSL
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class CollapseButton(val targetId: String, type: ContextType?, outline: Boolean = false) :
    BaseButton<CollapseButton>(type, outline) {
    init {
        data.toggle = "collapse"
        data.target = "#$targetId"
    }
}

@KframeDSL
inline fun DisplayHost.collapseButton(
    targetId: String,
    type: ContextType?, outline: Boolean = false,
    klass: String = "", id: String = "",
    builder: Builder<CollapseButton> = {}
): CollapseButton {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +CollapseButton(targetId, type, outline)(klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.collapseButton(
    target: Element<*, *>,
    type: ContextType?, outline: Boolean = false,
    klass: String = "", id: String = "",
    builder: Builder<CollapseButton> = {}
): CollapseButton {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    target.classes += "collapse"
    return +CollapseButton(target.idOrRandom, type, outline)(klass, id, builder)
}

fun Element<*, *>.makeCollapsible() {
    classes += "collapse"
}