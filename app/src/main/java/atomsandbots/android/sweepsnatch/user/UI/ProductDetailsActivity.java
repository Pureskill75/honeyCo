package atomsandbots.android.sweepsnatch.user.UI;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import atomsandbots.android.sweepsnatch.user.Model.ProductModel;
import atomsandbots.android.sweepsnatch.user.R;
import atomsandbots.android.sweepsnatch.user.RoomDatabase.DrawDatabase;
import atomsandbots.android.sweepsnatch.user.databinding.ActivityProductDetailsBinding;

public class ProductDetailsActivity extends AppCompatActivity {


    private DatabaseReference reference;
    //View binding in use
    private ActivityProductDetailsBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        View v = bind.getRoot();
        setContentView(v);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        reference = FirebaseDatabase.getInstance().getReference("Draws").child(user.getUid());


        final ProductModel product = (ProductModel) getIntent().getSerializableExtra("ProductInfo");
        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        boolean drawStatus = getIntent().getBooleanExtra("drawStatus", false);
        boolean isRate = getIntent().getBooleanExtra("isRate", false);
        boolean isDraw = getIntent().getBooleanExtra("isDraw", false);

        // check draw status only first 2 products are available for draw
        if (!drawStatus) {
            bind.enterInDraw.setText(R.string.coming_soon);
            bind.enterInDraw.setEnabled(false);
        }

        assert product != null;
        reference.child(product.getProductId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    bind.enterInDraw.setText(R.string.entered);
                    bind.enterInDraw.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setDataUI(product);

        bind.enterInDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterInDraw(product);
            }
        });
        if (isAdmin) {
            bind.enterInDraw.setVisibility(View.GONE);
        } else {
            bind.enterInDraw.setVisibility(View.VISIBLE);
        }

        //If Rate fragment is true, show like and dislike button.
        if (isRate) {
            bind.likesLayout.setVisibility(View.VISIBLE);

        } else {
            checkLikeStatus(product.getProductId());

            //Dislike logic to check the states in firebase node 'Votes'
            bind.likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Votes");
                    if (bind.likeBtn.getTag().equals("Liked")) {
                        reference.child(product.getProductId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    bind.likeBtn.setTag("Like");
                                    bind.likeBtn.setImageResource(R.drawable.like);
                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("Like", true);
                        reference.child(product.getProductId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    bind.likeBtn.setTag("Liked");
                                    bind.likeBtn.setImageResource(R.drawable.liked);
                                    bind.dislikeBtn.setTag("DisLike");
                                    bind.dislikeBtn.setImageResource(R.drawable.dislike);
                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            });


            //Dislike logic to check the states in firebase node 'Votes'
            bind.dislikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Votes");
                    if (bind.dislikeBtn.getTag().equals("DisLiked")) {
                        reference.child(product.getProductId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    bind.dislikeBtn.setTag("DisLike");
                                    bind.dislikeBtn.setImageResource(R.drawable.dislike);
                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("Like", false);
                        reference.child(product.getProductId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    bind.dislikeBtn.setTag("DisLiked");
                                    bind.dislikeBtn.setImageResource(R.drawable.disliked);
                                    bind.likeBtn.setTag("Like");
                                    bind.likeBtn.setImageResource(R.drawable.like);
                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            });
        }

        // if view from Rate or Hate screen the enter in draw visibility gone and price also
        if (isRate) {
            bind.enterInDraw.setVisibility(View.INVISIBLE);
            bind.linearLayoutPrice.setVisibility(View.GONE);
        }

    }

    private void checkLikeStatus(String id) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Votes");
        reference1.child(id).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.child("Like").getValue(Boolean.class)) {
                                bind.likeBtn.setTag("Liked");
                                bind.likeBtn.setImageResource(R.drawable.liked);
                            } else {
                                bind.dislikeBtn.setTag("DisLiked");
                                bind.dislikeBtn.setImageResource(R.drawable.disliked);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        reference1.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long no = snapshot.getChildrenCount();
                int positiveLikes = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.child("Like").getValue(Boolean.class)) {
                        positiveLikes++;
                    }
                }
                int negative = (int) (no - positiveLikes);
                bind.likeTxt.setText(positiveLikes + "");
                bind.dislikeTxt.setText(negative + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //set The UI Screen for
    private void setDataUI(ProductModel product) {
        Glide.with(ProductDetailsActivity.this).load(product.getImage()).placeholder(R.drawable.profile_placeholder).into(bind.productImage);
        bind.productTitle.setText(product.getProductName());
        bind.productPrice.setText(String.format("$%s", product.getPrice()));
        bind.productDescription.setText(product.getDescription());

    }

    private void EnterInDraw(final ProductModel product) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Image", product.getImage());
        hashMap.put("ProductName", product.getProductName());
        hashMap.put("Description", product.getDescription());
        hashMap.put("Price", product.getPrice());
        hashMap.put("Category", product.getCategory());
        hashMap.put("ProductId", product.getProductId());
        reference.child(product.getProductId()).setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DrawDatabase drawDatabase = new DrawDatabase(ProductDetailsActivity.this);

                            bind.productImage.invalidate();
                            BitmapDrawable drawable = (BitmapDrawable) bind.productImage.getDrawable();
                            Bitmap bitmap = drawable.getBitmap();
                            byte[] data = getBitmapAsByteArray(bitmap); // this is a function
                            long id = drawDatabase.insert(product.getProductName(), product.getDescription(), product.getPrice(), product.getCategory(), product.getProductId(), data);
                            if (id < 0) {
                                Toast.makeText(ProductDetailsActivity.this, "Data was not saved on room", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(ProductDetailsActivity.this, R.string.enter_in_draw_successfully, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductDetailsActivity.this, R.string.enter_in_draw_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}