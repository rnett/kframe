package com.rnett.kframe.structure

import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.xhr.ProgressEvent

data class EventHandler(
    val element: AnyElement,
    val event: String,
    val useCapture: Boolean,
    val handler: (Event) -> Unit
) {
    fun remove() {
        element.underlying.removeEventListener(event, handler, useCapture)
    }
}

fun AnyElement.removeEventHandler(eh: EventHandler) = if (eh.element == this) eh.remove() else Unit

class Events<E : Element<U, E>, U : HTMLElement>(val element: E) {

    inline fun <H : Event> on(event: String, useCapture: Boolean = false, noinline handler: (H) -> Unit) =
        element.on(event, useCapture, handler)

    fun abort(useCapture: Boolean = false, handler: (UIEvent) -> Unit) = on("abort", useCapture, handler)
    fun afterprint(useCapture: Boolean = false, handler: (Event) -> Unit) = on("afterprint", useCapture, handler)
    fun animationend(useCapture: Boolean = false, handler: (Event) -> Unit) = on("animationend", useCapture, handler)
    fun animationiteration(useCapture: Boolean = false, handler: (Event) -> Unit) =
        on("animationiteration", useCapture, handler)

    fun animationstart(useCapture: Boolean = false, handler: (Event) -> Unit) =
        on("animationstart", useCapture, handler)

    fun beforeprint(useCapture: Boolean = false, handler: (Event) -> Unit) = on("beforeprint", useCapture, handler)
    fun beforeunload(useCapture: Boolean = false, handler: (UIEvent) -> Unit) = on("beforeunload", useCapture, handler)
    fun blur(useCapture: Boolean = false, handler: (FocusEvent) -> Unit) = on("blur", useCapture, handler)
    fun canplay(useCapture: Boolean = false, handler: (Event) -> Unit) = on("canplay", useCapture, handler)
    fun canplaythrough(useCapture: Boolean = false, handler: (Event) -> Unit) =
        on("canplaythrough", useCapture, handler)

    fun change(useCapture: Boolean = false, handler: (Event) -> Unit) = on("change", useCapture, handler)
    fun click(useCapture: Boolean = false, handler: (MouseEvent) -> Unit) = on("click", useCapture, handler)
    fun contextmenu(useCapture: Boolean = false, handler: (MouseEvent) -> Unit) = on("contextmenu", useCapture, handler)
    fun copy(useCapture: Boolean = false, handler: (Event) -> Unit) = on("copy", useCapture, handler)
    fun cut(useCapture: Boolean = false, handler: (Event) -> Unit) = on("cut", useCapture, handler)
    fun dblclick(useCapture: Boolean = false, handler: (MouseEvent) -> Unit) = on("dblclick", useCapture, handler)
    fun drag(useCapture: Boolean = false, handler: (DragEvent) -> Unit) = on("drag", useCapture, handler)
    fun dragend(useCapture: Boolean = false, handler: (DragEvent) -> Unit) = on("dragend", useCapture, handler)
    fun dragenter(useCapture: Boolean = false, handler: (DragEvent) -> Unit) = on("dragenter", useCapture, handler)
    fun dragleave(useCapture: Boolean = false, handler: (DragEvent) -> Unit) = on("dragleave", useCapture, handler)
    fun dragover(useCapture: Boolean = false, handler: (DragEvent) -> Unit) = on("dragover", useCapture, handler)
    fun dragstart(useCapture: Boolean = false, handler: (DragEvent) -> Unit) = on("dragstart", useCapture, handler)
    fun drop(useCapture: Boolean = false, handler: (DragEvent) -> Unit) = on("drop", useCapture, handler)
    fun durationchange(useCapture: Boolean = false, handler: (Event) -> Unit) =
        on("durationchange", useCapture, handler)

    fun ended(useCapture: Boolean = false, handler: (Event) -> Unit) = on("ended", useCapture, handler)
    fun error(useCapture: Boolean = false, handler: (ProgressEvent) -> Unit) = on("error", useCapture, handler)
    fun focus(useCapture: Boolean = false, handler: (FocusEvent) -> Unit) = on("focus", useCapture, handler)
    fun focusin(useCapture: Boolean = false, handler: (FocusEvent) -> Unit) = on("focusin", useCapture, handler)
    fun focusout(useCapture: Boolean = false, handler: (FocusEvent) -> Unit) = on("focusout", useCapture, handler)
    fun fullscreenchange(useCapture: Boolean = false, handler: (Event) -> Unit) =
        on("fullscreenchange", useCapture, handler)

    fun fullscreenerror(useCapture: Boolean = false, handler: (Event) -> Unit) =
        on("fullscreenerror", useCapture, handler)

    fun hashchange(useCapture: Boolean = false, handler: (HashChangeEvent) -> Unit) =
        on("hashchange", useCapture, handler)

    fun input(useCapture: Boolean = false, handler: (InputEvent) -> Unit) = on("input", useCapture, handler)
    fun invalid(useCapture: Boolean = false, handler: (Event) -> Unit) = on("invalid", useCapture, handler)
    fun keydown(useCapture: Boolean = false, handler: (KeyboardEvent) -> Unit) = on("keydown", useCapture, handler)
    fun keypress(useCapture: Boolean = false, handler: (KeyboardEvent) -> Unit) = on("keypress", useCapture, handler)
    fun keyup(useCapture: Boolean = false, handler: (KeyboardEvent) -> Unit) = on("keyup", useCapture, handler)
    fun load(useCapture: Boolean = false, handler: (UIEvent) -> Unit) = on("load", useCapture, handler)
    fun loadeddata(useCapture: Boolean = false, handler: (Event) -> Unit) = on("loadeddata", useCapture, handler)
    fun loadedmetadata(useCapture: Boolean = false, handler: (Event) -> Unit) =
        on("loadedmetadata", useCapture, handler)

