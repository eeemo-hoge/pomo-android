package com.example.pomodoro

import android.media.RingtoneManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 画面スリープを防ぐ(フォアグラウンドサービス未実装のため)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent { MaterialTheme { App() } }
    }
}

private val FIELDS = listOf(
    "作業時間(分) 推奨25-90" to "25",
    "短い休憩(分) 推奨5-20" to "5",
    "長い休憩(分) 推奨20-60" to "20",
    "追加休憩(分) 推奨60-90" to "60",
    "セッション数 推奨4-12" to "4",
    "長休憩の間隔(セッション) 推奨2-5" to "4",
    "リマインダー間隔(分)" to "23",
)

private val STRETCH = listOf(
    "首と肩を伸ばす", "立ち上がる", "深呼吸する", "見えるものを5つ数える",
    "腕と脚を振る", "何度かまばたきする", "窓の外を眺める", "数分、瞑想する",
)

@Composable
fun App(vm: TimerViewModel = viewModel()) {
    val s by vm.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.sounds.collect {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            RingtoneManager.getRingtone(context, uri)?.play()
        }
    }

    Surface(Modifier.fillMaxSize()) {
        if (s.phase == NativeCore.PHASE_IDLE) {
            SetupScreen(onStart = vm::start)
        } else {
            TimerScreen(s, vm)
        }
    }
}

@Composable
fun SetupScreen(onStart: (List<Int>) -> Boolean) {
    val values = remember { mutableStateListOf(*FIELDS.map { it.second }.toTypedArray()) }
    var error by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("ポモドーロタイマー", fontSize = 24.sp)
        FIELDS.forEachIndexed { i, (label, _) ->
            OutlinedTextField(
                value = values[i],
                onValueChange = { values[i] = it.filter(Char::isDigit).take(4) },
                label = { Text(label) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (error) Text("すべて1以上の数値を入力してください", color = MaterialTheme.colorScheme.error)
        Button(
            onClick = { error = !onStart(values.map { it.toIntOrNull() ?: 0 }) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("開始") }
    }
}

@Composable
fun TimerScreen(s: TimerViewModel.UiState, vm: TimerViewModel) {
    val label = when (s.phase) {
        NativeCore.PHASE_WORK -> "作業中"
        NativeCore.PHASE_SHORT_BREAK -> "休憩"
        NativeCore.PHASE_LONG_BREAK -> "長い休憩"
        NativeCore.PHASE_ADDITIONAL_BREAK -> "追加休憩"
        else -> "完了!お疲れさまでした"
    }
    val finished = s.phase == NativeCore.PHASE_FINISHED

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text(label, fontSize = 28.sp)
        if (!finished) {
            Text("セッション ${s.session}/${s.sessions}(追加休憩 ${s.additionalCount}回)")
            Text("%02d:%02d".format(s.remainingSec / 60, s.remainingSec % 60), fontSize = 64.sp)
            LinearProgressIndicator(progress = { s.percent / 100f }, modifier = Modifier.fillMaxWidth())
            Text("${s.percent}%")
        }

        when (s.phase) {
            NativeCore.PHASE_WORK ->
                Button(onClick = vm::breakNow) { Text("今すぐ休憩(追加休憩)") }
            NativeCore.PHASE_SHORT_BREAK, NativeCore.PHASE_LONG_BREAK, NativeCore.PHASE_ADDITIONAL_BREAK ->
                Button(onClick = vm::skipBreak) { Text("作業に戻る") }
        }
        OutlinedButton(onClick = vm::reset) { Text(if (finished) "最初に戻る" else "中止") }
    }

    if (s.showReminder) {
        AlertDialog(
            onDismissRequest = vm::dismissReminder,
            title = { Text("ストレッチ&リフレッシュ") },
            text = { Column { STRETCH.forEach { Text("・$it") } } },
            confirmButton = { TextButton(onClick = vm::dismissReminder) { Text("OK") } },
        )
    }
}
