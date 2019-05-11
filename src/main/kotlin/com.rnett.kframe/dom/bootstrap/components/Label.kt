package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.structure.*
import com.rnett.kframe.structure.addons.`$`
import com.rnett.kframe.structure.addons.get
import com.rnett.kframe.structure.addons.id
import org.w3c.dom.HTMLLabelElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Label(target: String) : DisplayElement<HTMLLabelElement, Label>("label") {
    var target by attributes.boxedValue<String>("for")

    init {
        this.target = target
        Document[`$`.id(target)].forEach {
            //aria.labelledby = this.idOrRandom //TODO this will use random even if id is set in creation method
        }
    }
}

@KframeDSL
inline fun DisplayHost.label(
    target: String,
    klass: String = "", id: String = "",
    builder: Builder<Label> = {}
): Label {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Label(target)(klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.label(
    target: String, text: String,
    klass: String = "", id: String = "",
    builder: Builder<Label> = {}
): Label {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Label(target)(klass, id) {
        +text
        builder()
    }
}

@KframeDSL
inline fun DisplayHost.label(
    target: Element<*, *>,
    klass: String = "", id: String = "",
    builder: Builder<Label> = {}
): Label {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val l = +Label(target.idOrRandom)(klass, id, builder)
    target.aria.labelledby = l.idOrRandom
    return l
}

@KframeDSL
inline fun DisplayHost.label(
    target: Element<*, *>, text: String,
    klass: String = "", id: String = "",
    builder: Builder<Label> = {}
): Label {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +label(target, klass, id) {
        +text
        builder()
    }
}

@KframeDSL
inline fun Element<*, *>.withLabel(builder: Builder<Label> = {}) =
    (parent!! as DisplayHost).label(this, builder = builder)


@KframeDSL
inline fun Element<*, *>.withLabel(text: String, builder: Builder<Label> = {}) =
    (parent!! as DisplayHost).label(this, text = text, builder = builder)
