package es.etg.formularioderegistro

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val rgGenero = findViewById<RadioGroup>(R.id.rgGenero)
        val cbNoticias = findViewById<CheckBox>(R.id.cbNoticias)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        val tvResumen = findViewById<TextView>(R.id.tvResumen)

        btnEnviar.setOnClickListener {

            val nombre = etNombre.text.toString()
            val email = etEmail.text.toString()

            val idGenero = rgGenero.checkedRadioButtonId
            val genero = if (idGenero != -1) {
                findViewById<RadioButton>(idGenero).text.toString()
            } else {
                "No especificado"
            }

            val noticias = if (cbNoticias.isChecked) "Sí" else "No"

            val resumen = """
                Nombre: $nombre
                Email: $email
                Género: $genero
                Recibir noticias: $noticias
            """.trimIndent()

            tvResumen.text = resumen
        }
    }
}


