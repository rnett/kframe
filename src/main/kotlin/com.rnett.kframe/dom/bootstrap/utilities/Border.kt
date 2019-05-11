package com.rnett.kframe.dom.bootstrap.utilities

import com.rnett.kframe.dom.bootstrap.core.IHasClass

//TODO https://getbootstrap.com/docs/4.3/utilities/borders/
// probably use enums and class delegates


enum class Borders(override val klass: String) : IHasClass {
    All("border"),
    Top("border-top"), Right("border-right"), Bottom("border-bottom"), Left("border-left");
}

fun Bootstrap.Border.activated(vararg border: Borders?) {
    border.forEach {
        if (it == null)
            bootstrap.element.classes.removeAll { it.startsWith("border") }
        else
            bootstrap.element.classes += it.klass
    }
}

fun Bootstrap.Border.deactivated(vararg border: Borders?) {
    border.forEach {
        if (it == null)
            bootstrap.element.classes.removeAll { it.startsWith("border") }
        else
            bootstrap.element.classes += "${it.klass}-0}"
    }
}

enum class BorderRounding(override val klass: String) : IHasClass {
    All("rounded"),
    Top("rounded-top"), Right("rounded-right"), Bottom("rounded-bottom"), Left("rounded-left"),
    Circle("rounded-circle"), Pill("rounded-pill"), None("rounded-0");

    enum class Size(override val klass: String) : IHasClass {
        Small("rounded-sm"), Large("rounded-lg");
    }
}