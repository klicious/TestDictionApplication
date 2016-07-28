package com.ezlife.testdictionapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.telecom.RemoteConference;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * Created by Kwon on 2016-07-15.
 */
public class PracticeDictionActivity extends AppCompatActivity {

    private static final int TTS_DATA_CHECK = 1000;
    private static final int SPEECH_REQUEST_CODE = 1001;
    private static final int SPEECH_DATA_CHECK = 1002;

    TextView sampleText;
    TextView translation;
    EditText userInput;
    TextView resultText;
    String speech;

    boolean isEnglish = false;

    TextToSpeech tts;

    ArrayList<String> script;
    ArrayList<ArrayList<String>> eachScript;
    String[] cast = {"Rachel", "Ross", "Monica", "Joey", "Phoebe", "Chandler", "Everyone"};
    int castIndex;
    int[] scriptIndex, scriptIndexMax;

    public enum friends {
        Rachel(0), Ross(1), Monica(2), Joey(3), Phoebe(4), Chandler(5), Everyone(6);

        private int frdNo;

        private static Map<Integer, friends> map = new HashMap<Integer, friends>();

        static {
            for (friends frdEnum : friends.values()){
                map.put(frdEnum.frdNo, frdEnum);
            }
        }

        private friends(final int frd) { frdNo = frd; }

        public static friends valueOf(int frdNo) {
            return map.get(frdNo);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practicediction);

        Intent intent = getIntent();

        sampleText = (TextView) findViewById(R.id.sampleContentTextView);
        translation = (TextView) findViewById(R.id.translationContentTextView);
        userInput = (EditText) findViewById(R.id.userInputContentEditText);
        resultText = (TextView) findViewById(R.id.resultTextView);


        script = new ArrayList<String>();
        ArrayList<String> fullScript = new ArrayList<String>();
        ArrayList<String> Rachel = new ArrayList<String>();
        ArrayList<String> Ross = new ArrayList<String>();
        ArrayList<String> Monica = new ArrayList<String>();
        ArrayList<String> Joey = new ArrayList<String>();
        ArrayList<String> Phoebe = new ArrayList<String>();
        ArrayList<String> Chandler = new ArrayList<String>();
        eachScript = new ArrayList<ArrayList<String>>();

