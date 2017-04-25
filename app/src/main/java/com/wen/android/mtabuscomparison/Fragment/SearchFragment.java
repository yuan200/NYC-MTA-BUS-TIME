package com.wen.android.mtabuscomparison.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wen.android.mtabuscomparison.MainActivity;
import com.wen.android.mtabuscomparison.R;
import com.wen.android.mtabuscomparison.RoutesViewActivity;
import com.wen.android.mtabuscomparison.SearchResultActivity;
import com.wen.android.mtabuscomparison.handler.SearchHandler;

/**
 * Created by yuan on 4/10/2017.
 */

public class SearchFragment extends Fragment {

    public SearchFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_search_view,menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                displaySearchResult(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /**
     * start a new activity and display the search result
     */
    public void displaySearchResult(String userInput){
        SearchHandler searchHandler = new SearchHandler(userInput);
        if (searchHandler.keywordType() == 0){
            String[] stopcodeArray = new String[1];
            //get the bus code from the user input
            stopcodeArray[0] = userInput;
            if (stopcodeArray[0] == null) {
                return;
            }
            Intent intent = new Intent(getActivity(), SearchResultActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT,stopcodeArray);
            startActivity(intent);
        }else{
            String routeEntered = userInput.toUpperCase();
            Intent intent = new Intent(getActivity(), RoutesViewActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, routeEntered);
            startActivity(intent);
        }

    }

}
