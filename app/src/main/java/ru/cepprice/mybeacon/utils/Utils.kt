package ru.cepprice.mybeacon.utils

import org.altbeacon.beacon.Beacon

object Utils {

    fun generateRandomUuid(): String =
        "${randomHexFourDigitNumber()}${randomHexFourDigitNumber()}-" +
                "${randomHexFourDigitNumber()}-" +
                "${randomHexFourDigitNumber()}-" +
                "${randomHexFourDigitNumber()}-" +
                "${randomHexFourDigitNumber()}${randomHexFourDigitNumber()}${randomHexFourDigitNumber()}"

    private fun randomHexDigit(): String {
        val chars = "1234567890ABCDEF"
        return chars[(0..15).random()].toString()
    }

    private fun randomHexFourDigitNumber(): String =
        randomHexDigit()
            .plus(randomHexDigit())
            .plus(randomHexDigit())
            .plus(randomHexDigit())

}