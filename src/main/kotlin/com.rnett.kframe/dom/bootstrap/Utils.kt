package com.rnett.kframe.dom.bootstrap

import com.rnett.kframe.structure.*
import org.w3c.dom.HTMLDivElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

enum class ContextType {
    Primary, Secondary, Success, Danger, Warning, Info, Light, Dark;

    fun klass(starter: String) = "$starter-${this.name.toLowerCase()}"
}

enum class TextAlignment {
    Left, Right, Center, Justify;
}

fun DisplayElement<*, *>.textAlign(alignment: TextAlignment) {
    classes += "text-${alignment.name.toLowerCase()}"
}

fun DisplayElement<*, *>.mutedText() {
    classes += "text-muted"
}

fun DisplayElement<*, *>.textColor(type: ContextType) {
    classes += type.klass("text")
}

fun DisplayElement<*, *>.backgroundColor(type: ContextType) {
    classes += type.klass("bg")
}

fun DisplayElement<*, *>.transparentBackground() {
    classes += "bg-transparent"
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
