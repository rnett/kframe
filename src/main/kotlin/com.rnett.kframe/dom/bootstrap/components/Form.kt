package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayElement
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLDivElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class FormGroup : DisplayElement<HTMLDivElement, FormGroup>("div") {

    init {
        classes += "form-group"
        classes += "bmd-form-group"
    }
}

@KframeDSL
inline fun DisplayHost.formGroup(
    klass: String = "", id: String = "",
    builder: Builder<FormGroup> = {}
): FormGroup {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +FormGroup()(klass, id, builder)
}

//TODO radio buttons