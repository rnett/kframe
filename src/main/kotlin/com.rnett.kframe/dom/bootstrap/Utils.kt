package com.rnett.kframe.dom.bootstrap

import com.rnett.kframe.structure.DisplayElement

enum class ContextType {
    Primary, Secondary, Success, Danger, Warning, Info, Light, Dark;

    fun klass(starter: String) = "$starter-${this.name.toLowerCase()}"
}

enum class TextAlignment {
    Left, Right, Center, Justify;
}

fun DisplayElement<*, *>.textAlign(alignment: TextAlignment) {
    classes += "text-${alignment.name.toLowerCase()}"
}

fun DisplayElement<*, *>.mutedText() {
    classes += "text-muted"
}

fun DisplayElement<*, *>.textColor(type: ContextType) {
    classes += type.klass("text")
}

fun DisplayElement<*, *>.backgroundColor(type: ContextType) {
    classes += type.klass("bg")
}

fun DisplayElement<*, *>.transparentBackground() {
    classes += "bg-transparent"
}

