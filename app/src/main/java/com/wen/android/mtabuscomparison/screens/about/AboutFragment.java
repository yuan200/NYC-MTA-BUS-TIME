package com.wen.android.mtabuscomparison.screens.about;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wen.android.mtabuscomparison.R;

/**
 * Created by yuan on 4/10/2017.
 */

public class AboutFragment extends Fragment {
    private Button mSendEmail;
    private static final String TAG_EXCEPTION = "exception error";

    public AboutFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        mSendEmail = (Button)v.findViewById(R.id.send_email);
        mSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:nycmtabustime@gmail.com"));
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
