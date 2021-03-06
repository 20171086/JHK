package com.example.test5;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

public class imageScreen extends AppCompatActivity {

    private DatabaseReference mDatabase;

    public interface MyCallback {
        void onCallback(String value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_popup);

        Intent myIntent = getIntent();
        String file_name = myIntent.getStringExtra("file_name");

        showImage(file_name);

        Button yes = (Button) findViewById(R.id.image_yes);
        Button no = (Button) findViewById(R.id.image_no);

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(file_name.equals("digital0.png"))
                {
                    //digital
                    readIP(new MyCallback() {
                        @Override
                        public void onCallback(String ip) {
                            Toast.makeText(getApplicationContext(), "Connect ??????", Toast.LENGTH_SHORT).show();
                            Log.d("DG_IP", ip);
                            Integer port = 23456;
                            imageScreen.ConnectThread thread = new imageScreen.ConnectThread(ip, port);
                            Toast.makeText(getApplicationContext(), "Connect ??????", Toast.LENGTH_SHORT).show();
                            String send_data = "stop_digital";
                            thread.sendName(send_data);
                            thread.getStream(send_data);
                            thread.start();

                            send_data = "restart_digital/" + "/" + "/" + "/" + "/";

                            thread.sendName(send_data);
                            thread.getStream(send_data);
                            thread.start();
                        }
                    });
                }
                else
                {
                    //analog
                    readIP(new MyCallback() {
                        @Override
                        public void onCallback(String ip) {
                            Toast.makeText(getApplicationContext(), "Connect ??????", Toast.LENGTH_SHORT).show();
                            Log.d("DG_IP", ip);
                            Integer port = 23456;
                            imageScreen.ConnectThread thread = new imageScreen.ConnectThread(ip, port);
                            Toast.makeText(getApplicationContext(), "Connect ??????", Toast.LENGTH_SHORT).show();
                            String send_data = "stop_analog";
                            thread.sendName(send_data);
                            thread.getStream(send_data);
                            thread.start();

                            send_data = "restart_analog/" + "/" + "/" + "/" + "/";

                            thread.sendName(send_data);
                            thread.getStream(send_data);
                            thread.start();
                        }
                    });
                }
                showImage(file_name);
            }
        });
    }

    private void showImage(String file_name){
        ImageView device_image =(ImageView) findViewById(R.id.image_screen);
        AlertDialog.Builder popupDialogBuilder = new AlertDialog.Builder(this);

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://tree-afca6.appspot.com/");
        StorageReference storageRef = storage.getReference();
        storageRef.child(file_name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
    private void readIP(MyCallback myCallback){
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
