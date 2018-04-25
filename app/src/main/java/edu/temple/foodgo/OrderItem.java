package edu.temple.foodgo;

import com.google.firebase.database.DataSnapshot;

public class OrderItem {
    private DataSnapshot itemInDatabase;

    private String description, imageURL, name, price;


    public OrderItem(DataSnapshot databaseReference){
        itemInDatabase = databaseReference;
        description = databaseReference.child("description").getValue().toString();
        imageURL = databaseReference.child("foodImageURL").getValue().toString();
        name = databaseReference.child("name").getValue().toString();
        price = databaseReference.child("price").getValue().toString();
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public DataSnapshot getItemInDatabase() {
        return itemInDatabase;
    }

    @Override
    public String toString() {
        return getName();
    }
}
