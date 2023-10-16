package com.example.thesis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.Calendar;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.content.DialogInterface;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class ReservedOrders extends AppCompatActivity {

    private TextInputEditText productPrice;
    private int mOrderQuantity = 0;
    private Button totalBtn;
    private TextView quantityTextView, totalTextView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private LinearLayout itemContainer;
    private List<String> itemsList = new ArrayList<>();
    private List<TextView> quantityTextViewList = new ArrayList<>();
    private List<TextView> totalTextViewList = new ArrayList<>();
    private TextView subtotalTextView;
    private Button timePickerBtn;
    private TextView selectedTimeTextView;


    // Create a map to store the association between buttons and text views
    private Map<Button, TextView> buttonTextViewMap = new HashMap<>();

    // Helper method to retrieve the quantity TextView for a given item
    private EditText getItemQuantityTextView(TextView itemNameTextView) {
        LinearLayout itemLayout = (LinearLayout) itemNameTextView.getParent();
        return (EditText) itemLayout.getChildAt(1);
    }

    // Helper method to retrieve the total TextView for a given item
    private TextView getItemTotalTextView(TextView itemNameTextView) {
        LinearLayout itemLayout = (LinearLayout) itemNameTextView.getParent();
        return (TextView) itemLayout.getChildAt(2);
    }

    // Helper method to clear all item text fields
    private void clearItemTextFields() {
        for (Button button : buttonTextViewMap.keySet()) {
            TextView itemNameTextView = buttonTextViewMap.get(button);
            EditText quantityTextView = getItemQuantityTextView(itemNameTextView);
            TextView totalTextView = getItemTotalTextView(itemNameTextView);

            itemNameTextView.setText("");
            quantityTextView.setText("");
            totalTextView.setText("");

        }
    }



    private void showOptionDialogL(final Button lemonTButton, final Button lemonTYButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option for Lemon");
        String[] options = {"16z - ₱10", "22oz - ₱25"};

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedOption = options[which];

                if (selectedOption.equals("16z - ₱10")) {
                    lemonTButton.performClick();
                } else if (selectedOption.equals("22oz - ₱25")) {
                    lemonTYButton.performClick();
                }
            }
        });

        builder.create().show();
    }



    private void showOptionDialogG(final Button gulamanGButton, final Button gulamanHButton, final Button gulamanJButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option for Gulaman");
        String[] options = {"8oz - ₱5", "16oz - ₱5", "22oz - ₱25"};

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedOption = options[which];

                if (selectedOption.equals("8oz - ₱5")) {
                    gulamanGButton.performClick();
                } else if (selectedOption.equals("16oz - ₱5")) {
                    gulamanHButton.performClick();
                } else if (selectedOption.equals("22oz - ₱25")) {
                    gulamanJButton.performClick();
                }
            }
        });

        builder.create().show();
    }





    HashMap<String, Integer> itemPriceMap = new HashMap<>();

    public void initializeItemPriceMap() {
        // Add item prices to the itemPriceMap
        itemPriceMap.put("Kwek-kwek", 22);
        itemPriceMap.put("Fishball", 15);
        itemPriceMap.put("Fries", 25);
        itemPriceMap.put("Hotdog", 22);
        itemPriceMap.put("ChickenBalls", 20);
        itemPriceMap.put("Kikiam", 20);
        itemPriceMap.put("CheeseStick", 20);
        itemPriceMap.put("Dynamite", 30);
        itemPriceMap.put("Siomai", 20);
        itemPriceMap.put("Penoy", 22);
        itemPriceMap.put("Combo1", 39);
        itemPriceMap.put("Combo2", 49);
        itemPriceMap.put("Combo3", 49);
        itemPriceMap.put("Combo4", 49);
        itemPriceMap.put("Gulaman-8oz", 5);
        itemPriceMap.put("Gulaman-16oz", 10);
        itemPriceMap.put("Gulaman-22oz", 25);
        itemPriceMap.put("LemonTea-16oz", 10);
        itemPriceMap.put("LemonTea-22oz", 25);

    }

    private void updateSubtotal() {
        int subtotal = 0;
        for (Button itemLayout : buttonTextViewMap.keySet()) {
            TextView itemText = buttonTextViewMap.get(itemLayout);
            EditText itemQuantityTextView = getItemQuantityTextView(itemText);
            TextView itemTotalTextView = getItemTotalTextView(itemText);

            int itemQuantity = Integer.parseInt(itemQuantityTextView.getText().toString());
            int itemPrice = getItemPrice(itemText); // Retrieve the price for the item
            int itemTotal = itemQuantity * itemPrice;
            itemTotalTextView.setText("P" + itemTotal);

            subtotal += itemTotal;
        }

        subtotalTextView.setText(String.valueOf(subtotal));
    }

    private int getItemPrice(TextView itemText) {
        String itemName = itemText.getText().toString();
        Integer price = itemPriceMap.get(itemName);
        if (price != null) {
            return price.intValue();
        } else {
            // Handle the case where the item price is not found
            // You can return a default price or throw an exception
            // depending on your requirements.
            // For example, you can return 0 as the default price:
            return 0;
        }
    }

    private String getTime(int hourOfDay, int minute) {
        String suffix = (hourOfDay >= 12) ? "PM" : "AM";
        int hour = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
        String timeString = String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, suffix);
        return timeString;
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String selectedTime = getTime(hourOfDay, minute);
                selectedTimeTextView.setText("Pickup Time: " + selectedTime);
            }
        }, hour, minute, false);

        timePickerDialog.show();
    }






    private String getDayOfWeekString(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "MondayOrders";
            case Calendar.TUESDAY:
                return "TuesdayOrders";
            case Calendar.WEDNESDAY:
                return "WednesdayOrders";
            case Calendar.THURSDAY:
                return "ThursdayOrders";
            case Calendar.FRIDAY:
                return "FridayOrders";
            case Calendar.SATURDAY:
                return "SaturdayOrders";
            case Calendar.SUNDAY:
                return "SundayOrders";
            default:
                return null;
        }
    }

    private String getTimeOfDayString(int hour) {

        String timeOfDay;

        if (hour >= 8 && hour <= 12) {
            timeOfDay = "MorningOrders";
        } else if (hour >= 13 && hour <= 17) {
            timeOfDay = "AfternoonOrders";
        } else if (hour >= 18 && hour <= 23) {
            timeOfDay = "EveningOrders";
        } else {
            timeOfDay = null;
        }

        return timeOfDay;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_drinks);
        totalBtn = findViewById(R.id.total_button);
        itemContainer = findViewById(R.id.tableLayout);


        // Initialize the quantityTextView and totalTextView
        quantityTextView = new TextView(getApplicationContext());
        totalTextView = new TextView(getApplicationContext());
        TextView cashReceivedTextView = findViewById(R.id.cash);

        // Initialize the Firebase database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Find the add and minus buttons and set their onClickListeners

        Button clearButton = findViewById(R.id.clearBtn);
        subtotalTextView = findViewById(R.id.subtotal);
        Button drinksBtn = findViewById(R.id.drinksBtn);
        Button orderButton = findViewById(R.id.placeOrderBtn);

        //ITEM BUTTONS

        Button kwekkwekButton = findViewById(R.id.kwekkwekBtn);
        Button fishballButton = findViewById(R.id.fishballBtn);
        Button friesButton = findViewById(R.id.friesBtn);
        Button hotdogButton = findViewById(R.id.hotdogBtn);
        Button cballsButton = findViewById(R.id.chickenballsBtn);
        Button kikiamButton = findViewById(R.id.kikiamBtn);
        Button cheesestkButton = findViewById(R.id.cheesestickBtn);
        Button dynamiteButton = findViewById(R.id.dynamiteBtn);
        Button siomaiButton = findViewById(R.id.siomaiBtn);
        Button penoyButton = findViewById(R.id.penoyBtn);
        Button cb1Button = findViewById(R.id.combo1Btn);
        Button cb2Button = findViewById(R.id.combo2Btn);
        Button cb3Button = findViewById(R.id.combo3Btn);
        Button cb4Button = findViewById(R.id.combo4Btn);
        Button gulamanButton = findViewById(R.id.gulamanBtn);
        Button lemonteaButton = findViewById(R.id.lemonteaBtn);
        Button lemonTButton = findViewById(R.id.lemontea2Btn);
        Button lemonTYButton = findViewById(R.id.lemontea3Btn);
        Button gulamanGButton = findViewById(R.id.gulaman2Btn);
        Button gulamanHButton = findViewById(R.id.gulaman3Btn);
        Button gulamanJButton = findViewById(R.id.gulaman4Btn);
        Button pesoO = findViewById(R.id.peso1);
        Button pesoF = findViewById(R.id.peso5);
        Button pesoT = findViewById(R.id.peso10);
        Button pesoTw = findViewById(R.id.peso20);
        Button pesoFi = findViewById(R.id.peso50);
        Button pesoOh = findViewById(R.id.peso1h);
        Button pesoTh = findViewById(R.id.peso2h);
        Button peso5h = findViewById(R.id.peso5h);
        Button pesoOk = findViewById(R.id.peso1k);
        selectedTimeTextView = findViewById(R.id.selectedTimeTextView);
        TextView customerNameTextView = findViewById(R.id.customerNameTextView);
        TextView customerNameTxt = findViewById(R.id.customerNameContainer);
        Button reserve = findViewById(R.id.ReservedOrders);


        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the DrinksActivity
                Intent intent = new Intent(ReservedOrders.this, OrderList.class);
                startActivity(intent);
            }
        });




        selectedTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        customerNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReservedOrders.this);
                builder.setTitle("Enter Customer's Name");

                final EditText input = new EditText(ReservedOrders.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                input.setLayoutParams(layoutParams);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String customerName = input.getText().toString();
                        customerNameTxt.setText(customerName);
                    }
                });

                builder.setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });




