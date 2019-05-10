package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.structure.Document
import com.rnett.kframe.structure.Element
import com.rnett.kframe.structure.addons.`$`
import com.rnett.kframe.structure.addons.attributeEquals
import com.rnett.kframe.structure.addons.get

enum class PopoverDirection {
    Top, Right, Bottom, Left;
}

fun enableAllPopovers() {
    Document[`$`.attributeEquals("data-toggle", "popover")].forEach {
        it.underlying.asDynamic().popover()
    }
}

fun Element<*, *>.popover(
    title: String? = null,
    content: String? = null,
    direction: PopoverDirection? = null,
    dismissOnClick: Boolean = true,
    forceToA: Boolean = false
) {
    data.toggle = "popover"

    if (title != null)
        this.title = title

    if (content != null)
        data.content = content

    if (direction != null)
        data.placement = direction.name.toLowerCase()

    if (dismissOnClick) {

        if (forceToA)
            this.replaceUnderlyingWith("a")

        data.trigger = "focus"
        attributes["tabIndex"] = 0
    }
}