package com.example.cfprogresstracker.utils

object Phase {
    const val CODING = "CODING"
    const val BEFORE = "BEFORE"
    const val PENDING_SYSTEM_TEST = "PENDING_SYSTEM_TEST"
    const val SYSTEM_TEST = "SYSTEM_TEST"
    const val FINISHED = "FINISHED"
    const val WITHIN_2DAYS = "WITHIN_2DAYS"
    const val MORE = "MORE"
}


object Verdict {
    const val OK = "OK"
}

object FinishedContestFilter {
    const val ALL = "All Contests"
    const val GIVEN = "Given Contests"
}

object ProblemSetFilter {
    const val ALL = "All Problems"
    val RATING = listOf("800", "900", "1000", "1100", "1200", "1300", "1400", "1500", "1600",
        "1700", "1800", "1900", "2000", "2100", "2200", "2300", "2400", "2500", "2600", "2700",
        "2800", "2900", "3000", "3100", "3200", "3300", "3400", "3500")
}

object UserSubmissionFilter {
    const val ALL = "All Submissions"
    const val CORRECT = "Correct Submissions"
    const val INCORRECT = "Incorrect Submissions"
}