    fun loadstart(useCapture: Boolean = false, handler: (ProgressEvent) -> Unit) = on("loadstart", useCapture, handler)
    fun message(useCapture: Boolean = false, handler: (Event) -> Unit) = on("message", useCapture, handler)
    fun mousedown(useCapture: Boolean = false, handler: (MouseEvent) -> Unit) = on("mousedown", useCapture, handler)
    fun mouseenter(useCapture: Boolean = false, handler: (MouseEvent) -> Unit) = on("mouseenter", useCapture, handler)
    fun mouseleave(useCapture: Boolean = false, handler: (MouseEvent) -> Unit) = on("mouseleave", useCapture, handler)
    fun mousemove(useCapture: Boolean = false, handler: (MouseEvent) -> Unit) = on("mousemove", useCapture, handler)
    fun mouseover(useCapture: Boolean = false, handler: (MouseEvent) -> Unit) = on("mouseover", useCapture, handler)
    fun mouseout(useCapture: Boolean = false, handler: (MouseEvent) -> Unit) = on("mouseout", useCapture, handler)
    fun mouseup(useCapture: Boolean = false, handler: (MouseEvent) -> Unit) = on("mouseup", useCapture, handler)
    fun mousewheel(useCapture: Boolean = false, handler: (WheelEvent) -> Unit) = on("mousewheel", useCapture, handler)
    fun offline(useCapture: Boolean = false, handler: (Event) -> Unit) = on("offline", useCapture, handler)
    fun online(useCapture: Boolean = false, handler: (Event) -> Unit) = on("online", useCapture, handler)
    fun open(useCapture: Boolean = false, handler: (Event) -> Unit) = on("open", useCapture, handler)
    fun pagehide(useCapture: Boolean = false, handler: (PageTransitionEvent) -> Unit) =
        on("pagehide", useCapture, handler)

    fun pageshow(useCapture: Boolean = false, handler: (PageTransitionEvent) -> Unit) =
        on("pageshow", useCapture, handler)

    fun paste(useCapture: Boolean = false, handler: (Event) -> Unit) = on("paste", useCapture, handler)
    fun pause(useCapture: Boolean = false, handler: (Event) -> Unit) = on("pause", useCapture, handler)
    fun play(useCapture: Boolean = false, handler: (Event) -> Unit) = on("play", useCapture, handler)
    fun playing(useCapture: Boolean = false, handler: (Event) -> Unit) = on("playing", useCapture, handler)
    fun popstate(useCapture: Boolean = false, handler: (PopStateEvent) -> Unit) = on("popstate", useCapture, handler)
    fun progress(useCapture: Boolean = false, handler: (Event) -> Unit) = on("progress", useCapture, handler)
    fun ratechange(useCapture: Boolean = false, handler: (Event) -> Unit) = on("ratechange", useCapture, handler)
    fun resize(useCapture: Boolean = false, handler: (UIEvent) -> Unit) = on("resize", useCapture, handler)
    fun reset(useCapture: Boolean = false, handler: (Event) -> Unit) = on("reset", useCapture, handler)
    fun scroll(useCapture: Boolean = false, handler: (UIEvent) -> Unit) = on("scroll", useCapture, handler)
    fun search(useCapture: Boolean = false, handler: (Event) -> Unit) = on("search", useCapture, handler)
    fun seeked(useCapture: Boolean = false, handler: (Event) -> Unit) = on("seeked", useCapture, handler)
    fun seeking(useCapture: Boolean = false, handler: (Event) -> Unit) = on("seeking", useCapture, handler)
    fun select(useCapture: Boolean = false, handler: (UIEvent) -> Unit) = on("select", useCapture, handler)
    fun show(useCapture: Boolean = false, handler: (Event) -> Unit) = on("show", useCapture, handler)
    fun stalled(useCapture: Boolean = false, handler: (Event) -> Unit) = on("stalled", useCapture, handler)
    fun storage(useCapture: Boolean = false, handler: (StorageEvent) -> Unit) = on("storage", useCapture, handler)
    fun submit(useCapture: Boolean = false, handler: (Event) -> Unit) = on("submit", useCapture, handler)
    fun suspend(useCapture: Boolean = false, handler: (Event) -> Unit) = on("suspend", useCapture, handler)
    fun timeupdate(useCapture: Boolean = false, handler: (Event) -> Unit) = on("timeupdate", useCapture, handler)
    fun toggle(useCapture: Boolean = false, handler: (Event) -> Unit) = on("toggle", useCapture, handler)
    fun touchcancel(useCapture: Boolean = false, handler: (UIEvent) -> Unit) = on("touchcancel", useCapture, handler)
    fun touchend(useCapture: Boolean = false, handler: (UIEvent) -> Unit) = on("touchend", useCapture, handler)
    fun touchmove(useCapture: Boolean = false, handler: (UIEvent) -> Unit) = on("touchmove", useCapture, handler)
    fun touchstart(useCapture: Boolean = false, handler: (UIEvent) -> Unit) = on("touchstart", useCapture, handler)
    fun transitionend(useCapture: Boolean = false, handler: (UIEvent) -> Unit) =
        on("transitionend", useCapture, handler)

    fun unload(useCapture: Boolean = false, handler: (UIEvent) -> Unit) = on("unload", useCapture, handler)
    fun volumechange(useCapture: Boolean = false, handler: (Event) -> Unit) = on("volumechange", useCapture, handler)
    fun waiting(useCapture: Boolean = false, handler: (Event) -> Unit) = on("waiting", useCapture, handler)
    fun wheel(useCapture: Boolean = false, handler: (WheelEvent) -> Unit) = on("wheel", useCapture, handler)

}