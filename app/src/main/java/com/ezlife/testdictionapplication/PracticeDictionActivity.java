package com.ezlife.testdictionapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ezlife.testdictionapplication.Database.DatabaseManager;


/**
 * Created by Kwon on 2016-07-15.
 *
 * Started using git-laptop TODAY() #2016 08 13
 */
public class PracticeDictionActivity extends AppCompatActivity {

    private static final int SMALL_CHANGE = 12345;

    private static final int TTS_DATA_CHECK = 1000;
    private static final int SPEECH_REQUEST_CODE = 1001;
    private static final int SPEECH_DATA_CHECK = 1002;
    private static final int SETUP_WINDOW = 1003;

    public static final String CATEGORY = "category";
    public static final String PROGRAM = "program";
    public static final String SEASON = "season";
    public static final String EPISODE = "episode";

    DatabaseManager dbManager;
    Script curScript;

    TextView sampleText;
    TextView translation;
    TextView userInput;
    TextView resultText;
    LinearLayout analysisLayout;

    String speech;
    Script display;

    boolean isEnglish;
    boolean showAnalysis;

    TextToSpeech tts;

    ArrayList<String> script;
    ArrayList<ArrayList<String>> eachScript;
    ArrayList<Script> theScript;
    List<Integer> shuffle;
    Iterator scriptIterator;
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


        /*script = new ArrayList<String>();
        ArrayList<String> fullScript = new ArrayList<String>();
        ArrayList<String> Rachel = new ArrayList<String>();
        ArrayList<String> Ross = new ArrayList<String>();
        ArrayList<String> Monica = new ArrayList<String>();
        ArrayList<String> Joey = new ArrayList<String>();
        ArrayList<String> Phoebe = new ArrayList<String>();
        ArrayList<String> Chandler = new ArrayList<String>();
        eachScript = new ArrayList<ArrayList<String>>();*/

        /*
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
        */

