package com.rnett.kframe.structure

import com.rnett.kframe.dom.input.IDataElement
import com.rnett.kframe.structure.data.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.Text
import org.w3c.dom.events.Event
import kotlin.random.Random
import kotlin.reflect.KProperty0

@DslMarker
annotation class KframeDSL

interface ElementHost<S : ElementHost<S>> {
    fun addChild(child: Element<*, *>)
    fun addText(text: String): TextElement

    operator fun <T : AnyElement> T.unaryPlus(): T {
        addChild(this)

        if (this is IDataElement)
            Document.addDataElement(this)

        return this
    }

    operator fun String.unaryPlus() = addText(this)

    val children: List<Element<*, *>>

    //TODO check instance of IDataElement for views?

    @BindingDSL
    fun <B : Binding<T, E>, T, E : AnyElement> bind(binding: B, watch: Boolean = true): B {
        if (watch)
            Document.addBinding(binding)

        return binding
    }

    @BindingDSL
    fun <V : View<S, E>, E : AnyElement> V.bind(
        watch: Boolean = true,
        equalityCheck: EqualityCheck<*> = EqualityCheck.HashCode
    ): ViewBinding<V, E, S> {
        val binding = ViewBinding(this@ElementHost as S, this, equalityCheck)
        return bind(binding, watch)
    }

    @BindingDSL
    fun <V : View<S, E>, E : AnyElement> KProperty0<V>.bind(
        watch: Boolean = true,
        equalityCheck: EqualityCheck<*> = EqualityCheck.Equality
    ): ViewPropertyBinding<V, E, S> {
        val binding = ViewPropertyBinding(this@ElementHost as S, this, equalityCheck)
        return bind(binding, watch)
    }

    @BindingDSL
    operator fun <V : View<S, E>, E : AnyElement> V.unaryPlus() = bind(true)

    @BindingDSL
    operator fun <V : View<S, E>, E : AnyElement> V.unaryMinus() = bind(false)

    @BindingDSL
    operator fun <V : View<S, E>, E : AnyElement> KProperty0<V>.unaryPlus() = bind(true)

    @BindingDSL
    operator fun <V : View<S, E>, E : AnyElement> KProperty0<V>.unaryMinus() = bind(false)

    @BindingDSL
    fun <T, E : AnyElement> BindingCondition<T>.bind(watch: Boolean = true, builder: S.(T) -> E): Binding<T, E> {
        val binding = Binding({ (this@ElementHost as S).builder(it) }, this)
        return bind(binding)
    }

    @BindingDSL
    infix fun <T, E : AnyElement> BindingCondition<T>.bind(builder: S.(T) -> E): Binding<T, E> {
        return this.bind(true, builder)
    }

    @BindingDSL
    fun <T, E : AnyElement> KProperty0<T>.bind(
        watch: Boolean = true,
        equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
        builder: S.(T) -> E
    ): Binding<T, E> {
        return PropertyBindingCondition(this, equalityCheck).bind(watch, builder)
    }

    @BindingDSL
    infix fun <T, E : AnyElement> KProperty0<T>.bind(
        builder: S.(T) -> E
    ): Binding<T, E> {
        return this.bind(true, builder = builder)
    }

    @BindingDSL
    fun <T, E : AnyElement> T.bind(
        watch: Boolean = true,
        equalityCheck: EqualityCheck<*> = EqualityCheck.HashCode,
        builder: S.(T) -> E
    ): Binding<T, E> {
        return ValueBindingCondition(this, equalityCheck).bind(watch, builder)
    }

    @BindingDSL
    infix fun <T, E : AnyElement> T.bind(
        builder: S.(T) -> E
    ): Binding<T, E> {
        return this.bind(true, builder = builder)
    }
}

typealias AnyElementHost = ElementHost<*>

interface IDisplayHost<S : IDisplayHost<S>> : ElementHost<S>
interface IMetaHost<S : IMetaHost<S>> : ElementHost<S>

typealias DisplayHost = IDisplayHost<*>
typealias MetaHost = IMetaHost<*>

open class W3ElementWrapper<S : W3ElementWrapper<S, U>, U : org.w3c.dom.Element>(val underlying: U) :
    ElementHost<S> {

    override fun addChild(child: Element<*, *>) {
        underlying.append(child.underlying)
        _children.add(child)
        child.onAdded(this)
    }

    override fun addText(text: String): TextElement {
        val t = kotlin.browser.document.createTextNode(text)
        underlying.appendChild(t)
        return TextElement(t)
    }

    private val _children = mutableListOf<AnyElement>()

    override val children: List<AnyElement> = _children
}

typealias Builder<E> = E.() -> Unit

typealias AnyElement = Element<*, *>

private val usedIds = mutableSetOf<String>()
private val idRandom = Random(1)

abstract class Element<U : HTMLElement, S : Element<U, S>>(val tag: String) : ElementHost<S> {

    val underlying: U = kotlin.browser.document.createElement(tag) as U

    val attributes = Attributes(mutableMapOf(), this)
    val style = attributes.style

    var id by attributes.boxedValue<String>()

    fun setRandomId(): String {
        var id: String
        var tries = 0
        do {
            id = String(List(16 * (tries / 10)) { idRandom.nextInt(65, 91) }.map { it.toChar() }.toCharArray())
            tries++
        } while (id in usedIds)
        this.id = "random-$id"
        return this.id!!
    }

    val idOrRandom get() = id ?: setRandomId()

    val classes get() = attributes.classes
    var klass
        get() = attributes.classes.raw
        set(v) {
            classes.clear()
            classes.addAll(v.split(" "))
        }


    var required by attributes.flagValue()


    val _children = mutableListOf<Element<*, *>>()
    override val children: List<Element<*, *>> = _children

    override fun addChild(child: Element<*, *>) {
        _children.add(child)
        child.onAdded(this)
    }

    private var _parent: ElementHost<*>? = null
    val parent get() = _parent

    fun onAdded(parent: ElementHost<*>) {
        this._parent = parent
    }

    fun remove() {
        underlying.remove()
    }

    @KframeDSL
    inline operator fun invoke(builder: Builder<in S>): S {
        builder(this as S)
        return this
    }

    override fun addText(text: String): TextElement {
        val t = kotlin.browser.document.createTextNode(text)
        underlying.appendChild(t)
        return TextElement(t)
    }

    val on by lazy { Events(this as S) }

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

    inline operator fun invoke(klass: String = "", id: String = "", builder: Builder<in S> = {}): S {
        if (klass.isNotBlank())
            this.classes += klass

        if (id.isNotBlank())
            this.id = id

        builder(this as S)

        return this
    }

}

abstract class DisplayElement<U : HTMLElement, S : DisplayElement<U, S>>(tag: String) : Element<U, S>(tag),
    IDisplayHost<S>

abstract class MetaElement<U : HTMLElement, S : MetaElement<U, S>>(tag: String) : Element<U, S>(tag), IMetaHost<S>

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

class BasicDisplayElement<U : HTMLElement>(tag: String) : DisplayElement<U, BasicDisplayElement<U>>(tag)
class BasicMetaElement<U : HTMLElement>(tag: String) : DisplayElement<U, BasicMetaElement<U>>(tag)

typealias BasicDisplayBuilder<U> = BasicDisplayElement<U>.() -> Unit
typealias BasicMetaBuilder<U> = BasicMetaElement<U>.() -> Unit