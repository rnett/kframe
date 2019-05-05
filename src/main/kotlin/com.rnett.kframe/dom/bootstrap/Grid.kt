package com.rnett.kframe.dom.bootstrap

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
        div { }
    }

    var gutters: Boolean
        get() = "no-gutters" !in classes
        set(v) {
            if (v)
                classes.remove("no-gutters")
            else
                classes.add("no-gutters")
        }

    enum class Alignment(val klass: String) {
        Start("align-items-start"), Center("align-items-center"), End("align-items-end");
    }


    private var lastAlign: Alignment? = null
    var alignItems: Alignment?
        get() = lastAlign
        set(v) {
            if (lastAlign != null)
                classes.remove(lastAlign!!.klass)

            if (v != null)
                classes.add(v.klass)

            lastAlign = v
        }

    enum class Justification(val klass: String) {
        Start("justify-content-start"), Center("justify-content-center"), End("justify-content-end"),
        Around("justify-content-around"), Between("justify-content-between");
    }


    private var lastJustify: Justification? = null
    var justifyContent: Justification?
        get() = lastJustify
        set(v) {
            if (lastJustify != null)
                classes.remove(lastJustify!!.klass)

            if (v != null)
                classes.add(v.klass)

            lastJustify = v
        }

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

class Column(val size: Size, val breakpoint: Breakpoints = Breakpoints.None) :
    DisplayElement<HTMLDivElement, Column>("div") {

    sealed class Size(val classAddon: String) {
        data class Fixed(val size: Int) : Size("-$size")
        object Auto : Size("-auto")
        object Equal : Size("")
    }

    init {
        klass = "col" + breakpoint.classAddon + size.classAddon
    }

    enum class Alignment(val klass: String) {
        Start("align-self-start"), Center("align-self-center"), End("align-self-end");
    }


    private var lastAlign: Alignment? = null
    var alignSelf: Alignment?
        get() = lastAlign
        set(v) {
            if (lastAlign != null)
                classes.remove(lastAlign!!.klass)

            if (v != null)
                classes.add(v.klass)

            lastAlign = v
        }

    private var lastOrder: Int? = null
    var order: Int?
        get() = lastOrder
        set(v) {
            if (lastOrder != null)
                classes.remove("order-$lastOrder")

            if (v != null)
                classes.add("order-$v")

            lastOrder = v
        }

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
    breakpoint: Breakpoints = Breakpoints.None,
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
    breakpoint: Breakpoints = Breakpoints.None,
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