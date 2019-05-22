package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.dom.bootstrap.core.ClassElement
import com.rnett.kframe.dom.bootstrap.utilities.srOnly
import com.rnett.kframe.dom.span
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import com.rnett.kframe.structure.TextElement
import org.w3c.dom.HTMLDivElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Spinner(color: ContextType) : ClassElement<HTMLDivElement, Spinner>("div", "spinner-border") {
    var color: ContextType by classes.classDelegate(color) { it.klass("text") }

    var small by classes.presentDelegate("sm").withClass

    val loadingText: TextElement

    init {
        role = "status"
        span {
            srOnly()
            loadingText = +"Loading..."
        }
    }
}

class PulseSpinner(color: ContextType) : ClassElement<HTMLDivElement, PulseSpinner>("div", "spinner-grow") {
    var color: ContextType by classes.classDelegate(color) { it.klass("text") }

    var small by classes.presentDelegate("sm").withClass
}

@KframeDSL
inline fun DisplayHost.spinner(
    color: ContextType,
    klass: String = "", id: String = "",
    builder: Builder<Spinner> = {}
): Spinner {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Spinner(color)(klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.pulseSpinner(
    color: ContextType,
    klass: String = "", id: String = "",
    builder: Builder<PulseSpinner> = {}
): PulseSpinner {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +PulseSpinner(color)(klass, id, builder)
}
