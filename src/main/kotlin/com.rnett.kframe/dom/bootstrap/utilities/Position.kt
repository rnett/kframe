package com.rnett.kframe.dom.bootstrap.utilities

import com.rnett.kframe.dom.bootstrap.core.IHasClass

enum class Position(override val klass: String) : IHasClass {
    Static("position-static"), Relative("position-relative"), Absolute("position-absolute"),
    Fixed("position-fixed"), Sticky("position-sticky"),
    FixedTop("fixed-top"), FixedBottom("fixed-bottom"), StickyTop("sticky-top");
}