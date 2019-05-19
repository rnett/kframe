package com.rnett.kframe.structure

import com.rnett.kframe.dom.TitleElement
import com.rnett.kframe.dom.input.IDataElement
import com.rnett.kframe.dom.title
import com.rnett.kframe.structure.data.Binding
import kotlin.browser.window

@JsName("KframeDocument")
object Document {

    var _url: String = ""
    var _parameters: Parameters = Parameters(mapOf())

    val url get() = _url
    val parameters get() = _parameters

    val _elements: MutableSet<AnyElement> = mutableSetOf()

    val elements: Set<AnyElement> = _elements

    private val pagesByUrl: MutableMap<Route, Page> = mutableMapOf()
    private val pagesByName: MutableMap<String, Page> = mutableMapOf()

    internal fun addPage(page: Page): Page {
        if (page.route in pagesByUrl)
            throw IllegalArgumentException("Page with url ${page.route} already registered")

        if (page.name in pagesByName)
            throw IllegalArgumentException("Page with baseName ${page.name} already registered")

        pagesByUrl[page.route] = page
        pagesByName[page.name] = page

        return page
    }

    private lateinit var _page: Page
    val page get() = _page

    private val preEventSubscribers = mutableSetOf<() -> Unit>()
    private val postEventSubscribers = mutableSetOf<() -> Unit>()

    fun subscribePreEvent(body: () -> Unit) = preEventSubscribers.add(body)
    fun subscribePostEvent(body: () -> Unit) = postEventSubscribers.add(body)

    fun beforeEvent(body: () -> Unit) = subscribePreEvent(body)
    fun afterEvent(body: () -> Unit) = subscribePostEvent(body)

    fun unsubscribePreEvent(body: () -> Unit) = preEventSubscribers.remove(body)
    fun unsubscribePostEvent(body: () -> Unit) = postEventSubscribers.remove(body)

    fun preEventUpdate() {
        preEventSubscribers.forEach { it() }
    }

    fun postEventUpdate() {
        dataElements.forEach { it.update() }
        bindings.forEach { it.refresh() }
        postEventSubscribers.forEach { it() }
    }

    fun goto(page: String, loadedUrl: String, data: Parameters): Boolean {
        pagesByName[page].let {
            return if (it != null) {
                goto(it, loadedUrl, data)
                true
            } else
                false
        }
    }

    fun goto(page: String, loadedUrl: String, data: Map<String, String>) = goto(page, loadedUrl, Parameters(data))

    fun goto(page: String, loadedUrl: String, data: Map<String, Any>) =
        goto(page, loadedUrl, data.mapValues { it.value.toString() })

    fun goto(page: String, loadedUrl: String, vararg data: Pair<String, Any>) = goto(page, loadedUrl, data.toMap())

    fun goto(subpage: Page, loadedUrl: String, data: Parameters) {
        window.history.pushState(null, subpage.getTitle(data), loadedUrl)
        _parameters = data
        _url = loadedUrl
        _page = subpage

        head.children.filter { it is TitleElement }.forEach { it.remove() }

        head.title(subpage.getTitle(data))

        refreshPages()
    }

    fun findUrl(url: String): Pair<Page, Parameters>? {

        val (route, params) = if (url.startsWith("http")) {
            UrlResolver(url.substringAfter("//").substringAfter('/'), pagesByUrl.keys) ?: return null
        } else
            UrlResolver(url, pagesByUrl.keys) ?: return null

        return pagesByUrl[route]!! to params
    }

    fun gotoUrl(url: String): Boolean {
        val (page, data) = findUrl(url) ?: return false
        _url = url
        goto(page, url, data)
        return true
    }

    private val pageMounts = mutableListOf<PageMount<*>>()

    internal fun addPageMount(pageMount: PageMount<*>) {
        pageMounts.add(pageMount)
    }

    private val onPageChangeSubscribers = mutableListOf<() -> Unit>()

    fun onPageChange(event: () -> Unit) {
        onPageChangeSubscribers.add(event)
    }

    internal fun refreshPages() {
        pageMounts.forEach {
            it.update()
        }
        onPageChangeSubscribers.forEach {
            it()
        }
    }

    private val bindings = mutableListOf<Binding<*, *>>()

    internal fun addBinding(binding: Binding<*, *>) {
        bindings.add(binding)
    }

    private val dataElements = mutableListOf<IDataElement>()

    internal fun addDataElement(element: IDataElement) {
        dataElements.add(element)
    }

    private var loadPages = true

    @KframeDSL
    val head by lazy { Head() }
    @KframeDSL
    val body by lazy { Body() }
}


@KframeDSL
fun definePage(name: String, route: Route, title: String) =
    Document.addPage(Page(name, route, { title }))


@KframeDSL
fun definePage(name: String, route: Route, title: (Parameters) -> String) =
    Document.addPage(Page(name, route, title))


@KframeDSL
fun definePage(name: String, url: String, title: String) =
    definePage(name, Route(url), title)


@KframeDSL
fun definePage(name: String, url: String, title: (Parameters) -> String) =
    definePage(name, Route(url), title)

@KframeDSL
fun onPageChange(handler: () -> Unit) {
    Document.onPageChange(handler)
}

@KframeDSL
fun site(builder: Document.() -> Unit) {
    window.onload = {
        Document.builder()
        var url: String

        if ("?routerurl=" in window.location.href) {
            url = window.location.href.substringAfter("?routerurl=")
        } else {
            url = window.location.pathname

            if ('?' in window.location.href) {
                url += "?" + window.location.href.substringAfter('?')
            }
        }

        if (!Document.gotoUrl(url)) {
            Document.gotoUrl("")
        }
    }
}

