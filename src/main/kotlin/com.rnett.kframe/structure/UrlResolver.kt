package com.rnett.kframe.structure

fun partHeader(useTail: Boolean, optional: Boolean): String {
    val d = buildString {
        if (useTail)
            append("+")
        if (optional)
            append("?")
    }
    return d
}

sealed class RoutePart(open val raw: String) {
    object Wildcard : RoutePart("*")
    object Tailcard : RoutePart("...")
    data class Static(override val raw: String) : RoutePart(raw)
    data class Parameter(
        val name: String,
        val useTail: Boolean = false,
        val optional: Boolean = false
    ) : RoutePart("{${partHeader(useTail, optional)}$name}")
}

data class Route(val parts: List<RoutePart>) {

    val parameters = parts.filterIsInstance<RoutePart.Parameter>()
    val tailParameters = parameters.filter { it.useTail }

    constructor(vararg parts: RoutePart) : this(parts.toList())

    companion object {
        operator fun invoke(route: String): Route {
            val segments = route.split("/")

            val parts = mutableListOf<RoutePart>()

            loop@ for (part in segments) {
                when (part) {
                    "*" -> parts += RoutePart.Wildcard
                    "..." -> {
                        parts += RoutePart.Tailcard
                        break@loop
                    }
                    else -> {
                        if (part.startsWith('{')) {
                            val param = part.trimStart('{').trimEnd('}')
                            parts += when {
                                param.startsWith("?") -> RoutePart.Parameter(param.trimStart('?'), optional = true)
                                param.startsWith("+?") -> RoutePart.Parameter(param.trimStart('?', '+'), true, true)
                                param.startsWith("+") -> RoutePart.Parameter(param.trimStart('+'), true)
                                else -> RoutePart.Parameter(param)
                            }

                        } else
                            parts += RoutePart.Static(part)
                    }
                }

            }
            return Route(parts)
        }
    }

    val score = parts.count { it is RoutePart.Static } * 3 +
            parameters.count { !it.optional && !it.useTail } +
            parameters.count { !it.optional }

}

object UrlResolver {
    fun resolve(url: String, possibilities: Set<Route>): Pair<Route, Parameters>? {
        val works = possibilities.mapNotNull { r -> tryMatch(url, r)?.let { r to it } }

        return works.maxBy { it.first.score }
    }

    fun tryMatch(url: String, route: Route): Parameters? {
        val urlParams = if ('?' in url) {
            url.substringAfter('?').split("&")
                .map { it.split("=").let { it[0] to it[1] } }.toMap()
        } else
            emptyMap()

        val parts = url.substringBeforeLast('?').split("/")

        val readParams = tryRest(parts, route.parts, urlParams.keys) ?: return null

        val params = readParams + urlParams
        return Parameters(params)
    }

    fun tryRest(urlParts: List<String>, routeParts: List<RoutePart>, tailParams: Set<String>): Map<String, String>? {
        val url = urlParts[0]
        val route = routeParts[0]

        return when (route) {
            is RoutePart.Wildcard -> tryRest(urlParts.drop(1), routeParts.drop(1), tailParams)
            is RoutePart.Tailcard -> mapOf()
            is RoutePart.Static -> {
                if (url == route.raw)
                    tryRest(urlParts.drop(1), routeParts.drop(1), tailParams)
                else
                    null
            }
            is RoutePart.Parameter -> {
                if (route.useTail && route.name in tailParams) {
                    // skip this route part
                    return tryRest(urlParts, routeParts.drop(1), tailParams)
                }

                if (route.optional) {
                    val noUse = tryRest(urlParts, routeParts.drop(1), tailParams)
                    val use = tryRest(urlParts.drop(1), routeParts.drop(1), tailParams)

                    return if (use == null)
                        noUse
                    else
                        use + (route.name to url)

                } else {
                    val use = tryRest(urlParts.drop(1), routeParts.drop(1), tailParams)
                    return use?.plus(route.name to url)
                }
            }
        }
    }

    operator fun invoke(url: String, possibilities: Set<Route>) = resolve(url, possibilities)

}