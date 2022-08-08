package com.bdash.api.discord

fun main() {
    val input = generateSequence {
        readln().ifEmpty {
            null
        }
    }

    val output = StringBuilder()

    for (line in input.toList()) {
        val parsed = line
            .split("\\s+".toRegex())
            .filterNot { it == "*" }

        var (name, type) = parsed
        val description = parsed.drop(2).joinToString(separator = " ")

        val needsDefault = name.endsWith("?")
        val nullable = needsDefault || type.startsWith("?")

        if (needsDefault) {
            name = name.trimEnd('?')
        }
        if (nullable) {
            type = type.trimStart('?')
        }

        type = when (type) {
            "string" -> "String"
            "boolean" -> "Boolean"
            "snowflake" -> "Long"
            else -> error("Unknown type: $type")
        } + if (nullable) "?" else ""

        val default = if (needsDefault) " = null" else ""

        output.appendLine("""
            /**
            * $description
            **/
        """.trimIndent())

        output.appendLine("val $name: $type$default,")
    }

    println("result: \n$output")
}
