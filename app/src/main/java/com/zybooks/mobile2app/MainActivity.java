package com.zybooks.mobile2app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    private InventoryAdapter adapter;
    private SparePartRepository partsRepo;
    private boolean isLoggedIn;


    //all employees who need to be 'in the know' of the parts inventory
    private final String[] notificationPhoneList = {
            "(650) 555-1212", "+1 650 555-6789", "6505551212", "6505556789", "16505556789"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //get user privileges
        Intent prevIntent = getIntent();
        isLoggedIn = prevIntent.getBooleanExtra("isLoggedIn", false);


        // set up spare parts inventory repository
        partsRepo = new SparePartRepository(this);

        RecyclerView rvInventory = findViewById(R.id.rv_inventory);
        rvInventory.setLayoutManager(new LinearLayoutManager(this));

        adapter = new InventoryAdapter(partsRepo.getAllItems(), new InventoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SparePart item) {
                openEditMenu(item);
            }

            @Override
            public void onItemLongClick(SparePart item) {
                deleteSparePartFromTable(item);
            }
        });


        rvInventory.setAdapter(adapter);


        //set clicking action of FAB
        FloatingActionButton fab = findViewById(R.id.button_add_entry);
        fab.setOnClickListener(v -> {
            addSparePartToTable();
        });

    }


    private void addSparePartToTable() {
        //only allow parts to be added if user logged in
        if(!isLoggedIn) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_entry, null);

        EditText editId = dialogView.findViewById(R.id.edit_id);
        EditText editName = dialogView.findViewById(R.id.edit_name);
        EditText editPrice = dialogView.findViewById(R.id.edit_price);
        EditText editDescription = dialogView.findViewById(R.id.edit_description);
        EditText editQuantity = dialogView.findViewById(R.id.edit_quantity);

        new AlertDialog.Builder(this)
                .setTitle("Add New Item")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {

                    String oemNumber = editId.getText().toString().trim();
                    String itemName = editName.getText().toString();
                    double itemPrice = Double.parseDouble(editPrice.getText().toString());
                    String itemDescription = editDescription.getText().toString().trim();
                    int itemQuantity = Integer.parseInt(editQuantity.getText().toString());

                    try {
                        //adding item
                        boolean insertResult = partsRepo.addItem(oemNumber, itemName, itemPrice, itemDescription, itemQuantity);

                        if(insertResult) {

                            adapter.updateItems(partsRepo.getAllItems());

                            if(itemQuantity < 5) {

                                //sends to everyone who is on notificationPhoneList
                                sendQuantityAlert(oemNumber, itemName);
                            }
                        }

                    } catch (Exception e) {
                        Log.d("MainActivity","Error occurred while adding part " + oemNumber);
                    }

                })
                .show();
    }


    private void openEditMenu(SparePart item) {
        //only allow edit if logged in
        if(!isLoggedIn) {
            return;
        }
        // Inflate dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_item, null);

        // Get fields
        EditText oemInput = dialogView.findViewById(R.id.edit_text_oem);
        oemInput.setEnabled(false); //read only
        oemInput.setFocusable(false); //set view to unclickable
        EditText nameInput = dialogView.findViewById(R.id.edit_text_name);
        EditText priceInput = dialogView.findViewById(R.id.edit_text_price);
        EditText descInput = dialogView.findViewById(R.id.edit_text_description);
        EditText qtyInput = dialogView.findViewById(R.id.edit_text_quantity);

        // Pre fill with current values
        oemInput.setText(item.oemNumber);
        nameInput.setText(item.name);
        priceInput.setText(String.valueOf(item.price));
        descInput.setText(item.description);
        qtyInput.setText(String.valueOf(item.quantity));

        //build
        new AlertDialog.Builder(this)
                .setTitle("Edit Item")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String oemNumber = oemInput.getText().toString().trim();
                    String itemName = nameInput.getText().toString();
                    double itemPrice = Double.parseDouble(priceInput.getText().toString());
                    String itemDescription = descInput.getText().toString().trim();
                    int itemQuantity = Integer.parseInt(qtyInput.getText().toString());
                    editSparePartInTable(new SparePart(oemNumber, itemName, itemPrice, itemDescription, itemQuantity));
                })
                .setNegativeButton("Cancel", null)
                .show();

    }


    //based on item.oemNumber as key
    private void editSparePartInTable(SparePart item) {

        try {
            boolean updateResult = partsRepo.updateItem(item.oemNumber, item.name, item.price, item.description, item.quantity);
            if(!updateResult) {
                throw new RuntimeException();
            }
            adapter.updateItems(partsRepo.getAllItems());
        } catch (Exception e) {
            Log.d("MainActivity", "failed to edit entry on " + item.oemNumber);
        }
    }


    private void deleteSparePartFromTable(SparePart item) {
        //only allow delete if logged in
        if(!isLoggedIn) {
            return;
        }
        try {
            boolean deleteResult = partsRepo.deleteItem(item.oemNumber);
            if(!deleteResult) {
                throw new RuntimeException();
            }
            adapter.updateItems(partsRepo.getAllItems());
        } catch(Exception e) {
            Log.d("MainActivity","failed to delete entry on " + item.oemNumber );
        }
    }


    private void sendQuantityAlert(String oemNumber, String partName) {
        String message = "Uh oh! You are almost out of " + oemNumber + " | " + partName + "... Time to order more soon.";
        SmsManager smsManager = getSystemService(SmsManager.class);
        if(smsManager != null) {
            for (int i = 0; i < notificationPhoneList.length; i++) {
                smsManager.sendTextMessage(notificationPhoneList[i], null, message, null, null);
            }
        }

    }


}