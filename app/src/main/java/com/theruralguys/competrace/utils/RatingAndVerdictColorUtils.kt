package com.theruralguys.competrace.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.theruralguys.competrace.ui.theme.*

@Composable
fun getRatingTextColor(rating: Int?) = when (rating) {
    in 800..1199 -> NewbieGray
    in 1200..1399 -> PupilGreen
    in 1400..1599 -> SpecialistCyan
    in 1600..1899 -> ExpertBlue
    in 1900..2099 -> CandidMasterViolet
    in 2100..2399 -> MasterOrange
    in 2400..4000 -> GrandmasterRed
    else -> MaterialTheme.colorScheme.onSurface
}

@Composable
fun getRatingContainerColor(rating: Int?) =
    if (isDarkTheme()) when (rating) {
        in 800..1199 -> dark_NewbieGrayContainer
        in 1200..1399 -> dark_PupilGreenContainer
        in 1400..1599 -> dark_SpecialistCyanContainer
        in 1600..1899 -> dark_ExpertBlueContainer
        in 1900..2099 -> dark_CandidMasterVioletContainer
        in 2100..2399 -> dark_MasterOrangeContainer
        in 2400..4000 -> dark_GrandmasterRedContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    } else when (rating) {
        in 800..1199 -> light_NewbieGrayContainer
        in 1200..1399 -> light_PupilGreenContainer
        in 1400..1599 -> light_SpecialistCyanContainer
        in 1600..1899 -> light_ExpertBlueContainer
        in 1900..2099 -> light_CandidMasterVioletContainer
        in 2100..2399 -> light_MasterOrangeContainer
        in 2400..4000 -> light_GrandmasterRedContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }

@Composable
fun getVerdictColor(lastVerdict: String?, hasVerdictOK: Boolean) =
    if (hasVerdictOK) CorrectGreen else {
        lastVerdict.let {
            if (it == Verdict.TESTING) TestingGreen
            else if (Verdict.RED.contains(it)) IncorrectRed
            else PartialYellow
        }
    }

@Composable
fun getRatingChangeContainerColor(ratingChange: Int) =
    when{
        isDarkTheme() && ratingChange >= 0 -> dark_CorrectGreenContainer
        isDarkTheme() && ratingChange < 0 -> dark_IncorrectRedContainer
        ratingChange >= 0 -> light_CorrectGreenContainer
        else -> light_IncorrectRedContainer
    }
