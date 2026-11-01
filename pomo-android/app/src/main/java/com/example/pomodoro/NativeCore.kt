package com.example.pomodoro

/** C++ コア(pomodoro_core)への JNI ブリッジ。 */
object NativeCore {
    init { System.loadLibrary("pomodoro") }

    const val PHASE_IDLE = 0
    const val PHASE_WORK = 1
    const val PHASE_SHORT_BREAK = 2
    const val PHASE_LONG_BREAK = 3
    const val PHASE_ADDITIONAL_BREAK = 4
    const val PHASE_FINISHED = 5

    const val EVENT_PHASE_CHANGED = 1
    const val EVENT_REMINDER = 2

    external fun configure(
        work: Int, shortBreak: Int, longBreak: Int, additional: Int,
        sessions: Int, longInterval: Int, reminder: Int
    ): Boolean
    external fun start(nowSec: Long)
    external fun reset()
    external fun tick(nowSec: Long): Int
    external fun breakNow(nowSec: Long)
    external fun skipBreak(nowSec: Long)
    external fun phase(): Int
    external fun session(): Int
    external fun sessions(): Int
    external fun additionalCount(): Int
    external fun remainingSec(nowSec: Long): Long
    external fun percent(nowSec: Long): Int
}
