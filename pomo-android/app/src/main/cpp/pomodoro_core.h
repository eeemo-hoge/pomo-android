#pragma once
#include <cstdint>

// ポモドーロの状態遷移ロジック。標準C++のみ(Android非依存)なのでPCでもテスト可能。
namespace pomodoro {

enum class Phase : int {
    Idle = 0, Work = 1, ShortBreak = 2, LongBreak = 3, AdditionalBreak = 4, Finished = 5
};

// tick() の戻り値(ビットフラグ)
enum Event : int { EventNone = 0, EventPhaseChanged = 1, EventReminder = 2 };

struct Config {
    int32_t workMin = 25;
    int32_t shortBreakMin = 5;
    int32_t longBreakMin = 20;
    int32_t additionalBreakMin = 60;
    int32_t sessions = 4;
    int32_t longInterval = 4;   // 何セッションごとに長休憩か
    int32_t reminderMin = 23;   // ストレッチ通知の間隔(作業中)
};

class Core {
public:
    bool configure(const Config& cfg);          // 全項目 > 0 でなければ false
    void start(int64_t nowSec);
    void reset();
    int tick(int64_t nowSec);                   // 時間経過を反映し Event を返す
    void breakNow(int64_t nowSec);              // 作業中 'b' : 追加休憩へ
    void skipBreak(int64_t nowSec);             // 休憩中 'b' : 作業へ戻る

    Phase phase() const { return phase_; }
    int32_t session() const { return session_; }
    int32_t sessions() const { return cfg_.sessions; }
    int32_t additionalCount() const { return additionalCount_; }
    int64_t remainingSec(int64_t nowSec) const;
    int32_t percent(int64_t nowSec) const;

private:
    bool running() const { return phase_ != Phase::Idle && phase_ != Phase::Finished; }
    bool inBreak() const {
        return phase_ == Phase::ShortBreak || phase_ == Phase::LongBreak ||
               phase_ == Phase::AdditionalBreak;
    }
    int64_t phaseSeconds() const;
    void enter(Phase p, int64_t nowSec);
    void advanceAfterBreak(int64_t nowSec);

    Config cfg_{};
    Phase phase_ = Phase::Idle;
    int64_t phaseStart_ = 0;
    int64_t nextReminder_ = 0;
    int32_t session_ = 0;
    int32_t longCounter_ = 0;
    int32_t additionalCount_ = 0;
};

}  // namespace pomodoro
