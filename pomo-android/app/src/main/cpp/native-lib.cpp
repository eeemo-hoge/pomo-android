#include <jni.h>
#include "pomodoro_core.h"

// 全関数はメインスレッドからのみ呼ぶ前提(ViewModel側で保証)。
static pomodoro::Core g_core;

extern "C" {

#define JNI(name) Java_com_example_pomodoro_NativeCore_##name

JNIEXPORT jboolean JNICALL JNI(configure)(JNIEnv*, jobject, jint work, jint shortBreak,
        jint longBreak, jint additional, jint sessions, jint longInterval, jint reminder) {
    pomodoro::Config c;
    c.workMin = work;
    c.shortBreakMin = shortBreak;
    c.longBreakMin = longBreak;
    c.additionalBreakMin = additional;
    c.sessions = sessions;
    c.longInterval = longInterval;
    c.reminderMin = reminder;
    return g_core.configure(c) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL JNI(start)(JNIEnv*, jobject, jlong now) { g_core.start(now); }
JNIEXPORT void JNICALL JNI(reset)(JNIEnv*, jobject) { g_core.reset(); }
JNIEXPORT jint JNICALL JNI(tick)(JNIEnv*, jobject, jlong now) { return g_core.tick(now); }
JNIEXPORT void JNICALL JNI(breakNow)(JNIEnv*, jobject, jlong now) { g_core.breakNow(now); }
JNIEXPORT void JNICALL JNI(skipBreak)(JNIEnv*, jobject, jlong now) { g_core.skipBreak(now); }

JNIEXPORT jint JNICALL JNI(phase)(JNIEnv*, jobject) { return static_cast<jint>(g_core.phase()); }
JNIEXPORT jint JNICALL JNI(session)(JNIEnv*, jobject) { return g_core.session(); }
JNIEXPORT jint JNICALL JNI(sessions)(JNIEnv*, jobject) { return g_core.sessions(); }
JNIEXPORT jint JNICALL JNI(additionalCount)(JNIEnv*, jobject) { return g_core.additionalCount(); }
JNIEXPORT jlong JNICALL JNI(remainingSec)(JNIEnv*, jobject, jlong now) { return g_core.remainingSec(now); }
JNIEXPORT jint JNICALL JNI(percent)(JNIEnv*, jobject, jlong now) { return g_core.percent(now); }

}  // extern "C"
