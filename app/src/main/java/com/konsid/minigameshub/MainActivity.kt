package com.konsid.minigameshub

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.random.Random

class MainActivity : Activity() {
    private val blue = Color.rgb(38, 92, 160)
    private val dark = Color.rgb(24, 34, 50)
    private val green = Color.rgb(46, 150, 105)
    private val white = Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun page(title: String): LinearLayout {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(20.dp(), 24.dp(), 20.dp(), 20.dp())
            setBackgroundColor(Color.rgb(245, 247, 251))
        }
        val heading = TextView(this).apply {
            text = title
            textSize = 28f
            setTextColor(dark)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 22.dp())
        }
        root.addView(heading, LinearLayout.LayoutParams(-1, -2))
        return root
    }

    private fun label(text: String, size: Float = 18f): TextView =
        TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(dark)
            gravity = Gravity.CENTER
            setPadding(8.dp(), 12.dp(), 8.dp(), 12.dp())
        }

    private fun gameButton(text: String, action: () -> Unit): Button =
        Button(this).apply {
            this.text = text
            isAllCaps = false
            textSize = 18f
            setOnClickListener { action() }
        }

    private fun showHome() {
        val root = page("🎮 MiniGames Hub")
        root.addView(label("Διάλεξε ένα παιχνίδι!", 18f))
        root.addView(gameButton("🐍 Snake") { showSnake() }, matchWidth())
        root.addView(gameButton("⭕ Tic-Tac-Toe") { showTicTacToe() }, matchWidth())
        root.addView(gameButton("🧠 Memory") { showMemory() }, matchWidth())
        root.addView(gameButton("✊ Rock-Paper-Scissors") { showRps() }, matchWidth())
        root.addView(label("Παίξε και διασκέδασε!", 14f))
        setContentView(root)
    }

    private fun matchWidth() = LinearLayout.LayoutParams(-1, -2).apply {
        setMargins(0, 7.dp(), 0, 7.dp())
    }

    private fun addBack(root: LinearLayout) {
        root.addView(gameButton("← Αρχική") { showHome() }, matchWidth())
    }

    private fun showTicTacToe() {
        val root = page("⭕ Tic-Tac-Toe")
        val status = label("Σειρά: X")
        root.addView(status)
        val grid = GridLayout(this).apply {
            columnCount = 3
            rowCount = 3
            useDefaultMargins = true
        }
        val cells = Array(9) { "" }
        val buttons = Array(9) { i ->
            Button(this).apply {
                text = ""
                textSize = 30f
                setTextColor(dark)
                setOnClickListener {
                    if (cells[i].isNotEmpty() || status.text.toString().contains("Νίκη") ||
                        status.text.toString().contains("Ισοπαλία")) return@setOnClickListener
                    val mark = if (cells.count { it.isNotEmpty() } % 2 == 0) "X" else "O"
                    cells[i] = mark
                    text = mark
                    val winner = winningMark(cells)
                    when {
                        winner != null -> status.text = "Νίκησε ο $winner! 🎉"
                        cells.all { it.isNotEmpty() } -> status.text = "Ισοπαλία!"
                        else -> status.text = "Σειρά: " + if (mark == "X") "O" else "X"
                    }
                }
            }.also { grid.addView(it, GridLayout.LayoutParams().apply {
                width = 92.dp(); height = 92.dp()
                setMargins(3.dp(), 3.dp(), 3.dp(), 3.dp())
            }) }
        }
        root.addView(grid)
        root.addView(gameButton("Νέο παιχνίδι") {
            cells.fill("")
            buttons.forEach { it.text = "" }
            status.text = "Σειρά: X"
        }, matchWidth())
        addBack(root)
        setContentView(root)
    }

    private fun winningMark(c: Array<String>): String? {
        val lines = listOf(
            listOf(0,1,2), listOf(3,4,5), listOf(6,7,8),
            listOf(0,3,6), listOf(1,4,7), listOf(2,5,8),
            listOf(0,4,8), listOf(2,4,6)
        )
        return lines.firstOrNull { line ->
            c[line[0]].isNotEmpty() && c[line[0]] == c[line[1]] && c[line[1]] == c[line[2]]
        }?.let { c[it[0]] }
    }

    private fun showMemory() {
        val root = page("🧠 Memory")
        val status = label("Βρες τα 8 ζευγάρια!")
        root.addView(status)
        val grid = GridLayout(this).apply { columnCount = 4; rowCount = 4 }
        val symbols = listOf("🍎","🚀","🐱","⚽","🌟","🎵","🍕","🚗")
        val deck = (symbols + symbols).shuffled()
        val opened = mutableListOf<Int>()
        val matched = mutableSetOf<Int>()
        var locked = false
        val buttons = Array(16) { i ->
            Button(this).apply {
                text = "?"
                textSize = 22f
                setOnClickListener {
                    if (locked || i in matched || i in opened) return@setOnClickListener
                    text = deck[i]
                    opened.add(i)
                    if (opened.size == 2) {
                        val a = opened[0]; val b = opened[1]
                        if (deck[a] == deck[b]) {
                            matched.add(a); matched.add(b); opened.clear()
                            status.text = "Σωστά ζευγάρια: ${matched.size / 2}/8"
                            if (matched.size == 16) status.text = "Τα βρήκες όλα! 🎉"
                        } else {
                            locked = true
                            Handler(Looper.getMainLooper()).postDelayed({
                                this@MainActivity.runOnUiThread {
                                    (grid.getChildAt(a) as Button).text = "?"
                                    (grid.getChildAt(b) as Button).text = "?"
                                    opened.clear()
                                    locked = false
                                }
                            }, 850)
                        }
                    }
                }
            }.also { grid.addView(it, GridLayout.LayoutParams().apply {
                width = 72.dp(); height = 72.dp()
                setMargins(2.dp(), 2.dp(), 2.dp(), 2.dp())
            }) }
        }
        root.addView(grid)
        root.addView(gameButton("Ανακάτεμα / Επανεκκίνηση") { showMemory() }, matchWidth())
        addBack(root)
        setContentView(root)
    }

    private fun showRps() {
        val root = page("✊ Rock-Paper-Scissors")
        val result = label("Διάλεξε: πέτρα, ψαλίδι ή χαρτί")
        root.addView(result)
        val choices = listOf("Πέτρα" to "✊", "Ψαλίδι" to "✌️", "Χαρτί" to "✋")
        choices.forEach { (name, emoji) ->
            root.addView(gameButton("$emoji  $name") {
                val computer = choices.random()
                val outcome = when {
                    name == computer.first -> "Ισοπαλία!"
                    (name == "Πέτρα" && computer.first == "Ψαλίδι") ||
                    (name == "Ψαλίδι" && computer.first == "Χαρτί") ||
                    (name == "Χαρτί" && computer.first == "Πέτρα") -> "Κέρδισες! 🎉"
                    else -> "Κέρδισε ο υπολογιστής!"
                }
                result.text = "Εσύ: $name\nΥπολογιστής: ${computer.first}\n$outcome"
            }, matchWidth())
        }
        addBack(root)
        setContentView(root)
    }

    private fun showSnake() {
        val root = page("🐍 Snake")
        val score = label("Σκορ: 0")
        root.addView(score)
        val snake = SnakeView(this, { score.text = "Σκορ: $it" }, {
            score.text = "Τέλος παιχνιδιού! Σκορ: ${it}"
        })
        root.addView(snake, LinearLayout.LayoutParams(-1, 0, 1f).apply {
            setMargins(0, 8.dp(), 0, 8.dp())
        })
        root.addView(label("Σύρε το δάχτυλο για να αλλάξεις κατεύθυνση", 13f))
        root.addView(gameButton("Νέα παρτίδα") { showSnake() }, matchWidth())
        addBack(root)
        setContentView(root)
    }

    override fun onPause() {
        super.onPause()
    }

    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()
}

