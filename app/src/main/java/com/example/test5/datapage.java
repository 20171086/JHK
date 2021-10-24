package com.example.test5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class datapage extends AppCompatActivity {
    private DatabaseReference mDatabase;

    public interface MyCallback {
        void onCallback(ArrayList value);
    }
    public interface MyCallback2 {
        void onCallback(String value);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.datapage);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button btnpop = (Button) findViewById(R.id.sensorbtn);
        TextView sel_name = (TextView) findViewById(R.id.selected);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerAdapter(new ArrayList()));

        btnpop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                readSensors(new MyCallback() {
                    @Override
                    public void onCallback(ArrayList value) {
                        //get all names of sensors and set one using pop up listView
                        Log.d("TAG", value.toString());
                        showAlertDialog(value, new MyCallback2() {
                            @Override
                            public void onCallback(String value) {
                                sel_name.setText(value);
                                String cat = value.split("_")[0];
                                if(cat.equals("Analog"))
                                {
                                    readData(value, new MyCallback() {
                                        @Override
                                        public void onCallback(ArrayList value) {
                                            ArrayList<String> reclist = new ArrayList<String>();
                                            for (Object i: value) {
                                                reclist.add(String.valueOf(i));
                                            }
                                            Log.d("EXDATA", value.toString());
                                            recyclerView.setAdapter(new RecyclerAdapter(value));
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void readData(String name, MyCallback myCallback){
        mDatabase.child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                HashMap post = (HashMap) dataSnapshot.getValue();
                ArrayList result = new ArrayList(post.values());
                myCallback.onCallback(result);
                Log.w("FireBaseData", "getData" + result.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void readSensors(MyCallback myCallback){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                HashMap post = (HashMap) dataSnapshot.getValue();
                ArrayList keys = new ArrayList(post.keySet());
                myCallback.onCallback(keys);
                Log.w("FireBaseData", "getData" + keys.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void showAlertDialog(ArrayList list, MyCallback2 myCallback)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(datapage.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.popwindow, null);
        builder.setView(view);

        final ListView listview = (ListView)view.findViewById(R.id.sensor_list);
        final AlertDialog dialog = builder.create();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data = (String) listview.getItemAtPosition(position);
                myCallback.onCallback(data);
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
