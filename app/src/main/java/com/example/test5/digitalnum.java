package com.example.test5;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class digitalnum extends AppCompatActivity {

    private DatabaseReference mDatabase;

    public interface MyCallback1 {
        void onCallback(String value);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.digitalnum);

        Intent intent = new Intent(getApplicationContext(), imageScreen.class);
        intent.putExtra("file_name", "digital0.png");
        startActivity(intent);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //contents
        Button start_btn = (Button) findViewById(R.id.digitalnum_startbtn);
        Button load_btn = (Button) findViewById(R.id.digitalnum_loadbtn);
        Button stop_btn = (Button) findViewById(R.id.digitalnum_stopbtn);
        EditText ipaddr = (EditText) findViewById(R.id.digitalnum_addr_edit);
        EditText device = (EditText) findViewById(R.id.digitalnum_device_edit);
        EditText rotation = (EditText) findViewById(R.id.digitalnum_rotation_edit);
        EditText boxnum = (EditText) findViewById(R.id.digitalnum_boxnum_edit);
        EditText boxscale = (EditText) findViewById(R.id.digitalnum_boxscale_edit);
        EditText boxpos = (EditText) findViewById(R.id.digitalnum_boxpos_edit);

        load_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                readIP(new MyCallback1() {
                    @Override
                    public void onCallback(String ip) {
                        Log.d("DG_IP", ip);
                        ipaddr.setText(ip);
                    }
                });
                showImage();
            }
        });
        //save values and start raspberrypi
        start_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getApplicationContext(), "Connect ??????", Toast.LENGTH_SHORT).show();

                String addr = ipaddr.getText().toString();
                Integer port = 23456;
                digitalnum.ConnectThread thread = new digitalnum.ConnectThread(addr, port);

                Toast.makeText(getApplicationContext(), "Connect ??????", Toast.LENGTH_SHORT).show();

                String device_val = device.getText().toString();
                String rotation_val = rotation.getText().toString();
                String boxnum_val = boxnum.getText().toString();
                String boxscale_val = boxscale.getText().toString();
                String boxpos_val = boxpos.getText().toString();

                String send_data = "restart_digital/" + device_val + "/" + rotation_val + "/" + boxnum_val + "/" + boxscale_val + "/" + boxpos_val;

                thread.sendName(send_data);
                thread.getStream(send_data);
                thread.start();

                finish();
            }
        });

        //save values and start raspberrypi
        stop_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getApplicationContext(), "Connect ??????", Toast.LENGTH_SHORT).show();

                String addr = ipaddr.getText().toString();
                Integer port = 23456;
                digitalnum.ConnectThread thread = new digitalnum.ConnectThread(addr, port);

                Toast.makeText(getApplicationContext(), "Connect ??????", Toast.LENGTH_SHORT).show();

                String send_data = "stop_digital";

                thread.sendName(send_data);
                thread.getStream(send_data);
                thread.start();

                finish();
            }
        });
    }
    private void showImage(){
        ImageView device_image =(ImageView) findViewById(R.id.digitalnum_device_image);
        AlertDialog.Builder popupDialogBuilder = new AlertDialog.Builder(this);

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://tree-afca6.appspot.com/");
        StorageReference storageRef = storage.getReference();
        storageRef.child("digital0.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //????????? ?????? ?????????

                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(device_image);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //????????? ?????? ?????????
                Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void readIP(MyCallback1 myCallback){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String post = dataSnapshot.child("IP").getValue(String.class);
                myCallback.onCallback(post);
                Log.w("FireBaseData", "getData" + post);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
    class ConnectThread extends Thread {
        String hostname;
        Integer port;
        Socket socket;

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        InputStream input;

        public ConnectThread(String addr, Integer port) {
            hostname = addr;
            this.port = port;

            if (SDK_INT > 8){
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            try { //??????????????? ?????? ??????

                socket = new Socket(hostname, port);
                Log.d("Socket", "Socket ??????, ??????.");

            } catch (UnknownHostException uhe) {
                // ?????? ?????? ??? ???????????? ?????????(www.unknown-host.com)??? IP??? ????????? ??? ??????.
                Log.e("ADDR", " ?????? Error : ???????????? IP ????????? ????????? ??? ??????. (????????? ?????? ??? ?????? ????????? ?????? ??????)");

            } catch (IOException ioe) {
                // ?????? ?????? ???????????? I/O ?????? ??????.
                Log.e("NOANSWER", " ?????? Error : ???????????? ?????? ??????");

            } catch (SecurityException se) {
                // security manager?????? ???????????? ?????? ?????? ??????.
                Log.e("SEC", " ?????? Error : ??????(Security) ????????? ?????? ?????? ?????????(Security Manager)??? ?????? ??????. (?????????(proxy) ?????? ??????, ???????????? ?????? ?????? ??????)");

            } catch (IllegalArgumentException le) {
                // ?????? ?????? ??? ???????????? ?????? ??????(65536)??? ?????? ??????(0~65535)??? ?????????.
                Log.e("NETPARAM", " ?????? Error : ???????????? ????????? ??????????????? ???????????? ?????? ??????. (0~65535 ?????? ?????? ?????? ?????? ??????, null ?????????(proxy) ??????)");
            }
        }

        public void run() {
            try {
                byte[] buf = new byte[1024];
                OutputStream output = socket.getOutputStream();
                while(input.read(buf)>0)
                {
                    output.write(buf);
                    output.flush();
                }
                Log.d("DATASENDING", "????????? ??????");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("SENDINGERR","????????? ?????? ??????");
            }
            finally{
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void getStream(String data)
        {
            input = new ByteArrayInputStream(data.getBytes());
        }
        public void sendName(String name){
            try {
                byte[] data = name.getBytes();
                OutputStream output = socket.getOutputStream();
                output.write(data);
                Log.d("DATASENDING", "????????? ??????");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("SENDINGERR","????????? ?????? ??????");
            }
        }

    }
}

