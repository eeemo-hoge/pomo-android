package com.example.pomodoro

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    data class UiState(
        val phase: Int = NativeCore.PHASE_IDLE,
        val session: Int = 0,
        val sessions: Int = 0,
        val remainingSec: Long = 0,
        val percent: Int = 0,
        val additionalCount: Int = 0,
        val showReminder: Boolean = false,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    // 通知音を鳴らすタイミング(UI側で収集)
    private val _sounds = MutableSharedFlow<Unit>(extraBufferCapacity = 4)
    val sounds: SharedFlow<Unit> = _sounds.asSharedFlow()

    private var ticker: Job? = null
    private var reminderVisible = false

    // スリープ中も進む単調時計(秒)。端末時刻の変更に影響されない。
    private fun now(): Long = SystemClock.elapsedRealtime() / 1000

    /** v = [作業, 短休憩, 長休憩, 追加休憩, セッション数, 長休憩間隔, リマインダー間隔](分/回) */
    fun start(v: List<Int>): Boolean {
        if (!NativeCore.configure(v[0], v[1], v[2], v[3], v[4], v[5], v[6])) return false
        NativeCore.start(now())
        reminderVisible = false
        publish()
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (isActive) {
                delay(500)
                val ev = NativeCore.tick(now())
                if (ev != 0) onEvent(ev)
                publish()
                if (NativeCore.phase() == NativeCore.PHASE_FINISHED) break
            }
        }
        return true
    }

    fun breakNow() {
        NativeCore.breakNow(now())
        onEvent(NativeCore.EVENT_PHASE_CHANGED)
        publish()
    }

    fun skipBreak() {
        NativeCore.skipBreak(now())
        onEvent(NativeCore.EVENT_PHASE_CHANGED)
        publish()
    }

    fun reset() {
        ticker?.cancel()
        NativeCore.reset()
        reminderVisible = false
        publish()
    }

    fun dismissReminder() {
        reminderVisible = false
        publish()
    }

    private fun onEvent(ev: Int) {
        _sounds.tryEmit(Unit)
        val phase = NativeCore.phase()
        val enteredBreak = (ev and NativeCore.EVENT_PHASE_CHANGED) != 0 &&
            phase in NativeCore.PHASE_SHORT_BREAK..NativeCore.PHASE_ADDITIONAL_BREAK
        if ((ev and NativeCore.EVENT_REMINDER) != 0 || enteredBreak) reminderVisible = true
    }

    private fun publish() {
        val t = now()
        _state.value = UiState(
            phase = NativeCore.phase(),
            session = NativeCore.session(),
            sessions = NativeCore.sessions(),
            remainingSec = NativeCore.remainingSec(t),
            percent = NativeCore.percent(t),
            additionalCount = NativeCore.additionalCount(),
            showReminder = reminderVisible,
        )
    }
}
