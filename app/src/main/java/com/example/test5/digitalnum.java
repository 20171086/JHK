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
                Toast.makeText(getApplicationContext(), "Connect 시도", Toast.LENGTH_SHORT).show();

                String addr = ipaddr.getText().toString();
                Integer port = 23456;
                digitalnum.ConnectThread thread = new digitalnum.ConnectThread(addr, port);

                Toast.makeText(getApplicationContext(), "Connect 완료", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getApplicationContext(), "Connect 시도", Toast.LENGTH_SHORT).show();

                String addr = ipaddr.getText().toString();
                Integer port = 23456;
                digitalnum.ConnectThread thread = new digitalnum.ConnectThread(addr, port);

                Toast.makeText(getApplicationContext(), "Connect 완료", Toast.LENGTH_SHORT).show();

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
                //이미지 로드 성공시

                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(device_image);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //이미지 로드 실패시
                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
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
            try { //클라이언트 소켓 생성

                socket = new Socket(hostname, port);
                Log.d("Socket", "Socket 생성, 연결.");

            } catch (UnknownHostException uhe) {
                // 소켓 생성 시 전달되는 호스트(www.unknown-host.com)의 IP를 식별할 수 없음.
                Log.e("ADDR", " 생성 Error : 호스트의 IP 주소를 식별할 수 없음. (잘못된 주소 값 또는 호스트 이름 사용)");

            } catch (IOException ioe) {
                // 소켓 생성 과정에서 I/O 에러 발생.
                Log.e("NOANSWER", " 생성 Error : 네트워크 응답 없음");

            } catch (SecurityException se) {
                // security manager에서 허용되지 않은 기능 수행.
                Log.e("SEC", " 생성 Error : 보안(Security) 위반에 대해 보안 관리자(Security Manager)에 의해 발생. (프록시(proxy) 접속 거부, 허용되지 않은 함수 호출)");

            } catch (IllegalArgumentException le) {
                // 소켓 생성 시 전달되는 포트 번호(65536)이 허용 범위(0~65535)를 벗어남.
                Log.e("NETPARAM", " 생성 Error : 메서드에 잘못된 파라미터가 전달되는 경우 발생. (0~65535 범위 밖의 포트 번호 사용, null 프록시(proxy) 전달)");
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
                Log.d("DATASENDING", "데이터 송신");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("SENDINGERR","데이터 송신 오류");
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
                Log.d("DATASENDING", "데이터 송신");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("SENDINGERR","데이터 송신 오류");
            }
        }

    }
}

