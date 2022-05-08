package com.quotion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class ItemActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {
    private static int value;
    private EditText ed;
    private ImageView adding_items;
    private ListView item_list;
    private ArrayList<String> values=new ArrayList<String>(); //ArrayList takes the user input
    private ArrayAdapter<String> adapter; // ArrayAdapter is used to insert a value in ListView

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        ed = findViewById(R.id.text_edit);
        adding_items = findViewById(R.id.add_text);
        item_list = findViewById(R.id.listview);

        adding_items.setOnClickListener(this);
        item_list.setOnItemLongClickListener(this); // Giving the reference of the onclick function

        // Load in existing messages
        getQuotes();
    }

    protected void getQuotes(){

        // Get reference of quote amount
        DatabaseReference myRef = database.getReference("Users/" + LoginState.uID + "/value");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                value = dataSnapshot.getValue(Integer.class);
                System.out.println(value);

                if (!(value ==0)){
                    for (int i = 0; i < value; i++) {

                        DatabaseReference reference = database.getReference("Users/" + LoginState.uID + "/quotes/" + i);

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String quote = dataSnapshot.getValue(String.class);

                                if(!values.contains(quote))
                                {
                                    values.add(quote);
                                    adapter=new ArrayAdapter<String>(ItemActivity.this,android.R.layout.simple_list_item_1,values);
                                    item_list.setAdapter(adapter);
                                    ed.setText("");
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w("Failed to read value.", error.toException());
                            }});
                    }
                } else{
                    System.out.println("No Quotes");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to read value.", error.toException());
            }});
    }

    @Override
    public void onClick(View view) {

        String add_item=ed.getText().toString();


        if(values.contains(add_item)) // Checks whether item already exists
        {
            Toast.makeText(getBaseContext(),"Item Already Exist", Toast.LENGTH_LONG).show();
        }   // Enter the element if it does not exist
        else
        {
            // Send item to Firebase database

            DatabaseReference myRef = database.getReference("Users/" + LoginState.uID + "/value");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ItemActivity.value = dataSnapshot.getValue(Integer.class);
                    System.out.println(value);
                    return;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("Failed to read value.", error.toException());
                }
            });

            // Send quote values to database
            myRef = database.getReference("Users/" + LoginState.uID + "/quotes/" + value);
            myRef.setValue(add_item);

            // Update value to n+1
            myRef = database.getReference("Users/" + LoginState.uID + "/value");
            myRef.setValue(value + 1);

            startActivity(new Intent(this, ItemActivity.class));
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        final int removing_item=position;
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemActivity.this); // Ask the user to get the confirmation before deleting an item from the listView
        builder.setMessage("Do you want to delete").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                values.remove(removing_item);
                adapter.notifyDataSetChanged();
                Toast.makeText(getBaseContext(), "Item Deleted", Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton("Cancel", null).show();

        return true;
    }
}