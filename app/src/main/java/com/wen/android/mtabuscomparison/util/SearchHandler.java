package com.wen.android.mtabuscomparison.util;

/**
 * Created by yuan on 4/21/2017.
 */

public class SearchHandler {
    private String mKeyword;

    public SearchHandler(String keyword){
        mKeyword = keyword;
    }

    public int keywordType(){
        String firstCharacter = Character.toString(mKeyword.charAt(0));
        //if return true then it is a stop code otherwise is a route
        boolean isStopCode = firstCharacter.matches("\\d+");
        if (isStopCode == true){
            //a stop code
            return 0;
        } else {
            // a route
            return 1;
        }
    }
}
