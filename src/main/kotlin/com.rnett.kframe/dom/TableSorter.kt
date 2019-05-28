package com.rnett.kframe.dom

import com.rnett.kframe.dom.bootstrap.*
import com.rnett.kframe.structure.Attributes

inline fun <T : Comparable<T>> TransformComparator(crossinline transform: (String) -> T) = Comparator<String> { a, b ->
    transform(a).compareTo(transform(b))
}

fun Table.makeSortable(columns: Set<Int>, transforms: Map<Int, Comparator<String>>) {
    val head = children.firstOrNull { it is TableHead }?.children?.firstOrNull { it is TableRow } ?: return

    val headers = head.children.filterIsInstance<TableDataElement>().filter { it.isTh }.withIndex().filter {
        it.index in columns
    }.map {
        Triple(it.index, it.value, transforms[it.index] ?: Comparator { a, b -> a.compareTo(b) })
    }

    headers.forEach { (_, it, _) ->

        it.underlying.innerHTML = it.underlying.innerHTML
            .removeSuffix("  &#x25B2;").removeSuffix("  &#x25BC;")
            .removeSuffix("  ▲").removeSuffix("  ▼")

        it.attributes.remove("data-sort")
    }

    fun TableDataElement.doSort(index: Int, transform: Comparator<String>) {
        if (attributes["data-sort"] == null) {
            if (attributes["data-default-desc"] == null)
                attributes["data-sort"] = "asc"
            else
                attributes["data-sort"] = Attributes.Value.Box("desc")
        } else {
            val sort = attributes["data-sort"]!!.raw
            if (sort == "desc")
                attributes["data-sort"] = Attributes.Value.Box("asc")
            else
                attributes["data-sort"] = Attributes.Value.Box("desc")
        }

        val sort = attributes["data-sort"]!!.raw

        sortBy(index, sort == "desc", transform)

        headers.forEach { (_, it, _) ->

            it.underlying.innerHTML = it.underlying.innerHTML
                .removeSuffix("  &#x25B2;").removeSuffix("  &#x25BC;")
                .removeSuffix("  ▲").removeSuffix("  ▼")
        }

        if (sort == "asc") {
            underlying.innerHTML += "  &#x25B2;"
        } else if (sort == "desc") {
            underlying.innerHTML += "  &#x25BC;"
        }
    }

    headers.forEach { (index, header, transform) ->
        header.on.click {
            header.apply { doSort(index, transform) }
        }
        header.style.cursor = "pointer"
    }

    headers.forEach { (index, header, transform) ->
        header.apply {
            if ("data-sort-start" in attributes) {
                doSort(index, transform)
            }
        }
    }

}

fun Table.sortBy(column: Int, desc: Boolean, comp: Comparator<String>) {
    val body = children.firstOrNull { it is TableBody } ?: return

    val rows = body.children.filterIsInstance<TableRow>().map { it to it.children[column].underlying.innerHTML.trim() }

    rows.forEach {
        body.removeChild(it.first)
    }

    val rowComp: Comparator<Pair<TableRow, String>> = if (desc)
        Comparator { a, b -> comp.compare(b.second, a.second) }
    else
        Comparator { a, b -> comp.compare(a.second, b.second) }

    rows.sortedWith(rowComp).forEach {
        body.addChild(it.first)
    }
}