private class SnakeView(
    context: android.content.Context,
    private val onScore: (Int) -> Unit,
    private val onGameOver: (Int) -> Unit
) : View(context) {
    private val handler = Handler(Looper.getMainLooper())
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cols = 18
    private val rows = 26
    private val snake = mutableListOf(Pair(8, 13), Pair(7, 13), Pair(6, 13))
    private var food = Pair(12, 13)
    private var dx = 1
    private var dy = 0
    private var score = 0
    private var over = false
    private var downX = 0f
    private var downY = 0f

    private val tick = object : Runnable {
        override fun run() {
            if (over) return
            val head = snake.first()
            val next = Pair(head.first + dx, head.second + dy)
            if (next.first !in 0 until cols || next.second !in 0 until rows ||
                next in snake.dropLast(if (next == food) 0 else 1)) {
                over = true
                onGameOver(score)
                invalidate()
                return
            }
            snake.add(0, next)
            if (next == food) {
                score++
                onScore(score)
                placeFood()
            } else snake.removeAt(snake.lastIndex)
            invalidate()
            handler.postDelayed(this, 150)
        }
    }

    init { setBackgroundColor(Color.rgb(235, 242, 235)); handler.postDelayed(tick, 150) }

    private fun placeFood() {
        do { food = Pair(Random.nextInt(cols), Random.nextInt(rows)) } while (food in snake)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cell = minOf(width.toFloat() / cols, height.toFloat() / rows)
        val left = (width - cell * cols) / 2f
        val top = (height - cell * rows) / 2f
        paint.color = Color.rgb(215, 226, 215)
        for (x in 0..cols) canvas.drawLine(left + x * cell, top, left + x * cell, top + rows * cell, paint)
        for (y in 0..rows) canvas.drawLine(left, top + y * cell, left + cols * cell, top + y * cell, paint)
        paint.color = Color.RED
        canvas.drawCircle(left + (food.first + .5f) * cell, top + (food.second + .5f) * cell, cell * .38f, paint)
        snake.forEachIndexed { index, part ->
            paint.color = if (index == 0) Color.rgb(20, 90, 55) else Color.rgb(55, 160, 90)
            canvas.drawRoundRect(
                left + part.first * cell + 1, top + part.second * cell + 1,
                left + (part.first + 1) * cell - 1, top + (part.second + 1) * cell - 1,
                cell * .2f, cell * .2f, paint
            )
        }
        if (over) {
            paint.color = Color.BLACK
            paint.textSize = 42f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("GAME OVER", width / 2f, height / 2f, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { downX = event.x; downY = event.y; return true }
            MotionEvent.ACTION_UP -> {
                val x = event.x - downX
                val y = event.y - downY
                if (maxOf(kotlin.math.abs(x), kotlin.math.abs(y)) > 25) {
                    if (kotlin.math.abs(x) > kotlin.math.abs(y)) {
                        if (x > 0 && dx != -1) { dx = 1; dy = 0 }
                        else if (x < 0 && dx != 1) { dx = -1; dy = 0 }
                    } else {
                        if (y > 0 && dy != -1) { dx = 0; dy = 1 }
                        else if (y < 0 && dy != 1) { dx = 0; dy = -1 }
                    }
                }
                return true
            }
        }
        return true
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacksAndMessages(null)
        super.onDetachedFromWindow()
    }
}
