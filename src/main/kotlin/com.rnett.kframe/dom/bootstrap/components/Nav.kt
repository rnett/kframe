package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.bootstrap.core.ClassElement
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLOListElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KframeDSL
inline fun DisplayHost.nav(
    klass: String = "", id: String = "",
    builder: Builder<Nav> = {}
): Nav {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Nav()(klass, id, builder)
}

class Nav : ClassElement<HTMLOListElement, Nav>("ol", "nav") {
    var vertical by classes.presentDelegate("flex-column")
    var pills by classes.presentDelegate.withClass
    var fill by classes.presentDelegate.withClass
    var justified by classes.presentDelegate.withClass

    internal val _items = mutableListOf<NavItem>()
    val items: List<NavItem> get() = _items

}

@KframeDSL
fun Nav.item(
    klass: String = "", id: String = "",
    builder: Builder<NavItem> = {}
): NavItem {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val item = +NavItem()(klass, id, builder)
    _items.add(item)
    return item
}

class NavItem : ClassElement<HTMLLIElement, NavItem>("li", "nav-item")

class NavLink : ClassElement<HTMLAnchorElement, NavLink>("a", "nav-link") {
    var disabled by classes.presentDelegate
    var active by classes.presentDelegate
    var href by attributes.boxedValue<String>()
}

@KframeDSL
inline fun NavItem.link(
    href: String = "",
    klass: String = "", id: String = "",
    builder: Builder<NavLink> = {}
): NavLink {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +NavLink()(klass, id) {
        if (href.isNotBlank())
            this.href = href

        builder()
    }
}

@KframeDSL
inline fun Nav.linkItem(
    href: String = "",
    klass: String = "", id: String = "",
    crossinline builder: Builder<NavLink> = {}
): NavLink {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val result: NavLink
    item {
        result = link(href, klass, id, builder)
    }

    return result
}