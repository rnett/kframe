package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.dom.bootstrap.Heading
import com.rnett.kframe.dom.bootstrap.core.ClassElement
import com.rnett.kframe.dom.bootstrap.h
import com.rnett.kframe.dom.div
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.Element
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLDivElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Dropdown(val buttonType: ContextType?) : ClassElement<HTMLDivElement, Dropdown>("div", "dropdown") {

    val button = button(buttonType) {
        classes += "dropdown-toggle"
        data.toggle = "dropdown"

        aria.haspopup = true
        aria.expanded = false
    }

    val menu = div("dropdown-menu") {

    }

    fun item(element: Element<*, *>) {
        element.classes += "dropdown-item"
    }

    inline fun item(builder: Dropdown.() -> Element<*, *>) {
        val element = builder()
        element.classes += "dropdown-item"
    }


    @KframeDSL
    fun divider() = menu.div("dropdown-divider")

    @KframeDSL
    inline fun header(level: Int, builder: Builder<Heading> = {}) = menu.h(level, "dropdown-header", builder = builder)

}

@KframeDSL
inline fun DisplayHost.dropdown(
    buttonType: ContextType? = null,
    klass: String = "", id: String = "",
    builder: Builder<Dropdown> = {}
): Dropdown {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Dropdown(buttonType)(klass, id, builder)
}