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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import atomsandbots.android.sweepsnatch.user.Adapter.HomeAdapter;
import atomsandbots.android.sweepsnatch.user.Adapter.ProductRoomAdapter;
import atomsandbots.android.sweepsnatch.user.Model.ProductModel;
import atomsandbots.android.sweepsnatch.user.Model.ProductRoomModel;
import atomsandbots.android.sweepsnatch.user.R;
import atomsandbots.android.sweepsnatch.user.RoomDatabase.DrawDatabase;

public class DrawFragment extends Fragment {
    private RecyclerView drawRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_draw, container, false);

        drawRecyclerView = view.findViewById(R.id.draw_recyclerView);

        final List<ProductModel> productModelList = new ArrayList<>();
        drawRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        drawRecyclerView.setHasFixedSize(true);
        drawRecyclerView.setItemViewCacheSize(20);
        drawRecyclerView.setDrawingCacheEnabled(true);

        ConnectivityManager conMgr = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            List<ProductRoomModel> productRoomModelList = new ArrayList<>();
            Toast.makeText(getContext(), "No Internet is available", Toast.LENGTH_SHORT).show();
            DrawDatabase baseAdapter = new DrawDatabase(getContext());
            Cursor cursor = baseAdapter.getdata();
            while (cursor.moveToNext()) {
                ProductRoomModel product = new ProductRoomModel(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        null);
                if (cursor.getBlob(5).length > 0) {
                    byte[] imgByte = cursor.getBlob(5);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                    product.setImage(bitmap);
                }
                productRoomModelList.add(product);
            }

            ProductRoomAdapter roomAdapter = new ProductRoomAdapter(productRoomModelList, getContext());
            drawRecyclerView.setAdapter(roomAdapter);
            roomAdapter.notifyDataSetChanged();

        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            // Draw fragment load user draw item data
            assert user != null;
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Draws").child(user.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    productModelList.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ProductModel product = snapshot1.getValue(ProductModel.class);
                        productModelList.add(product);
                    }
                    //Reuse Home Adapter
                    HomeAdapter homeAdapter = new HomeAdapter(productModelList, getContext(), false, false,false);
                    drawRecyclerView.setAdapter(homeAdapter);
                    homeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return view;
    }
}