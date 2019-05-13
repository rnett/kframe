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

    @KframeDSL
    val head by lazy { Head() }
    @KframeDSL
    val body by lazy { Body() }

    fun mount() {
        //body.underlying.innerHTML = ""
        head.children.forEach {
            it.remove()
        }
        //head.underlying.innerHTML = ""
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

}

class Body internal constructor() :
    W3ElementWrapper<Body, HTMLBodyElement>(
        run {
            console.log("Loading body")
            document.documentElement!!.children.asList().forEach {
                console.log(it)
            }

            document.getElementsByTagName("body").asList().forEach {
                console.log(it)
            }

            var body = document.getElementsByTagName("body").asList().firstOrNull()

            if (body == null) {
                body = document.body
            } else
                println("Got body from tag")

            if (body == null) {
                println("Adding body element")
                body = document.createElement("body")
                document.documentElement?.appendChild(body as HTMLBodyElement)
            } else
                println("Got body from property")

            body
        } as HTMLBodyElement
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