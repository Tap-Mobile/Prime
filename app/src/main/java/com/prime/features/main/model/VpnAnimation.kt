package com.prime.features.main.model

/**
 * Developed by
 * @author Aleksandr Artemov
 */
data class Frames(val min: Int, val max: Int)

sealed class VpnAnimation(val frames: Frames) {
    val isTransition: Boolean
        get() = this is Transition

    sealed class State(frames: Frames) : VpnAnimation(frames) {
        object Idle : State(Frames(0, 47))
        object Loading : State(Frames(48, 95))
        object Connected : State(Frames(144, 191))
        object Error : State(Frames(240, 287))
    }

    sealed class Transition(frames: Frames, val nextState: VpnAnimation) : VpnAnimation(frames) {
        object Connecting : Transition(Frames(96, 143), State.Connected)
        object Disconnecting : Transition(Frames(192, 239), State.Idle)
    }

    object Skip : VpnAnimation(Frames(-1, -1))
}

fun VpnAnimation.asTransition() = this as VpnAnimation.Transition

fun VpnAnimation.asState() = this as VpnAnimation.State