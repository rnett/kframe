package com.rnett.kframe.dom.bootstrap.utilities

import com.rnett.kframe.dom.bootstrap.Breakpoint
import com.rnett.kframe.structure.AnyElement
import com.rnett.kframe.structure.Element

enum class Display(val value: String) {
    None("none"), Inline("inline"), InlineBlock("inline-block"), Block("block"),
    Table("table"), TableCell("table-cell"), TableRow("table-row"),
    Flex("flex"), InlineFlex("inline-flex");
}

private val oldSizes = mutableMapOf<AnyElement, List<String>>()
fun Element<*, *>.hide() {
    if ("d-none" in classes)
        return

    oldSizes[this] = classes.filter { it.startsWith("d-") }
    classes.removeAll { it.startsWith("d-") }
    bootstrap.display(Display.None)
}

fun Element<*, *>.show() {
    if ("d-none" !in classes)
        return

    classes.remove("d-none")
    classes.addAll(oldSizes[this] ?: emptyList())
    oldSizes.remove(this)
}

fun Bootstrap.display(display: Display?, breakpoint: Breakpoint? = null) {
    if (display == null)
        element.classes.removeAll { it.startsWith("d-") }
    else {
        if (breakpoint == null || breakpoint == Breakpoint.None)
            element.classes += "d-${display.value}"
        else
            element.classes += "d-${breakpoint.klass}-${display.value}"
    }
}