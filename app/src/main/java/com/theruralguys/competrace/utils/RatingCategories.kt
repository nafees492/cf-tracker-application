package com.theruralguys.competrace.utils

import androidx.compose.ui.graphics.Color
import com.theruralguys.competrace.ui.theme.*

enum class RatingCategories(val title: String, val startValue: Int, val endValue: Int, val color: Color){
    Unrated("Unrated", 0, 800, PartialYellow),
    Newbie("Newbie", 800, 1199, NewbieGray),
    Pupil("Pupil", 1200, 1399, PupilGreen),
    Specialist("Specialist", 1400, 1599, SpecialistCyan),
    Expert("Expert", 1600, 1899, ExpertBlue),
    CandidateMaster("Candidate Master", 1900, 2099, CandidMasterViolet),
    Master("Master", 2100, 2299, MasterOrange),
    InternationalMaster("International Master", 2300, 2399, MasterOrange),
    Grandmaster("Grandmaster", 2400, 2599, GrandmasterRed),
    InternationalGrandmaster("International Grandmaster", 2600, 2999, GrandmasterRed),
    LegendaryGrandmaster("Legendary Grandmaster", 3000, 4000, GrandmasterRed),
}