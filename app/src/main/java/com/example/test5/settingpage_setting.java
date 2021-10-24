package com.example.test5;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class settingpage_setting extends Fragment {

    View view;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settingpage_setting, container, false);

        Button digital = (Button) view.findViewById(R.id.settingbtn1);
        Button analog = (Button) view.findViewById(R.id.settingbtn2);
        Button vibration = (Button) view.findViewById(R.id.settingbtn3);

        digital.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity().getApplicationContext(), digitalnum.class);
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

}
