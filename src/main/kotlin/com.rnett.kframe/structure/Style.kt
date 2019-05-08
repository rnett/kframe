package com.rnett.kframe.structure

import org.w3c.dom.get
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Style(private val rules: MutableMap<String, Value>, val element: Element<*, *>) : Attributes.Value() {
    abstract class Value {
        abstract val raw: String

        override fun toString(): String {
            return raw
        }

        data class Raw(override val raw: String) : Value()
        data class Box<T>(val value: T) : Value() {
            override val raw = value.toString()
        }
    }

    fun toCssName(name: String): String {
        val regex = Regex("([A-Z])")
        return regex.replace(name.removeSuffix("Raw")) { "-" + it.value.toLowerCase() }
    }

    operator fun get(key: String) = rules[key]
    operator fun set(key: String, value: Value?) {
        if (value == null)
            rules.remove(key)
        else
            rules[key] = value

        element.underlying.attributes["style"]?.value = raw
    }

    operator fun set(key: String, value: String) = set(key, Value.Box(value))
    operator fun set(key: String, value: Int) = set(key, Value.Box(value))

    fun <T : Value> getValue(key: String) =
        this[key] as? T

    inner class ValueDelegate<T : Value>(val key: String?, val prefix: String? = null) : ReadWriteProperty<Any?, T?> {

        fun name(property: KProperty<*>): String {
            val end = key ?: toCssName(property.name)

            return if (prefix != null)
                "$prefix-$end"
            else
                end

        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return getValue(name(property))
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            this@Style[name(property)] = value
        }
    }

    inner class BoxValueDelegate<T>(val key: String?, val prefix: String? = null) : ReadWriteProperty<Any?, T?> {

        fun name(property: KProperty<*>): String {
            val end = key ?: toCssName(property.name)

            return if (prefix != null)
                "$prefix-$end"
            else
                end

        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return getValue<Value.Box<T>>(name(property))?.value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {

            if (value == null)
                this@Style[name(property)] = null
            else
                this@Style[name(property)] = Value.Box(value)
        }
    }

    fun <T : Value> value(key: String, prefix: String) = ValueDelegate<T>(key, prefix)
    fun <T : Value> value(key: String) = ValueDelegate<T>(key)
    fun <T : Value> value() = ValueDelegate<T>(null)

    fun <T> boxedValue(key: String, prefix: String) = BoxValueDelegate<T>(key, prefix)
    fun <T> boxedValue(key: String) = BoxValueDelegate<T>(key)
    fun <T> boxedValue() = BoxValueDelegate<T>(null)

    val stringValue get() = boxedValue<String>()

    fun toRaw(): String = rules.entries.joinToString(";") { "${it.key}: ${it.value}" }
    override val raw get() = toRaw()

    operator fun invoke(builder: Style.() -> Unit) = builder()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false
        if (!super.equals(other)) return false

        other as Style

        if (rules != other.rules) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + rules.hashCode()
        return result
    }

    abstract inner class CompositeStyle(val baseName: String, val prefix: String) {

        constructor(name: String) : this(name, name)

        fun <T : Value> value(key: String) = ValueDelegate<T>(key, prefix)
        fun <T : Value> value() = ValueDelegate<T>(null, prefix)

        fun <T> boxedValue(key: String) = BoxValueDelegate<T>(key, prefix)
        fun <T> boxedValue() = BoxValueDelegate<T>(null, prefix)

        val stringValue get() = boxedValue<String>()

        var raw by this@Style.boxedValue<String>(baseName)

    }

    inner class Border : CompositeStyle("border") {
        var color by value<Color>()
        var style by stringValue
        var width by value<Size>()

        var top by stringValue
        var bottom by stringValue
        var left by stringValue
        var right by stringValue

        val collapse by stringValue

        operator fun invoke(builder: Border.() -> Unit) = builder()
    }

    val border = Border()

    inner class Outline : CompositeStyle("outline") {
        var color by value<Color>()
        var style by stringValue
        var width by value<Size>()
        var offset by value<Size>()

        operator fun invoke(builder: Outline.() -> Unit) = builder()
    }

    val outline = Outline()

    var color by value<Color>()

    inner class Background : CompositeStyle("background") {
        var color by value<Color>()
        var image by stringValue
        var repeat by stringValue
        var position by stringValue
        var attachment by stringValue
        var size by stringValue
        var origin by stringValue
        var clip by stringValue

        operator fun invoke(builder: Background.() -> Unit) = builder()
    }

    val background = Background()

    inner class Margin : CompositeStyle("margin") {
        var top by value<Size>()
        var bottom by value<Size>()
        var left by value<Size>()
        var right by value<Size>()

        var all by this@Style.value<Size>("margin")

        operator fun invoke(builder: Margin.() -> Unit) = builder()
    }

    val margin = Margin()

    inner class Padding : CompositeStyle("padding") {
        var top by value<Size>()
        var bottom by value<Size>()
        var left by value<Size>()
        var right by value<Size>()

        var all by this@Style.value<Size>("padding")

        operator fun invoke(builder: Padding.() -> Unit) = builder()
    }

    val padding = Padding()

    var height by value<Size>()
    var maxHeight by value<Size>()
    var minHeight by value<Size>()

    var width by value<Size>()
    var maxWidth by value<Size>()
    var minWidth by value<Size>()

    inner class Text {
        var align by boxedValue<String>("align", "text")
        var decoration by boxedValue<String>("decoration", "text")
        var transform by boxedValue<String>("transform", "text")
        var indent by boxedValue<String>("indent", "text")

        var lineHeight by value<Size>("line-height")

        val font = Font()

        operator fun invoke(builder: Text.() -> Unit) = builder()
    }

    inner class Font : CompositeStyle("font") {
        var family by stringValue
        var style by stringValue
        var size by value<Size>()
        var weight by stringValue
        var variant by stringValue

        operator fun invoke(builder: Font.() -> Unit) = builder()
    }

    val text = Text()

    /**
     * An alias for text.font
     */
    val font = text.font

    var verticalAlign by boxedValue<String>("vertical-align")

    inner class ListStyle : CompositeStyle("list-style") {
        var type by stringValue
        var image by stringValue
        var position by stringValue

        operator fun invoke(builder: ListStyle.() -> Unit) = builder()
    }

    val listStyle = ListStyle()

    inner class TableStyle {
        var layout by boxedValue<String>("table-layout")
        var borderSpacing by boxedValue<String>("border-spacing")
        var captionSize by boxedValue<String>("caption-size")
        var emptyCells by boxedValue<String>("empty-cells")

        operator fun invoke(builder: TableStyle.() -> Unit) = builder()
    }

    var display by stringValue
    var visibility by stringValue

    var position by stringValue
    var clip by stringValue
    var zIndex by stringValue

    var top by value<Size>()
    var bottom by value<Size>()
    var left by value<Size>()
    var right by value<Size>()

    var overflow by stringValue
    var overflowX by stringValue
    var overflowY by stringValue

    var float by stringValue

    @JsName("clearProp")
    var clear by stringValue

    var boxSizing by stringValue

    var opacity by stringValue

    var boxShadow by stringValue
    var textShadow by stringValue

    var counterReset by stringValue

    var counterIncrement by stringValue

    var content by stringValue


    var textOverflow by stringValue

    var wordWrap by stringValue

    var wordBreak by stringValue


    var objectFit by stringValue


    //TODO allow for keyframes (can I do this without a stylesheet?)

    inner class Animation : CompositeStyle("animation") {
        var name by stringValue

        var duration by stringValue

        var delay by stringValue

        var iterationCount by stringValue

        var direction by stringValue

        var timingFunction by stringValue

        var fillMode by stringValue

        operator fun invoke(builder: Animation.() -> Unit) = builder()
    }

    var animation = Animation()


    inner class Columns : CompositeStyle("columns", "column") {
        var count by boxedValue<Int>()
        var fill by stringValue
        var gap by value<Size>()

        inner class Rule : CompositeStyle("column-rule") {
            var color by value<Color>()
            var style by stringValue
            var width by value<Size>()

            operator fun invoke(builder: Rule.() -> Unit) = builder()
        }

        val rule = Rule()

        var span by stringValue

        var width by value<Size>()

        operator fun invoke(builder: Columns.() -> Unit) = builder()
    }

    var columns = Columns()


    inner class Flex : CompositeStyle("flex") {
        var direction by stringValue
        var wrap by stringValue
        var flow by stringValue

        var basis by value<Size>()

        var grow by boxedValue<Int>()
        var shrink by boxedValue<Int>()

        operator fun invoke(builder: Flex.() -> Unit) = builder()
    }


    var flex = Flex()


    var justifyContent by stringValue

    var alignItems by stringValue

    var alignContent by stringValue

    var order by stringValue

    var alignSelf by stringValue


    var resize by stringValue


    inner class Grid : CompositeStyle("grid") {
        var area by stringValue

        inner class Auto {
            var columns by value<Size>()
            var flow by stringValue
            var rows by value<Size>()

            operator fun invoke(builder: Auto.() -> Unit) = builder()
        }

        val auto = Auto()

        inner class Column : CompositeStyle("grid-column") {
            var end by stringValue
            var gap by value<Size>()
            var start by stringValue

            operator fun invoke(builder: Column.() -> Unit) = builder()
        }

        val column = Column()

        var gap by value<Size>()


        inner class Row : CompositeStyle("grid-row") {
            var end by stringValue
            var gap by value<Size>()
            var start by stringValue

            operator fun invoke(builder: Row.() -> Unit) = builder()
        }

        val row = Row()

        inner class Template : CompositeStyle("grid-template") {
            var areas by stringValue
            var columns by stringValue
            var rows by stringValue

            operator fun invoke(builder: Template.() -> Unit) = builder()
        }

        val template = Template()

        operator fun invoke(builder: Grid.() -> Unit) = builder()
    }

    var grid = Grid()


    var whiteSpace by stringValue

}

