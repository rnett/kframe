package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.div
import com.rnett.kframe.structure.*
import org.w3c.dom.HTMLDivElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class InputGroup : DisplayElement<HTMLDivElement, InputGroup>("div") {

    init {
        classes += "input-group"
    }

    fun large() {
        classes += "input-group-lg"
    }

    fun small() {
        classes += "input-group-sm"
    }
}

@KframeDSL
inline fun DisplayHost.inputGroup(
    klass: String = "", id: String = "",
    builder: Builder<InputGroup> = {}
): InputGroup {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +InputGroup()(klass, id, builder)
}

enum class InputGroupPosition {
    Prepend, Append;
}

class InputGroupPart(val position: InputGroupPosition) : DisplayElement<HTMLDivElement, InputGroupPart>("div") {
    init {
        classes += "input-group-${position.name.toLowerCase()}"
    }
}

@KframeDSL
inline fun InputGroup.inputGroupPart(
    position: InputGroupPosition,
    klass: String = "", id: String = "",
    builder: Builder<InputGroupPart> = {}
): InputGroupPart {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +InputGroupPart(position)(klass, id, builder)
}

@KframeDSL
inline fun InputGroup.prepend(
    klass: String = "", id: String = "",
    builder: Builder<InputGroupPart> = {}
): InputGroupPart {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return inputGroupPart(InputGroupPosition.Prepend, klass, id, builder)
}

@KframeDSL
inline fun InputGroup.append(
    klass: String = "", id: String = "",
    builder: Builder<InputGroupPart> = {}
): InputGroupPart {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return inputGroupPart(InputGroupPosition.Append, klass, id, builder)
}

@KframeDSL
inline fun InputGroupPart.text(
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLDivElement> = {}
): BasicDisplayElement<HTMLDivElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return div(klass, id) {
        classes += "input-group-text"
        builder()
    }
}

@KframeDSL
inline fun InputGroupPart.text(
    text: String,
    klass: String = "", id: String = "",
    builder: BasicDisplayBuilder<HTMLDivElement> = {}
): BasicDisplayElement<HTMLDivElement> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return text(klass, id) {
        +text
        builder()
    }
}
