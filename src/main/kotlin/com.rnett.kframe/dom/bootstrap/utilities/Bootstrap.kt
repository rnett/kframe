package com.rnett.kframe.dom.bootstrap.utilities

import com.rnett.kframe.dom.bootstrap.ContextType
import com.rnett.kframe.structure.DisplayElement
import com.rnett.kframe.structure.Element
import org.w3c.dom.HTMLAnchorElement

class Bootstrap(val element: Element<*, *>) {

    var float by element.classes.optionalClassDelegate<Float> { it.name.toLowerCase() }

    class Flex(val bootstrap: Bootstrap) {
        var justification by bootstrap.element.classes.optionalClassDelegate<Justification>()
        var alignItems by bootstrap.element.classes.optionalClassDelegate<Alignment> { it.itemClass }
        var alignSelf by bootstrap.element.classes.optionalClassDelegate<Alignment> { it.selfClass }
        var alignContent by bootstrap.element.classes.optionalClassDelegate<Alignment> { it.contentClass }

        var fill by bootstrap.element.classes.presentDelegate("flex-fill")

        var grow by bootstrap.element.classes.optionalClassDelegate<Int> { "flex-grow-$it" }
        var shrink by bootstrap.element.classes.optionalClassDelegate<Int> { "flex-shrink-$it" }

        var wrapping by bootstrap.element.classes.optionalClassDelegate<Wrapping> { if (it == Wrapping.None) null else it.klass }

        var order by bootstrap.element.classes.optionalClassDelegate<Int> { "order-$it" }
    }

    val flex = Flex(this)

    var backgroundColor by element.classes.optionalClassDelegate<ContextType> { it.klass("bg") }
    var textColor by element.classes.optionalClassDelegate<ContextType> { it.klass("text") }

    class Border(val bootstrap: Bootstrap) {
        var rounding by bootstrap.element.classes.optionalClassDelegate<BorderRounding>()
        var size by bootstrap.element.classes.optionalClassDelegate<BorderRounding.Size>()
        var color by bootstrap.element.classes.optionalClassDelegate<ContextType> { it.klass("border") }

        var activated: List<Borders>
            get() = bootstrap.element.classes.mapNotNull { k -> Borders.values().firstOrNull { it.klass == k } }
            set(bs) {
                activated(*bs.toTypedArray())
            }

        var deactivated: List<Borders>
            get() = bootstrap.element.classes.mapNotNull { k ->
                Borders.values().firstOrNull { "-0" in k && it.klass == k.substringBefore("-0") }
            }
            set(bs) {
                deactivated(*bs.toTypedArray())
            }
    }

    val border = Border(this)

    var hideText by element.classes.presentDelegate("text-hide")

    var overflow by element.classes.optionalClassDelegate<Overflow>()

    var position by element.classes.optionalClassDelegate<Position>()

    var shadow by element.classes.optionalClassDelegate<Shadow>()

    var width by element.classes.optionalClassDelegate<Sizing> { it.widthClass }
    var height by element.classes.optionalClassDelegate<Sizing> { it.heightClass }

    var viewportWidth by element.classes.optionalClassDelegate<Sizing> { "v" + it.widthClass }
    var viewportHeight by element.classes.optionalClassDelegate<Sizing> { "v" + it.heightClass }

    open class SizeSetter(val property: String, val bootstrap: Bootstrap) {
        operator fun set(side: Side, spacing: Spacing) {
            val start = "$property${side.value}-"

            bootstrap.element.classes.removeAll { it.startsWith(start) }

            bootstrap.element.classes += "$start${spacing.value}"
        }

        fun clear() {
            bootstrap.element.classes.removeAll { it.startsWith("$property-") }
        }
    }

    class MarginSideSetter(property: String, bootstrap: Bootstrap) : SizeSetter(property, bootstrap) {
        operator fun set(side: Side, spacing: NegativeSpacing) {
            val start = "$property${side.value}-"

            bootstrap.element.classes.removeAll { it.startsWith(start) }

            bootstrap.element.classes += "$start${spacing.value}"
        }
    }

    val margin = MarginSideSetter("m", this)
    val padding = SizeSetter("p", this)

    var stretchedLink by element.classes.presentDelegate("stretched-link")

    class Text(val bootstrap: Bootstrap) {
        var align by bootstrap.element.classes.optionalClassDelegate<TextAlignment>()
        var muted by bootstrap.element.classes.presentDelegate("text-muted")

        var wrap by bootstrap.element.classes.presentDelegate("text-wrap")
        var nowrap by bootstrap.element.classes.presentDelegate("text-nowrap")
        var truncate by bootstrap.element.classes.presentDelegate("text-truncate")
        var textBreak by bootstrap.element.classes.presentDelegate("text-break")

        var transform by bootstrap.element.classes.optionalClassDelegate<TextTransform>()

        var italic by bootstrap.element.classes.presentDelegate("text-italic")
        var fontWeight by bootstrap.element.classes.optionalClassDelegate<FontWeight>()

        var monospace by bootstrap.element.classes.presentDelegate("text-monospace")

        var reset by bootstrap.element.classes.presentDelegate("text-reset")
        var noDecoration by bootstrap.element.classes.presentDelegate("text-decoration-none")
    }

    val text = Text(this)

    var verticalAlign by element.classes.optionalClassDelegate<VerticalAlignment>()

    var visible by element.classes.optionalClassDelegate<Boolean?> {
        when (it) {
            true -> "visible"
            false -> "invisible"
            else -> null
        }
    }

}

var DisplayElement<HTMLAnchorElement, *>.stretched: Boolean
    get() = "stretched-link" in classes
    set(v) {
        if (v)
            classes += "stretched-link"
        else
            classes.remove("stretched-link")
    }