        init();
/*         int index = 0;
        for(Script ret : theScript) {
            Log.d("HELLO", "index = " + index);
            Log.d("HELLO", "English = " + ret.getEnglish());
            Log.d("HELLO", "Korean = " + ret.getKorean());

        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.script_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_script_menu :
                callMainActivity();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ttsInit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }

    @Override
    protected void onStop() {
        super.onStop();
        tts.stop();
    }

    private void init () {
        setTheMenuBar();

        initValues();

        initDB();

        ttsInit();

        voiceInit();

        styleInit();

        languageInit();

        getScript();
    }

    private void initDB() {
        dbManager = new DatabaseManager(getApplicationContext());
        dbManager.openDatabase();
        //dbManager.openDatabase(username);
        //curScript = dbManager.getLastScript();
    }

    private void setTheMenuBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.menu_title);
        setSupportActionBar(myToolbar);
    }

    private void initValues() {
        curScript = new Script();
        curScript.setUsername("default");
        curScript.setCategory("Drama");
        curScript.setProgram("Friends");
        curScript.setSeason(1);
        curScript.setEpisode(1);
        curScript.setLineNumber(0);
        curScript.setScore(0);

        sampleText = (TextView) findViewById(R.id.sampleContentTextView);
        translation = (TextView) findViewById(R.id.translationContentTextView);
        userInput = (TextView) findViewById(R.id.userInputContentEditText);
        resultText = (TextView) findViewById(R.id.resultTextView);
        analysisLayout = (LinearLayout) findViewById(R.id.resultAnalysisLayout);

        theScript = new ArrayList<Script>();

        showAnalysis = false;

    }

    private void languageInit() {
        isEnglish = true;
        setLanguage(isEnglish);
    }

    private void setLanguage(boolean isEng) {
        if(isEng) {
            tts.setLanguage(Locale.ENGLISH);
        } else {
            tts.setLanguage(Locale.KOREAN);
        }
    }

    private void styleInit() {
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));
        sampleText.setTypeface(font);
        translation.setTypeface(font);
        userInput.setTypeface(font);
        resultText.setTypeface(font);
        analysisLayout.setVisibility(LinearLayout.INVISIBLE);
    }

    private void getScript() {
        ArrayList<String> fullScript = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        theScript.clear();
        sb.append(curScript.getCategory());
        sb.append("/");
        sb.append(curScript.getProgram());
        if (curScript.getCategory().equals("Drama")) {
            sb.append("/");
            sb.append("Season");
            sb.append(curScript.getSeasonString());
            sb.append("/");
            sb.append(curScript.getProgram());
            sb.append("_");
            sb.append("S");
            sb.append(curScript.getSeasonString());
            sb.append("_");
            sb.append("E");
            sb.append(curScript.getEpisodeString());
        }
        sb.append(".txt");

        String path = sb.toString();

        try {
            fullScript = OpenFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String english = "", korean = "";
        boolean flag = true;
        for (String ret : fullScript) {
            if(flag) {
                flag = false;
                continue;
            }
            if (english == "") {
                english = ret;
            } else if(korean == "") {
                korean = ret;
                theScript.add(new Script(english, korean));
                english = "";
                korean = "";
            } else {
                System.out.println(english + " :: " + korean + " :: " + ret);
            }
        }
        shuffleList();
/*
        int index = 0;
        Log.d("HELLO", "the length of THE SCRIPT : " + theScript.size());
        for(Script ret : theScript) {
            Log.d("HELLO", "index = " + index);
            Log.d("HELLO", "English = " + ret.getEnglish());
            Log.d("HELLO", "Korean = " + ret.getKorean());

        }*/
    }

    private void voiceInit() {
        if (voiceInputAvailable()) {
            voiceInputDetail();
        } else {
            Log.d("HELLO", "VOICE INPUT UNAVAILABLE");
        }
    }

    private void shuffleList() {
        shuffle = new ArrayList<Integer>();
        for(int i = 0; i < theScript.size(); i++) {
            shuffle.add(i);
        }

        Collections.shuffle(shuffle);

        scriptIterator = shuffle.iterator();

        nextIndex();
        showScripts();
    }

    private void ttsInit() {
        //Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        //startActivityForResult(intent, TTS_DATA_CHECK);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                setLanguage(isEnglish);
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
                StringSimilarity str = new StringSimilarity();
                //String result = str.getSimilarity(sampleText.getText().toString(), speech, isEnglish);
                double resultDouble = str.getSimilarityDouble(sampleText.getText().toString(), speech, isEnglish);
                //result = "Result = " + result + "\n Speech = " + speech;
                int resultInt = (int) (resultDouble * 100);
                //String result = String.format("Your Diction Accuracy is : %.3f", resultDouble);
                String result = resultInt + "%";
                resultText.setText(result);

                diff_match_patch dmp = new diff_match_patch();

                if(resultInt > 80) {
                    // Green
                    resultText.setTextColor(Color.rgb(76,202,80));
                } else if(resultInt > 60) {
                    // Yellow
                    resultText.setTextColor(Color.rgb(255,193,7));
                } else {
                    // Deep Orange
                    resultText.setTextColor(Color.rgb(255,87,34));
                }

                /*
                 * SET WEBVIEW
                 */
                LinkedList<diff_match_patch.Diff> diff = dmp.diff_main(speech.toLowerCase(), sampleText.getText().toString().toLowerCase());
                dmp.diff_cleanupSemantic(diff);
                WebView webView = (WebView) findViewById(R.id.webView_difference_analysis);
                // Set background Transparent
                webView.setBackgroundColor(Color.TRANSPARENT);
                webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
                // Set font size
                final WebSettings webSettings = webView.getSettings();
                Resources res = getResources();
                float fontSize = res.getDimension(R.dimen.webViewTxtSize);
                webSettings.setDefaultFontSize((int) fontSize);

                webView.loadDataWithBaseURL("file:///android_asset/", dmp.diff_prettyHtml(diff), "text/html", "utf-8", null);


                break;

            case SETUP_WINDOW :
                if (data != null) {
                    curScript.setCategory(data.getStringExtra(CATEGORY));
                    curScript.setProgram(data.getStringExtra(PROGRAM));
                    if (curScript.getCategory().equals("Drama")) {
                        curScript.setSeasonString(data.getStringExtra(SEASON));
                        curScript.setEpisodeString(data.getStringExtra(EPISODE));
                    }
                    // TODO : Retrieve the last active lineNumber from database and update curScript.setLineNumber(#get the last activity from DB#);
                    Log.d("HELLO", "c/p/s/e = " + curScript.getCategory() + curScript.getProgram() + curScript.getSeasonString() + curScript.getEpisodeString());
                    getScript();

                }

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
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        final String utteranceId=this.hashCode() + "";
        Log.d("HELLO", "This is the text of Speech :: " + text);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
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

    private void callMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(CATEGORY, curScript.getCategory());
        intent.putExtra(PROGRAM, curScript.getProgram());
        if (curScript.getCategory().equals("Drama")) {
            intent.putExtra(SEASON, curScript.getSeasonString());
            intent.putExtra(EPISODE, curScript.getEpisodeString());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, SETUP_WINDOW);
    }
    public void onSetUpBtnClicked(View v) {
        callMainActivity();
    }

    public void onShowAnalysisBtnClicked(View v) {
        showAnalysis = !showAnalysis;
        if(showAnalysis) {
            showAnalysisLayout();
        } else {
            hideAnalysisLayout();
        }
    }

    private void showAnalysisLayout() {
        analysisLayout.setVisibility(LinearLayout.VISIBLE);
    }

    private void hideAnalysisLayout() {
        analysisLayout.setVisibility(LinearLayout.INVISIBLE);
    }

    public void onReadSampleTextBtnClicked(View v) {
        String str = sampleText.getText().toString();
        setLanguage(isEnglish);
        doSpeak(str);
    }

    public void onReadTranslationTextBtnClicked(View v) {
        String str = translation.getText().toString();
        setLanguage(!isEnglish);
        doSpeak(str);
    }

    public void onReadUserInputTextBtnClicked(View v) {
        String str = userInput.getText().toString();
        setLanguage(isEnglish);
        doSpeak(str);
    }

    public void onSwapBtnClicked(View v) {
        isEnglish = !isEnglish;
        setLanguage(isEnglish);
        showScripts();

    }

    public void onCompleteBtnClicked(View v) {
        StringSimilarity str = new StringSimilarity();
        String result = str.getSimilarity(sampleText.getText().toString(), userInput.getText().toString(), isEnglish);

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
            sampleText.setText(display.getEnglish());
            translation.setText(display.getKorean());
            // TODO :: TRANSLATION TEXT e.g) translationText.setText(eachScriptTranslation.get(castIndex).get(scriptIndex[castIndex]));
        } else {
            // TODO :: SWAP CONTENTS
            sampleText.setText(display.getKorean());
            translation.setText(display.getEnglish());
        }
    }

    private void nextIndex(){
        /*if (scriptIndex[castIndex] < scriptIndexMax[castIndex] - 1) {
            scriptIndex[castIndex]++;
        } else {
            castIndex++;
        }

        if (castIndex >= scriptIndex.length) {
            castIndex = 0;
        }*/

        if (scriptIterator.hasNext()) {
            curScript.setLineNumber((int) scriptIterator.next());
            display = theScript.get(curScript.getLineNumber());
        }
    }

    // generates random number from min(inclusive) to max(exclusive)
    private int rng(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min) + min;
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
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, new Long(5000));
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(5000));
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(3000));
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, text);
        String language;
        if(isEnglish) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        } else {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN);
        }
        intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"en"});

        /* SPEECH_REQUEST_CODE = 1001 */
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    private void addScriptToDB(Script script) {

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