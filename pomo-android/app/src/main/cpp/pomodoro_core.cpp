#include "pomodoro_core.h"
#include <algorithm>

namespace pomodoro {

bool Core::configure(const Config& c) {
    if (c.workMin <= 0 || c.shortBreakMin <= 0 || c.longBreakMin <= 0 ||
        c.additionalBreakMin <= 0 || c.sessions <= 0 || c.longInterval <= 0 ||
        c.reminderMin <= 0) {
        return false;
    }
    cfg_ = c;
    reset();
    return true;
}

void Core::start(int64_t nowSec) {
    session_ = 1;
    longCounter_ = 0;
    additionalCount_ = 0;
    enter(Phase::Work, nowSec);
}

void Core::reset() {
    phase_ = Phase::Idle;
    session_ = 0;
    longCounter_ = 0;
    additionalCount_ = 0;
}

void Core::enter(Phase p, int64_t nowSec) {
    phase_ = p;
    phaseStart_ = nowSec;
    nextReminder_ = (p == Phase::Work) ? cfg_.reminderMin * 60LL : 0;
}

int64_t Core::phaseSeconds() const {
    switch (phase_) {
        case Phase::Work:            return cfg_.workMin * 60LL;
        case Phase::ShortBreak:      return cfg_.shortBreakMin * 60LL;
        case Phase::LongBreak:       return cfg_.longBreakMin * 60LL;
        case Phase::AdditionalBreak: return cfg_.additionalBreakMin * 60LL;
        default:                     return 0;
    }
}

void Core::advanceAfterBreak(int64_t nowSec) {
    if (phase_ == Phase::AdditionalBreak) {   // 追加休憩後は同じセッションをやり直す
        enter(Phase::Work, nowSec);
    } else if (session_ >= cfg_.sessions) {
        phase_ = Phase::Finished;
    } else {
        ++session_;
        enter(Phase::Work, nowSec);
    }
}

int Core::tick(int64_t nowSec) {
    if (!running()) return EventNone;
    const int64_t elapsed = std::max<int64_t>(0, nowSec - phaseStart_);

    if (elapsed >= phaseSeconds()) {
        if (phase_ == Phase::Work) {
            ++longCounter_;
            if (longCounter_ >= cfg_.longInterval) {
                longCounter_ = 0;
                enter(Phase::LongBreak, nowSec);
            } else {
                enter(Phase::ShortBreak, nowSec);
            }
        } else {
            advanceAfterBreak(nowSec);
        }
        return EventPhaseChanged;
    }

    if (phase_ == Phase::Work && elapsed >= nextReminder_) {
        const int64_t step = cfg_.reminderMin * 60LL;
        nextReminder_ = (elapsed / step + 1) * step;   // 溜まっても1回だけ通知
        return EventReminder;
    }
    return EventNone;
}

void Core::breakNow(int64_t nowSec) {
    if (phase_ != Phase::Work) return;
    ++additionalCount_;
    enter(Phase::AdditionalBreak, nowSec);
}

void Core::skipBreak(int64_t nowSec) {
    if (inBreak()) advanceAfterBreak(nowSec);
}

int64_t Core::remainingSec(int64_t nowSec) const {
    if (!running()) return 0;
    return std::max<int64_t>(0, phaseSeconds() - (nowSec - phaseStart_));
}

int32_t Core::percent(int64_t nowSec) const {
    const int64_t total = phaseSeconds();
    if (!running() || total <= 0) return 0;
    const int64_t p = (std::max<int64_t>(0, nowSec - phaseStart_) * 100) / total;
    return static_cast<int32_t>(std::min<int64_t>(100, p));
}

}  // namespace pomodoro
