package com.rnett.kframe.dom.bootstrap.components

import com.rnett.kframe.structure.DisplayElement
import org.w3c.dom.HTMLDivElement

class FormGroup : DisplayElement<HTMLDivElement, FormGroup>("div") {

    init {
        classes += "form-group"
    }
}

//TODO radio buttons