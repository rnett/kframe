package com.rnett.kframe.dom.bootstrap.utilities

import com.rnett.kframe.dom.bootstrap.core.IHasClass

enum class Overflow(override val klass: String) : IHasClass {
    Hidden("overflow-hidden"), Auto("overflow-auto");
}