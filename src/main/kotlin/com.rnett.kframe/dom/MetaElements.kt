package com.rnett.kframe.dom

import com.rnett.kframe.structure.*
import org.w3c.dom.HTMLLinkElement
import org.w3c.dom.HTMLMetaElement
import org.w3c.dom.HTMLScriptElement
import org.w3c.dom.HTMLTitleElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class TitleElement() : MetaElement<HTMLTitleElement, TitleElement>("title") {

    private val titleElement = +""

    var titleText: String
        get() = titleElement.value
        set(v) {
            titleElement.value = v
        }

    constructor(title: String) : this() {
        this.titleText = title
    }

}

@KframeDSL
inline fun MetaHost.title(
    title: String = "",
    klass: String = "",
    id: String = "",
    builder: Builder<TitleElement> = {}
): TitleElement {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +TitleElement(title)(klass, id, builder)
}


@KframeDSL
inline fun ElementHost<*>.script(
    klass: String = "", id: String = "",
    builder: BasicMetaBuilder<HTMLScriptElement> = {}
): BasicMetaElement<HTMLScriptElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicMetaElement<HTMLScriptElement>("script")(klass, id, builder)
}

@KframeDSL
inline fun ElementHost<*>.script(
    src: String,
    klass: String = "", id: String = "",
    builder: BasicMetaBuilder<HTMLScriptElement> = {}
): BasicMetaElement<HTMLScriptElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicMetaElement<HTMLScriptElement>("script")(klass, id) {
        attributes["src"] = src
        builder()
    }
}

@KframeDSL
inline fun ElementHost<*>.script(
    src: String,
    integrity: String,
    crossorigin: String = "anonymous",
    klass: String = "", id: String = "",
    builder: BasicMetaBuilder<HTMLScriptElement> = {}
): BasicMetaElement<HTMLScriptElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicMetaElement<HTMLScriptElement>("script")(klass, id) {
        attributes["src"] = src
        attributes["integrity"] = integrity
        attributes["crossorigin"] = crossorigin
        builder()
    }
}

@KframeDSL
fun Body.bootstrapJs() {
    script(
        src = "https://code.jquery.com/jquery-3.3.1.slim.min.js",
        integrity = "sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
    ) {
        attributes["type"] = "text/javascript"
    }
    script(
        src = "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js",
        integrity = "sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
    ) {
        attributes["type"] = "text/javascript"
    }
    script(
        src = "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js",
        integrity = "sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
    ) {
        attributes["type"] = "text/javascript"
    }
}

@KframeDSL
inline fun MetaHost.remoteStylesheet(
    src: String,
    klass: String = "", id: String = "",
    builder: BasicMetaBuilder<HTMLLinkElement> = {}
): BasicMetaElement<HTMLLinkElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicMetaElement<HTMLLinkElement>("link")(klass, id) {
        attributes["href"] = src
        attributes["rel"] = "stylesheet"
        builder()
    }
}

@KframeDSL
inline fun MetaHost.remoteStylesheet(
    src: String,
    integrity: String,
    crossorigin: String = "anonymous",
    klass: String = "", id: String = "",
    builder: BasicMetaBuilder<HTMLLinkElement> = {}
): BasicMetaElement<HTMLLinkElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicMetaElement<HTMLLinkElement>("link")(klass, id) {
        attributes["href"] = src
        attributes["rel"] = "stylesheet"
        attributes["integrity"] = integrity
        attributes["crossorigin"] = crossorigin
        builder()
    }
}

@KframeDSL
fun Head.bootstrapCss() {
    remoteStylesheet(
        src = "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css",
        integrity = "sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
    )
    viewport()
}

@KframeDSL
inline fun MetaHost.viewport(
    content: String = "width=device-width, initial-scale=1, shrink-to-fit=no",
    klass: String = "", id: String = "",
    builder: BasicMetaBuilder<HTMLMetaElement> = {}
): BasicMetaElement<HTMLMetaElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicMetaElement<HTMLMetaElement>("meta")(klass, id) {
        attributes["content"] = content
        attributes["name"] = "viewport"
        builder()
    }
}