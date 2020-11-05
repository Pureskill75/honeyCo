package atomsandbots.android.sweepsnatch.user.Registration;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import atomsandbots.android.sweepsnatch.user.R;
import atomsandbots.android.sweepsnatch.user.UI.MainActivity;
import atomsandbots.android.sweepsnatch.user.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        atomsandbots.android.sweepsnatch.user.databinding.ActivityRegisterBinding binding
                = ActivityRegisterBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);


        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoLoginPage();
            }
        });

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoSignUpPage();
            }
        });
    }

    private void GotoSignUpPage() {
        startActivity(new Intent(RegisterActivity.this, SignUpActivity.class));
    }

    private void GotoLoginPage() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
        boolean isLogin = preferences.getBoolean("isLogin", false);
        boolean isAdmin = preferences.getBoolean("isAdmin", false);
        if (isLogin) {
            if (isAdmin) {
                Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        }

    }


    //Is this Supposed to be here at all?
    private void DialogNoInternet() {
        new AlertDialog.Builder(RegisterActivity.this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.internet_error))
                .setPositiveButton(R.string.no_internet_exit_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.no_internet_retry_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onStart();
                    }
                }).show();
    }
}