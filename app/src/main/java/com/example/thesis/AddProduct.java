package com.example.thesis;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

public class AddProduct extends AppCompatActivity {

    private TextInputEditText productNameEdt, productPriceEdt, productQtyEdt;
    private Button addProductBtn;
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String productID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        productNameEdt = findViewById(R.id.idEdtProductName);
        productPriceEdt = findViewById(R.id.idEdtProductPrice);
        productQtyEdt = findViewById(R.id.idEdtProductQty);
        addProductBtn = findViewById(R.id.idBtnAddProduct);
        loadingPB = findViewById(R.id.idPBLoading);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        String productId = databaseRef.child("Orders").push().getKey();

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the values from the text fields
                String productName = productNameEdt.getText().toString().trim();
                String productPrice = productPriceEdt.getText().toString().trim();
                String productQty = productQtyEdt.getText().toString().trim();

                // Create a Product object with the retrieved data
                ProductRVModal product = new ProductRVModal(productName, productPrice, productQty);

                // Add the product to the Firebase Realtime Database under the generated ID
                databaseRef.child("Orders").child(productId).setValue(product);

                // Show a success message to the user
                Toast.makeText(AddProduct.this, "Product added successfully!", Toast.LENGTH_SHORT).show();

                // Clear the text fields
                productNameEdt.setText("");
                productPriceEdt.setText("");
                productQtyEdt.setText("");



            }
        });
    }
}


//        // Get a reference to the root of your Firebase Realtime Database
//        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
//
//// Add some data to your Firebase Realtime Database
//        databaseRef.child("nigga").child("nigga").child("nigga").setValue("Nigga");
//
//// Add a ValueEventListener to listen for changes to the data at the location we just added data to
//        databaseRef.child("users").child("userId123").child("name").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String name = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Name: " + name);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.w(TAG, "onCancelled", databaseError.toException());
//            }
//        });
//
//
//    }
//}