package com.rnett.kframe.dom.bootstrap.core

import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.structure.Classes
import com.rnett.kframe.structure.DisplayElement
import com.rnett.kframe.structure.Element
import org.w3c.dom.HTMLElement

interface IHasClass {
    val klass: String
}

abstract class ClassElement<U : HTMLElement, S : ClassElement<U, S>>(tag: String, val addedClass: String) :
    DisplayElement<U, S>(tag) {

    init {
        classes += addedClass
    }

    val String.withClass get() = "$addedClass-$this"

    val Classes.PresentDelegate.withClass get() = this@ClassElement.classes.presentDelegate(this.klass, addedClass)
    val <T> Classes.ClassDelegate<T>.withClass
        get() = this@ClassElement.classes.classDelegate(
            this.value,
            { toClass(it).withClass })
    val <T> Classes.OptionalClassDelegate<T>.withClass
        get() = this@ClassElement.classes.optionalClassDelegate(
            this.value,
            { toClass(it)?.withClass })
}

enum class ElementSize(override val klass: String) : IHasClass {
    Small("sm"), Large("lg");
}

abstract class SizedClassElement<U : HTMLElement, S : SizedClassElement<U, S>>(tag: String, addedClass: String) :
    ClassElement<U, S>(tag, addedClass) {
    var size by classes.optionalClassDelegate<ElementSize>()
}

fun Element<*, *>.applyContext(contextType: ContextType?, prefix: String? = null, onNull: String? = null) {

    val klass = if (this is ClassElement<*, *> && prefix == null)
        contextType?.name?.toLowerCase()?.withClass ?: onNull
    else {
        if (contextType == null)
            onNull
        else
            "$prefix-${contextType.name.toLowerCase()}"
    }

    if (klass != null)
        classes += klass
}

