package com.example.pandora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.pandora.R
import com.example.pandora.Login


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.get_started)

        val getStartedButton: Button = findViewById(R.id.btn_get_started)
        getStartedButton.setOnClickListener {

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

}
