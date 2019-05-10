package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.AElement
import com.rnett.kframe.dom.a
import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.dom.bootstrap.core.SizedClassElement
import com.rnett.kframe.dom.bootstrap.core.applyContext
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayElement
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class BaseButton<B : BaseButton<B>>(val type: ContextType?, val outline: Boolean = false) :
    SizedClassElement<HTMLButtonElement, B>("button", "btn") {

    init {
        attributes["type"] = "button"
        classes += "btn"

        applyContext(type, if (outline) "btn-outline" else null)
    }

    var block by classes.presentDelegate.withClass

    var active by attributes.flagValue()
    var disabled by attributes.flagValue()

}

class Button(type: ContextType?, outline: Boolean = false) : BaseButton<Button>(type, outline)

@KframeDSL
inline fun DisplayHost.button(
    type: ContextType?, outline: Boolean = false,
    klass: String = "", id: String = "",
    builder: Builder<Button> = {}
): Button {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Button(type, outline)(klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.bootstrapButton(
    type: ContextType?, outline: Boolean = false,
    klass: String = "", id: String = "",
    builder: Builder<Button> = {}
): Button {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return button(type, outline, klass, id, builder)
}


fun AElement.asButton(type: ContextType?) {
    attributes["role"] = "button"
    classes += "btn"
    classes += type?.klass("btn") ?: "btn-link"
}

@KframeDSL
inline fun DisplayHost.linkButton(
    href: String,
    type: ContextType?,
    klass: String = "", id: String = "",
    builder: Builder<AElement> = {}
): AElement {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return a(href) {
        asButton(type)
    }
}

fun DisplayElement<HTMLInputElement, *>.asButton(
    type: ContextType?,
    inputType: String = "button",
    outline: Boolean = false
) {
    attributes["type"] = inputType
    classes += "btn"
    classes += if (outline)
        type?.klass("btn-outline") ?: "btn-link"
    else
        type?.klass("btn") ?: "btn-link"
}

//TODO button groups