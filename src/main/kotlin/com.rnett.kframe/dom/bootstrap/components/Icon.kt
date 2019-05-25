package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.bootstrap.core.ClassElement
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Icon(val icon: String) : ClassElement<HTMLElement, Icon>("i", "material-icon") {
    init {
        +icon
    }
}

@KframeDSL
inline fun DisplayHost.icon(
    icon: String,
    klass: String = "", id: String = "",
    builder: Builder<Icon> = {}
): Icon {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Icon(icon)(klass, id, builder)
}