package atomsandbots.android.sweepsnatch.user.UI;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import atomsandbots.android.sweepsnatch.user.Model.UserModel;
import atomsandbots.android.sweepsnatch.user.R;
import atomsandbots.android.sweepsnatch.user.RoomDatabase.DataBaseAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {


    private CircleImageView profileImage;
    private TextView nameText, emailText, phoneText, postcodeTxt, countryText, addressText;

    private FirebaseUser firebaseUser;
    private UserModel user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        findIDs(view);
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null) {
            Toast.makeText(getContext(), "No Internet is available", Toast.LENGTH_SHORT).show();
            DataBaseAdapter baseAdapter = new DataBaseAdapter(getContext());
            Cursor cursor = baseAdapter.getData();
            while (cursor.moveToNext()) {
                nameText.setText(cursor.getString(0));
                emailText.setText(cursor.getString(1));
                phoneText.setText(cursor.getString(2));
                postcodeTxt.setText(cursor.getString(3));
                countryText.setText(cursor.getString(4));
                addressText.setText(cursor.getString(5));
                if (cursor.getBlob(6).length > 1) {
                    byte[] imgByte = cursor.getBlob(6);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                    profileImage.setImageBitmap(bitmap);
                }
            }
        } else {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            loadData();
        }

        return view;
    }

    private void findIDs(View view) {
        profileImage = view.findViewById(R.id.profile_image);
        nameText = view.findViewById(R.id.username);
        emailText = view.findViewById(R.id.profile_email);
        phoneText = view.findViewById(R.id.profile_number);
        postcodeTxt = view.findViewById(R.id.profile_postcode);
        countryText = view.findViewById(R.id.profileCountry);
        addressText = view.findViewById(R.id.profileAddress);
    }

    private void loadData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(UserModel.class);
                    assert user != null;
                    nameText.setText(user.getName());
                    emailText.setText(user.getEmail());
                    phoneText.setText(user.getPhone());
                    postcodeTxt.setText(user.getPostcode());
                    countryText.setText(user.getCountry());
                    addressText.setText(user.getAddress());
                    if (user.getImage().equalsIgnoreCase("null")) {
                        profileImage.setImageResource(R.drawable.profile_placeholder);
                    } else {

                        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile_placeholder).
                                into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
}