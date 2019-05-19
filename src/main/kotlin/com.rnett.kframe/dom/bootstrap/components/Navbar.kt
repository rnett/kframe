package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.*
import com.rnett.kframe.dom.bootstrap.Breakpoint
import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.dom.bootstrap.core.ClassElement
import com.rnett.kframe.structure.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Navbar : ClassElement<HTMLElement, Navbar>("nav", "navbar") {

    enum class Theme {
        Light, Dark
    }

    val expand by classes.classDelegate(Breakpoint.None) {
        when (it) {
            Breakpoint.None -> "navbar-expand"
            else -> "navbar-expand-${it.klass}"
        }
    }

    var theme by classes.optionalClassDelegate<Theme> { "navbar-${it.name.toLowerCase()}" }

    val nav: Nav
    val body = div("collapse navbar-collapse") {
        nav = nav()
        nav.classes.remove("nav")
        nav.classes.add("navbar-nav")
    }

    val toggle = rawButton("navbar-toggler") {
        data.toggle = "collapse"
        data.target = body.idOrRandom
        aria.controls = body.id
        aria.expanded = false
        aria.label = "Toggle navigation"
        span("navbar-toggler-icon")
    }

    init {
        role = "navigation"
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
inline fun Navbar.routerLink(
    title: String,
    url: String,
    crossinline builder: Builder<NavLink> = {}
): NavLink {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return linkItem("") {
        on.click {
            Document.gotoUrl(url)
        }
        +title
        active = Document.page == Document.findUrl(url)?.first
        makeClickable()
    }
}

@KframeDSL
inline fun Navbar.routerLink(
    page: Page,
    url: String = "/${page.name.toLowerCase()}",
    parameters: Parameters = Parameters(mapOf()),
    title: String = page.name,
    crossinline builder: Builder<NavLink> = {}
): NavLink {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return linkItem("") {
        on.click {
            Document.goto(page, url, parameters)
        }
        +title
        active = Document.page == page
        makeClickable()
    }
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