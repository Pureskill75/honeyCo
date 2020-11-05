package atomsandbots.android.sweepsnatch.user.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import atomsandbots.android.sweepsnatch.user.Model.ProductRoomModel;
import atomsandbots.android.sweepsnatch.user.R;

public class ProductRoomAdapter extends RecyclerView.Adapter<ProductRoomAdapter.ViewHolder> {
    private final List<ProductRoomModel> productModelList;

    public ProductRoomAdapter(List<ProductRoomModel> productModelList, Context context) {
        this.productModelList = productModelList;

    }

    @NonNull
    @Override
    public ProductRoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.draw_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRoomAdapter.ViewHolder holder, int position) {
        Bitmap image = productModelList.get(position).getImage();
        String name = productModelList.get(position).getProductName();
        String cat = productModelList.get(position).getCategory();
        String price = productModelList.get(position).getPrice();

        //set data to item
        holder.setData(image, name, cat, price);
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView Image;
        private final TextView Name;
        private final TextView Category;
        private final TextView Price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.product_image);
            Name = itemView.findViewById(R.id.product_name);
            Category = itemView.findViewById(R.id.product_category);
            Price = itemView.findViewById(R.id.product_price);
        }

        public void setData(Bitmap image, String name, String cat, String price) {
            Image.setImageBitmap(image);
            Name.setText(name);
            Category.setText(cat);
            Price.setText(String.format("$%s", price));
        }
    }
}
