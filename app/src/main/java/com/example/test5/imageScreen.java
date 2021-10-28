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
                            Toast.makeText(getApplicationContext(), "Connect 시도", Toast.LENGTH_SHORT).show();
                            Log.d("DG_IP", ip);
                            Integer port = 23456;
                            imageScreen.ConnectThread thread = new imageScreen.ConnectThread(ip, port);
                            Toast.makeText(getApplicationContext(), "Connect 완료", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), "Connect 시도", Toast.LENGTH_SHORT).show();
                            Log.d("DG_IP", ip);
                            Integer port = 23456;
                            imageScreen.ConnectThread thread = new imageScreen.ConnectThread(ip, port);
                            Toast.makeText(getApplicationContext(), "Connect 완료", Toast.LENGTH_SHORT).show();
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
