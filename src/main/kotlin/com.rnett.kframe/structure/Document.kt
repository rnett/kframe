package com.rnett.kframe.structure

import kotlin.browser.window

internal data class SubpageInstance(val page: Page, val data: Parameters) {
    fun build() = page.mount()
    fun urlString() = "/${page.url}" +
            if (data.isNotEmpty())
                "/?${data.entries.joinToString("&") { it.run { "$key=$value" } }}"
            else
                ""
}

object Document {
    var _url: String = ""
    var _parameters: Parameters = Parameters(mapOf())

    val url get() = _url
    val parameters get() = _parameters

    val _elements: MutableSet<AnyElement> = mutableSetOf()

    val elements: Set<AnyElement> = _elements

    private val pagesByUrl: MutableMap<String, Page> = mutableMapOf()
    private val pagesByName: MutableMap<String, Page> = mutableMapOf()

    fun addPage(page: Page){
        if(page.url in pagesByUrl)
            throw IllegalArgumentException("Page with url ${page.url} already registered")

        if(page.name in pagesByName)
            throw IllegalArgumentException("Page with name ${page.name} already registered")

        pagesByUrl[page.url] = page
        pagesByName[page.name] = page
    }

    private var currentPage: SubpageInstance? = null

    fun preEventUpdate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun postEventUpdate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun goto(page: String, data: Parameters): Boolean {
        pagesByName[page].let {
            return if (it != null) {
                goto(it, data)
                true
            } else
                false
        }
    }

    fun goto(page: String, data: Map<String, String>) = goto(page, Parameters(data))

    fun goto(page: String, data: Map<String, Any>) = goto(page, data.mapValues { it.value.toString() })
    fun goto(page: String, vararg data: Pair<String, Any>) = goto(page, data.toMap())

    fun goto(subpage: Page, data: Parameters) {
        val si = SubpageInstance(subpage, data)
        window.history.pushState(null, subpage.getTitle(data), si.urlString())
        _parameters = data
        currentPage = si
        currentPage!!.build()
    }

    private fun findUrl(url: String): Pair<Page, Parameters>? {
        //TODO
        throw NotImplementedError()
    }

    fun gotoUrl(url: String): Boolean {
        val (page, data) = findUrl(url) ?: return false
        _url = url
        goto(page, data)
        return true
    }
}

fun Document.page(name: String, url: String, title: String, builder: Page.(Parameters) -> Unit){
    Document.addPage(Page(name, url, {title}, builder))
}

fun Document.page(name: String, url: String, title: (Parameters) -> String, builder: Page.(Parameters) -> Unit){
    Document.addPage(Page(name, url, title, builder))
}

fun site(builder: Document.() -> Unit){
    Document.builder()

    var url = window.location.href

    if("?routerurl=" in window.location.href){
       url = window.location.protocol + "//" + window.location.hostname + window.location.href.substringAfter("?routerurl=")
    }

    Document.gotoUrl(url)
}

