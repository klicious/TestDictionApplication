package com.ezlife.testdictionapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LinearLayout categoryLayout, programLayout, seLayout;
    Button completeBtn;
    ArrayAdapter<CharSequence> adapter;

    String category, program, season, episode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                *//*tts.setLanguage(Locale.ENGLISH);
                Set<Locale> test = tts.getAvailableLanguages();
                for(Locale a : test) {
                    String ret = a.toString();
                    Log.d("HELLO", "List of Available Languages for TTS :: " + ret);

                }
                Log.d("HELLO", "Default Language is :: " + tts.getDefaultEngine().toString());
                Log.d("HELLO", "Default Language is :: " + tts.getDefaultLanguage().toString());
                String str = "How can I add the damn language?";
                tts.setLanguage(Locale.UK);
                tts.speak(str, TextToSpeech.QUEUE_ADD, null, null);*//*
            }
        });*/

        init();

    }

    private void init() {
        Intent intent = getIntent();
        category = intent.getStringExtra(PracticeDictionActivity.CATEGORY);
        program = intent.getStringExtra(PracticeDictionActivity.PROGRAM);
        if(category.equals("Drama")){
            season = intent.getStringExtra(PracticeDictionActivity.SEASON);
            episode = intent.getStringExtra(PracticeDictionActivity.EPISODE);
        }

        setCategorySpinner();
        categoryLayout = (LinearLayout) findViewById(R.id.categoryLayout);
        programLayout = (LinearLayout) findViewById(R.id.programLayout);
        seLayout = (LinearLayout) findViewById(R.id.seLayout);

        programLayout.setVisibility(View.INVISIBLE);
        seLayout.setVisibility(View.INVISIBLE);
    }

    public void onCompleteBtnClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), PracticeDictionActivity.class);

        intent.putExtra(PracticeDictionActivity.CATEGORY, category);
        intent.putExtra(PracticeDictionActivity.PROGRAM, program);
        if (category.equals("Drama")){
            intent.putExtra(PracticeDictionActivity.SEASON, season);
            intent.putExtra(PracticeDictionActivity.EPISODE, episode);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setCategorySpinner() {
        /*
         * Category Spinner SETUP
         */

        Spinner categorySpinner = (Spinner) findViewById(R.id.categorySpinner);

        adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Category_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);

        if(!category.equals(null)) {
            int spinnerPosition = adapter.getPosition(category);
            categorySpinner.setSelection(spinnerPosition);
        }

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setScriptSpinner((String) parent.getItemAtPosition(position));
                category = (String) parent.getItemAtPosition(position);
                if(!category.equals(getString(R.string.cat_expressions))) {
                    programLayout.setVisibility(View.VISIBLE);
                } else {
                    programLayout.setVisibility(View.INVISIBLE);
                }
                if(category.equals("Drama")){
                    seLayout.setVisibility(View.VISIBLE);
                } else {
                    seLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setScriptSpinner(String category) {
        /*
         * Script Spinner SETUP
         */

        int resId;
        switch (category) {
            case "Drama" :
                resId = R.array.Drama_array;
                break;
            case "Movies" :
                resId = R.array.Movies_array;
                break;
            default:
                resId = R.array.Category_array;
        }

        Spinner scriptSpinner = (Spinner) findViewById(R.id.scriptSpinner);

        adapter = ArrayAdapter.createFromResource(
                this,
                resId,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        scriptSpinner.setAdapter(adapter);

        if(!program.equals(null)) {
            int spinnerPosition = adapter.getPosition(program);
            scriptSpinner.setSelection(spinnerPosition);
        }

        scriptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                program = (String) parent.getItemAtPosition(position);
                setSeasonSpinner(program);
                setEpisodeSpinner(program);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setSeasonSpinner(String program){

        switch (program) {
            case "Friends" :
                break;
            default :

        }

        Spinner seasonSpinner = (Spinner) findViewById(R.id.seasonSpinner);

        List<String> list = new ArrayList<String>();

        for(int i = 0; i < 1; i++) {
            int a = i +1;
            String value;
            if (a < 10) {
                value = "0" + a;
            } else {
                value = String.valueOf(a);
            }
            list.add(value);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item, list
        );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        seasonSpinner.setAdapter(dataAdapter);
/*

        if(!season.equals(null)) {
            int spinnerPosition = adapter.getPosition(season);
            seasonSpinner.setSelection(spinnerPosition);
        }
*/

        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                season = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setEpisodeSpinner(String program){

        switch (program) {
            case "Friends" :
                episode = String.valueOf(16);
                break;
            default :
                episode = String.valueOf(10);
        }

        Spinner episodeSpinner = (Spinner) findViewById(R.id.episodeSpinner);

        List<String> list = new ArrayList<String>();

        for(int i = 0; i < Integer.valueOf(episode); i++) {
            int a = i +1;
            String value;
            if (a < 10) {
                value = "0" + a;
            } else {
                value = String.valueOf(a);
            }
            list.add(value);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item, list
        );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        episodeSpinner.setAdapter(dataAdapter);
/*

        if(!episode.equals(null)) {
            int spinnerPosition = adapter.getPosition(episode);
            episodeSpinner.setSelection(spinnerPosition);
        }
*/

        episodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                episode = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
