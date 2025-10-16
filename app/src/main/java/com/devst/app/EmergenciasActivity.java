package com.devst.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class EmergenciasActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PERMISSION = 1;
    private String numeroLlamadaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencias);

        Button btnCarabineros = findViewById(R.id.btn_carabineros);
        Button btnBomberos = findViewById(R.id.btn_bomberos);
        Button btnAmbulancia = findViewById(R.id.btn_ambulancia);
        Button btnContactos = findViewById(R.id.btnContactos);

        // Llamar a Carabineros
        btnCarabineros.setOnClickListener(v -> mostrarAdvertenciaLlamada("Carabineros", "133"));

        // Llamar a Bomberos
        btnBomberos.setOnClickListener(v -> mostrarAdvertenciaLlamada("Bomberos", "132"));

        // Llamar Ambulancia
        btnAmbulancia.setOnClickListener(v -> mostrarAdvertenciaLlamada("Ambulancia", "131"));

        // Abrir Contactos
        btnContactos.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
            startActivity(intent);
        });
    }

    private void mostrarAdvertenciaLlamada(String servicio, String numero) {
        new AlertDialog.Builder(this)
                .setTitle("¡Advertencia!")
                .setMessage("Estás a punto de llamar a " + servicio +
                        ". Si realizas esta acción imprudentemente, podría tener consecuencias legales.\n\n¿Deseas continuar?")
                .setPositiveButton("Aceptar", (dialog, which) -> realizarLlamada(numero))
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void realizarLlamada(String numero) {
        numeroLlamadaActual = numero;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + numero));
            startActivity(intent);
        }
    }

    // Resultado de la solicitud de permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                realizarLlamada(numeroLlamadaActual);
            } else {
                Toast.makeText(this, "Permiso de llamada denegado.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
