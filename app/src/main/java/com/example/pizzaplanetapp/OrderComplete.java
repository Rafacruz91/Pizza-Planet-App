package com.example.pizzaplanetapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.util.ArrayList;

public class OrderComplete extends AppCompatActivity {

    //gives user an overview of their placed order (items, price, tax, total price) along with
    // the ability to call store or text driver

    private static final String TAG = "OrderComplete";

    private RecyclerView recyclerView;
    private ArrayList<OrderItem> orderData;
    private OrderAdapter orderAdapter;
    private Object itemCount,totalPrice;
    private TextView textTotalPrice, textTotalItems;

    DatabaseReference database;
    int girdColumnCount = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_order);

        Intent intent = getIntent();
        totalPrice = intent.getExtras().get("total price");
        itemCount = intent.getExtras().get("item count");


        recyclerView = findViewById(R.id.recyclerCartView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, girdColumnCount));

        textTotalPrice = findViewById(R.id.totalAmountSummary_TextView);
        textTotalItems = findViewById(R.id.totItemAmountSummary_TextView);

        textTotalPrice.setText("$" + String.format("%.2f", totalPrice));
        textTotalItems.setText(itemCount.toString());

        //Reference to firebase database starting at pizza node
        database = FirebaseDatabase.getInstance().getReference("order");

        // initialize the array that will be used to hold the cart items
        orderData = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderData);

        recyclerView.setAdapter(orderAdapter);


        loadData();
    }

    // initializeData with firebase
    private void loadData() {
        Log.d(TAG, "inside of loadData");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                orderData.clear();//assures no duplicates are introduced
//                resetTotalPrice();//reset price to zero

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    itemCount++;//keeps track of the number of items in cart
                    OrderItem item = dataSnapshot.getValue(OrderItem.class);

                    //keeps track of total price of cart
                    if (item.getPrice() != null) {
//                        totalPrice += Float.valueOf(item.getPrice());

                    }
                    //sets the total of the cart as items are added or removed
//                    textTotalPrice.setText("$" + String.format("%.2f", totalPrice));
                    orderData.add(item);
                }
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d(TAG, "end of loadData");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        database = FirebaseDatabase.getInstance().getReference();
        database.child("order").removeValue();
    }

    //shows an alert displaying the information like store address
    public void btn_moreInfo(View view) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Store Information");
        alertBuilder.setMessage("Address: blah blah blah");
        alertBuilder.show();

    }

    //allows user to make a phone call to store
    public void btn_callStore(View view) {

        Intent intent = new Intent(Intent.ACTION_DIAL);//doesn't need manifest permissions
        intent.setData(Uri.parse("tel:8007777777"));
        startActivity(intent);
    }
}
