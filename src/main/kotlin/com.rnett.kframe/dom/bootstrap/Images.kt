package com.rnett.kframe.dom.bootstrap

import com.rnett.kframe.dom.ImageElement
import com.rnett.kframe.dom.img
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KframeDSL
inline fun DisplayHost.imgFluid(
    src: String,
    klass: String = "", id: String = "",
    builder: Builder<ImageElement> = {}
): ImageElement {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return img(src, klass, id) {
        classes += "img-fluid"
        builder()
    }
}

@KframeDSL
fun ImageElement.thumbnail() {
    classes += "img-thumbnail"
}