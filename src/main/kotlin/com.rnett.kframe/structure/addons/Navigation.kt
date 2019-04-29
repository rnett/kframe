package com.rnett.kframe.structure.addons

import com.rnett.kframe.structure.AnyElement
import com.rnett.kframe.structure.Document

infix fun Document.select(m: Selector): List<AnyElement> = m.select(elements.toList())
operator fun Document.get(m: Selector): List<AnyElement> = select(m)

infix fun AnyElement.select(m: Selector): List<AnyElement> = m.select(this.children)
operator fun AnyElement.get(m: Selector) = select(m)

infix fun AnyElement.topSelect(m: Selector) = Document.select(m)

infix fun Iterable<AnyElement>.select(m: Selector) = flatMap { it[m] }
operator fun Iterable<AnyElement>.get(m: Selector) = select(m)

infix fun Sequence<AnyElement>.select(m: Selector) = flatMap { it[m].asSequence() }
operator fun Sequence<AnyElement>.get(m: Selector) = select(m)

infix fun Selector.selectFrom(element: AnyElement) = element[this]
infix fun Selector.selectFrom(elements: Iterable<AnyElement>) = elements[this]
infix fun Selector.selectFrom(elements: Sequence<AnyElement>) = elements[this]

operator fun Selector.get(element: AnyElement) = this selectFrom element
operator fun Selector.get(elements: Iterable<AnyElement>) = this selectFrom elements
operator fun Selector.get(elements: Sequence<AnyElement>) = this selectFrom elements

@Suppress("FunctionName")
@SelectorSeparatorDSL
fun `$`(jquerySelector: String) = jquerySelector(jquerySelector)

fun jquerySelector(jqyerySelector: String): Selector {
    var start = `$`

    //TODO don't break on spaces in quotes
    val matches =
        Regex(" < | > | | + | ~ |([#.:\\[])?(?:\\1|[^ #.:/])*").findAll(jqyerySelector).toList()
            .flatMap { it.groupValues }
            .filter { it.isNotEmpty() }
            .filter { it == " " || it.length > 1 }

    for (m in matches) {

        when (m[0]) {
            ' ' -> {
                when (m) {
                    " " -> start = start.descendant
                    " > " -> start = start.child
                    " < " -> start = start.parent
                    " + " -> start = start.nextTo
                    " ~ " -> start = start.siblings
                }
            }
            '#' -> {
                val ids = m.split(",").map { it.trim('#') }
                start = start.id(*ids.toTypedArray())
            }
            '.' -> {
                val classes = m.split(",").map { it.trim('.') }
                start = start.klass(*classes.toTypedArray())
            }
            ':' -> {
                val sel = m.drop(1)
                when (sel) {
                    "first-child" -> start = start.firstChild()
                    "last-child" -> start = start.lastChild()
                    "only-child" -> start = start.onlyChild()
                    else -> {
                        when {
                            "nth-child" in sel -> {
                                val n = "nth-child[(]([0-9]*)[)]".toRegex().find(sel)!!.groupValues[1].toInt()
                                start = start.childAt(n)
                            }
                            "nth-last-child" in sel -> {
                                val n = "nth-last-child[(]([0-9]*)[)]".toRegex().find(sel)!!.groupValues[1].toInt()
                                start = start.lastChildAt(n)

                            }
                        }
                    }
                }
            }
            '[' -> {
                val str = m.trim('[', ']')
                val attr = str.split('=')[0]
                val value = str.split('=')[1]
                start = start.attributeEquals(attr, value)
            }
            else -> {
                val tags = m.split(",")
                start = start.tag(*tags.toTypedArray())
            }
        }
    }

    console.log(start)

    return start
}
