package com.rnett.kframe.structure

import com.rnett.kframe.structure.Document.url
import org.w3c.dom.HTMLBodyElement
import org.w3c.dom.HTMLHeadElement
import org.w3c.dom.asList
import kotlin.browser.document
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

//TODO option on page mount method to reload whole document instead of mount points

class PageMount<S : ElementHost<S>>(val parent: S, val page: Page, val builder: S.() -> Unit) {

    private val elements = mutableListOf<Removable>()

    fun clear() {
        elements.forEach { it.remove() }
        elements.clear()
    }

    fun mount() {
        if (Document.page == page) {
            parent.addSubscriber = {
                elements.add(it)
            }
            parent.builder()
            parent.addSubscriber = null
        }
    }

    fun update() {
        clear()
        if (Document.page == page)
            mount()
    }
}

internal fun <E : ElementHost<E>> E.withPage(page: Page, builder: E.() -> Unit): PageMount<E> {
    contract { callsInPlace(builder, InvocationKind.AT_MOST_ONCE) }

    val pm = PageMount(this, page, builder)
    Document.addPageMount(pm)
    return pm
}

data class Parameters(val params: Map<String, String>) : Map<String, String> by params

class Page(
    val name: String,
    val route: Route,
    val getTitle: (Parameters) -> String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as Page

        if (name != other.name) return false
        if (route != other.route) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + url.hashCode()
        return result
    }

    val active get() = Document.page == this

}

class Body internal constructor() :
    W3ElementWrapper<Body, HTMLBodyElement>(
        (
                document.getElementsByTagName("body").asList().firstOrNull()
                    ?: document.body
                    ?: document.createElement("body").also {
                        document.documentElement?.appendChild(it as HTMLBodyElement)
                    }
                ) as HTMLBodyElement
    ),
    IDisplayHost<Body> {
    operator fun invoke(builder: Body.() -> Unit) = apply(builder)
}

class Head internal constructor() :
    W3ElementWrapper<Head, HTMLHeadElement>(
        (
                document.getElementsByTagName("head").asList().firstOrNull()
                    ?: document.head
                    ?: document.createElement("head").also {
                        document.documentElement?.appendChild(it)
                    }
                ) as HTMLHeadElement
    ),
    IMetaHost<Head> {
    operator fun invoke(builder: Head.() -> Unit) = apply(builder)
}