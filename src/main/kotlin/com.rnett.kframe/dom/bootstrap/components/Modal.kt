package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.dom.bootstrap.Heading
import com.rnett.kframe.dom.bootstrap.core.ClassElement
import com.rnett.kframe.dom.bootstrap.h
import com.rnett.kframe.dom.div
import com.rnett.kframe.dom.rawButton
import com.rnett.kframe.dom.span
import com.rnett.kframe.lazyInit
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.Events
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.UIEvent
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Modal : ClassElement<HTMLDivElement, Modal>("div", "modal") {
    init {
        attributes["role"] = "dialog"
    }

    enum class Size(val klass: String?) {
        Small("sm"), Default(null), Large("lg"), ExtraLarge("xl");
    }

    var size by classes.optionalClassDelegate<Size> { it.klass }.withClass

    val dialog = div("modal-dialog") {
        attributes["role"] = "document"
    }

    @Deprecated("You are most likely looking for Modal.body", replaceWith = ReplaceWith("body"))
    var content: ModalContent by lazyInit { dialog.run { +ModalContent() } }
    var header: ModalHeader by lazyInit { body.run { +ModalHeader() } }
    var footer: ModalFooter by lazyInit { body.run { +ModalFooter() } }
    var body: ModalBody by lazyInit { body.run { +ModalBody() } }

    var fade by classes.presentDelegate

    init {
        fade = true
    }

    var scrollable by dialog.classes.presentDelegate("modal-dialog-scrollable")
    var verticallyCentered by dialog.classes.presentDelegate("modal-dialog-centered")

    fun activate() {
        underlying.asDynamic().modal("show")
    }

    fun deactivate() {
        underlying.asDynamic().modal("hide")
    }

}

@KframeDSL
inline fun DisplayHost.modal(
    klass: String = "", id: String = "",
    builder: Builder<Modal> = {}
): Modal {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return +Modal()(klass, id, builder)
}

class ModalContent : ClassElement<HTMLDivElement, ModalContent>("div", "modal-content")

class ModalHeader : ClassElement<HTMLDivElement, ModalHeader>("div", "modal-header") {

    @KframeDSL
    fun closeButton() {
        rawButton("close") {
            data.dismiss = "modal"
            span {
                +"&times;"
            }
        }
    }

}

class ModalFooter : ClassElement<HTMLDivElement, ModalFooter>("div", "modal-footer")
class ModalBody : ClassElement<HTMLDivElement, ModalBody>("div", "modal-body")

fun Button.dismissModal() {
    data.dismiss = "modal"
}

@KframeDSL
inline fun ModalHeader.title(
    title: String = "",
    level: Int = 5,
    klass: String = "", id: String = "",
    builder: Builder<Heading> = {}
): Heading {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return h(level, "modal-title")(klass, id) {

        if (title.isNotBlank())
            +title

        builder()
    }
}

fun Button.activateModal(modal: Modal) {
    data.toggle = "modal"
    data.target = "#${modal.idOrRandom}"
}

fun Events<Modal, *>.show(useCapture: Boolean = false, handler: (UIEvent) -> Unit) =
    on("show.bs.modal", useCapture, handler)

fun Events<Modal, *>.shown(useCapture: Boolean = false, handler: (UIEvent) -> Unit) =
    on("shown.bs.modal", useCapture, handler)

fun Events<Modal, *>.hide(useCapture: Boolean = false, handler: (UIEvent) -> Unit) =
    on("hide.bs.modal", useCapture, handler)

fun Events<Modal, *>.hidden(useCapture: Boolean = false, handler: (UIEvent) -> Unit) =
    on("hidden.bs.modal", useCapture, handler)
