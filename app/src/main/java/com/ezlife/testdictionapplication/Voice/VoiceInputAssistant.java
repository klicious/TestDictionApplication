package com.ezlife.testdictionapplication.Voice;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;

import com.ezlife.testdictionapplication.PracticeDictionActivity;
import com.ezlife.testdictionapplication.StringSimilarity;

import java.util.List;

/**
 * Created by Kwon on 2016-07-21.
 */

public class VoiceInputAssistant {

    public static boolean voiceInputAvailable(Context context) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        return (activities.size() != 0);
    }

    public void voiceInputDetail(Context context) {
        Intent intent = new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
        LanguageDetailsChecker checker = new LanguageDetailsChecker();
        context.sendOrderedBroadcast(intent, null, checker, null, Activity.RESULT_OK, null, null);
        Log.d("HELLO", "Android Speech API :: Language Preference :: " + checker.getLanguagePreference());
        List<String> sample = checker.getSupportedLanguages();
        for(String ret : sample) {
            Log.d("HELLO", "Android Speech API :: Supported Languages :: " + ret);
        }
    }

    /*
     * This requestcode is used to differentiate between multiple microphone-buttons on a single fragment.
     */
    private Fragment fragment;

    public VoiceInputAssistant(Fragment fragment) {
        this.fragment = fragment;
    }

    /*
     * Fire an intent to start the speech recognition activity.
     *
     * @param prompt Specify the R.string.string_id resource for the prompt-text during voice-recognition here
     */
    public void startVoiceRecognitionActivity(String text) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, text);
        /* SPEECH_REQUEST_CODE = 1001 */
        fragment.startActivityForResult(intent, 1001);
    }

}


class LanguageDetailsChecker extends BroadcastReceiver {
    private List<String> supportedLanguages;

    private String languagePreference;

    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    public void setSupportedLanguages(List<String> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    public String getLanguagePreference() {
        return languagePreference;
    }

    public void setLanguagePreference(String languagePreference) {
        this.languagePreference = languagePreference;
    }

    public LanguageDetailsChecker() {
        languagePreference = "no data";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle results = getResultExtras(true);
        if (results.containsKey(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE)) {
            languagePreference = results.getString(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE);
        }
        if (results.containsKey(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES)) {
            supportedLanguages = results.getStringArrayList(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES);
        }
    }
}