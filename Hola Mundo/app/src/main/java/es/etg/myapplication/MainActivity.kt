package es.etg.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val btnSaludar = findViewById<Button>(R.id.btnSaludar)
        val tvMensaje = findViewById<TextView>(R.id.tvMensaje)

        btnSaludar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val saludo = if (nombre.isNotBlank()) {
                getString(R.string.saludo_personal, nombre)
            } else {
                getString(R.string.saludo_generico)
            }
            tvMensaje.text = saludo
        }
    }
}
