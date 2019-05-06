package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.AElement
import com.rnett.kframe.dom.a
import com.rnett.kframe.dom.bootstrap.li
import com.rnett.kframe.dom.bootstrap.ol
import com.rnett.kframe.structure.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLLIElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Breadcrumb : DisplayElement<HTMLElement, Breadcrumb>("nav") {
    val listElement = ol("breadcrumb") {

    }

    @KframeDSL
    inline fun oldItem(url: String, builder: Builder<AElement> = {}) {
        listElement.li("breadcrumb-item").a(href = url, builder = builder)
    }

    @KframeDSL
    inline fun currentItem(builder: BasicDisplayBuilder<HTMLLIElement> = {}) {
        listElement.li("breadcrumb-item active", builder = builder)
    }
}

@KframeDSL
inline fun DisplayHost.breadcrumb(
    klass: String = "", id: String = "",
    builder: Builder<Breadcrumb> = {}
): Breadcrumb {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Breadcrumb()(klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.breadcrumb(
    vararg history: Pair<String, String>,
    current: String,
    builder: Builder<Breadcrumb> = {}
): Breadcrumb {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return breadcrumb {

        history.forEach { (name, url) ->
            oldItem(url) { +name }
        }

        currentItem { +current }
        builder()
    }
}