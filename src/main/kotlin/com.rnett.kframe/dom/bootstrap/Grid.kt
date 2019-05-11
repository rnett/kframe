package com.rnett.kframe.dom.bootstrap

import com.rnett.kframe.dom.bootstrap.utilities.Alignment
import com.rnett.kframe.dom.bootstrap.utilities.Justification
import com.rnett.kframe.dom.div
import com.rnett.kframe.structure.Builder
import com.rnett.kframe.structure.DisplayElement
import com.rnett.kframe.structure.DisplayHost
import com.rnett.kframe.structure.KframeDSL
import org.w3c.dom.HTMLDivElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Row : DisplayElement<HTMLDivElement, Row>("div") {
    init {
        klass = "row"
    }

    var noGutters by classes.presentDelegate("no-gutters")


    var alignItems by classes.optionalClassDelegate<Alignment> { it.itemClass }


    var justifyContent by classes.optionalClassDelegate<Justification>()

}

@KframeDSL
inline fun DisplayHost.row(
    id: String = "",
    builder: Builder<Row> = {}
): Row {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return +Row()(id = id)(builder)
}

class Column(val size: Size, val breakpoint: Breakpoint = Breakpoint.None) :
    DisplayElement<HTMLDivElement, Column>("div") {

    sealed class Size(val classAddon: String) {
        data class Fixed(val size: Int) : Size("-$size")
        object Auto : Size("-auto")
        object Equal : Size("")
    }

    init {
        klass = "col" + breakpoint.classAddon + size.classAddon
    }

    var alignSelf by classes.optionalClassDelegate<Alignment> { it.selfClass }

    var order by classes.optionalClassDelegate<Int> { "order-$it" }

    private var lastOffset: Int? = null
    var offset: Int?
        get() = lastOffset
        set(v) {
            if (lastOffset != null)
                classes.remove("offset-$lastOffset")

            if (v != null)
                classes.add("offset-$v")

            lastOffset = v
        }

}

@KframeDSL
inline fun Row.col(
    size: Column.Size,
    breakpoint: Breakpoint = Breakpoint.None,
    id: String = "",
    builder: Builder<Column> = {}
): Column {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return +Column(size, breakpoint)(id = id)(builder)
}

@KframeDSL
inline fun Row.col(
    size: Int? = null,
    breakpoint: Breakpoint = Breakpoint.None,
    id: String = "",
    builder: Builder<Column> = {}
): Column {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return col(size?.let { Column.Size.Fixed(it) } ?: Column.Size.Equal, breakpoint, id, builder)
}

@KframeDSL
fun Row.horizontalBreak() = div {
    klass = "w-100"
}