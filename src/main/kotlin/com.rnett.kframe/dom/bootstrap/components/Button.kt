package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.AElement
import com.rnett.kframe.dom.a
import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.structure.*
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Button(val type: ContextType?, val outline: Boolean = false) :
    DisplayElement<HTMLButtonElement, Button>("button") {
    init {
        attributes["type"] = "button"
        classes += "btn"
        classes += if (outline)
            type?.klass("btn-outline") ?: "btn-link"
        else
            type?.klass("btn") ?: "btn-link"

    }

    fun large() {
        classes += "btn-lg"
    }

    fun small() {
        classes += "btn-sm"
    }

    fun block() {
        classes += "btn-block"
    }

    private var _active = false

    var active: Boolean
        get() = _active
        set(v) {
            if (v)
                classes += "active"
            else
                classes -= "active"
            _active = v
        }

    private var _disabled = false
    var disabled: Boolean
        get() = _disabled
        set(v) {
            if (v)
                attributes["disabled"] = Attributes.Present
            else
                attributes["disabled"] = null

            _disabled = v
        }

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

//TODO Checkbox and Radio button types w/ input
//TODO button groups