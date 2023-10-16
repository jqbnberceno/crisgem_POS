package com.example.thesis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class OrderList extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    OrderAdapter orderAdapter;
    ArrayList<Order> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        recyclerView = findViewById(R.id.orderList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the child reference based on the current day of the week
        database = FirebaseDatabase.getInstance().getReference("ReservedOrders").child(getDayOfWeekChild());

        list = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, list);
        recyclerView.setAdapter(orderAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear(); // Clear the list before populating it with new data

                for (DataSnapshot keySnapshot : snapshot.getChildren()) {
                    String key = keySnapshot.getKey(); // Retrieve the unique key

                    String customerName = keySnapshot.child("customerName").getValue(String.class);
                    Long orderTotal = keySnapshot.child("orderTotal").getValue(Long.class);
                    String time = keySnapshot.child("time").getValue(String.class);

                    Order order = new Order(key, customerName, orderTotal, time);
                    list.add(order);
                }

                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
            }
        });

        // Set click listener for the delete button
        orderAdapter.setOnDeleteClickListener(new OrderAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(String key) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderList.this);
                builder.setTitle("Delete Reservation")
                        .setMessage("Do you want to delete this reservation?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteOrder(key);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        orderAdapter.setOnConfirmClickListener(new OrderAdapter.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(String key, Order order) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderList.this);
                builder.setTitle("Confirm Reservation")
                        .setMessage("Do you want to confirm this reservation?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference destinationRef = FirebaseDatabase.getInstance().getReference("orders").child(getDayOfWeekChild()).push();
                                destinationRef.setValue(order); // Move the data to the destination node

                                DatabaseReference sourceRef = FirebaseDatabase.getInstance().getReference("ReservedOrders").child(getDayOfWeekChild()).child(key);
                                sourceRef.removeValue(); // Delete the current node

                                // Update Expected_Income in the corresponding day of the week child node
                                DatabaseReference incomeRef = FirebaseDatabase.getInstance().getReference("orders").child(getDayOfWeekChild()).child("Expected_Income");
                                incomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Long expectedIncome = snapshot.getValue(Long.class);
                                        if (expectedIncome == null) {
                                            expectedIncome = 0L;
                                        }
                                        expectedIncome += order.getOrderTotal();
                                        incomeRef.setValue(expectedIncome);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle the error if needed
                                    }
                                });

                                // Display a success message or perform any other action
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

// Rest of your code...


    }

    // Method to get the child reference based on the current day of the week
    private String getDayOfWeekChild() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayOfWeekChild;

        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                dayOfWeekChild = "SundayOrders";
                break;
            case Calendar.MONDAY:
                dayOfWeekChild = "MondayOrders";
                break;
            case Calendar.TUESDAY:
                dayOfWeekChild = "TuesdayOrders";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeekChild = "WednesdayOrders";
                break;
            case Calendar.THURSDAY:
                dayOfWeekChild = "ThursdayOrders";
                break;
            case Calendar.FRIDAY:
                dayOfWeekChild = "FridayOrders";
                break;
            case Calendar.SATURDAY:
                dayOfWeekChild = "SaturdayOrders";
                break;
            default:
                // Use a default child if the current day is not recognized
                dayOfWeekChild = "DefaultOrders";
                break;
        }

        return dayOfWeekChild;
    }

    // Method to delete an order from Firebase
    private void deleteOrder(String key) {
        database.child(key).removeValue();
    }
}
