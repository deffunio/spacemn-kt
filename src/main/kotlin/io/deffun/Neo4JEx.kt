package io.deffun

import org.neo4j.driver.Value

fun Value.stringOrDefault(default: String): String {
    return if (this.isNull) default else this.asString()
}

fun Value.stringOrEmpty(): String {
    return stringOrDefault("")
}
