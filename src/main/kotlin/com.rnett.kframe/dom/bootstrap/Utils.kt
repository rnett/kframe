package com.rnett.kframe.dom.bootstrap

import com.rnett.kframe.structure.BasicDisplayBuilder
import com.rnett.kframe.structure.BasicDisplayElement
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLDivElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

enum class ContextType {
    Primary, Secondary, Success, Danger, Warning, Info, Light, Dark;

    fun klass(starter: String) = "$starter-${this.name.toLowerCase()}"
}


@KframeDSL
inline fun DisplayHost.container(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLDivElement> = {}
): BasicDisplayElement<HTMLDivElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicDisplayElement<HTMLDivElement>("div")("$klass container", id, builder)
}

@KframeDSL
inline fun DisplayHost.containerFluid(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLDivElement> = {}
): BasicDisplayElement<HTMLDivElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicDisplayElement<HTMLDivElement>("div")("$klass container-fluid", id, builder)
}
