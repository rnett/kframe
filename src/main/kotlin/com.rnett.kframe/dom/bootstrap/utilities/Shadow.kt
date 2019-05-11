package com.rnett.kframe.dom.bootstrap.utilities

import com.rnett.kframe.dom.bootstrap.core.IHasClass

enum class Shadow(override val klass: String) : IHasClass {
    None("shadow-none"), Small("shadow-sm"), Regular("shadow"), Large("shadow-lg");
}