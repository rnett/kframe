package com.rnett.kframe.structure

import com.rnett.kframe.dom.bootstrap.utilities.Bootstrap
import com.rnett.kframe.dom.input.IDataElement
import com.rnett.kframe.structure.data.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.Text
import org.w3c.dom.events.Event
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.random.Random
import kotlin.reflect.KProperty0

@DslMarker
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.TYPE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY
)
@Retention(AnnotationRetention.BINARY)
annotation class KframeDSL

interface Removable {
    fun remove()
}

//TODO better bndings using onAdd, like pages
interface ElementHost<S : ElementHost<S>> {
    fun addChild(child: Element<*, *>)
    fun removeChild(child: Element<*, *>)
    fun addText(text: String): TextElement

    operator fun <T : AnyElement> T.unaryPlus(): T {
        this@ElementHost.addChild(this)

        if (this is IDataElement)
            Document.addDataElement(this)

        return this
    }

    operator fun String.unaryPlus() = addText(this)

    val children: List<Element<*, *>>

    //TODO check instance of IDataElement for views?

    @BindingDSL
    fun <B : Binding<T, E>, T, E : Removable> bind(binding: B, watch: Boolean = true): B {
        if (watch)
            Document.addBinding(binding)

        return binding
    }

    @BindingDSL
    fun <V : View<S, E>, E : AnyElement> V.bindView(
        watch: Boolean = true,
        equalityCheck: EqualityCheck<*> = EqualityCheck.HashCode
    ): ViewBinding<V, E, S> {
        val binding = ViewBinding(this@ElementHost as S, this, equalityCheck)
        return bind(binding, watch)
    }

    @BindingDSL
    fun <V : View<S, E>, E : AnyElement> KProperty0<V>.bindViewProperty(
        watch: Boolean = true,
        equalityCheck: EqualityCheck<*> = EqualityCheck.Equality
    ): ViewPropertyBinding<V, E, S> {
        val binding = ViewPropertyBinding(this@ElementHost as S, this, equalityCheck)
        return bind(binding, watch)
    }

    @BindingDSL
    operator fun <V : View<S, E>, E : AnyElement> V.unaryPlus() = bindView(true)

    @BindingDSL
    operator fun <V : View<S, E>, E : AnyElement> V.unaryMinus() = bindView(false)

    @BindingDSL
    operator fun <V : View<S, E>, E : AnyElement> KProperty0<V>.unaryPlus() = bindViewProperty(true)

    @BindingDSL
    operator fun <V : View<S, E>, E : AnyElement> KProperty0<V>.unaryMinus() = bindViewProperty(false)

    @BindingDSL
    fun <T, E : Removable> BindingCondition<T>.bind(watch: Boolean = true, builder: S.(T) -> E): Binding<T, E> {
        val binding = Binding({ (this@ElementHost as S).builder(it) }, this)
        return bind(binding)
    }

    @BindingDSL
    fun <T, E : Removable> KProperty0<T>.bindProperty(
        watch: Boolean = true,
        equalityCheck: EqualityCheck<*> = EqualityCheck.Equality,
        builder: S.(T) -> E
    ): Binding<T, E> {
        return PropertyBindingCondition(this, equalityCheck).bind(watch, builder)
    }

    @BindingDSL
    fun <T, E : Removable> T.bindValue(
        watch: Boolean = true,
        equalityCheck: EqualityCheck<*> = EqualityCheck.HashCode,
        builder: S.(T) -> E
    ): Binding<T, E> {
        return ValueBindingCondition(this, equalityCheck).bind(watch, builder)
    }

    @BindingDSL
    fun <T, E : Removable> (() -> T).bindFunction(
        watch: Boolean = true,
        equalityCheck: EqualityCheck<*> = EqualityCheck.HashCode,
        builder: S.(T) -> E
    ): Binding<T, E> {
        return FunctionBindingCondition(this, equalityCheck).bind(watch, builder)
    }

    operator fun Page.invoke(builder: S.() -> Unit) = (this@ElementHost as S).withPage(this, builder)

}

typealias AnyElementHost = ElementHost<*>

interface IDisplayHost<S : IDisplayHost<S>> : ElementHost<S>
interface IMetaHost<S : IMetaHost<S>> : ElementHost<S>

typealias DisplayHost = IDisplayHost<*>
typealias MetaHost = IMetaHost<*>

open class W3ElementWrapper<S : W3ElementWrapper<S, U>, U : org.w3c.dom.Element>(val underlying: U) :
    ElementHost<S> {

    internal var addSubscriber: ((Removable) -> Unit)? = null

    override fun addChild(child: Element<*, *>) {
        underlying.appendChild(child.underlying)
        _children.add(child)
        child.onAdded(this)

        if (addSubscriber != null)
            addSubscriber!!(child)
    }

    override fun removeChild(child: Element<*, *>) {
        underlying.removeChild(child.underlying)
        _children.remove(child)
    }

    override fun addText(text: String): TextElement {
        val t = kotlin.browser.document.createTextNode(text)
        underlying.appendChild(t)

        val te = TextElement(t)
        if (addSubscriber != null)
            addSubscriber!!(te)

        return te
    }

    private val _children = mutableListOf<AnyElement>()

    override val children: List<AnyElement> get() = _children
}

