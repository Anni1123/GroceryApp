package com.example.groceryapp;

public class ModelProducts {
    public ModelProducts() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDiscountprice() {
        return discountprice;
    }

    public void setDiscountprice(String discountprice) {
        this.discountprice = discountprice;
    }

    public String getDiscountnote() {
        return discountnote;
    }

    public void setDiscountnote(String discountnote) {
        this.discountnote = discountnote;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    String title;

    public ModelProducts(String title, String description, String productId, String category, String quantity,
                         String productIcon, String originalPrice, String discountprice,
                         String discountnote, String timestamp, String discountAvailable) {
        this.title = title;
        this.description = description;
        this.productId = productId;
        Category = category;
        Quantity = quantity;
        this.productIcon = productIcon;
        this.originalPrice = originalPrice;
        this.discountprice = discountprice;
        this.discountnote = discountnote;
        this.timestamp = timestamp;
        this.discountAvailable = discountAvailable;
    }

    String description;
    String productId;
    String Category;
    String Quantity;
    String productIcon;
    String originalPrice;
    String discountprice;
    String discountnote;
    String timestamp;
    String discountAvailable;
}