package com.devst.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class CamaraActivity extends AppCompatActivity {

    private ImageView imagenPrevia;
    private Bitmap fotoBitmap;

    private final ActivityResultLauncher<Intent> tomarFotoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    fotoBitmap = (Bitmap) result.getData().getExtras().get("data");
                    imagenPrevia.setImageBitmap(fotoBitmap);
                    Toast.makeText(this, "Foto tomada", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        imagenPrevia = findViewById(R.id.imgPreview);
        Button btnTomarFoto = findViewById(R.id.btnTomarFoto);
        Button btnUsarFoto = findViewById(R.id.btnUsarFoto);

        btnTomarFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnUsarFoto.setOnClickListener(v -> {
            if (fotoBitmap != null) {
                // Convertimos a URI temporal para enviar
                Uri tempUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), fotoBitmap, "perfil", null));
                setResult(RESULT_OK, new Intent().setData(tempUri));
                finish();
            } else {
                Toast.makeText(this, "No hay foto para usar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<String> permisoCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) abrirCamara();
                else Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            });

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tomarFotoLauncher.launch(intent);
    }
}
