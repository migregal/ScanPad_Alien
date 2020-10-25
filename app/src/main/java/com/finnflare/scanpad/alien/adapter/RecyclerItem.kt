package com.finnflare.scanpad.alien.adapter

class RecyclerItem(
    var scanResult: String,
    var scanCount: Int
) : Comparable<RecyclerItem> {
    override fun compareTo(other: RecyclerItem): Int = this.scanResult.compareTo(other.scanResult)
    override fun toString(): String = "$scanResult $scanCount"
}