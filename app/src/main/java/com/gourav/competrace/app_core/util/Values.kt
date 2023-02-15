package com.gourav.competrace.utils

object Phase {
    const val CODING = "CODING"
    const val BEFORE = "BEFORE"
    const val PENDING_SYSTEM_TEST = "PENDING_SYSTEM_TEST"
    const val SYSTEM_TEST = "SYSTEM_TEST"
    const val FINISHED = "FINISHED"
    const val WITHIN_7DAYS = "WITHIN_7DAYS"
    const val AFTER_7DAYS = "AFTER_7DAYS"
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

object ContestRated {
    const val DIV1 = "Div. 1"
    const val DIV2 = "Div. 2"
    const val DIV3 = "Div. 3"
    const val DIV4 = "Div. 4"
    const val UNRATED = "Unrated"

    val contestRatedList = arrayListOf(DIV1, DIV2, DIV3, DIV4, UNRATED)
}

object FinishedContestFilter {
    const val ALL = "All Contests"
    const val PARTICIPATED = "Participated Contests"
}

object UserSubmissionFilter {
    const val ALL = "All Submissions"
    const val CORRECT = "Correct Submissions"
    const val INCORRECT = "Incorrect Submissions"
}

object CardValues {
    const val TriangularFractionOfCard = 0.15f
    const val RectangleFractionOfTriangle = 0.5f
}