package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.*
import com.rnett.kframe.dom.bootstrap.Heading
import com.rnett.kframe.dom.bootstrap.h
import com.rnett.kframe.structure.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLParagraphElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Card : DisplayElement<HTMLDivElement, Card>("div") {
    init {
        classes += "card"
    }
}

@KframeDSL
inline fun Card.topImage(src: String, builder: Builder<ImageElement> = {}): ImageElement {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return img(src = src, klass = "card-img-top", builder = builder)
}

@KframeDSL
inline fun Card.image(src: String, builder: Builder<ImageElement> = {}): ImageElement {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return img(src = src, klass = "card-img", builder = builder)
}

@KframeDSL
inline fun Card.body(builder: Builder<CardBody>): CardBody {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +CardBody()(builder)
}

class CardBody : DisplayElement<HTMLDivElement, CardBody>("div") {
    init {
        classes += "card-body"
    }
}

@KframeDSL
inline fun CardBody.title(level: Int = 5, builder: Builder<Heading> = {}): Heading {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return h(level, "card-title", builder = builder)
}

@KframeDSL
fun CardBody.title(title: String, level: Int = 5) = title(level) { +title }

@KframeDSL
inline fun CardBody.subtitle(level: Int = 6, builder: Builder<Heading> = {}): Heading {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return h(level, "card-subtitle") {
        classes += "text-muted"
        builder()
    }
}

@KframeDSL
fun CardBody.subtitle(subtitle: String, level: Int = 6) = title(level) { +subtitle }

@KframeDSL
inline fun CardBody.link(href: String?, builder: Builder<AElement> = {}): AElement {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return a(href, "card-link", builder = builder)
}

@KframeDSL
inline fun CardBody.text(builder: BasicDisplayBuilder<HTMLParagraphElement> = {}): BasicDisplayElement<HTMLParagraphElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return p("card-text", builder = builder)
}

@KframeDSL
fun CardBody.text(text: String) = this.text { +text }

//TODO list groups

@KframeDSL
inline fun Card.header(klass: String = "", id: String = "", builder: BasicDisplayBuilder<HTMLDivElement>) =
    div("card-header", id)(klass, builder = builder)

@KframeDSL
inline fun Card.footer(klass: String = "", id: String = "", builder: BasicDisplayBuilder<HTMLDivElement>) =
    div("card-footer", id)(klass, builder = builder)

@KframeDSL
inline fun DisplayHost.cardGroup(builder: BasicDisplayBuilder<HTMLDivElement>) = div("card-group", builder = builder)

@KframeDSL
inline fun DisplayHost.cardDeck(builder: BasicDisplayBuilder<HTMLDivElement>) = div("card-deck", builder = builder)

@KframeDSL
inline fun DisplayHost.cardColumns(builder: BasicDisplayBuilder<HTMLDivElement>) =
    div("card-columns", builder = builder)
