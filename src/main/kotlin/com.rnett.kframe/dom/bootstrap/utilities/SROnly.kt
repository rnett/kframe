package com.rnett.kframe.dom.bootstrap.utilities

import com.rnett.kframe.structure.AnyElement

fun AnyElement.srOnly(focusable: Boolean = false) {
    classes += "sr-only"
    if (focusable)
        classes += "sr-only-focusable"
}