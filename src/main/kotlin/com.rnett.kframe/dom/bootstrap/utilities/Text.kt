package com.rnett.kframe.dom.bootstrap.utilities

import com.rnett.kframe.dom.bootstrap.core.IHasClass


enum class TextAlignment : IHasClass {
    Left, Right, Center, Justify;

    override val klass: String
        get() = "text-${name.toLowerCase()}"
}

enum class TextTransform : IHasClass {
    Lowercase, Uppercase, Capitalize;

    override val klass: String
        get() = "text-${name.toLowerCase()}"
}

enum class FontWeight : IHasClass {
    Bold, Bolder, Normal, Light, Lighter;

    override val klass: String
        get() = "font-weight-${name.toLowerCase()}"
}