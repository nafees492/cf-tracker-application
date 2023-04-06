package com.gourav.competrace.app_core.util

import com.gourav.competrace.R

object Phase {
    const val CODING = "CODING"
    const val BEFORE = "BEFORE"
    const val PENDING_SYSTEM_TEST = "PENDING_SYSTEM_TEST"
    const val SYSTEM_TEST = "SYSTEM_TEST"
    const val FINISHED = "FINISHED"
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

enum class ContestRatedCategories(val value: String) {
    DIV1("Div. 1"),
    DIV2("Div. 2"),
    DIV3("Div. 3"),
    DIV4("Div. 4"),
    UNRATED("Unrated")
}

object UserSubmissionFilter {
    val ALL = UiText.StringResource(R.string.all_submissions)
    val CORRECT = UiText.StringResource(R.string.correct_submissions)
    val INCORRECT = UiText.StringResource(R.string.incorrect_submissions)
}

object CardValues {
    const val TriangularFractionOfCard = 0.15f
    const val RectangleFractionOfTriangle = 0.5f
}