// Set an OnClickListener to the drinks button
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the DrinksActivity
                Intent intent = new Intent(ReservedOrders.this, Products.class);
                startActivity(intent);
            }
        });

        // Declare the subtotal variable as an instance variable

// ...
        final int[] subtotal = {0};

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear all item text fields
                clearItemTextFields();
                cashReceivedTextView.setText("0");
                customerNameTxt.setText("");
                selectedTimeTextView.setText("Pickup Time:");


                // Remove all buttons and associated views from the item container
                for (Button button : buttonTextViewMap.keySet()) {
                    TextView itemNameTextView = buttonTextViewMap.get(button);
                    LinearLayout itemLayout = (LinearLayout) itemNameTextView.getParent();
                    itemContainer.removeView(itemLayout);
                }

                // Clear the buttonTextViewMap
                buttonTextViewMap.clear();

                // Reset the subtotal to 0
                subtotal[0] = 0;
                subtotalTextView.setText(String.valueOf(subtotal[0]));

                // Clear the quantities and recalculate the subtotal
                for (Button itemLayout : buttonTextViewMap.keySet()) {
                    TextView itemText = buttonTextViewMap.get(itemLayout);
                    EditText itemQuantityTextView = getItemQuantityTextView(itemText);
                    TextView itemTotalTextView = getItemTotalTextView(itemText);

                    itemQuantityTextView.setText("0");
                    itemTotalTextView.setText("P0");
                }
            }
        });

        pesoO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current value of the cash received TextView
                String cashReceived = cashReceivedTextView.getText().toString().trim();

                // Check if the cash received is empty
                if (cashReceived.isEmpty()) {
                    cashReceived = "0"; // Set the default value to 0
                }

                // Convert the string value to a number
                int cash;
                try {
                    cash = Integer.parseInt(cashReceived);
                } catch (NumberFormatException e) {
                    Toast.makeText(ReservedOrders.this, "Invalid cash amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the value of pesoO (1 peso) to the cash received
                cash += 1;

                // Update the cash received TextView with the new value
                cashReceivedTextView.setText(String.valueOf(cash));
            }
        });


        pesoF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current value of the cash received TextView
                String cashReceived = cashReceivedTextView.getText().toString().trim();

                // Check if the cash received is empty
                if (cashReceived.isEmpty()) {
                    cashReceived = "0"; // Set the default value to 0
                }

                // Convert the string value to a number
                int cash;
                try {
                    cash = Integer.parseInt(cashReceived);
                } catch (NumberFormatException e) {
                    Toast.makeText(ReservedOrders.this, "Invalid cash amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the value of pesoO (1 peso) to the cash received
                cash += 5;

                // Update the cash received TextView with the new value
                cashReceivedTextView.setText(String.valueOf(cash));
            }
        });

        pesoT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current value of the cash received TextView
                String cashReceived = cashReceivedTextView.getText().toString().trim();

                // Check if the cash received is empty
                if (cashReceived.isEmpty()) {
                    cashReceived = "0"; // Set the default value to 0
                }

                // Convert the string value to a number
                int cash;
                try {
                    cash = Integer.parseInt(cashReceived);
                } catch (NumberFormatException e) {
                    Toast.makeText(ReservedOrders.this, "Invalid cash amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the value of pesoO (1 peso) to the cash received
                cash += 10;

                // Update the cash received TextView with the new value
                cashReceivedTextView.setText(String.valueOf(cash));
            }
        });

        pesoTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current value of the cash received TextView
                String cashReceived = cashReceivedTextView.getText().toString().trim();

                // Check if the cash received is empty
                if (cashReceived.isEmpty()) {
                    cashReceived = "0"; // Set the default value to 0
                }

                // Convert the string value to a number
                int cash;
                try {
                    cash = Integer.parseInt(cashReceived);
                } catch (NumberFormatException e) {
                    Toast.makeText(ReservedOrders.this, "Invalid cash amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the value of pesoO (1 peso) to the cash received
                cash += 20;

                // Update the cash received TextView with the new value
                cashReceivedTextView.setText(String.valueOf(cash));
            }
        });

        pesoFi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current value of the cash received TextView
                String cashReceived = cashReceivedTextView.getText().toString().trim();

                // Check if the cash received is empty
                if (cashReceived.isEmpty()) {
                    cashReceived = "0"; // Set the default value to 0
                }

                // Convert the string value to a number
                int cash;
                try {
                    cash = Integer.parseInt(cashReceived);
                } catch (NumberFormatException e) {
                    Toast.makeText(ReservedOrders.this, "Invalid cash amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the value of pesoO (1 peso) to the cash received
                cash += 50;

                // Update the cash received TextView with the new value
                cashReceivedTextView.setText(String.valueOf(cash));
            }
        });

        pesoOh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current value of the cash received TextView
                String cashReceived = cashReceivedTextView.getText().toString().trim();

                // Check if the cash received is empty
                if (cashReceived.isEmpty()) {
                    cashReceived = "0"; // Set the default value to 0
                }

                // Convert the string value to a number
                int cash;
                try {
                    cash = Integer.parseInt(cashReceived);
                } catch (NumberFormatException e) {
                    Toast.makeText(ReservedOrders.this, "Invalid cash amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the value of pesoO (1 peso) to the cash received
                cash += 100;

                // Update the cash received TextView with the new value
                cashReceivedTextView.setText(String.valueOf(cash));
            }
        });

        pesoTh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current value of the cash received TextView
                String cashReceived = cashReceivedTextView.getText().toString().trim();

                // Check if the cash received is empty
                if (cashReceived.isEmpty()) {
                    cashReceived = "0"; // Set the default value to 0
                }

                // Convert the string value to a number
                int cash;
                try {
                    cash = Integer.parseInt(cashReceived);
                } catch (NumberFormatException e) {
                    Toast.makeText(ReservedOrders.this, "Invalid cash amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the value of pesoO (1 peso) to the cash received
                cash += 200;

                // Update the cash received TextView with the new value
                cashReceivedTextView.setText(String.valueOf(cash));
            }
        });

        peso5h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current value of the cash received TextView
                String cashReceived = cashReceivedTextView.getText().toString().trim();

                // Check if the cash received is empty
                if (cashReceived.isEmpty()) {
                    cashReceived = "0"; // Set the default value to 0
                }

                // Convert the string value to a number
                int cash;
                try {
                    cash = Integer.parseInt(cashReceived);
                } catch (NumberFormatException e) {
                    Toast.makeText(ReservedOrders.this, "Invalid cash amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the value of pesoO (1 peso) to the cash received
                cash += 500;

                // Update the cash received TextView with the new value
                cashReceivedTextView.setText(String.valueOf(cash));
            }
        });

        pesoOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current value of the cash received TextView
                String cashReceived = cashReceivedTextView.getText().toString().trim();

                // Check if the cash received is empty
                if (cashReceived.isEmpty()) {
                    cashReceived = "0"; // Set the default value to 0
                }

                // Convert the string value to a number
                int cash;
                try {
                    cash = Integer.parseInt(cashReceived);
                } catch (NumberFormatException e) {
                    Toast.makeText(ReservedOrders.this, "Invalid cash amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the value of pesoO (1 peso) to the cash received
                cash += 1000;

                // Update the cash received TextView with the new value
                cashReceivedTextView.setText(String.valueOf(cash));
            }
        });








        // Declare a variable to store the subtotal
        initializeItemPriceMap();

        kwekkwekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(kwekkwekButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Kwek-kwek");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(kwekkwekButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(kwekkwekButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(kwekkwekButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(kwekkwekButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        fishballButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(fishballButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Fishball");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(fishballButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(fishballButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(fishballButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(fishballButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });



        friesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(friesButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Fries");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(friesButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(friesButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(friesButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(friesButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        hotdogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(hotdogButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Hotdog");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(hotdogButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(hotdogButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(hotdogButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(hotdogButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        cballsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(cballsButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("ChickenBalls");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(cballsButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(cballsButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(cballsButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(cballsButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        kikiamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(kikiamButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Kikiam");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(kikiamButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(kikiamButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(kikiamButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(kikiamButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        cheesestkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(cheesestkButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("CheeseStick");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(cheesestkButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(cheesestkButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(cheesestkButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(cheesestkButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        dynamiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(dynamiteButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Dynamite");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(dynamiteButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(dynamiteButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(dynamiteButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(dynamiteButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });


        siomaiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(siomaiButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Siomai");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(siomaiButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(siomaiButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(siomaiButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(siomaiButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        penoyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(penoyButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Penoy");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(penoyButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(penoyButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(penoyButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(penoyButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        cb1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(cb1Button)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Combo1");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(cb1Button);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(cb1Button);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(cb1Button, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(cb1Button);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        cb2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(cb2Button)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Combo2");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(cb2Button);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(cb2Button);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(cb2Button, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(cb2Button);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        cb3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(cb3Button)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Combo3");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(cb3Button);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(cb3Button);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(cb3Button, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(cb3Button);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        cb4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(cb4Button)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Combo4");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(cb4Button);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(cb4Button);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(cb4Button, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(cb4Button);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        cb4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(cb4Button)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Combo4");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(cb4Button);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(cb4Button);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(cb4Button, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(cb4Button);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        gulamanGButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(gulamanGButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Gulaman-8oz");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(gulamanGButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(gulamanGButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(gulamanGButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(gulamanGButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        gulamanHButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(gulamanHButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Gulaman-16oz");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(gulamanHButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(gulamanHButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(gulamanHButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(gulamanHButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        gulamanJButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(gulamanJButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("Gulaman-22oz");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(gulamanJButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(gulamanJButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(gulamanJButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(gulamanJButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        lemonTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(lemonTButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("LemonTea-16oz");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(lemonTButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(lemonTButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(lemonTButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(lemonTButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        lemonTYButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is already added
                if (!buttonTextViewMap.containsKey(lemonTYButton)) {
                    // Create a new TableRow to contain the TextView and EditText pair
                    TableRow itemRow = new TableRow(getApplicationContext());
                    itemRow.setGravity(Gravity.END);

                    TextView itemText = new TextView(getApplicationContext());
                    itemText.setText("LemonTea-22oz");
                    itemText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    itemText.setPadding(40, 3, 8, 8);

                    // Set layout parameters for the item name TextView
                    TableRow.LayoutParams itemNameParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    itemText.setLayoutParams(itemNameParams);
                    itemRow.addView(itemText);

                    final EditText quantityEditText = new EditText(getApplicationContext());
                    quantityEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    quantityEditText.setText("1");
                    quantityEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    TableRow.LayoutParams quantityLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    quantityLayoutParams.setMargins(16, 0, 16, 0);
                    quantityEditText.setLayoutParams(quantityLayoutParams);
                    quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemRow.addView(quantityEditText);

                    final TextView totalTextView = new TextView(getApplicationContext());
                    totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    int price = getItemPrice(itemText);  // Modify the price as needed
                    int total = price;
                    totalTextView.setText(String.valueOf("P" + total));
                    TableRow.LayoutParams totalLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    totalLayoutParams.setMargins(0, 0, 16, 0);
                    totalTextView.setLayoutParams(totalLayoutParams);
                    itemRow.addView(totalTextView);

                    final Button minusButton = new Button(getApplicationContext());
                    minusButton.setText("-");
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    buttonLayoutParams.setMargins(16, 0, 0, 0);
                    minusButton.setLayoutParams(buttonLayoutParams);
                    itemRow.addView(minusButton);

                    final Button deleteButton = new Button(getApplicationContext());
                    deleteButton.setText("Clear");
                    TableRow.LayoutParams deleteButtonLayoutParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonLayoutParams.setMargins(16, 0, 0, 0);
                    deleteButton.setLayoutParams(deleteButtonLayoutParams);
                    itemRow.addView(deleteButton);

                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                int total = quantity * price;
                                totalTextView.setText(String.valueOf("P" + total));
                                quantityEditText.setText(String.valueOf(quantity));

                                // Update the subtotal by subtracting the price
                                subtotal[0] -= price;
                                if (subtotal[0] < 0) {
                                    subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                                }
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }

                            if (quantity == 0) {
                                // Remove the TableRow from the table layout
                                itemContainer.removeView(itemRow);
                                buttonTextViewMap.remove(lemonTYButton);
                            }
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the TableRow from the table layout
                            itemContainer.removeView(itemRow);

                            // Remove the button and associated text views from the map
                            buttonTextViewMap.remove(lemonTYButton);

                            // Get the item's total value
                            int itemTotal = Integer.parseInt(totalTextView.getText().toString().replace("P", ""));

                            // Decrement the subtotal by the item's total value
                            subtotal[0] -= itemTotal;

                            if (subtotal[0] < 0) {
                                subtotal[0] = 0; // Set subtotal to zero if it becomes negative
                            }

                            subtotalTextView.setText(String.valueOf(subtotal[0]));
                            updateSubtotal();
                        }
                    });

                    // Add the TableRow to the TableLayout
                    itemContainer.addView(itemRow);

                    // Add the button and associated text views to the map
                    buttonTextViewMap.put(lemonTYButton, itemText);

                    quantityEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // No action needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // No action needed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String quantityText = s.toString();
                            if (!quantityText.isEmpty()) {
                                int quantity = Integer.parseInt(quantityText);
                                int total = quantity * price;
                                totalTextView.setText("P" + total);

                                // Calculate the new subtotal based on the quantity and price
                                subtotal[0] = quantity * price;
                                subtotalTextView.setText(String.valueOf(subtotal[0]));
                                updateSubtotal();
                            }
                        }
                    });

                    // Update subtotal with the initial item's price
                    subtotal[0] += price;
                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                } else {
                    // If the button is already added, increase the quantity by 1
                    TextView itemNameTextView = buttonTextViewMap.get(lemonTYButton);
                    EditText quantityEditText = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    quantity++;

                    int price = getItemPrice(itemNameTextView);// Modify the price as needed
                    int newTotal = quantity * price;
                    totalTextView.setText("P" + newTotal);
                    quantityEditText.setText(String.valueOf(quantity));

                    // Update the quantities and recalculate the subtotal
                    subtotal[0] = 0;
                    for (Button itemButton : buttonTextViewMap.keySet()) {
                        TextView itemText = buttonTextViewMap.get(itemButton);
                        EditText itemQuantityEditText = getItemQuantityTextView(itemText);
                        TextView itemTotalTextView = getItemTotalTextView(itemText);

                        int itemQuantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        int itemPrice = getItemPrice(itemText);
                        int itemTotal = itemQuantity * itemPrice;
                        itemTotalTextView.setText("P" + itemTotal);

                        subtotal[0] += itemTotal;
                    }

                    subtotalTextView.setText(String.valueOf(subtotal[0]));
                }
            }
        });

        gulamanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialogG(gulamanGButton, gulamanHButton, gulamanJButton);

            }
        });


        lemonteaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialogL(lemonTButton, lemonTYButton);
            }
        });




        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        Map<String, Integer> orderNumbersMap = new HashMap<>();

        // Declare orderNumbersMap as a class-level variable

        // Retrieve the orderTotalSum value from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        double orderTotalSum = 0.0;

        totalBtn.setOnClickListener(new View.OnClickListener() {
            private double orderTotalSum = 0.0;

            @Override
            public void onClick(View view) {
                // Check if pickup time and customer's name are empty
                if (selectedTimeTextView.getText().toString().trim().isEmpty() || customerNameTxt.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ReservedOrders.this, "Please enter the pickup time and customer's name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (buttonTextViewMap.isEmpty()) {
                    Toast.makeText(ReservedOrders.this, "No Orders Added", Toast.LENGTH_SHORT).show();
                    return;
                }

                String cashReceivedStr = cashReceivedTextView.getText().toString().trim();
                if (TextUtils.isEmpty(cashReceivedStr)) {
                    Toast.makeText(ReservedOrders.this, "Please enter the cash amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                double cashReceived;
                try {
                    cashReceived = Double.parseDouble(cashReceivedStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(ReservedOrders.this, "Invalid cash amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                orderTotalSum = 0.0; // Reset orderTotalSum for each order

                Map<String, Object> orderMap = new HashMap<>();
                StringBuilder confirmationMessage = new StringBuilder();
                confirmationMessage.append("Order Details:\n");
                Set<String> orderedProductsSet = new HashSet<>();

                for (Button button : buttonTextViewMap.keySet()) {
                    TextView itemNameTextView = buttonTextViewMap.get(button);
                    TextView quantityTextView = getItemQuantityTextView(itemNameTextView);
                    TextView totalTextView = getItemTotalTextView(itemNameTextView);

                    String itemName = itemNameTextView.getText().toString();
                    String quantity = quantityTextView.getText().toString();
                    String total = totalTextView.getText().toString().replace("P", "");

                    double itemTotal = Double.parseDouble(total);
                    orderTotalSum += itemTotal;

                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("itemName", itemName);
                    itemMap.put("quantity", quantity);
                    itemMap.put("total", total);

                    orderMap.put(itemName, itemMap);

                    confirmationMessage.append(itemName).append(" (").append(quantity).append(") - ").append("P").append(total).append("\n");
                    orderedProductsSet.add(itemName);
                }

                StringBuilder orderedProducts = new StringBuilder();
                for (String product : orderedProductsSet) {
                    orderedProducts.append(product).append(",");
                }

                if (orderedProducts.length() > 0) {
                    orderedProducts.setLength(orderedProducts.length() - 1);
                }

                String customerName = customerNameTxt.getText().toString();

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                String dayOrdersRef = getDayOfWeekString(dayOfWeek);
                String timeOrdersRef = getTimeOfDayString(hour);

                AlertDialog.Builder builder = new AlertDialog.Builder(ReservedOrders.this);
                builder.setTitle("Order Confirmation")
                        .setMessage(confirmationMessage.toString() + "\nCustomer Name: " + customerName + "\nCash Received: ₱" + cashReceivedStr + "\nChange: ₱" + (cashReceived - orderTotalSum) + "\nTotal: ₱" + orderTotalSum + "\nTime: " + selectedTimeTextView.getText().toString())
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String cashReceivedStr = cashReceivedTextView.getText().toString().trim();
                                if (cashReceivedStr.isEmpty()) {
                                    Toast.makeText(ReservedOrders.this, "Please enter the cash amount", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                double cashReceived = Double.parseDouble(cashReceivedStr);
                                double change = cashReceived - orderTotalSum;

                                if (cashReceived < orderTotalSum) {
                                    Toast.makeText(ReservedOrders.this, "Insufficient cash received", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                customerNameTxt.setText("");

                                for (Button button : buttonTextViewMap.keySet()) {
                                    TextView itemNameTextView = buttonTextViewMap.get(button);
                                    TextView quantityTextView = getItemQuantityTextView(itemNameTextView);
                                    TextView totalTextView = getItemTotalTextView(itemNameTextView);
                                    LinearLayout itemLayout = (LinearLayout) itemNameTextView.getParent();
                                    itemContainer.removeView(itemLayout);
                                    itemLayout.removeView(itemNameTextView);
                                    itemLayout.removeView(quantityTextView);
                                    itemLayout.removeView(totalTextView);
                                }

                                buttonTextViewMap.clear();
                                subtotal[0] = 0;
                                subtotalTextView.setText("0");

                                DatabaseReference orderTotalRef = databaseReference.child("ReservedOrders")
                                        .child(dayOrdersRef)
                                        .child("orderTotalSum");
                                orderTotalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        double previousOrderTotalSum = 0.0;
                                        if (dataSnapshot.exists()) {
                                            previousOrderTotalSum = dataSnapshot.getValue(Double.class);
                                        }
                                        double updatedOrderTotalSum = previousOrderTotalSum + orderTotalSum;
                                        orderTotalRef.setValue(updatedOrderTotalSum);

                                        orderMap.put("orderTotal", orderTotalSum);
                                        orderMap.put("cashReceived", cashReceived);
                                        orderMap.put("change", change);
                                        orderMap.put("time", selectedTimeTextView.getText().toString());
                                        orderMap.put("customerName", customerName);

                                        if (dayOrdersRef != null && timeOrdersRef != null) {
                                            DatabaseReference dayOrdersReference = databaseReference.child("ReservedOrders").child(dayOrdersRef);

                                            String orderKey = dayOrdersReference.push().getKey();
                                            DatabaseReference orderRef = dayOrdersReference.child(orderKey);
                                            orderRef.setValue(orderMap);

                                            DatabaseReference timeOrderRef = dayOrdersReference.child(timeOrdersRef);

                                            Integer currentOrderNumber = orderNumbersMap.get(timeOrdersRef);

                                            if (currentOrderNumber == null) {
                                                currentOrderNumber = 0;
                                            } else {
                                                currentOrderNumber++;
                                            }

                                            orderNumbersMap.put(timeOrdersRef, currentOrderNumber);

                                            String orderNumberKey = orderKey + "-" + currentOrderNumber;

                                            DatabaseReference indexRef = timeOrderRef.child(orderNumberKey);
                                            indexRef.setValue(orderedProducts.toString());
                                        } else {
                                            // Handle the case when dayOrdersRef or timeOrdersRef is null
                                            Toast.makeText(ReservedOrders.this, "Failed to place the Reservation. Please try again.", Toast.LENGTH_SHORT).show();
                                        }

                                        Toast.makeText(ReservedOrders.this, "Reservation placed successfully!", Toast.LENGTH_SHORT).show();
                                        totalTextView.setText("0");

                                        // Save the orderTotalSum to SharedPreferences
                                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putFloat("orderTotalSum", (float) updatedOrderTotalSum);
                                        editor.apply();

                                        cashReceivedTextView.setText(""); // Clear the cash received
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle the database error if needed
                                        Toast.makeText(ReservedOrders.this, "Failed to place the order. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

    }
}
