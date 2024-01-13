package com.smh.nxleave.design.component.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally


data class Transition(
    val enterTransition: EnterTransition,
    val exitTransition: ExitTransition,
    val popEnterTransition: EnterTransition,
    val popExitTransition: ExitTransition
)

const val durationMillis = 100
const val slideDurationMillis = 300
fun loadTransitions(isMainEntry: Boolean): Transition {
    val enterTransition = if (isMainEntry) {
        fadeIn(tween(durationMillis))
    } else {
        slideInHorizontally(
            initialOffsetX = { 1500 },
            animationSpec = tween(slideDurationMillis)
        )
    }
    val exitTransition = if (isMainEntry) {
        fadeOut(tween(durationMillis))
    } else {
        slideOutHorizontally(
            targetOffsetX = { -1500 },
            animationSpec = tween(slideDurationMillis)
        )
    }
    val popEnterTransition =
        if (isMainEntry) {
            fadeIn(tween(durationMillis))
        } else {
            slideInHorizontally(
                initialOffsetX = { -1500 },
                animationSpec = tween(slideDurationMillis)
            )
        }
    val popExitTransition = if (isMainEntry) {
        fadeOut(tween(durationMillis))
    } else {
        slideOutHorizontally(
            targetOffsetX = { 1500 },
            animationSpec = tween(slideDurationMillis)
        )
    }
    return Transition(
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition
    )
}