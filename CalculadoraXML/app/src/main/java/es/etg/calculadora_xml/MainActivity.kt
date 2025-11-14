package es.etg.calculadora_xml

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    private lateinit var txtResult: TextView
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var prefs: SharedPreferences

    private var currentInput = ""
    private var operator = ""
    private var firstNumber: Double? = null
    private var resultDisplayed = false
    private var percentUsed = false
    private var darkMode = false

    // Colores modo oscuro
    private val darkBackground = "#121218"
    private val darkTextViewBg = "#2B2B2E"
    private val darkButtonTint = "#3A3A3F"
    private val darkTextColor = "#FFFFFF"

    // Colores modo claro
    private val lightBackground = "#FFFFFF"
    private val lightTextViewBg = "#DDDDDD"
    private val lightButtonTint = "#E0E0E0"
    private val lightTextColor = "#000000"

    // Botones números y operadores
    private val numberButtonIds = listOf(
        R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
        R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
    )

    private val operatorButtonIds = listOf(
        R.id.btnSum, R.id.btnMin, R.id.btnMul, R.id.btnDiv,
        R.id.btnEq, R.id.btnAc, R.id.btnDel, R.id.btn100, R.id.btnHpy
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("calcPrefs", Context.MODE_PRIVATE)

        txtResult = findViewById(R.id.txtResult)
        mainLayout = findViewById(R.id.main)

        txtResult.text = "0"

        // Recuperar el estado de modo oscuro
        darkMode = prefs.getBoolean("darkMode", false)
        if (darkMode) applyDarkTheme() else applyLightTheme()

        // Números y punto
        for (id in numberButtonIds) {
            findViewById<Button?>(id)?.setOnClickListener { onNumberPressed((it as Button).text.toString()) }
        }

        // Operadores
        findViewById<Button?>(R.id.btnSum)?.setOnClickListener { onOperatorPressed("+") }
        findViewById<Button?>(R.id.btnMin)?.setOnClickListener { onOperatorPressed("-") }
        findViewById<Button?>(R.id.btnMul)?.setOnClickListener { onOperatorPressed("*") }
        findViewById<Button?>(R.id.btnDiv)?.setOnClickListener { onOperatorPressed("/") }

        // Funciones
        findViewById<Button?>(R.id.btnEq)?.setOnClickListener { onEqualPressed() }
        findViewById<Button?>(R.id.btnAc)?.setOnClickListener { clearAll() }
        findViewById<Button?>(R.id.btnDel)?.setOnClickListener { onDeletePressed() }
        findViewById<Button?>(R.id.btn100)?.setOnClickListener { onPercentPressed() }

        // Botón especial =D → modo oscuro
        findViewById<Button?>(R.id.btnHpy)?.setOnClickListener { toggleDarkMode() }
    }

    // -------------------- Lógica --------------------
    private fun onNumberPressed(digit: String) {
        if (resultDisplayed) {
            if (operator.isEmpty()) firstNumber = null
            currentInput = digit
            resultDisplayed = false
            percentUsed = false
            updateDisplay()
            return
        }

        if (digit == "." && currentInput.contains(".")) return
        currentInput += digit
        updateDisplay()
    }

    private fun onOperatorPressed(op: String) {
        percentUsed = false
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if (value != null) {
                if (firstNumber == null) {
                    firstNumber = value
                } else if (operator.isNotEmpty()) {
                    firstNumber = compute(firstNumber!!, value, operator)
                }
            }
            currentInput = ""
            operator = op
            resultDisplayed = false
            updateDisplay()
            return
        }

        if (firstNumber != null) {
            operator = op
            updateDisplay()
        }
    }

    private fun onEqualPressed() {
        if (firstNumber != null && operator.isNotEmpty() && currentInput.isNotEmpty()) {
            val second = currentInput.toDoubleOrNull() ?: return
            val result = compute(firstNumber!!, second, operator)
            txtResult.text = if (result.isNaN()) "Error" else formatNumber(result)
            firstNumber = if (result.isNaN()) null else result
            currentInput = ""
            operator = ""
            resultDisplayed = true
            percentUsed = false
        }
    }

    private fun onPercentPressed() {
        if (percentUsed) return
        if (currentInput.isEmpty() && firstNumber == null) return

        if (firstNumber != null && operator.isNotEmpty() && currentInput.isNotEmpty()) {
            val bRaw = currentInput.toDoubleOrNull() ?: return
            val computed = when (operator) {
                "+", "-" -> firstNumber!! * bRaw / 100.0
                "*", "/" -> bRaw / 100.0
                else -> bRaw
            }
            currentInput = formatNumber(computed)
            percentUsed = true
            updateDisplay()
            return
        }

        if (currentInput.isNotEmpty()) {
            val v = currentInput.toDoubleOrNull() ?: return
            currentInput = formatNumber(v / 100.0)
            percentUsed = true
            updateDisplay()
            return
        }

        if (firstNumber != null && currentInput.isEmpty()) {
            firstNumber = firstNumber!! / 100.0
            percentUsed = true
            updateDisplay()
        }
    }

    private fun onDeletePressed() {
        if (resultDisplayed) return
        if (currentInput.isNotEmpty()) currentInput = currentInput.dropLast(1)
        else if (operator.isNotEmpty()) operator = ""
        else firstNumber = null
        updateDisplay()
    }

    private fun clearAll() {
        currentInput = ""
        operator = ""
        firstNumber = null
        resultDisplayed = false
        percentUsed = false
        txtResult.text = "0"
    }

    private fun updateDisplay() {
        val text = buildString {
            if (firstNumber != null) append(formatNumber(firstNumber!!))
            if (operator.isNotEmpty()) append(operator)
            append(currentInput)
        }
        txtResult.text = if (text.isEmpty()) "0" else text
    }

    private fun compute(a: Double, b: Double, op: String): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b == 0.0) Double.NaN else a / b
            else -> Double.NaN
        }
    }

    private fun formatNumber(value: Double): String {
        return if (value % 1.0 == 0.0) value.toInt().toString()
        else String.format("%.8f", value).trimEnd('0').trimEnd('.')
    }

    // -------------------- Tema / Modo oscuro --------------------
    private fun toggleDarkMode() {
        darkMode = !darkMode
        if (darkMode) applyDarkTheme() else applyLightTheme()
        // Guardar el estado
        prefs.edit().putBoolean("darkMode", darkMode).apply()
    }

    private fun applyDarkTheme() {
        mainLayout.setBackgroundColor(Color.parseColor(darkBackground))
        txtResult.setBackgroundColor(Color.parseColor(darkTextViewBg))
        txtResult.setTextColor(Color.parseColor(darkTextColor))

        recolorNumberButtons(darkButtonTint, darkTextColor)
        recolorOperatorButtons()
    }

    private fun applyLightTheme() {
        mainLayout.setBackgroundColor(Color.parseColor(lightBackground))
        txtResult.setBackgroundColor(Color.parseColor(lightTextViewBg))
        txtResult.setTextColor(Color.parseColor(lightTextColor))

        recolorNumberButtons(lightButtonTint, lightTextColor)
        recolorOperatorButtons()
    }

    private fun recolorNumberButtons(bgHex: String, textHex: String) {
        val tint = ColorStateList.valueOf(Color.parseColor(bgHex))
        val txtColor = Color.parseColor(textHex)
        for (id in numberButtonIds) {
            findViewById<Button?>(id)?.let {
                it.backgroundTintList = tint
                it.setTextColor(txtColor)
            }
        }
    }

    private fun recolorOperatorButtons() {
        val orange = Color.parseColor("#FF9800")
        val txtColor = Color.WHITE
        for (id in operatorButtonIds) {
            findViewById<Button?>(id)?.let {
                it.backgroundTintList = ColorStateList.valueOf(orange)
                it.setTextColor(txtColor)
            }
        }
    }
}