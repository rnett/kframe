package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.AElement
import com.rnett.kframe.dom.a
import com.rnett.kframe.dom.bootstrap.Breakpoint
import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.dom.bootstrap.core.ClassElement
import com.rnett.kframe.dom.div
import com.rnett.kframe.dom.span
import com.rnett.kframe.structure.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Navbar : ClassElement<HTMLElement, Navbar>("nav", "navbar") {

    enum class Theme {
        Light, Dark
    }

    val expand by classes.optionalClassDelegate<Breakpoint> {
        when (it) {
            Breakpoint.None -> null
            else -> "navbar-expand-${it.klass}"
        }
    }

    val theme by classes.optionalClassDelegate<Theme> { "navbar-${it.name.toLowerCase()}" }

    val nav: Nav
    val body = div("collapse navbar-collapse") {
        nav = nav()
        nav.classes.remove("nav")
        nav.classes.add("navbar-nav")
    }
}

@KframeDSL
inline fun DisplayHost.navbar(
    klass: String = "", id: String = "",
    builder: Builder<Navbar> = {}
): Navbar {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Navbar()(klass, id, builder)
}

@KframeDSL
inline fun Navbar.brand(
    text: String = "",
    href: String = "",
    klass: String = "", id: String = "",
    builder: Builder<AElement> = {}
): AElement {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +body.a(href, klass, id) {

        classes += "navbar-brand"

        if (text.isNotBlank())
            +text

        builder()
    }
}

@KframeDSL
fun Navbar.item(
    klass: String = "", id: String = "",
    builder: Builder<NavItem> = {}
): NavItem {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return nav.item(klass, id, builder)
}

@KframeDSL
inline fun Navbar.linkItem(
    href: String = "",
    klass: String = "", id: String = "",
    crossinline builder: Builder<NavLink> = {}
): NavLink {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return nav.linkItem(href, klass, id, builder)
}

@KframeDSL
inline fun Navbar.dropdownItem(
    buttonType: ContextType? = null,
    klass: String, id: String,
    crossinline builder: Builder<Dropdown>
): Dropdown {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val result: Dropdown
    item {
        result = dropdown(buttonType, klass, id, builder)
    }
    return result
}

@KframeDSL
inline fun Navbar.text(
    text: String = "",
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLSpanElement> = {}
): BasicDisplayElement<HTMLSpanElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return body.span(klass, id) {

        if (text.isNotBlank())
            +text

        builder()
    }
}