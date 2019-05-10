package com.rnett.kframe.dom.bootstrap

enum class Breakpoint(val klass: String) {
    Small("sm"), Medium("md"), Large("lg"), XL("xl"), None("");

    val classAddon = if (klass.isBlank()) "" else "-$klass"

}