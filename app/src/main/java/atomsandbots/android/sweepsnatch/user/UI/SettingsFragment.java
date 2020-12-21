package atomsandbots.android.sweepsnatch.user.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atomsandbots.android.sweepsnatch.user.Model.UserModel;
import atomsandbots.android.sweepsnatch.user.R;
import atomsandbots.android.sweepsnatch.user.Registration.RegisterActivity;
import atomsandbots.android.sweepsnatch.user.RoomDatabase.DataBaseAdapter;
import atomsandbots.android.sweepsnatch.user.databinding.FragmentSettingsBinding;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {


    private static final int IMG_REQ = 1001;

    FirebaseUser user;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
    private Uri imageUri;
    private StorageTask uploadTask;
    private String user_image;

    private FragmentSettingsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.setting_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo != null) {
                LogoutUser();
            }
            return true;
        }
        return false;
    }

    private void LogoutUser() {
        androidx.appcompat.app.AlertDialog.Builder logoutDialog = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        logoutDialog.setTitle("Logout")
                .setMessage("Are you sure to logout")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLogin", false);
                        editor.apply();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getContext(), RegisterActivity.class));
                        getActivity().finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View binding in use
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View v = binding.getRoot();


        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null) {
            Toast.makeText(getContext(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
            binding.saveBtnSettings.setEnabled(false);
            binding.profileImageSettings.setEnabled(false);
            binding.deleteAccountBtn.setEnabled(false);
            DataBaseAdapter baseAdapter = new DataBaseAdapter(getContext());
            Cursor cursor = baseAdapter.getData();
            while (cursor.moveToNext()) {
                binding.profileName.setText(cursor.getString(0));
                //two entries
                binding.editPhoneNumber.setText(cursor.getString(2));
                binding.editPhoneNumber.setText(cursor.getString(3));
                binding.countryTv.setText(cursor.getString(4));
                binding.editAddress.setText(cursor.getString(5));
                if (cursor.getBlob(6).length > 1) {
                    byte[] imgByte = cursor.getBlob(6);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                    binding.profileImageSettings.setImageBitmap(bitmap);
                }
            }
        } else {
            binding.deleteAccountBtn.setEnabled(true);
            binding.saveBtnSettings.setEnabled(true);
            binding.profileImageSettings.setEnabled(true);
            mStorageRef = FirebaseStorage.getInstance().getReference().child("Profiles");
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            user = FirebaseAuth.getInstance().getCurrentUser();
            // Load user profile data
            assert user != null;
            databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        assert user != null;
                        binding.profileName.setText(user.getName());
                        binding.editPhoneNumber.setText(user.getPhone());
                        binding.postcodeTv.setText(user.getPostcode());
                        binding.countryTv.setText(user.getCountry());
                        binding.editAddress.setText(user.getAddress());
                        user_image = user.getEmail();
                        if (user.getImage().equalsIgnoreCase("")) {
                            binding.profileImageSettings.setImageResource(R.drawable.profile_placeholder);
                        } else {
                            Picasso.get().load(user.getImage()).placeholder(R.drawable.profile_placeholder)
                                    .into(binding.profileImageSettings);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });


            binding.profileImageSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImage();
                }
            });
            binding.saveBtnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateProfile();
                }
            });
            binding.deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteAccount();
                }
            });
        }
        return v;
    }

    //If user chooses to delete
    private void DeleteAccount() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Delete Account")
                .setMessage("Are you sure to delete account?")
                .setPositiveButton("Yes,", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;
                        final String uid = user.getUid();
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                    reference.child("Users").child(uid).removeValue();
                                    reference.child("Draws").child(uid).removeValue();
                                    getContext().deleteDatabase("ProfileData" + uid);
                                    getContext().deleteDatabase("DrawsData" + uid);
                                    SharedPreferences preferences = getContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
                                    preferences.edit().clear().apply();
                                    Intent intent = new Intent(getContext(), RegisterActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    requireActivity().finishAffinity();
                                } else {
                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNeutralButton("No", null)
                .show();
    }


    private void UpdateProfile() {
        Map<String, Object> map = new HashMap<>();
        final String name = binding.profileName.getText().toString().trim();
        final String phone = binding.editPhoneNumber.getText().toString().trim();
        final String postcode = binding.postcodeTv.getText().toString().trim();
        final String country = binding.countryTv.getText().toString().trim();
        final String address = binding.editAddress.getText().toString().trim();
        map.put("Name", name);
        map.put("Phone", phone);
        map.put("Postcode", postcode);
        map.put("Country", country);
        map.put("Address", address);
        databaseReference.child(user.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DataBaseAdapter adapter = new DataBaseAdapter(getContext());
                    adapter.update(user_image, name, phone, postcode, country, address);

                    Toast.makeText(getContext(), R.string.update_profile_successfully, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Go to gallery and pic Image
    private void openImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_chooser)), IMG_REQ);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();
        if (imageUri != null) {
            String img_name = user_image.substring(0, user_image.indexOf("."));
            final StorageReference fileReference = mStorageRef.child(img_name
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

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("Image", mUri);
                        databaseReference.child(user.getUid()).updateChildren(hashMap);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                            byte[] data = getBitmapAsByteArray(bitmap); // this is a function
                            DataBaseAdapter adapter = new DataBaseAdapter(getContext());
                            adapter.updateImage(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), R.string.no_image_select, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.profileImageSettings.setImageURI(imageUri);
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    //Clean up fragment via onDestroyView
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}