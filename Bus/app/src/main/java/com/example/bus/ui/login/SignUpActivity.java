package com.example.bus.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bus.MainActivity;
import com.example.bus.R;
import com.example.bus.ui.dbs.HelperDB;
import com.example.bus.ui.dbs.UserDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText signupUsername, signupEmail, signupPassword;
    private FirebaseAuth auth;
    private UserDatabase userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signupUsername = findViewById(R.id.signup_username);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        Button signupButton = findViewById(R.id.signup_button);

        auth = FirebaseAuth.getInstance();
        userDatabase = UserDatabase.getInstance(this);

        signupButton.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String username = signupUsername.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Store in SQLite
        HelperDB newUser = new HelperDB(username, email, password);
        userDatabase.userDao().insertUser(newUser);

        // Store in Firebase for online sync
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Signup Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
