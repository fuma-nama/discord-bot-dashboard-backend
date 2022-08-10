package com.bdash.api.discord

fun main() {
    val input = generateSequence {
        readln().ifEmpty {
            null
        }
    }

    val output = StringBuilder()

    for (line in input.toList()) {
        var (name, type, description) = line
            .split("\t")

        val needsDefault = name.endsWith("?")
        val nullable = needsDefault || type.startsWith("?")
        val isArray = type.startsWith("array of ")

        if (needsDefault) {
            name = name.trimEnd('?')
        }
        if (nullable) {
            type = type.trimStart('?')
        }
        if (isArray) {
            type = type.removePrefix("array of ")
        }

        type = when (type.lowercase()) {
            "string" -> "String"
            "boolean" -> "Boolean"
            "snowflake" -> "Long"
            "integer" -> "Int"
            else -> "/* $type */"
        }

        if (isArray) {
            type = "Array<$type>"
        }

        if (nullable) {
            type += "?"
        }


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
