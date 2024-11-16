package karen.gutierrez.verduritassa_karengutierrez;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditarCultivoActivity extends AppCompatActivity {

    private EditText etNombreCultivo, etFechaCultivo;
    private Button btnGuardar;
    private String cultivoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cultivo);

        // Inicializar las vistas
        etNombreCultivo = findViewById(R.id.etNombreCultivo);
        etFechaCultivo = findViewById(R.id.etFechaCultivo);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Obtener los datos pasados por el Intent
        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String fecha = intent.getStringExtra("fecha");
        cultivoId = intent.getStringExtra("cultivoId");

        // Mostrar los datos en los campos de edición
        etNombreCultivo.setText(nombre);
        etFechaCultivo.setText(fecha);

        // Guardar los cambios al presionar el botón
        btnGuardar.setOnClickListener(v -> {
            String nombreNuevo = etNombreCultivo.getText().toString();
            String fechaNueva = etFechaCultivo.getText().toString();

            if (!nombreNuevo.isEmpty() && !fechaNueva.isEmpty()) {
                actualizarCultivo(cultivoId, nombreNuevo, fechaNueva);
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarCultivo(String cultivoId, String nombre, String fecha) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("EditarCultivo", "Actualizando cultivo con ID: " + cultivoId);

        // Verifica que los valores no sean vacíos
        if (nombre.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar el documento en Firestore
        db.collection("cultivos")
                .document(cultivoId)  // Usamos el ID para identificar el cultivo
                .update("nombre", nombre, "fecha", fecha)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EditarCultivo", "Cultivo actualizado correctamente");
                    Toast.makeText(this, "Cultivo actualizado", Toast.LENGTH_SHORT).show();
                    finish();  // Cerrar la actividad y volver a la anterior
                })
                .addOnFailureListener(e -> {
                    Log.e("EditarCultivo", "Error al actualizar el cultivo", e);
                    Toast.makeText(this, "Error al actualizar el cultivo", Toast.LENGTH_SHORT).show();
                });
    }
}

