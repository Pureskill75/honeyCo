package atomsandbots.android.sweepsnatch.user.AdminPanel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import atomsandbots.android.sweepsnatch.user.Adapter.HomeAdapter;
import atomsandbots.android.sweepsnatch.user.Extras.GridSpacingItemDecoration;
import atomsandbots.android.sweepsnatch.user.Model.ProductModel;
import atomsandbots.android.sweepsnatch.user.R;
import atomsandbots.android.sweepsnatch.user.Registration.RegisterActivity;
import atomsandbots.android.sweepsnatch.user.UI.HomeFragment;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

    // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin Panel");




        final RecyclerView adminRecyclerView = findViewById(R.id.admin_recyclerview);
        //firebase instance for products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
        final List<ProductModel> productModelList = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(AdminMainActivity.this,2);
        adminRecyclerView.setLayoutManager(gridLayoutManager);
        int spanCount = 2; // 3 columns
        int spacing = 15; // 50px
        boolean includeEdge = false;
        adminRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, false));
        adminRecyclerView.setHasFixedSize(true);
        adminRecyclerView.setItemViewCacheSize(20);
        adminRecyclerView.setDrawingCacheEnabled(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productModelList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ProductModel product = snapshot1.getValue(ProductModel.class);
                    productModelList.add(product);
                }
                HomeAdapter homeAdapter = new HomeAdapter(productModelList,AdminMainActivity.this,true,true,false);
                adminRecyclerView.setAdapter(homeAdapter);
                homeAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Button addNew = findViewById(R.id.add_new_product);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewProduct();
            }
        });
    }

    private void AddNewProduct() {
        startActivity(new Intent(AdminMainActivity.this,NewProductActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout_admin){
           LogOutAdmin();
        }
        return false;
    }

    private void LogOutAdmin() {
        AlertDialog.Builder logoutDialog = new AlertDialog.Builder(AdminMainActivity.this);
        logoutDialog.setTitle("Logout")
                .setMessage("Are you sure to logout")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("LoginDetails",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLogin",false);
                        editor.apply();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(AdminMainActivity.this, RegisterActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }
}