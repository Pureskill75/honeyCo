package atomsandbots.android.sweepsnatch.user.Registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import atomsandbots.android.sweepsnatch.user.R;
import atomsandbots.android.sweepsnatch.user.RoomDatabase.DataBaseAdapter;
import atomsandbots.android.sweepsnatch.user.UI.MainActivity;
import atomsandbots.android.sweepsnatch.user.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String name, email, password;


    private GoogleSignInClient googleSignInClient;
    private final String TAG = "GoogleMessage";
    private static final int RC_SIGN_IN = 9009;

    //View binding in use
    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create an Account");

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("Users");

        GoogleSignIn();

        GotoLogin();

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create account UI
                SignUp();
            }
        });
    }

    //Sign up new user method
    private void SignUp() {
        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Signing Up");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (Validation()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                DataBaseAdapter adapter = new DataBaseAdapter(SignUpActivity.this);
                                long id = adapter.insert(name, email, "", "", "", "", "");
                                if (id < 0) {
                                    Toast.makeText(SignUpActivity.this, "Data was not saved on room", Toast.LENGTH_SHORT).show();
                                }
                                Map<String, Object> map = new HashMap<>();
                                map.put("Name", name);
                                map.put("Email", email);
                                map.put("Phone", "null");
                                map.put("Postcode", "null");
                                map.put("Country", "null");
                                map.put("Address", "null");
                                map.put("Image", "null");
                                myRef.child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            SharedPreferences preferences = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putBoolean("isLogin", true);
                                            editor.putBoolean("isAdmin", false);
                                            editor.apply();
                                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            progressDialog.dismiss();
        }
    }

    private boolean Validation() {
        boolean valid = true;
        name = binding.signUpName.getText().toString().trim();
        email = binding.signUpEmail.getText().toString().trim();
        password = binding.signUpPassword.getText().toString().trim();

        if (name.isEmpty()) {
            binding.signUpName.setError("missing");
            binding.signUpName.requestFocus();
            valid = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signUpEmail.setError("invalid email");
            binding.signUpEmail.requestFocus();
            valid = false;
        }
        if (email.isEmpty()) {
            binding.signUpEmail.setError("empty");
            binding.signUpEmail.requestFocus();
            valid = false;
        }
        if (password.length() < 6) {
            binding.signUpPassword.setError("short");
            binding.signUpPassword.requestFocus();
            valid = false;
        }
        if (password.isEmpty()) {
            binding.signUpPassword.requestFocus();
            binding.signUpPassword.setError("missing");
            valid = false;
        }
        return valid;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }


    //If you have an account, send user to login activity
    private void GotoLogin() {
        String text1 = "Already have an account?";
        String text2 = "Login";
        SpannableString spannableString = new SpannableString(text1 + text2);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        };
        spannableString.setSpan(clickableSpan, text1.length(), text1.length() + text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.loginText.setText(spannableString);
        binding.loginText.setLinkTextColor(Color.parseColor(getString(R.string.have_or_not_have_account_txtcolor)));
        binding.loginText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //Start Google signIn Auth here
    private void GoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);


        //Google sign-in in create account activity
        binding.googleSignInCa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContinueGoogle();
            }
        });

    }

    ProgressDialog pd;

    private void ContinueGoogle() {
        pd = new ProgressDialog(SignUpActivity.this);
        pd.setMessage("Please Wait");
        pd.show();
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }
        }else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
    }


    // //Handle Google signIn Auth here
    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            FirebaseGoogleAuth(account);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SignInError", e.toString());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }


    //Firebase Authentication
    private void FirebaseGoogleAuth(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    boolean newUser = task.getResult().getAdditionalUserInfo().isNewUser();
                    SharedPreferences preferences = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isLogin", true);
                    editor.putBoolean("isAdmin", false);
                    editor.apply();
                    if (newUser) {
                        //If the user is new execute this block
                        FirebaseUser user = mAuth.getCurrentUser();
                        DataBaseAdapter adapter = new DataBaseAdapter(SignUpActivity.this);
                        assert user != null;
                        long id = adapter.insert(user.getDisplayName(), user.getEmail(), "", "", "", "", "");
                        if (id < 0) {
                            Toast.makeText(SignUpActivity.this, "Data was not saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Successfully saved", Toast.LENGTH_SHORT).show();
                        }
                        updateUI(user);
                    } else {
                        //Continue with Sign up
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    pd.dismiss();
                } else {
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        pd.dismiss();
    }

    private void updateUI(FirebaseUser user) {
        //Update user info
        Map<String, Object> map = new HashMap<>();
        map.put("Name", user.getDisplayName());
        map.put("Email", user.getEmail());
        map.put("Phone", "null");
        map.put("Postcode", "null");
        map.put("Country", "null");
        map.put("Address", "null");
        map.put("Image", "null");
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef.child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}