package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.structure.Document
import com.rnett.kframe.structure.Element
import com.rnett.kframe.structure.addons.`$`
import com.rnett.kframe.structure.addons.attributeEquals
import com.rnett.kframe.structure.addons.get


fun enableAllTooltips() {
    Document[`$`.attributeEquals("data-toggle", "tooltip")].forEach {
        it.underlying.asDynamic().popover()
    }
}

fun Element<*, *>.tooltip(
    title: String? = null,
    direction: PopoverDirection? = null,
    setTabIndex: Boolean = false
) {
    data.toggle = "tooltip"

    if (title != null)
        this.title = title

    if (direction != null)
        data.placement = direction.name.toLowerCase()

    if (setTabIndex)
        attributes["tabIndex"] = 0
}

var Element<*, *>.tooltip: String?
    get() {
        if (title == null || data.toggle != "tooltip")
            return null
        else
            return title
    }
    set(text) {
        tooltip(text)
    }