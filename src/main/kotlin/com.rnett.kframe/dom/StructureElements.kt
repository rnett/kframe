package com.rnett.kframe.dom

import com.rnett.kframe.structure.*
import org.w3c.dom.*

@KframeDSL
fun DisplayHost.div(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLDivElement> = {}
) = +BasicDisplayElement<HTMLDivElement>("div")(klass, id)(builder)

@KframeDSL
fun DisplayHost.button(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLButtonElement> = {}
) = +BasicDisplayElement<HTMLButtonElement>("button")(klass, id)(builder)

@KframeDSL
fun DisplayHost.span(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLSpanElement> = {}
) = +BasicDisplayElement<HTMLSpanElement>("span")(klass, id)(builder)

class ImageElement : DisplayElement<HTMLImageElement, ImageElement>("img") {
    var src by attributes.boxedValue<String>()

    var height by attributes.boxedValue<Int>()
    var width by attributes.boxedValue<Int>()
}

@KframeDSL
fun DisplayHost.img(
    klass: String = "", id: String = "",
    builder: Builder<ImageElement> = {}
) = +ImageElement()(klass, id)(builder)

@KframeDSL
fun DisplayHost.img(
    src: String,
    klass: String = "", id: String = "",
    builder: Builder<ImageElement> = {}
) = +ImageElement()(klass, id) {
    this.src = src
}

@KframeDSL
fun DisplayHost.img(
    src: String,
    height: Int, width: Int,
    klass: String = "", id: String = "",
    builder: Builder<ImageElement> = {}
) = +ImageElement()(klass, id) {
    this.src = src
    this.height = height
    this.width = width
}

class AElement : DisplayElement<HTMLAnchorElement, AElement>("a") {
    var href by attributes.boxedValue<String>()
    var download by attributes.boxedValue<String>()
    var type by attributes.boxedValue<String>()
}

@KframeDSL
fun DisplayHost.a(
    klass: String = "", id: String = "",
    builder: Builder<AElement> = {}
) = +AElement()(klass, id)(builder)

@KframeDSL
fun DisplayHost.a(
    href: String,
    klass: String = "", id: String = "",
    builder: Builder<AElement> = {}
) = +AElement()(klass, id) {
    this.href = href
    builder()
}