package com.rnett.kframe.dom.bootstrap

import com.rnett.kframe.structure.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLOListElement
import org.w3c.dom.HTMLUListElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class ListElement<U : HTMLElement, S : ListElement<U, S>>(tag: String) : DisplayElement<U, S>(tag)

class OrderedList : ListElement<HTMLOListElement, OrderedList>("ol")
class UnorderedList : ListElement<HTMLUListElement, UnorderedList>("ul")

@KframeDSL
inline fun DisplayHost.ol(
    klass: String = "",
    id: String = "",
    builder: Builder<OrderedList> = {}
): OrderedList {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +OrderedList()(klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.ul(
    klass: String = "",
    id: String = "",
    builder: Builder<UnorderedList> = {}
): UnorderedList {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +UnorderedList()(klass, id, builder)
}

@KframeDSL
inline fun ListElement<*, *>.li(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLLIElement> = {}
): BasicDisplayElement<HTMLLIElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicDisplayElement<HTMLLIElement>("li")(klass, id, builder)
}