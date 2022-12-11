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

object UserSubmissionFilter {
    const val ALL = "All Submissions"
    const val CORRECT = "Correct Submissions"
    const val INCORRECT = "Incorrect Submissions"
}