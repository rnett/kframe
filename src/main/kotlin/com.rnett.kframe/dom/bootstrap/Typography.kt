package com.rnett.kframe.dom.bootstrap

import com.rnett.kframe.structure.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLHeadingElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Heading(val level: Int) : DisplayElement<HTMLHeadingElement, Heading>("h$level")

@KframeDSL
inline fun DisplayHost.h(
    level: Int,
    klass: String = "", id: String = "",
    builder: Builder<Heading> = {}
): Heading {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return +Heading(level)(klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.small(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLElement> = {}
): BasicDisplayElement<HTMLElement> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return +BasicDisplayElement<HTMLElement>("small")(klass, id)(builder)
}

inline fun Heading.secondaryText(builder: BasicDisplayBuilder<HTMLElement>): BasicDisplayElement<HTMLElement> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return small {
        classes += "text-muted"
        builder()
    }
}

fun DisplayElement<*, *>.asHeading(level: Int) {
    classes.add("h$level")
}

@KframeDSL
inline fun DisplayHost.displayHeading(
    size: Int,
    id: String = "",
    builder: Builder<Heading> = {}
): Heading {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return +Heading(1)(id = id) {
        classes += "display-$size"
        builder()
    }
}