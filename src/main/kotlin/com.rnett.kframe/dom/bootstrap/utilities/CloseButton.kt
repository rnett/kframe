package com.rnett.kframe.dom.bootstrap.utilities

import com.rnett.kframe.dom.rawButton
import com.rnett.kframe.dom.span
import com.rnett.kframe.structure.BasicDisplayBuilder
import com.rnett.kframe.structure.BasicDisplayElement
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLButtonElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KframeDSL
fun DisplayHost.closeButton(
    klass: String = "",
    id: String = "",
    builder: BasicDisplayBuilder<HTMLButtonElement>
): BasicDisplayElement<HTMLButtonElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return rawButton(klass = klass, id = id) {
        classes += "close"
        attributes["type"] = "button"
        span {
            +"&times;"
        }

        builder()
    }
}