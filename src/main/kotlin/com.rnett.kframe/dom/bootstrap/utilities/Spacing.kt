package com.rnett.kframe.dom.bootstrap.utilities

enum class Spacing(val value: String) {
    None("0"), Auto("auto"),
    X025("1"), X05("2"), X1("3"), X15("4"), X3("5");

    operator fun unaryMinus() = NegativeSpacing(this)

}

data class NegativeSpacing(val spacing: Spacing) {
    operator fun unaryMinus() = spacing

    val value: String
        get() =
            when (spacing) {
                Spacing.None -> spacing.value
                Spacing.Auto -> spacing.value
                else -> "n${spacing.value}"
            }

}

enum class Side(val value: String) {
    Top("t"), Bottom("b"), Left("l"), Right("r"),
    LeftRight("x"), TopBottom("y"),
    All("");
}