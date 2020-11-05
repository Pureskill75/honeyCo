package atomsandbots.android.sweepsnatch.user.Model;

import android.graphics.Bitmap;

public class ProductRoomModel {
    private String ProductName, Description, Price, Category, ProductId;
    private Bitmap Image;

    public ProductRoomModel() {
    }

    public ProductRoomModel(String productName, String description, String price, String category, String productId, Bitmap image) {
        ProductName = productName;
        Description = description;
        Price = price;
        Category = category;
        ProductId = productId;
        Image = image;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }
}

