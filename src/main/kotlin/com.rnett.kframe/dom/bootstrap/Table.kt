package com.rnett.kframe.dom.bootstrap

import com.rnett.kframe.dom.div
import com.rnett.kframe.structure.*
import org.w3c.dom.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class TableElement<U : HTMLElement, S : TableElement<U, S>>(tag: String) : DisplayElement<U, S>(tag)

class Table : TableElement<HTMLTableElement, Table>("table") {
    init {
        classes += "table"
    }

    var striped by classes.presentDelegate("table-striped")
    var dark by classes.presentDelegate("table-dark")
    var bordered by classes.presentDelegate("table-bordered")
    var borderless by classes.presentDelegate("table-borderless")
    var hover by classes.presentDelegate("table-hover")
    var small by classes.presentDelegate("table-sm")

}

@KframeDSL
inline fun DisplayHost.table(
    klass: String = "",
    id: String = "",
    builder: Builder<Table> = {}
): Table {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return +Table()(klass, id, builder)
}

@KframeDSL
inline fun DisplayHost.responsiveTable(
    id: String,
    builder: Builder<Table> = {}
): BasicDisplayElement<HTMLDivElement> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return div("table-responsive") {
        table(klass, id, builder)
    }
}

class TableHead : TableElement<HTMLTableSectionElement, TableHead>("thead")

@KframeDSL
inline fun Table.thead(
    id: String = "",
    builder: Builder<TableHead> = {}
): TableHead {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return +TableHead()(id = id)(builder)
}

class TableBody : TableElement<HTMLTableSectionElement, TableBody>("tbody")

@KframeDSL
inline fun Table.tbody(
    id: String = "",
    builder: Builder<TableBody> = {}
): TableBody {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return +TableBody()(id = id)(builder)
}

class TableFoot : TableElement<HTMLTableSectionElement, TableFoot>("tfoot")

@KframeDSL
inline fun Table.tfoot(
    id: String = "",
    builder: Builder<TableFoot> = {}
): TableFoot {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return +TableFoot()(id = id)(builder)
}

class TableRow : DisplayElement<HTMLTableRowElement, TableRow>("tr")

@KframeDSL
inline fun TableElement<*, *>.tr(
    id: String = "",
    builder: Builder<TableRow> = {}
): TableRow {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return +TableRow()(id = id)(builder)
}

class TableDataElement(tag: String) : DisplayElement<HTMLTableCellElement, TableDataElement>(tag) {
    var scope by attributes.boxedValue<String>()

    val isTh = tag == "th"

    fun colScope() {
        scope = "col"
    }

    fun rowScope() {
        scope = "row"
    }
}

@KframeDSL
inline fun TableRow.td(
    id: String = "",
    builder: Builder<TableDataElement> = {}
): TableDataElement {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return +TableDataElement("td")(id = id)(builder)
}

@KframeDSL
inline fun TableRow.th(
    id: String = "",
    builder: Builder<TableDataElement> = {}
): TableDataElement {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return +TableDataElement("th")(id = id)(builder)
}

@KframeDSL
inline fun TableRow.th(
    scope: String,
    id: String = "",
    builder: Builder<TableDataElement> = {}
): TableDataElement {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return th(id) {
        this.scope = scope
        builder()
    }
}

@KframeDSL
inline fun TableHead.header(vararg names: String, builder: TableDataElement.(String) -> Unit = {}) =
    tr {
        names.forEach {
            th {
                scope = "col"
                +it
                builder(it)
            }
        }
    }

@KframeDSL
inline fun TableBody.row(vararg data: String, builder: TableDataElement.(String) -> Unit = {}) =
    tr {
        data.forEach {
            td {
                +it
                builder(it)
            }
        }
    }

@KframeDSL
inline fun TableBody.namedRow(name: String, vararg data: String, builder: TableDataElement.(String) -> Unit = {}) =
    tr {
        th {
            +name
            scope = "row"
        }
        data.forEach {
            td {
                +it
                builder(it)
            }
        }
    }