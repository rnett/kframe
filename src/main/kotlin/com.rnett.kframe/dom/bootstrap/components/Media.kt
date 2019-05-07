package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.bootstrap.core.ClassElement
import com.rnett.kframe.structure.BasicDisplayBuilder
import com.rnett.kframe.structure.BasicDisplayElement
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLDivElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Media : ClassElement<HTMLDivElement, Media>("div", "media")

@KframeDSL
inline fun Media.body(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLDivElement> = {}
): BasicDisplayElement<HTMLDivElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicDisplayElement<HTMLDivElement>("div")("media-body")(klass, id, builder)
}