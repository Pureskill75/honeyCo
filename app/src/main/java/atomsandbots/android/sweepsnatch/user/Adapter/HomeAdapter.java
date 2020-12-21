package atomsandbots.android.sweepsnatch.user.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.internal.service.Common;
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
import java.util.List;
import java.util.Objects;

import atomsandbots.android.sweepsnatch.user.Model.ProductModel;
import atomsandbots.android.sweepsnatch.user.R;

import atomsandbots.android.sweepsnatch.user.RoomDatabase.DrawDatabase;
import atomsandbots.android.sweepsnatch.user.UI.ProductDetailsActivity;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private static final int productLayout = 91;
    private static final int drawLayout = 92;
    private final boolean isProductLayout;
    private final boolean isAdmin;
    private final boolean isRate;
    private final List<ProductModel> productModelList;
    private final Context context;

// isRate check if the view is come from Rate or Hate activity or not.
    public HomeAdapter(List<ProductModel> productModelList, Context context, boolean isProductLayout, boolean isAdmin,boolean isRate) {
        this.productModelList = productModelList;
        this.context = context;
        this.isProductLayout = isProductLayout;
        this.isAdmin = isAdmin;
        this.isRate = isRate;
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == productLayout) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.draw_item_layout, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (isProductLayout) {
            return productLayout;
        } else {
            return drawLayout;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeAdapter.ViewHolder holder, final int position) {
        String image = productModelList.get(position).getImage();
        String name = productModelList.get(position).getProductName();
        String cat = productModelList.get(position).getCategory();
        String price = productModelList.get(position).getPrice();

        //set data to item
        holder.setData(image, name, cat, price);

        //Go to product details activity

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoProductDetails(position);
            }
        });

        // if view is in Admin enter in draw button hide.
        if (isProductLayout) {

            // if view from admin panel enter in draw button visibility is false
            if (isAdmin) {
                final PopupMenu popup = new PopupMenu(context, holder.itemView);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //registering popup with OnMenuItemClickListener
                        popup.show();
                        return true;
                    }
                });
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.popup_delete) {
                            //Delete product from Firebase
                            final String pId = productModelList.get(position).getProductId();
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child("Products").child(pId).removeValue();
                            reference.child("Draws").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        if (snapshot1.child(pId).exists()) {
                                            Log.v("Snapshot", snapshot1.getRef().child(pId).toString());
                                            snapshot1.getRef().child(pId).removeValue();
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        return false;
                    }
                });

            }
        }
    }

    private void GotoProductDetails(int position) {
        Intent intent = new Intent(context, ProductDetailsActivity.class);
        intent.putExtra("ProductInfo", productModelList.get(position));
        if (position > 1){
            intent.putExtra("drawStatus",false);
        }else {
            intent.putExtra("drawStatus",true);
        }
        if (isAdmin) {
            intent.putExtra("isAdmin", true);
        } else {
            intent.putExtra("isAdmin", false);
        }
        if (isRate){
            intent.putExtra("isRate", true);
        }
        if (!isProductLayout){
            intent.putExtra("isDraw", true);
        }
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView Image;
        private TextView Name, Category, Price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.product_image);
            Name = itemView.findViewById(R.id.product_name);
            Category = itemView.findViewById(R.id.product_category);
            Price = itemView.findViewById(R.id.product_price);
        }

        public void setData(String image, String name, String cat, String price) {
            Glide.with(context).load(image).placeholder(R.drawable.profile_placeholder).into(Image);
            Name.setText(name);
            Category.setText(cat);
            Price.setText(String.format("$%s", price));
        }
    }
}