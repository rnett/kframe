package com.rnett.kframe.dom

import com.rnett.kframe.structure.*
import org.w3c.dom.HTMLTitleElement

class TitleElement() : MetaElement<HTMLTitleElement, TitleElement>("title"){

    private val titleElement = +""

    var title: String
        get() = titleElement.value
        set(v){ titleElement.value = v }

    constructor(title: String): this(){
        this.title = title
    }

}

@KframeDSL
fun MetaHost.title(title: String = "", builder: Builder<TitleElement> = {}) = +TitleElement(title)(builder)