enum class SizeUnit {
    percent, cm, em, rem, ex, `in`, mm, pc, pt, px, vh, vw, vmin, raw;


    val unit = if (name == "raw") "" else if (name == "percent") "%" else name.trim('`')

    companion object {
        val inch = `in`

        operator fun get(value: String) = when (value.trim('s')) {
            "%" -> percent
            "in" -> `in`
            "percent" -> percent
            "inches" -> inch
            "" -> raw
            else -> if (value.isBlank()) raw else values().find { it.unit == value.trim('s') } ?: raw
        }

        operator fun invoke(value: String) = get(value)
    }
}


data class Size(val length: Number, val units: SizeUnit) : Style.Value() {
    override val raw: String
        get() = length.toString() + units.toString()

    companion object {
        fun Char.isLetterOrPercent() = this == '%' || (this.toString().match("[a-zA-z]")?.isNotEmpty() ?: false)
    }

    constructor(sizeAndUnits: String) : this(
        sizeAndUnits.dropLastWhile { it.isLetterOrPercent() }.toDouble(),
        SizeUnit[sizeAndUnits.takeLastWhile { it.isLetterOrPercent() }]
    )

    override fun toString(): String {
        return raw
    }

}

val Number.px get() = Size(this, SizeUnit.px)
val Number.rem get() = Size(this, SizeUnit.rem)
val Number.percent get() = Size(this, SizeUnit.percent)
val Number.cm get() = Size(this, SizeUnit.cm)
val Number.em get() = Size(this, SizeUnit.em)
val Number.ex get() = Size(this, SizeUnit.ex)
val Number.inch get() = Size(this, SizeUnit.inch)
val Number.mm get() = Size(this, SizeUnit.mm)
val Number.pc get() = Size(this, SizeUnit.pc)
val Number.pt get() = Size(this, SizeUnit.pt)
val Number.vh get() = Size(this, SizeUnit.vh)
val Number.vw get() = Size(this, SizeUnit.vw)
val Number.vmin get() = Size(this, SizeUnit.vmin)