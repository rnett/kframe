package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.structure.*
import com.rnett.kframe.structure.Element
import org.w3c.dom.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class ListGroupElement<U : HTMLElement, S : ListGroupElement<U, S>>(tag: String) : DisplayElement<U, S>(tag) {
    init {
        classes += "list-group"
    }

    var dense by classes.presentDelegate("bmd-list-group-sm")

    fun Element<*, *>.asItem() {
        this.classes += "list-group-item"
    }
}

class OrderedListGroup : ListGroupElement<HTMLOListElement, OrderedListGroup>("ol")
class UnorderedListGroup : ListGroupElement<HTMLUListElement, UnorderedListGroup>("ul")

@KframeDSL
inline fun DisplayHost.orderedListGroup(
    klass: String = "",
    id: String = "",
    builder: Builder<OrderedListGroup> = {}
): OrderedListGroup {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +OrderedListGroup()(klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.unorderedListGroup(
    klass: String = "",
    id: String = "",
    builder: Builder<UnorderedListGroup> = {}
): UnorderedListGroup {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +UnorderedListGroup()(klass, id, builder)
}

class ListGroupItem : DisplayElement<HTMLLIElement, ListGroupItem>("li") {
    init {
        classes += "list-group-item"
    }

    private var _active = false
    var active: Boolean
        get() = _active
        set(v) {
            _active = v
            if (v) {
                classes.add("active")
            } else
                classes.remove("active")
        }

    private var _disabled = false
    var disabled: Boolean
        get() = _disabled
        set(v) {
            _disabled = v
            if (v) {
                classes.add("disabled")
            } else
                classes.remove("disabled")
        }

    private var _flush = false
    var flush: Boolean
        get() = _flush
        set(v) {
            _flush = v
            if (v) {
                classes.add("list-group-flush")
            } else
                classes.remove("list-group-flush")
        }

    private var _horizontal = false
    var horizontal: Boolean
        get() = _horizontal
        set(v) {
            _horizontal = v
            if (v) {
                classes.add("list-group-horizontal")
            } else
                classes.remove("list-group-horizontal")
        }

    fun Element<*, *>.forBadges() {
        classes += listOf("d-flex", "justify-content-between", "align-items-center")
    }

    fun Element<*, *>.asItem(action: Boolean = true, context: ContextType? = null) {
        this.classes.removeAll { it.startsWith("btn") }
        classes += "list-group-item"

        if (action)
            classes += "list-group-item-action"

        if (context != null)
            classes += context.klass("list-group-item")
    }

}

@KframeDSL
inline fun ListGroupElement<*, *>.item(
    context: ContextType? = null,
    klass: String = "", id: String = "",
    builder: Builder<ListGroupItem> = {}
): ListGroupItem {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +ListGroupItem()(klass, id) {

        if (context != null)
            classes += context.klass("list-group-item")

        builder()
    }
}

class TwoPartListGroupItem : DisplayElement<HTMLDivElement, TwoPartListGroupItem>("div") {
    init {
        classes += "bmd-list-group-col"
    }
}

@KframeDSL
inline fun DisplayHost.itemComponents(
    klass: String = "", id: String = "",
    builder: Builder<TwoPartListGroupItem> = {}
): TwoPartListGroupItem {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +TwoPartListGroupItem()(klass, id, builder)
}

@KframeDSL
inline fun TwoPartListGroupItem.heading(
    text: String = "",
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLHeadingElement> = {}
): BasicDisplayElement<HTMLHeadingElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicDisplayElement<HTMLHeadingElement>("p")(klass, id) {
        classes += "list-group-item-heading"

        if (text.isNotBlank())
            +text

        builder()
    }
}

@KframeDSL
inline fun TwoPartListGroupItem.text(
    text: String = "",
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLHeadingElement> = {}
): BasicDisplayElement<HTMLHeadingElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicDisplayElement<HTMLHeadingElement>("p")(klass, id) {
        classes += "list-group-item-text"

        if (text.isNotBlank())
            +text

        builder()
    }
}