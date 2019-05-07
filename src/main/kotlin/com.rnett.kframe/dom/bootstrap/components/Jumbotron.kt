package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.p
import com.rnett.kframe.structure.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHRElement
import org.w3c.dom.HTMLParagraphElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@KframeDSL
inline fun DisplayHost.hr(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLHRElement> = {}
): BasicDisplayElement<HTMLHRElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +BasicDisplayElement<HTMLHRElement>("hr")(klass, id, builder)
}

class Jumbotron(val fluid: Boolean) : DisplayElement<HTMLDivElement, Jumbotron>("div") {
    init {
        classes += "jumbotron"

        if (fluid)
            classes += "jumbotron-fluid"

    }
}

@KframeDSL
inline fun Jumbotron.lead(
    text: String = "",
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLParagraphElement> = {}
): BasicDisplayElement<HTMLParagraphElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return p(klass, id) {

        if (text.isNotBlank())
            +text

        classes += "lead"

        builder()
    }
}