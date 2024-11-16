package karen.gutierrez.verduritassa_karengutierrez;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText nameEditText;
    EditText countryEditText;
    EditText genderEditText;
    EditText passwordEditText;
    Button registerButton;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.emailEditText);
        nameEditText = findViewById(R.id.nameEditText);
        countryEditText = findViewById(R.id.countryEditText);
        genderEditText = findViewById(R.id.genderEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();



                registrar(email, password);
            }

        });

    };

    private void registrar(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    String name = nameEditText.getText().toString();
                    String pais = countryEditText.getText().toString();
                    String genero = genderEditText.getText().toString();

                    // Crear un objeto para almacenar datos
                    Map<String, Object> usuario = new HashMap<>();
                    usuario.put("nombre", name);
                    usuario.put("genero", genero);
                    usuario.put("correo", email);
                    usuario.put("pais", pais);
                    ;

                    // Guardar los datos en Firestore
                    db.collection("usuarios")
                            .add(usuario)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(getApplicationContext(), "Usuario guardado con éxito", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                            });

                    Intent listaCultivos = new Intent(RegistroActivity.this, ListaCultivoActivity.class);
                            startActivity(listaCultivos);
                } else {
                    Toast.makeText(RegistroActivity.this, "Autenticación fallida",
                            Toast.LENGTH_SHORT).show();
                }
                }

        });

    }
}