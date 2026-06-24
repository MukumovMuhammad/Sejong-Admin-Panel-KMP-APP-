package com.example.AdminPanel.data.utills

fun String?.getFormattedTimeOfPost() : Pair<String, String>{

    var formattedDate = ""
    var formattedTime = ""

    if (!this.isNullOrBlank()) {
        val parts = this.split(" ")
        if (parts.size >= 2) {
            val datePart = parts[0]
            val timePart = parts[1]
            val timePieces = timePart.split(":")
            val datePieces = datePart.split("-")
            if (datePieces.size >= 3) formattedDate = "${datePieces[0]}/${datePieces[1]}/${datePieces[2]}"
            if (timePieces.size >= 2) formattedTime = "${timePieces[0]}:${timePieces[1]}"
        }
    }

    return Pair(formattedDate, formattedTime)
}