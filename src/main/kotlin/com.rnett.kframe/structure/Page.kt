package com.rnett.kframe.structure

import com.rnett.kframe.dom.title
import com.rnett.kframe.structure.Document.url
import org.w3c.dom.HTMLBodyElement
import org.w3c.dom.HTMLHeadElement
import org.w3c.dom.asList
import kotlin.browser.document

data class Parameters(val params: Map<String, String>) : Map<String, String> by params

class Page(
    val name: String,
    val route: Route,
    val getTitle: (Parameters) -> String,
    val builder: Page.(Parameters) -> Unit
) {

    fun mount() {
        body.underlying.innerHTML = ""
        head.underlying.innerHTML = ""
        builder(Document.parameters)
        head.title(getTitle(Document.parameters))
    }

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

    @KframeDSL
    val head
        get() = Head()
    @KframeDSL
    val body
        get() = Body()

}

class Body internal constructor() :
    W3ElementWrapper<Body, HTMLBodyElement>(
        (kotlin.browser.document.getElementsByTagName("body").asList().firstOrNull() as HTMLBodyElement?
            ?: kotlin.browser.document.createElement("body").also {
                document.documentElement?.appendChild(it)
            } as HTMLBodyElement)
    ),
    IDisplayHost<Body> {
    operator fun invoke(builder: Body.() -> Unit) = apply(builder)
}

class Head internal constructor() :
    W3ElementWrapper<Head, HTMLHeadElement>(
        (kotlin.browser.document.getElementsByTagName("head").asList().firstOrNull()
            ?: kotlin.browser.document.createElement("head").also {
                document.documentElement?.appendChild(it)
            }) as HTMLHeadElement
    ),
    IMetaHost<Head> {
    operator fun invoke(builder: Head.() -> Unit) = apply(builder)
}