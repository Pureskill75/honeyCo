package atomsandbots.android.sweepsnatch.user.UI;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import atomsandbots.android.sweepsnatch.user.R;
import atomsandbots.android.sweepsnatch.user.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Forgot Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firebaseAuth = FirebaseAuth.getInstance();

        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.sendEmail.getText().toString();
                if (TextUtils.isEmpty(email)){
                    binding.sendEmail.setError("Empty");
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.sendEmail.setError("invalid");
                }else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this, R.string.check_you_email, Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return false;
    }
}