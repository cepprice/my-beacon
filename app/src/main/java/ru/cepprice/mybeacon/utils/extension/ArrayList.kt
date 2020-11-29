package ru.cepprice.mybeacon.utils.extension

import org.altbeacon.beacon.Beacon

fun List<Beacon>.quickSort(): List<Beacon> =
    if (size < 2 ) this
    else {
        val pivot = first()
        val (smaller, greater) = drop(1).partition { it.distance <= pivot.distance }
        smaller.quickSort() + pivot + greater.quickSort()
    }