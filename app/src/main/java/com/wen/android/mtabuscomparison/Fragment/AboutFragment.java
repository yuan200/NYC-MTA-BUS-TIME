package com.wen.android.mtabuscomparison.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.wen.android.mtabuscomparison.R;

/**
 * Created by yuan on 4/10/2017.
 */

public class AboutFragment extends Fragment {
    private Button mMTA_Bus_Time_link;
    private Button mSendEmail;

    public AboutFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        mMTA_Bus_Time_link = (Button)v.findViewById(R.id.mta_bus_time_link);
        mSendEmail = (Button)v.findViewById(R.id.send_email);
        mMTA_Bus_Time_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=info.mta.bustime"));
                try{
                    startActivity(intent);
                }catch ( ActivityNotFoundException e){
                    Toast.makeText(getContext(),"Sorry, Can't found the App store",Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        mSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:longying2011@gmail.com"));
                try{
                    startActivity(emailIntent);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(getContext(),"Sorry, Can't found the Email App",Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        return v;
    }
}
