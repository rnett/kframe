package com.rnett.kframe.dom

import com.rnett.kframe.structure.BasicDisplayBuilder
import com.rnett.kframe.structure.BasicDisplayElement
import com.rnett.kframe.structure.DisplayHost
import org.w3c.dom.HTMLDivElement

fun DisplayHost.div(builder: BasicDisplayBuilder<HTMLDivElement>) =
    +BasicDisplayElement<HTMLDivElement>("div")(builder)