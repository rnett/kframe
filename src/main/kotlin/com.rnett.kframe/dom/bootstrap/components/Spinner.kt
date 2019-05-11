package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.dom.bootstrap.core.ClassElement
import com.rnett.kframe.dom.bootstrap.utilities.srOnly
import com.rnett.kframe.dom.span
import com.rnett.kframe.structure.TextElement
import org.w3c.dom.HTMLDivElement

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