package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.AElement
import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.dom.bootstrap.Heading
import com.rnett.kframe.dom.bootstrap.core.ClassElement
import com.rnett.kframe.dom.bootstrap.core.applyContext
import com.rnett.kframe.dom.rawButton
import com.rnett.kframe.dom.span
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLDivElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Alert(val type: ContextType) : ClassElement<HTMLDivElement, Alert>("div", "alert") {

    init {
        applyContext(type)
        role = "alert"
    }

    @KframeDSL
    fun dismissButton(animated: Boolean = true) {
        classes += "alert-dismissible"
        if (animated) {
            classes += ".fade"
            classes += ".show"
        }
        rawButton("close") {
            data.dismiss = "alert"
            attributes["aria-label"] = "Close"
            span {
                attributes["aria-hidden"] = "true"
                underlying.innerHTML = "&times;"
            }
        }

    }
}

@KframeDSL
inline fun Alert.link(
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

        classes += "alert-link"

        builder()
    }
}

@KframeDSL
inline fun Alert.heading(
    level: Int = 4,
    klass: String = "", id: String = "",
    builder: Builder<Heading> = {}
): Heading {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return +Heading(level)(klass, id) {
        classes += "alert-heading"
        builder()
    }
}

@KframeDSL
inline fun DisplayHost.alert(
    type: ContextType,
    id: String = "",
    builder: Builder<Alert> = {}
): Alert {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Alert(type)(id = id)(builder)
}