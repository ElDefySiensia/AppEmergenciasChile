package com.devst.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PerfilActivity extends AppCompatActivity {

    private TextView mostrarUsuario, mostrarCorreo, mostrarClave;
    private Button btnCamara, btnGaleria;
    private ImageView pfpPerfil;
    private Uri imagenSeleccionada;

    private final ActivityResultLauncher<Intent> abrirCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imagenSeleccionada = result.getData().getData();
                    if (imagenSeleccionada != null) {
                        pfpPerfil.setImageURI(imagenSeleccionada);
                        Toast.makeText(this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private final ActivityResultLauncher<String> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imagenSeleccionada = uri;
                    pfpPerfil.setImageURI(imagenSeleccionada);
                    Toast.makeText(this, "Perfil actualizado desde galería", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> permisoCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) abrirCamara();
                else Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mostrarUsuario = findViewById(R.id.mostrar_usuario);
        mostrarCorreo = findViewById(R.id.mostrar_correo);
        mostrarClave = findViewById(R.id.mostrar_clave);
        pfpPerfil = findViewById(R.id.pfp_perfil);
        btnCamara = findViewById(R.id.btnCamara);
        btnGaleria = findViewById(R.id.btnGaleria);

        // datos de usuario
        mostrarUsuario.setText("Usuario: " + getIntent().getStringExtra("usuario"));
        mostrarCorreo.setText("Correo: " + getIntent().getStringExtra("email_usuario"));
        mostrarClave.setText("Clave: " + getIntent().getStringExtra("clave"));

        btnCamara.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnGaleria.setOnClickListener(v -> seleccionarImagenLauncher.launch("image/*"));
    }

    private void abrirCamara() {
        Intent intent = new Intent(this, CamaraActivity.class);
        abrirCamaraLauncher.launch(intent);
    }
}

