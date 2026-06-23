package com.example.skite.data.entities.enums

enum class SessionAttendance(val short: String, val value: String) {
    PRESENT("P", "Présent"),
    MISSING("A", "Absent"),
    MISSING_MC("MC", "Certificat médical"),
    MISSING_PARENT("PN", "Mot parental"),
    OTHER("O", "Autre");

    companion object {
        fun getByValue(value: String): SessionAttendance? {
            return entries.find { it.value == value }
        }
    }
}
