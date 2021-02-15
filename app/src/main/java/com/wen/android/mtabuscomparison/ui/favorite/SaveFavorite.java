package com.wen.android.mtabuscomparison.ui.favorite;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wen.android.mtabuscomparison.R;
import com.wen.android.mtabuscomparison.feature.stop.BusDatabase;
import com.wen.android.mtabuscomparison.feature.favorite.FavoriteStop;

import java.util.Date;
import java.util.concurrent.Executors;

public class SaveFavorite extends AppCompatActivity {
    private EditText mSaveStopCode1;
    private EditText mSaveStopCode2;
    private EditText mSaveStopCode3;
    private EditText mGroupName;
    private Button mSaveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_comparison);

        mSaveStopCode1 = (EditText) findViewById(R.id.save_stop_code1);
        mSaveStopCode2 = (EditText) findViewById(R.id.save_stop_code2);
        mSaveStopCode3 = (EditText) findViewById(R.id.save_stop_code3);
        mGroupName = (EditText) findViewById(R.id.save_group_name) ;
        mSaveButton = (Button) findViewById(R.id.save_group_button);
        mSaveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(mSaveStopCode1.getText().toString().equals("")){
                    Toast.makeText(SaveFavorite.this,"Please enter a stop code",Toast.LENGTH_LONG).show();
                    return;
                }
                saveStopCode();
                finish();
            }
        });


    }

    public void saveStopCode(){
        FavoriteStop favorite = new FavoriteStop(mSaveStopCode1.getText().toString(),
                mSaveStopCode2.getText().toString(),
                mSaveStopCode3.getText().toString(),
                "",
                "",
                mGroupName.getText().toString(),
                new Date());
        Executors.newSingleThreadExecutor().execute(() ->
                BusDatabase.Companion.getInstance(getApplicationContext()).favoriteStopDao().insertAll(favorite)
                );
    }


}
