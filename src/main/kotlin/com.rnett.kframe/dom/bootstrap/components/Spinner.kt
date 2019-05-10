package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.dom.bootstrap.core.ClassElement
import org.w3c.dom.HTMLDivElement

class Spinner(color: ContextType) : ClassElement<HTMLDivElement, Spinner>("div", "spinner-border") {
    var color: ContextType by classes.classDelegate(color) { it.klass("text") }

    var small by classes.presentDelegate("sm").withClass
}

class PulseSpinner(color: ContextType) : ClassElement<HTMLDivElement, PulseSpinner>("div", "spinner-grow") {
    var color: ContextType by classes.classDelegate(color) { it.klass("text") }

    var small by classes.presentDelegate("sm").withClass
}