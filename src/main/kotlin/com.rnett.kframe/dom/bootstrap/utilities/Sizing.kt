package com.rnett.kframe.dom.bootstrap.utilities

enum class Sizing(private val klass: String) {
    Auto("auto"), P25("25"), P50("50"), P75("75"), P100("100");

    val widthClass = "w-$klass"
    val heightClass = "h-$klass"
}