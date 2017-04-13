package com.wen.android.mtabuscomparison.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wen.android.mtabuscomparison.MainActivity;
import com.wen.android.mtabuscomparison.R;
import com.wen.android.mtabuscomparison.SearchResultActivity;

/**
 * Created by yuan on 4/10/2017.
 */

public class SearchFragment extends Fragment {
    private EditText mSearchField;
    private Button mSearchButton;

    public SearchFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        mSearchField = (EditText)v.findViewById(R.id.bus_stop_code);
        mSearchButton = (Button)v.findViewById(R.id.search_button);

        mSearchButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                displaySearchResult();
            }
        });

        return v;
    }

    /**
     * start a new activity and display the search result
     */
    public void displaySearchResult(){
        String[] stopcodeArray = new String[1];
        //get the bus code from the user input
        stopcodeArray[0] = mSearchField.getText().toString();
        if (stopcodeArray[0] == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,stopcodeArray);
        startActivity(intent);
    }
}
