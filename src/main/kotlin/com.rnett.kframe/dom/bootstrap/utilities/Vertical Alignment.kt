package com.rnett.kframe.dom.bootstrap.utilities

import com.rnett.kframe.dom.bootstrap.core.IHasClass

enum class VerticalAlignment(override val klass: String) : IHasClass {
    Baseline("align-baseline"),
    Top("align-top"), Middle("align-middle"), Bottom("align-bottom"),
    TextTop("align-text-top"), TextBottom("align-text-bottom");
}