typealias Builder<E> = E.() -> Unit

typealias AnyElement = Element<*, *>

private val usedIds = mutableSetOf<String>()
private val idRandom = Random(1)

abstract class Element<U : HTMLElement, S : Element<U, S>>(tag: String) : ElementHost<S>, Removable {

    var tag: String = tag
        private set

    var underlying: U = kotlin.browser.document.createElement(tag) as U
        private set

    val attributes = Attributes(mutableMapOf(), this)
    val style by lazy { Style(mutableMapOf(), this) }
    val classes = Classes(mutableSetOf(), this)

    var id by attributes.boxedValue<String>()

    var title by attributes.boxedValue<String>()

    fun setRandomId(): String {
        var id: String
        var tries = 0
        do {
            id = String(List(16 * ((tries / 10) + 1)) { idRandom.nextInt(65, 91) }.map { it.toChar() }.toCharArray())
            tries++
        } while (id in usedIds)
        this.id = "random-$id"
        return this.id!!
    }

    val idOrRandom get() = id ?: setRandomId()
    var klass
        get() = classes.joinToString(" ")
        set(v) {
            classes.clear()
            classes.addAll(v.split(" "))
        }


    var required by attributes.flagValue()

    var srOnly by classes.presentDelegate("sr-only")

    var role by attributes.boxedValue<String>()

    val data by lazy { Data(this) }
    val aria by lazy { Aria(this) }
    val bootstrap by lazy { Bootstrap(this) }

    val _children = mutableListOf<Element<*, *>>()
    override val children: List<Element<*, *>> = _children

    internal var addSubscriber: ((Removable) -> Unit)? = null

    override fun addChild(child: Element<*, *>) {
        underlying.appendChild(child.underlying)
        _children.add(child)
        child.onAdded(this)

        if (addSubscriber != null)
            addSubscriber!!(child)
    }

    private var _parent: ElementHost<*>? = null
    val parent get() = _parent

    fun onAdded(parent: ElementHost<*>) {
        this._parent = parent
    }

    override fun removeChild(child: Element<*, *>) {
        underlying.removeChild(child.underlying)
        _children.remove(child)
    }

    override fun remove() {
        parent?.removeChild(this)
        underlying.remove()
    }

    /**
     * This will result in errors if you attempt to access a no-longer present field of underlying
     *
     * TODO check
     */
    fun replaceUnderlyingWith(newTag: String) {
        val new = kotlin.browser.document.createElement(newTag).asDynamic() as U

        new.innerHTML = underlying.innerHTML
        underlying.getAttributeNames().forEach {
            //TODO make sure this adds attributes
            new.setAttribute(it, underlying.attributes[it]!!.value)
        }
        this.underlying = new
    }

    @KframeDSL
    inline operator fun invoke(builder: Builder<in S>): S {
        builder(this as S)
        return this
    }

    override fun addText(text: String): TextElement {
        val t = document.createTextNode(text)
        underlying.appendChild(t)

        val te = TextElement(t)
        if (addSubscriber != null)
            addSubscriber!!(te)

        return te
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
            classes += klass

        if (id.isNotBlank())
            this.id = id

        builder(this as S)

        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as Element<*, *>

        if (tag != other.tag) return false
        if (underlying != other.underlying) return false
        if (attributes != other.attributes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + underlying.hashCode()
        result = 31 * result + attributes.hashCode()
        return result
    }

}

abstract class DisplayElement<U : HTMLElement, S : DisplayElement<U, S>>(tag: String) : Element<U, S>(tag),
    IDisplayHost<S>

abstract class MetaElement<U : HTMLElement, S : MetaElement<U, S>>(tag: String) : Element<U, S>(tag), IMetaHost<S>

//TODO easy dynamic text

class TextElement(private var _underlying: Text) : Removable {
    var value: String
        get() = _underlying.wholeText
        set(v) {
            val new = Text(v)
            _underlying.replaceWith(new)
            _underlying = new
        }

    val underlying get() = _underlying

    override fun remove() {
        underlying.remove()
    }
}

class BasicDisplayElement<U : HTMLElement>(tag: String) : DisplayElement<U, BasicDisplayElement<U>>(tag)
class BasicMetaElement<U : HTMLElement>(tag: String) : DisplayElement<U, BasicMetaElement<U>>(tag)

typealias BasicDisplayBuilder<U> = BasicDisplayElement<U>.() -> Unit
typealias BasicMetaBuilder<U> = BasicMetaElement<U>.() -> Unit

var ElementHost<*>.addSubscriber: ((Removable) -> Unit)?
    get() = if (this is AnyElement)
        this.addSubscriber
    else
        (this as W3ElementWrapper<*, *>).addSubscriber
    set(v) {
        if (this is AnyElement)
            this.addSubscriber = v
        else
            (this as W3ElementWrapper<*, *>).addSubscriber = v
    }