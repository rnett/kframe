package com.rnett.kframe.dom.bootstrap.utilities

import com.rnett.kframe.dom.bootstrap.Breakpoint
import com.rnett.kframe.dom.bootstrap.core.IHasClass

enum class Justification(override val klass: String) : IHasClass {
    Start("justify-content-start"), Center("justify-content-center"), End("justify-content-end"),
    Around("justify-content-around"), Between("justify-content-between");
}


enum class Alignment(private val klass: String) {
    Start("start"), Center("center"), End("end"),
    Baseline("baseline"), Stretch("stretch");

    val itemClass = "align-items-$klass"
    val selfClass = "align-self-$klass"
    val contentClass = "align-content-$klass"

}

fun Bootstrap.flex(inline: Boolean = false, breakpoint: Breakpoint? = null) {
    if (inline)
        display(Display.InlineFlex, breakpoint)
    else
        display(Display.Flex, breakpoint)
}

fun Bootstrap.Flex.row(reverse: Boolean = false) {
    if (reverse)
        bootstrap.element.classes += "flex-row-reverse"
    else
        bootstrap.element.classes += "flex-row"
}

fun Bootstrap.Flex.column(reverse: Boolean = false) {
    if (reverse)
        bootstrap.element.classes += "flex-column-reverse"
    else
        bootstrap.element.classes += "flex-column"
}

enum class Wrapping(override val klass: String) : IHasClass {
    None("flex-nowrap"), Wrap("flex-wrap"), Reverse("flex-wrap-reverse")
}