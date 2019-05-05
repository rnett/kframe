package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.AElement
import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayElement
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLSpanElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Badge(val type: ContextType) : DisplayElement<HTMLSpanElement, Badge>("badge") {
    init {
        classes += "badge"
        classes += type.klass("badge")
    }

    fun pill() {
        classes += "badge-pill"
    }
}

@KframeDSL
inline fun DisplayHost.badge(
    type: ContextType,
    id: String = "",
    builder: Builder<Badge> = {}
): Badge {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Badge(type)(id = id)(builder)
}

fun AElement.asBadge(type: ContextType) {
    classes += "badge"
    classes += type.klass("badge")
}