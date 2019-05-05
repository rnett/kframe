package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.AElement
import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.dom.bootstrap.Heading
import com.rnett.kframe.dom.button
import com.rnett.kframe.dom.span
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayElement
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLDivElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Alert(val type: ContextType) : DisplayElement<HTMLDivElement, Alert>("div") {

    init {
        classes += "alert"
        classes += type.klass("alert")
        attributes["role"] = "alert"
    }

    fun AElement.alertLink() {
        this.classes += "alert-link"
    }

    fun Heading.alertHeading() {
        this.classes += "alert-heading"
    }

    @KframeDSL
    fun dismissButton(animated: Boolean = true) {
        classes += "alert-dismissible"
        if (animated) {
            classes += ".fade"
            classes += ".show"
        }
        button("close") {
            attributes["data-dismiss"] = "alert"
            attributes["aria-label"] = "Close"
            span {
                attributes["aria-hidden"] = "true"
                underlying.innerHTML = "&times;"
            }
        }

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