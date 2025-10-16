package com.devst.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.location.Location;
import android.location.LocationManager;
import androidx.appcompat.app.AlertDialog;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class HomeActivity extends AppCompatActivity {

    // Variables
    private String usuario = "";
    private String emailUsuario = "";
    private String clave = "";
    private TextView tvBienvenida;
    private String camaraID = null;
    private boolean luz = false;

    // Activity Result (para recibir datos de PerfilActivity)
    private final ActivityResultLauncher<Intent> editarPerfilLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String nombre = result.getData().getStringExtra("nombre_editado");
                    if (nombre != null) {
                        tvBienvenida.setText("Hola, " + nombre);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Referencias
        tvBienvenida = findViewById(R.id.tvBienvenida);
        Button btnIrPerfil = findViewById(R.id.btnIrPerfil);
        Button btnAbrirWeb = findViewById(R.id.btnAbrirWeb);
        Button btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        Button btnCompartir = findViewById(R.id.btnCompartir);
        Button btnLlamada = findViewById(R.id.btnLlamada);
        Button btnGuia = findViewById(R.id.btnGuia);
        Button btnUbicacion = findViewById(R.id.btnUbicacion);

        // Recibir dato del Login
        emailUsuario = getIntent().getStringExtra("email_usuario");
        if (emailUsuario == null) emailUsuario = "";
        tvBienvenida.setText("Bienvenido: " + emailUsuario);
        String usuario = getIntent().getStringExtra("usuario");
        String clave = getIntent().getStringExtra("clave");

        // Evento: Intent explícito → ProfileActivity (esperando resultado)
        btnIrPerfil.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, PerfilActivity.class);
            i.putExtra("usuario", usuario);
            i.putExtra("email_usuario", emailUsuario);
            i.putExtra("clave", clave);
            editarPerfilLauncher.launch(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Evento: Intent implícito → abrir web
        btnAbrirWeb.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://senapred.cl/informate/eventos");
            Intent viewWeb = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(viewWeb);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Evento: Intent implícito → enviar correo
        btnEnviarCorreo.setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:")); // Solo apps de correo
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailUsuario});
            email.putExtra(Intent.EXTRA_SUBJECT, "Correo de Emergencia.");
            email.putExtra(Intent.EXTRA_TEXT, "Me encuentro en una emergencia, enviar ayuda.");
            startActivity(Intent.createChooser(email, "Enviar correo con:"));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Evento: Intent implícito → compartir texto
        btnCompartir.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, "Descarga App Emergencias y conectate conmigo!");
            startActivity(Intent.createChooser(share, "Compartir usando:"));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        btnLlamada.setOnClickListener(v -> {
            Intent abrirLlamadas = new Intent(HomeActivity.this, EmergenciasActivity.class);
            startActivity(abrirLlamadas);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        btnUbicacion.setOnClickListener(v -> {
            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Advertencia")
                    .setMessage("Para esta acción asegúrese de que la ubicación se encuentra activada.")
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        mostrarUbicacion();
                    })
                    .setCancelable(true)
                    .show();
        });

        btnGuia.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, GuiaActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void mostrarUbicacion() {
        // Verificar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }

        // Obtener ubicación actual
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc == null) loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (loc != null) {
                double lat = loc.getLatitude();
                double lon = loc.getLongitude();
                Uri geoUri = Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(Mi Ubicación)");
                Intent intent = new Intent(Intent.ACTION_VIEW, geoUri);
                intent.setPackage("com.google.android.apps.maps"); // Abrir en Google Maps
                startActivity(intent);
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación. Asegúrese de que el GPS esté activado.", Toast.LENGTH_LONG).show();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al obtener ubicación", Toast.LENGTH_SHORT).show();
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    mostrarUbicacion(); // Reintenta mostrar ubicación
                } else {
                    Toast.makeText(this, "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show();
                }
            });

    // ===== Menú en HomeActivity =====
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_perfil) {
            // Ir al perfil (explícito)
            Intent i = new Intent(this, PerfilActivity.class);
            i.putExtra("email_usuario", emailUsuario);
            editarPerfilLauncher.launch(i);
            return true;
        } else if (id == R.id.action_web) {
            // Abrir web (implícito)
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com")));
            return true;
        } else if (id == R.id.action_salir) {
            finish(); // Cierra HomeActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}