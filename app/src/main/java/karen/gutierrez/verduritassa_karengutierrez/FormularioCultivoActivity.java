package karen.gutierrez.verduritassa_karengutierrez;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FormularioCultivoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_cultivo);

        EditText alias = findViewById(R.id.alias);  // EditText para nombre
        EditText editFechaCultivo = findViewById(R.id.editFechaCultivo);  // EditText para la fecha
        Spinner spinnerPlanta = findViewById(R.id.spinnerPlanta);  // Spinner para seleccionar la planta
        Button guardarCultivo = findViewById(R.id.guardarCultivo);  // Botón de guardar

        // Crear un ArrayAdapter con las opciones de plantas
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.plantas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Asignar el adapter al Spinner
        spinnerPlanta.setAdapter(adapter);

        // Configurar el DatePicker para el campo de fecha
        editFechaCultivo.setOnClickListener(v -> {
            // Obtener la fecha actual para inicializar el DatePicker
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            // Crear el DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    FormularioCultivoActivity.this,
                    (view, year1, month1, dayOfMonth1) -> {
                        // Establecer la fecha seleccionada en el campo de texto
                        String fechaSeleccionada = year1 + "-" + (month1 + 1) + "-" + dayOfMonth1;
                        editFechaCultivo.setText(fechaSeleccionada);
                    },
                    year, month, dayOfMonth
            );

            // Mostrar el DatePickerDialog
            datePickerDialog.show();
        });

        guardarCultivo.setOnClickListener(v -> {
            // Obtener los valores de los EditText
            String nombreCultivo = alias.getText().toString();
            String fechaCultivo = editFechaCultivo.getText().toString();
            String nombrePlanta = spinnerPlanta.getSelectedItem().toString();  // Obtener la planta seleccionada

            // Validar que los campos no estén vacíos
            if (!nombreCultivo.isEmpty() && !fechaCultivo.isEmpty() && !nombrePlanta.isEmpty()) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    // Obtener el correo del usuario autenticado
                    String email = currentUser.getEmail();

                    // Convertir la fecha de cultivo de String a Date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date fecha = null;
                    try {
                        fecha = dateFormat.parse(fechaCultivo);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (fecha != null) {
                        // Determinar los días de cosecha según la planta seleccionada
                        int diasCosecha = 0;
                        switch (nombrePlanta) {
                            case "Tomate":
                                diasCosecha = 80;
                                break;
                            case "Cebolla":
                                diasCosecha = 120;
                                break;
                            case "Lechuga":
                                diasCosecha = 85;
                                break;
                            case "Apio":
                                diasCosecha = 150;
                                break;
                            case "Choclo":
                                diasCosecha = 90;
                                break;
                        }

                        // Sumar los días a la fecha de cultivo
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(fecha);
                        calendar.add(Calendar.DAY_OF_YEAR, diasCosecha);  // Sumar días a la fecha

                        // Obtener la fecha de cosecha
                        Date fechaCosecha = calendar.getTime();
                        String fechaCosechaString = dateFormat.format(fechaCosecha);

                        // Crear un mapa con los datos del cultivo y asociar el correo
                        Map<String, Object> cultivo = new HashMap<>();
                        cultivo.put("nombre", nombreCultivo);
                        cultivo.put("fecha", fechaCultivo);
                        cultivo.put("planta", nombrePlanta);
                        cultivo.put("correo", email);  // Asociar el correo del usuario
                        cultivo.put("fecha_cosecha", fechaCosechaString);  // Fecha de cosecha

                        // Guardar el cultivo en Firestore
                        db.collection("cultivos")
                                .add(cultivo)
                                .addOnSuccessListener(documentReference -> {
                                    // Mostrar mensaje de éxito
                                    Toast.makeText(this, "Cultivo guardado", Toast.LENGTH_SHORT).show();
                                    finish(); // Cierra la actividad y vuelve a la lista de cultivos
                                })
                                .addOnFailureListener(e -> {
                                    // Mostrar mensaje de error
                                    Toast.makeText(this, "Error al guardar el cultivo", Toast.LENGTH_SHORT).show();
                                    Log.e("FormularioCultivo", "Error al guardar el cultivo: ", e);  // Log de error
                                });
                    }
                } else {
                    // Si no hay usuario autenticado
                    Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Si algún campo está vacío
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

}





