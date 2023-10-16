package com.example.thesis;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductRVModal implements Parcelable {
    private String productName;
    private String productPrice;
    private String productQty;
    private String productID;

    public ProductRVModal(){

    }

    protected ProductRVModal(Parcel in) {
        productName = in.readString();
        productPrice = in.readString();
        productQty = in.readString();
    }

    public static final Creator<ProductRVModal> CREATOR = new Creator<ProductRVModal>() {
        @Override
        public ProductRVModal createFromParcel(Parcel in) {
            return new ProductRVModal(in);
        }

        @Override
        public ProductRVModal[] newArray(int size) {
            return new ProductRVModal[size];
        }
    };

    public ProductRVModal(String productName, String productPrice, String productQty, String productID) {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public ProductRVModal(String productName, String productPrice, String productQty) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQty = productQty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(productName);
        parcel.writeString(productPrice);
        parcel.writeString(productQty);
    }
}
