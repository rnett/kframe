package com.rnett.kframe.structure

import org.w3c.dom.HTMLElement
import org.w3c.dom.Text
import org.w3c.dom.events.Event
import kotlin.browser.document

@DslMarker
annotation class KframeDSL

interface ElementHost {
    fun addChild(child: Element<*, *>)
    fun addText(text: String): TextElement

    operator fun <T : AnyElement> T.unaryPlus(): T{
        addChild(this)
        return this
    }
    operator fun String.unaryPlus() = addText(this)
}

interface DisplayHost: ElementHost
interface MetaHost: ElementHost

open class W3ElementWrapper<U : org.w3c.dom.Element>(val underlying: U) : ElementHost {
    override fun addChild(child: Element<*, *>) {
        underlying.append(child.underlying)
    }

    override fun addText(text: String): TextElement {
        val t = kotlin.browser.document.createTextNode(text)
        underlying.appendChild(t)
        return TextElement(t)
    }
}

typealias Builder<E> = E.() -> Unit

typealias AnyElement = Element<*, *>
abstract class Element<U : HTMLElement, S : Element<U, S>>(val tag: String) : ElementHost {

    val underlying: U = kotlin.browser.document.createElement(tag) as U

    val attributes = Attributes(mutableMapOf(), this)
    val style = attributes.style

    val id by attributes.boxedValue<String>()
    val classes get() = attributes.classes
    var klass
        get() = attributes.classes.raw
        set(v) {
            classes.clear()
            classes.add(v)
        }

    val _children = mutableListOf<Element<*, *>>()
    val children: List<Element<*, *>> = _children

    override fun addChild(child: Element<*, *>) {
        _children.add(child)
    }

    @KframeDSL
    operator fun invoke(builder: Builder<in S>): S{
        builder(this as S)
        return this
    }

    override fun addText(text: String): TextElement {
        val t = kotlin.browser.document.createTextNode(text)
        underlying.appendChild(t)
        return TextElement(t)
    }

    @KframeDSL
    inline fun on(event: String, useCapture: Boolean = false, noinline handler: (Event) -> Unit): EventHandler {
        underlying.addEventListener(event, handler, useCapture)
        return EventHandler(this, event, useCapture, handler)
    }

    private val currentListeners = mutableSetOf<String>()

    @KframeDSL
    fun <H : Event> on(
        event: String,
        useCapture: Boolean = false,
        handler: (H) -> Unit
    ): EventHandler {
        val actualHandler: (Event) -> Unit =
            if (event in currentListeners) {
                { handler(it as H) }
            } else {
                currentListeners.add(event);
                { Document.preEventUpdate(); handler(it as H); Document.postEventUpdate() }
            }

        underlying.addEventListener(event, actualHandler, useCapture)
        return EventHandler(this, event, useCapture, actualHandler)
    }

    @KframeDSL
    inline fun String.on(useCapture: Boolean = false, noinline handler: (Event) -> Unit) = on(this, useCapture, handler)
    @KframeDSL
    inline operator fun String.invoke(useCapture: Boolean = false, noinline handler: (Event) -> Unit) =
        on(this, useCapture, handler)
}

abstract class DisplayElement<U: HTMLElement, S: DisplayElement<U, S>>(tag: String) : Element<U, S>(tag), DisplayHost
abstract class MetaElement<U: HTMLElement, S: MetaElement<U, S>>(tag: String) : Element<U, S>(tag), MetaHost

class TextElement(private var _underlying: Text) {
    var value: String
        get() = _underlying.wholeText
        set(v) {
            val new = Text(v)
            _underlying.replaceWith(new)
            _underlying = new
        }

    val underlying get() = _underlying
}

class BasicDisplayElement<U: HTMLElement>(tag: String) : DisplayElement<U, BasicDisplayElement<U>>(tag)
class BasicMetaElement<U: HTMLElement>(tag: String) : DisplayElement<U, BasicDisplayElement<U>>(tag)

typealias BasicDisplayBuilder<U> = BasicDisplayElement<U>.() -> Unit
typealias BasicMetaBuilder<U> = BasicMetaElement<U>.() -> Unit