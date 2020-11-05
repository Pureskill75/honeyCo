package atomsandbots.android.sweepsnatch.user.Model;

import java.io.Serializable;

public class ProductModel implements Serializable {
    private String ProductName, Description, Price, Category, ProductId,Image;

    public ProductModel() {
    }

    public ProductModel(String productName, String description, String price, String category, String productId, String image) {
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

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