        try {
            fullScript = OpenFile("Drama/Friends/Season1/Friendstranscript101.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        eachScript.add(Rachel);
        eachScript.add(Ross);
        eachScript.add(Monica);
        eachScript.add(Joey);
        eachScript.add(Phoebe);
        eachScript.add(Chandler);
        eachScript.add(fullScript);

        for(String ret : fullScript) {
            boolean isCast = false;

            String[] parts = ret.split(":");

            for(String name : cast) {
                if (name.equals(parts[0])){
                    isCast = true;

                    eachScript.get(
                            friends.valueOf(name).ordinal())
                            .add(parts[1].replaceAll("\\(.*\\)", "").replaceAll("\\[.*\\]", "").trim());
                    break;
                }

            }

        }

        Comparator<String> x =  new Comparator<String>() {
            @Override
            public int compare (String o1, String o2){
                if (o1.length() > o2.length())
                    return 1;
                if (o1.length() < o2.length())
                    return -1;
                return 0;
            }
        };
        scriptIndex = new int[eachScript.size()];
        scriptIndexMax = new int[eachScript.size()];

        Log.d("HELLO", "eachScript.size() = " + eachScript.size() + " :: scriptIndex.length() = " + scriptIndex.length + " :: scripIndexMax.length() = " + scriptIndexMax.length);

        for (int i = 0 ; i < eachScript.size(); i++){
            Collections.sort(eachScript.get(i), x);
            scriptIndex[i] = 0;
            scriptIndexMax[i] = eachScript.get(i).size();
            Log.d("HELLO", "eachScript.get(" + i + ").size() = " + eachScript.get(i).size());
        }

        init();

    }

    private void init () {
        ttsInit();
        if (voiceInputAvailable()) {
            voiceInputDetail();
        } else {
            Log.d("HELLO", "VOICE INPUT UNAVAILABLE");
        }
        setFavCharSpinner();
    }

    private void ttsInit() {
        //Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        //startActivityForResult(intent, TTS_DATA_CHECK);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                isEnglish = true;
                tts.setLanguage(Locale.ENGLISH);
            }
        });
    }

    private void recognizeSpeech() {
        // 2 Choices, "RecognizerIntent" , "by creating an instance of SpeechRecognizer"
        // #1 RecognizerIntent
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Specify language model
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Specify how many results to receive. Results listed in order of confidence
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        // Set Language for speech recognition
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        //Start Listening
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    /*
     * TODO : MAKE A FUNCTION THAT CHANGES ALL LANGUAGE SETTINGS WHEN "SWAPPING" BETWEEN THE LANGUAGES
      * under recognizeSpeech() RecognizerIntent.EXTRA_LANGUAGE, "en-US" <-> "kor"
      * isEnglish != isEnglish
      * tts language settings switch
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TTS_DATA_CHECK :
                Intent installData = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installData);
                break;

            case SPEECH_REQUEST_CODE :
                if (data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speech = "No Data Input";
                    if (result.size() > 0) {
                        speech = result.get(0);
                    }
                } else {
                    speech = "No Data Retrieved from voice recognition";
                }
                userInput.setText(speech);
                String result = StringSimilarity.getSimilarity(sampleText.getText().toString(), speech);
                result = "Result = " + result + "\n Speech = " + speech;
                resultText.setText(result);
                break;

            default :
        }
    }

    private void doSpeak(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(text);
        } else {
            ttsUnder20(text);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        final HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
        //tts.shutdown();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        final String utteranceId=this.hashCode() + "";
        Log.d("HELLO", "This is the text of Speech :: " + text);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        //tts.shutdown();
    }

    private ArrayList<String> OpenFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(path), "UTF-8"));

        ArrayList<String> lines = new ArrayList<String>();

        String line;
        while((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();
        return lines;

    }

    public void onReadSampleTextBtnClicked(View v) {
        String str = sampleText.getText().toString();
        tts.setLanguage(Locale.ENGLISH);
        doSpeak(str);
    }

    public void onReadTranslationTextBtnClicked(View v) {
        String str = translation.getText().toString();
        tts.setLanguage(Locale.KOREA);
        doSpeak(str);
    }

    public void onReadUserInputTextBtnClicked(View v) {
        String str = userInput.getText().toString();
        doSpeak(str);
    }

    public void onSwapBtnClicked(View v) {
        isEnglish = !isEnglish;
        showScripts();

    }

    public void onCompleteBtnClicked(View v) {
        String result = StringSimilarity.getSimilarity(sampleText.getText().toString(), userInput.getText().toString());

        startVoiceRecognitionActivity(sampleText.getText().toString());
        resultText.setText("Result = " + result + " :: Speech = " + speech);

    }

    public void onSpeakBtnClicked(View v) {
        startVoiceRecognitionActivity(sampleText.getText().toString());
    }

    public void onNextBtnClicked(View v) {
        nextIndex();
        showScripts();
    }

    private void showScripts() {
        if(isEnglish) {
            sampleText.setText(eachScript.get(castIndex).get(scriptIndex[castIndex]));
            // TODO :: TRANSLATION TEXT e.g) translationText.setText(eachScriptTranslation.get(castIndex).get(scriptIndex[castIndex]));
        } else {
            // TODO :: SWAP CONTENTS
            // sampleText.setText(eachScriptTranslation.get(castIndex).get(scriptIndex[castIndex]));
            // translationText.setText(eachScript.get(castIndex).get(scriptIndex[castIndex]));
        }
    }

    private void nextIndex(){
        if (scriptIndex[castIndex] < scriptIndexMax[castIndex] - 1) {
            scriptIndex[castIndex]++;
        } else {
            castIndex++;
        }

        if (castIndex >= scriptIndex.length) {
            castIndex = 0;
        }
    }

    // generates random number from min(inclusive) to max(exclusive)
    private int rng(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min) + min;
    }

    private void setFavCharSpinner() {
        /*
         * Favorite Character Spinner SETUP
         */

        Spinner favCharSpinner = (Spinner) findViewById(R.id.favCharSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Friends_casts,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        favCharSpinner.setAdapter(adapter);

        favCharSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String favChar = (String) parent.getItemAtPosition(position);
                castIndex = friends.valueOf(favChar).ordinal();
                sampleText.setText(eachScript.get(castIndex).get(scriptIndex[castIndex]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /*
     * VOICE INPUT ACTIVITY/ASSISTANT
     */
    private boolean voiceInputAvailable() {
        PackageManager pm = getApplicationContext().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        return (activities.size() != 0);
    }

    private void voiceInputDetail() {
        Intent intent = new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
        LanguageDetailsChecker checker = new LanguageDetailsChecker();
        getApplicationContext().sendOrderedBroadcast(intent, null, checker, null, Activity.RESULT_OK, null, null);
        Log.d("HELLO", "Android Speech API :: Language Preference :: " + checker.getLanguagePreference());
        if (!checker.getLanguagePreference().equals("no data")){
            List<String> sample = checker.getSupportedLanguages();
            Log.d("HELLO", "Sample length = " + sample.size());
            for(String ret : sample) {
                Log.d("HELLO", "Android Speech API :: Supported Languages :: " + ret);
            }
        }
    }

    /*
     * Fire an intent to start the speech recognition activity.
     *
     * @param prompt Specify the R.string.string_id resource for the prompt-text during voice-recognition here
     */
    private void startVoiceRecognitionActivity(String text) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, text);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        /* SPEECH_REQUEST_CODE = 1001 */
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
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