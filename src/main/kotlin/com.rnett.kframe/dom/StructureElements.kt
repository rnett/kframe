package com.rnett.kframe.dom

import com.rnett.kframe.structure.*
import org.w3c.dom.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KframeDSL
inline fun DisplayHost.div(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLDivElement> = {}
): BasicDisplayElement<HTMLDivElement> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return +BasicDisplayElement<HTMLDivElement>("div")(klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.button(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLButtonElement> = {}
): BasicDisplayElement<HTMLButtonElement> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return +BasicDisplayElement<HTMLButtonElement>("button")(klass, id)(builder)
}

@KframeDSL
inline fun DisplayHost.span(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLSpanElement> = {}
): BasicDisplayElement<HTMLSpanElement> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return +BasicDisplayElement<HTMLSpanElement>("span")(klass, id)(builder)
}


class ImageElement : DisplayElement<HTMLImageElement, ImageElement>("img") {
    var src by attributes.boxedValue<String>()

    var height by attributes.boxedValue<Int>()
    var width by attributes.boxedValue<Int>()
}

@KframeDSL
inline fun DisplayHost.img(
    klass: String = "", id: String = "",
    builder: Builder<ImageElement> = {}
): ImageElement {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return +ImageElement()(klass, id)(builder)
}

@KframeDSL
inline fun DisplayHost.img(
    src: String,
    klass: String = "", id: String = "",
    builder: Builder<ImageElement> = {}
): ImageElement {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return +ImageElement()(klass, id) {
        this.src = src
        builder()
    }
}

@KframeDSL
inline fun DisplayHost.img(
    src: String,
    height: Int, width: Int,
    klass: String = "", id: String = "",
    builder: Builder<ImageElement> = {}
): ImageElement {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return +ImageElement()(klass, id) {
        this.src = src
        this.height = height
        this.width = width
        builder()
    }
}

class AElement : DisplayElement<HTMLAnchorElement, AElement>("a") {
    var href by attributes.boxedValue<String>()
    var download by attributes.boxedValue<String>()
    var type by attributes.boxedValue<String>()
}

@KframeDSL
inline fun DisplayHost.a(
    href: String? = null,
    klass: String = "", id: String = "",
    builder: Builder<AElement> = {}
): AElement {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return +AElement()(klass, id) {
        if (href != null)
            this.href = href
        builder()
    }
}

@KframeDSL
inline fun DisplayHost.p(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLParagraphElement> = {}
): BasicDisplayElement<HTMLParagraphElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicDisplayElement<HTMLParagraphElement>("p")(klass, id)(builder)
}