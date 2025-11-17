package es.etg.encuesta

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var progreso: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rgSatisfaccion = findViewById<RadioGroup>(R.id.rgSatisfaccion)
        val cbAndroid = findViewById<CheckBox>(R.id.cbAndroid)
        val cbIos = findViewById<CheckBox>(R.id.cbIos)
        val cbWeb = findViewById<CheckBox>(R.id.cbWeb)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        val tvResultado = findViewById<TextView>(R.id.tvResultado)
        progreso = findViewById(R.id.progreso)

        btnEnviar.setOnClickListener {
            val seleccion = findViewById<RadioButton>(
                rgSatisfaccion.checkedRadioButtonId
            )?.text ?: getString(R.string.no_respuesta)

            val plataformas = mutableListOf<String>()
            if (cbAndroid.isChecked) plataformas.add(getString(R.string.op_android))
            if (cbIos.isChecked) plataformas.add(getString(R.string.op_ios))
            if (cbWeb.isChecked) plataformas.add(getString(R.string.op_web))

            progreso.visibility = View.VISIBLE
            tvResultado.text = ""

            // Simular envío (ProgressBar)
            Handler(Looper.getMainLooper()).postDelayed({
                progreso.visibility = View.GONE
                val resumen = getString(
                    R.string.resumen_encuesta,
                    seleccion,
                    if (plataformas.isEmpty()) getString(R.string.sin_opcion) else plataformas.joinToString(", ")
                )
                tvResultado.text = resumen
            }, 2000)
        }
    }
}
