package karen.gutierrez.verduritassa_karengutierrez;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListaCultivoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_cultivos);

        // Configuración de la barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ListaCultivoActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Configuración de Firestore y lista
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ListView lista = findViewById(R.id.lista);

        // Obtener el usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();

            // Escuchar cambios en tiempo real
            db.collection("cultivos")
                    .whereEqualTo("correo", email)
                    .addSnapshotListener((snapshots, error) -> {
                        if (error != null) {
                            Log.w("FirestoreListener", "Error al escuchar cambios: ", error);
                            Toast.makeText(this, "Error al escuchar cambios", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (snapshots != null) {
                            List<Cultivo> listaCultivos = new ArrayList<>();

                            for (QueryDocumentSnapshot document : snapshots) {
                                String id = document.getId();
                                String nombre = document.getString("nombre");
                                String fecha = document.getString("fecha");
                                String fechaCosecha = document.getString("fecha_cosecha");

                                listaCultivos.add(new Cultivo(id, nombre, fecha, fechaCosecha));
                            }

                            // Actualizar el adaptador
                            CultivoAdapter adapter = new CultivoAdapter(ListaCultivoActivity.this, listaCultivos);
                            lista.setAdapter(adapter);
                        }
                    });
        } else {
            Intent intent = new Intent(ListaCultivoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Configuración del botón "Agregar Cultivo"
        ImageButton btnAgregarCultivo = findViewById(R.id.btnAgregarCultivo);
        btnAgregarCultivo.setOnClickListener(v -> {
            Intent intent = new Intent(ListaCultivoActivity.this, FormularioCultivoActivity.class);
            startActivity(intent);
        });
    }

    // Método para mostrar el BottomSheetDialog
    public void showBottomSheetDialog(Cultivo cultivo) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_menu, null);

        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetView.findViewById(R.id.btnEdit).setOnClickListener(v -> {
            Intent intent = new Intent(ListaCultivoActivity.this, EditarCultivoActivity.class);
            intent.putExtra("nombre", cultivo.getNombre());
            intent.putExtra("fecha", cultivo.getFecha());
            intent.putExtra("cultivoId", cultivo.getId());
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.btnDelete).setOnClickListener(v -> {
            eliminarCultivo(cultivo.getId());
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void eliminarCultivo(String cultivoId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Eliminar el documento en Firestore
        db.collection("cultivos")
                .document(cultivoId)  // Usamos el ID para identificar el cultivo
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Mostrar mensaje de éxito
                    Toast.makeText(this, "Cultivo eliminado", Toast.LENGTH_SHORT).show();
                    obtenerCultivos();  // Recargar los cultivos
                })
                .addOnFailureListener(e -> {
                    // Mostrar mensaje de error si ocurre un problema
                    Toast.makeText(this, "Error al eliminar el cultivo", Toast.LENGTH_SHORT).show();
                });
    }

    private void obtenerCultivos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ListView lista = findViewById(R.id.lista); // Asegúrate de que el ID sea correcto

        // Obtener el email del usuario autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();  // Obtener el email del usuario autenticado

            db.collection("cultivos")
                    .whereEqualTo("correo", email)  // Filtrar los cultivos por el correo
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Cultivo> listaCultivos = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();  // Obtener el ID del documento
                                String nombre = document.getString("nombre");
                                String fecha = document.getString("fecha");
                                String fechaCosecha = document.getString("fecha_cosecha");  // Obtener la fecha de cosecha

                                listaCultivos.add(new Cultivo(id, nombre, fecha, fechaCosecha));  // Crear objeto Cultivo con la fecha de cosecha
                            }

                            // Usar el adaptador para mostrar la lista de cultivos con la fecha de cosecha
                            CultivoAdapter adapter = new CultivoAdapter(ListaCultivoActivity.this, listaCultivos);
                            lista.setAdapter(adapter);
                        } else {
                            Toast.makeText(getApplicationContext(), "Error al leer los datos", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Si el usuario no está autenticado, redirigir a la pantalla de inicio de sesión
            Intent intent = new Intent(ListaCultivoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    }






