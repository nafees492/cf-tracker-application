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
    // Green
    const val OK = "OK"
    const val TESTING = "TESTING"

    //Yellow
    const val TLE = "TIME_LIMIT_EXCEEDED"

    //Red
    const val WA = "WRONG_ANSWER"
    const val FAILED = "FAILED"
    const val CE = "COMPILATION_ERROR"
    const val CRASHED = "CRASHED"
    const val REJECTED = "REJECTED"

    val RED = setOf(WA, FAILED, CE, CRASHED, REJECTED)
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