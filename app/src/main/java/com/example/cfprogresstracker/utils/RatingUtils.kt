package com.example.cfprogresstracker.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.cfprogresstracker.ui.theme.*

@Composable
fun getRatingColor(rating: Int?) = when(rating){
    in 800..1199 -> NewbieGray
    in 1200..1399 -> PupilGreen
    in 1400..1599 -> SpecialistCyan
    in 1600..1899 -> ExpertBlue
    in 1900..2099 -> CandidMasterViolet
    in  2100..2399 -> MasterOrange
    in 2400..4000 -> GrandmasterRed
    else -> MaterialTheme.colorScheme.onSurface
}

@Composable
fun getRatingGradientColor(rating: Int?) = when(rating){
    in 800..1199 -> listOf(NewbieGrayL, NewbieGrayD)
    in 1200..1399 -> listOf(PupilGreenL, PupilGreenD)
    in 1400..1599 -> listOf(SpecialistCyanL, SpecialistCyanD)
    in 1600..1899 -> listOf(ExpertBlueL, ExpertBlueD)
    in 1900..2099 -> listOf(CandidMasterVioletL, CandidMasterVioletD)
    in  2100..2399 -> listOf(MasterOrangeL, MasterOrangeD)
    in 2400..4000 -> listOf(GrandmasterRedL, GrandmasterRedD)
    else -> listOf(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.primaryContainer)
}