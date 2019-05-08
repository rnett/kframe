package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.bootstrap.core.ClassElement
import com.rnett.kframe.lazyInit
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayElement
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLOListElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KframeDSL
inline fun DisplayHost.tabNav(
    klass: String = "", id: String = "",
    builder: Builder<TabNav> = {}
): TabNav {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +TabNav()(klass, id, builder)
}

class TabNav : ClassElement<HTMLOListElement, TabNav>("ol", "nav") {
    var vertical by classes.presentDelegate("flex-column")
    var pills by classes.presentDelegate.withClass
    var fill by classes.presentDelegate.withClass
    var justified by classes.presentDelegate.withClass

    init {
        classes += "nav-tabs"
        attributes["role"] = "tablist"
    }

    internal val _items = mutableListOf<TabItem>()
    val items: List<TabItem> get() = _items

}

@KframeDSL
fun TabNav.item(
    klass: String = "", id: String = "",
    builder: Builder<TabItem> = {}
): TabItem {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val item = TabItem()(klass, id, builder)
    _items.add(item)
    return item
}

class TabItem : ClassElement<HTMLLIElement, TabItem>("li", "nav-item")

class TabLink : ClassElement<HTMLAnchorElement, TabLink>("a", "nav-link") {

    var disabled by classes.presentDelegate
    var active by classes.presentDelegate
    var href by attributes.boxedValue<String>()

    init {
        data.toggle = "tab"
    }

    fun activate() {
        underlying.asDynamic().tab("show")
    }
}

@KframeDSL
inline fun TabItem.link(
    href: String = "",
    klass: String = "", id: String = "",
    builder: Builder<TabLink> = {}
): TabLink {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +TabLink()(klass, id, builder)
}

@KframeDSL
inline fun TabNav.linkItem(
    href: String = "",
    klass: String = "", id: String = "",
    crossinline builder: Builder<TabLink> = {}
): TabLink {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val result: TabLink
    item {
        result = link(klass, id) {
            if (href.isNotBlank())
                this.href = href

            builder()
        }
    }

    return result
}

class TabContent(val nav: TabNav) : ClassElement<HTMLDivElement, TabContent>("div", "tab-content") {

    internal val _tabs = mutableMapOf<TabLink, TabPane>()
    val tabs: Map<TabLink, TabPane> get() = this._tabs

    val panes get() = tabs.values

    var fadeAll
        get() = this._tabs.values.all { it.fade }
        set(v) {
            this._tabs.values.forEach { it.fade = v }
        }

    internal fun addNavFor(pane: TabPane, navBuilder: TabLink.(TabPane) -> Unit): TabLink {
        val link = nav.linkItem("#${pane.idOrRandom}") {
            if (pane.active)
                this.active = true

            navBuilder(pane)
        }

        _tabs[link] = pane

        return link
    }
}

class TabPane : ClassElement<HTMLDivElement, TabPane>("div", "tab-pane") {
    var fade by classes.presentDelegate
    var show by classes.presentDelegate
    var active by classes.presentDelegate

    fun starter() {
        show = true
        active = true
    }

    fun activate() {
        (this.parent as TabContent).tabs.entries.first { it.value == this }.key.activate()
    }

}

@KframeDSL
inline fun DisplayHost.tabContent(
    nav: TabNav,
    klass: String = "", id: String = "",
    builder: Builder<TabContent> = {}
): TabContent {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +TabContent(nav)(klass, id, builder)
}

@KframeDSL
fun TabContent.tabPane(
    starter: Boolean = false,
    klass: String = "", id: String = "",
    navBuilder: TabLink.(TabPane) -> Unit = {},
    builder: Builder<TabPane> = {}
): Pair<TabLink, TabPane> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val pane = +TabPane()(klass, id) {
        if (starter)
            starter()
        builder()
    }

    return this.addNavFor(pane, navBuilder) to pane
}

@KframeDSL
fun TabContent.tabPane(
    name: String,
    starter: Boolean = false,
    klass: String = "", id: String = "",
    navBuilder: TabLink.(TabPane) -> Unit = {},
    builder: Builder<TabPane> = {}
): Pair<TabLink, TabPane> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    return tabPane(starter, klass, id, {
        +name
        navBuilder(it)
    }, builder)
}

class Tabs() : DisplayElement<HTMLDivElement, Tabs>("div") {
    var _nav by lazyInit { tabNav() }
    val nav get() = _nav

    var _content by lazyInit { tabContent(nav) }
    val content get() = _content

    constructor(nav: TabNav) : this() {
        this._nav = nav
    }

    constructor(nav: TabNav, content: TabContent) : this(nav) {
        this._content = content
    }
}

@KframeDSL
inline fun DisplayHost.tabs(
    klass: String = "", id: String = "",
    builder: Builder<Tabs> = {}
): Tabs {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Tabs()(klass, id, builder)
}