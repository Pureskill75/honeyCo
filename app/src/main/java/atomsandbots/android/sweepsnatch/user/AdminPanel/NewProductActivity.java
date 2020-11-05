package atomsandbots.android.sweepsnatch.user.AdminPanel;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import atomsandbots.android.sweepsnatch.user.R;

public class NewProductActivity extends AppCompatActivity {
    private ImageView productImage;
    private EditText Name, Price, Description, ID;
    private String name, price, description, id, category;
    private Spinner Category;
    private Button addProduct;

    private static final int IMG_REQ = 1001;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
    private Uri imageUri;
    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        findIDs();

        mStorageRef = FirebaseStorage.getInstance().getReference().child("Products");
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProduct();
            }
        });
        //Item listener for the array of categories.
        Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] cat = getResources().getStringArray(R.array.product_categories);
                category = cat[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void findIDs() {
        productImage = findViewById(R.id.product_image);
        addProduct = findViewById(R.id.addProduct);
        Name = findViewById(R.id.product_name);
        Price = findViewById(R.id.product_price);
        Description = findViewById(R.id.productDescription);
        Category = findViewById(R.id.product_category);
        ID = findViewById(R.id.productID);
    }

    private void AddProduct() {
        if (uploadTask != null && uploadTask.isInProgress()) {
            Toast.makeText(NewProductActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
        } else {
            uploadProduct();
        }
    }

    private void openImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent,"Select any Image"),IMG_REQ);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
        }
    }

    private void uploadProduct() {
        if (Validation()){

            final ProgressDialog pd = new ProgressDialog(NewProductActivity.this);
            pd.setMessage("Uploading");
            pd.show();
            if (imageUri != null) {
                final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(imageUri));
                uploadTask = fileReference.putFile(imageUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return fileReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = (Uri) task.getResult();
                            String mUri = downloadUri.toString();

                            databaseReference = FirebaseDatabase.getInstance().getReference("Products").child(ID.getText().toString().trim());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("Image", mUri);
                            hashMap.put("ProductName",name );
                            hashMap.put("Description",description );
                            hashMap.put("Price",price );
                            hashMap.put("Category",category );
                            hashMap.put("ProductId",id );
                            databaseReference.setValue(hashMap);
                            Toast.makeText(NewProductActivity.this, "Upload successfully", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                            finish();
                        } else {
                            Toast.makeText(NewProductActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            } else {
                pd.dismiss();
                Toast.makeText(NewProductActivity.this, "No Image is selected", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean Validation() {
        boolean valid = true;
        name = Name.getText().toString().trim();
        id = ID.getText().toString().trim();
        price = Price.getText().toString().trim();
        description = Description.getText().toString().trim();
        if (imageUri == null){
            Toast.makeText(this, "No Image is selected", Toast.LENGTH_SHORT).show();
        }
        if (name.isEmpty()) {
            Name.setError("empty");
            Name.requestFocus();
            valid = false;
        }
        if (id.isEmpty()) {
            ID.requestFocus();
            ID.setError("missing");
            valid = false;
        }
        if (price.isEmpty()) {
            Price.setError("empty");
            Price.requestFocus();
            valid = false;
        }
        if (description.isEmpty()) {
            Description.requestFocus();
            Description.setError("missing");
            valid = false;
        }
        if (category.equalsIgnoreCase("Select")) {
            valid = false;
            Toast.makeText(this, "Please select a category for product", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }
}