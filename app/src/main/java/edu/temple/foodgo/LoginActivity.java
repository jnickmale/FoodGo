package edu.temple.foodgo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();




        Button signInButton = (Button)findViewById(R.id.email_sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((AutoCompleteTextView)findViewById(R.id.email)).getText().toString();
                String username = ((AutoCompleteTextView)findViewById(R.id.username)).getText().toString();
                String password = ((EditText)findViewById(R.id.password)).getText().toString();
                if(email.equals("")){
                    Toast.makeText(LoginActivity.this, "Please enter an email.",
                            Toast.LENGTH_SHORT).show();
                }else if(username.equals("")){
                    Toast.makeText(LoginActivity.this, "Please enter a username.",
                            Toast.LENGTH_SHORT).show();
                }else if(password.equals("")){
                    Toast.makeText(LoginActivity.this, "Please enter a password.",
                            Toast.LENGTH_SHORT).show();
                }else {
                    signIn(email, username, password);
                }
            }
        });

        Button registerButton = (Button)findViewById(R.id.email_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((AutoCompleteTextView)findViewById(R.id.email)).getText().toString();
                String username = ((AutoCompleteTextView)findViewById(R.id.username)).getText().toString();
                String password = ((EditText)findViewById(R.id.password)).getText().toString();
                if(email.equals("")){
                    Toast.makeText(LoginActivity.this, "Please enter an email.",
                            Toast.LENGTH_SHORT).show();
                }else if(username.equals("")){
                    Toast.makeText(LoginActivity.this, "Please enter a username.",
                            Toast.LENGTH_SHORT).show();
                }else if(password.equals("")){
                    Toast.makeText(LoginActivity.this, "Please enter a password.",
                            Toast.LENGTH_SHORT).show();
                }else {
                    registerUser(email, username, password);
                }
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser){
        if(currentUser != null){
            //start the main activity with the currentUsers info
            Intent intent = new Intent(LoginActivity.this, RestaurantActivity.class);
            startActivity(intent);
        }
    }

    /**
     * sign the user in with the given information
     * @param email
     * @param username
     * @param password
     */
    public void signIn(String email, String username, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login attempt", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login attempt", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * register the user with the given information
     * @param email
     * @param username
     * @param password
     */
    public void registerUser(String email, String username, String password){
        final String usernameConstant = username;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("registration attempt", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(usernameConstant).build();
                                user.updateProfile(profileUpdates);
                            }

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("registration attempt", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}
