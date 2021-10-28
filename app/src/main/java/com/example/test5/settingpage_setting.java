package com.example.test5;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class settingpage_setting extends Fragment {

    View view;
    private DatabaseReference mDatabase;

    public interface MyCallback {
        void onCallback(String value);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settingpage_setting, container, false);

        Button digital = (Button) view.findViewById(R.id.settingbtn1);
        Button analog = (Button) view.findViewById(R.id.settingbtn2);
        Button vibration = (Button) view.findViewById(R.id.settingbtn3);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        digital.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity().getApplicationContext(), digitalnum.class);
                readIP(new MyCallback() {
                    @Override
                    public void onCallback(String ip) {
                        Toast.makeText(view.getContext(), "Connect 시도", Toast.LENGTH_SHORT).show();
                        Log.d("DG_IP", ip);
                        Integer port = 23456;
                        settingpage_setting.ConnectThread thread = new settingpage_setting.ConnectThread(ip, port);
                        Toast.makeText(view.getContext(), "Connect 완료", Toast.LENGTH_SHORT).show();

                        String send_data = "restart_digital/" + "/" + "/" + "/" + "/";

                        thread.sendName(send_data);
                        thread.getStream(send_data);
                        thread.start();
                    }
                });
                startActivity(intent);
            }
        });

        analog.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity().getApplicationContext(), analog.class);
                startActivity(intent);
            }
        });

        vibration.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity().getApplicationContext(), stm32_layout.class);
                startActivity(intent);
            }
        });
        return view;
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
