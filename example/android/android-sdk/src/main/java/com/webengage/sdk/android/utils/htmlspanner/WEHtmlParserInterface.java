package com.webengage.sdk.android.utils.htmlspanner;

import android.graphics.Color;
import android.text.Spannable;
import android.util.Log;

import com.webengage.sdk.android.utils.htmlspanner.HtmlSpanner;

public class WEHtmlParserInterface {
    public Spannable fromHtml(String htmlString, int textColor, int backGroundColor, float textSize){
        HtmlSpanner spanner = new HtmlSpanner(textColor,textSize);
        spanner.setBackgroundColor(backGroundColor);
        htmlString = removeBlankSpaceFromProperties(htmlString);
        return spanner.fromHtml(htmlString);

    }

    public Spannable fromHtml(String htmlString,  float textSize){
        return fromHtml(htmlString,Color.parseColor("#000000"),Color.parseColor("#00000000"),textSize);
    }

    public Spannable fromHtml(String htmlString){

//        if(htmlString.endsWith("<br >")) {
//            htmlString = htmlString.substring(0, htmlString.length() - 5);
//            fromHtml(htmlString);
//        }

//        if(htmlString.endsWith("<br>")) {
//            htmlString = htmlString.substring(0, htmlString.length() - 4);
//            fromHtml(htmlString);
//        }

//        if(htmlString.startsWith("<p>") && htmlString.endsWith("</p>")){
//            htmlString = htmlString.substring(3,htmlString.length()-4);
//            fromHtml(htmlString);
//        }

        //Log.d("HTMLSpanner", "string : "+htmlString);
        return fromHtml(htmlString,Color.parseColor("#000000"),Color.parseColor("#00000000"),12);
    }


    private String  removeBlankSpaceFromProperties(String html){
        StringBuilder outputString = new StringBuilder();
        outputString.append("");
        String[] strArray = html.split(">");
        for (String stringAsValue:strArray) {
            if(stringAsValue.contains("<") &&stringAsValue.contains(" ") && !stringAsValue.contains("</")){
                //Log.i ("HTML Spanner ","Start TAG with space = "+stringAsValue);
                String output = replaceBlankSpace(stringAsValue);
                outputString.append(output);
                outputString.append(">");
            }else if (stringAsValue.contains("<") && !stringAsValue.contains("</")){
                //Log.i ("HTML Spanner ","Start TAG without space = "+stringAsValue);
                outputString.append(stringAsValue);
                outputString.append(">");
            }else if (stringAsValue.contains("</")){
                //Log.i ("HTML Spanner ","END TAG = "+stringAsValue);
                outputString.append(stringAsValue);
                outputString.append(">");
            }else{
                //Log.e ("HTML Spanner ","what is this ??? = "+stringAsValue);
                //Log.e ("HTML Spanner ","what is this ??? = "+stringAsValue.length());
                outputString.append(stringAsValue);
               // outputString.append(">");
            }
        }
        return outputString.toString();
    }
    private String replaceBlankSpace(String starTag){
        boolean propertyParsingStarted = false;
        StringBuilder outputString = new StringBuilder();
        outputString.append("");
        final int len = starTag.length();
        for (int i = 0; i < len; i++) {
            char currentCharacter = starTag.charAt(i);
            if ( currentCharacter == ' ') {
                if(propertyParsingStarted){
                    continue;
                }
            }else if(starTag.charAt(i) == '='){
                propertyParsingStarted = true;
            }else if(starTag.charAt(i) == ';'){
                //propertyParsingStarted = false;
            }
            outputString.append(currentCharacter);
        }
        //Log.i ("HTML Spanner ","replaceBlankSpace = "+outputString.toString());
        return outputString.toString();
    }
}
