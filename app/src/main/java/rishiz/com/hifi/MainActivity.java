package rishiz.com.hifi;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private EditText edtUsername, edtEmail, edtPassword;
    private TextView txtLoginInfo;
    private Button btnSubmit;
    private boolean isSignUp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUsername = findViewById(R.id.username);
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtLoginInfo = findViewById(R.id.loginInfo);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            launchFriendsActivity();
            finish();
        }
        txtLoginInfo.setOnClickListener(v -> {
            if (isSignUp) {
                isSignUp = false;
                edtUsername.setVisibility(View.GONE);
                btnSubmit.setText("Log In");
                txtLoginInfo.setText("Dont have an account? Sing up");
            } else {
                isSignUp = true;
                edtUsername.setVisibility(View.VISIBLE);
                btnSubmit.setText("Sign Up");
                txtLoginInfo.setText("Already have an account ? log In");
            }

        });
        btnSubmit.setOnClickListener(v -> {
            if (edtEmail.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()) {
                if (isSignUp && edtUsername.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (isSignUp) {
                handleSignUp();
            } else {
                handleLogIn();
            }
        });

    }

    private void handleSignUp() {
        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isComplete()) {
                        FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(edtUsername.getText().toString(), edtEmail.getText().toString(), ""));
                        launchFriendsActivity();
                        Toast.makeText(MainActivity.this, "SignUp Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }catch (Exception e){
            if(edtPassword.getText().toString().length()<6){
                Toast.makeText(MainActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            }else
            {//need to fix this issue
                Toast.makeText(MainActivity.this, "The email address is badly formatted",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleLogIn() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isComplete()) {
                    launchFriendsActivity();
                    Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void launchFriendsActivity(){
        startActivity(new Intent(MainActivity.this,FriendsActivity.class));
    }
}