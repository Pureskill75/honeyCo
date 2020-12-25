package atomsandbots.android.sweepsnatch.user.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import atomsandbots.android.sweepsnatch.user.Model.UserModel;
import atomsandbots.android.sweepsnatch.user.R;
import atomsandbots.android.sweepsnatch.user.RoomDatabase.DataBaseAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private TextView userNameTextView, userEmailTextView;

    private CircleImageView profileImageView;
    private ImageView networkConnectionImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.drawable.toolbar_design);
        setSupportActionBar(toolbar);


        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        //find layout and start nav destination implementation
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_draws, R.id.nav_profile, R.id.nav_rate, R.id.nav_setting)
                .setOpenableLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawer.closeDrawers();
                if (menuItem.getItemId() == R.id.nav_home) {
                    navController.popBackStack(R.id.nav_home, true);
                    navController.navigate(R.id.nav_home);
                    //profile frag
                } else if (menuItem.getItemId() == R.id.nav_profile) {
                    navController.popBackStack(R.id.nav_home, false);
                    navController.navigate(R.id.nav_profile);
                    //Users draws frag
                } else if (menuItem.getItemId() == R.id.nav_draws) {
                    navController.popBackStack(R.id.nav_home, false);
                    navController.navigate(R.id.nav_draws);
                    //Rate or Hate fragment
                } else if (menuItem.getItemId() == R.id.nav_explore) {
                    navController.popBackStack(R.id.nav_home, false);
                    navController.navigate(R.id.nav_rate);
                    //Settings frag
                } else if (menuItem.getItemId() == R.id.nav_setting) {
                    navController.popBackStack(R.id.nav_home, false);
                    navController.navigate(R.id.nav_setting);

                } else if (menuItem.getItemId() == R.id.nav_share) {
                    ShareApp();
                } else if (menuItem.getItemId() == R.id.nav_about) {
                    navController.popBackStack(R.id.nav_home, false);
                    navController.navigate(R.id.nav_about);

                }
                return true;
            }
        });

        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.header_name);
        userEmailTextView = headerView.findViewById(R.id.header_email);
        profileImageView = headerView.findViewById(R.id.header_imageView);


        if (netInfo == null) {
            Toast.makeText(MainActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
            alertDialog();
            DataBaseAdapter baseAdapter = new DataBaseAdapter(MainActivity.this);
            Cursor cursor = baseAdapter.getData();

            while (cursor.moveToNext()) {
                userNameTextView.setText(cursor.getString(0));
                userEmailTextView.setText(cursor.getString(1));
                byte[] image = cursor.getBlob(6);
                if (image.length > 1) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    profileImageView.setImageBitmap(bitmap);
                } else {
                    profileImageView.setImageResource(R.drawable.profile_placeholder);
                }
            }


        } else {

            //Get Firebase instance to display info for current user
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel user = snapshot.getValue(UserModel.class);
                            userNameTextView.setText(user.getName());
                            userEmailTextView.setText(user.getEmail());
                            if (user.getImage().equalsIgnoreCase("null")) {
                                profileImageView.setImageResource(R.drawable.profile_placeholder);
                            } else {
                                Glide.with(getApplicationContext()).load(user.getImage()).placeholder(R.drawable.profile_placeholder)
                                        .into(profileImageView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
            }
        }
    }

    //This alert dialog will appear should the user open MainActivity when all network == null
    private void alertDialog() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Connection Failed")
                .setMessage("Please Check Your Internet Connection")
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (netInfo == null) {
                            alertDialog();
                        } else {
                            //If Network is back on, reload MainActivity
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create();
        dialog.show();
    }


    //share intent builder for sharing app
    private void ShareApp() {
        ShareCompat.IntentBuilder.from(MainActivity.this)
                .setType("text/plain")
                .setChooserTitle("Chooser Tittle")
                .setText("http://play.google.com/store/apps/details?id=" + this.getPackageName())
                .startChooser();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
