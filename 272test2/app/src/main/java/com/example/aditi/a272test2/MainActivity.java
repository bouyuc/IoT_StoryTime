package com.example.aditi.a272test2;

        import java.io.IOException;
        import java.io.InputStream;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.util.ArrayList;

        import java.util.List;
        import java.util.Locale;
        import java.util.concurrent.ExecutionException;

        import android.app.Activity;
        import android.content.ActivityNotFoundException;
        import android.content.Intent;
        import android.graphics.Color;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.speech.RecognizerIntent;
        import android.support.v4.content.ContextCompat;
        import android.view.View;
        import android.webkit.WebView;
        import android.webkit.WebViewClient;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import javax.json.Json;
        import javax.json.JsonArray;
        import javax.json.JsonObject;
        import javax.json.JsonReader;

        import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
        import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.*;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import okhttp3.MediaType;
        import okhttp3.MultipartBody;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.RequestBody;
        import okhttp3.Response;

        import static java.security.AccessController.getContext;

public class MainActivity extends Activity {
    private final int SPEECH_RECOGNITION_CODE = 1;
    private TextView txtOutput;
    private ImageButton btnMicrophone;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private WebView back;
    private Button btn;
    private EditText tmpIn;
    private String api_path = "http://api.giphy.com/v1/gifs/search?&api_key=dc6zaTOxFJmzC&limit=1&rating=y";
    private String query = "&q=";
    OkHttpClient client = new OkHttpClient();
    String text;
    /*
        Param1: Type of var to send to Task class
        Param2: Name of the method that shows progress
        Param3: Return var type
     */
    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
        System.out.println("PRINTING COLOR ");

    }
    private class doStuff extends AsyncTask<String, Void, String>{

        @Override

        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//            RequestBody requestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("{\"hue\" :50000}","")
//                    .build();

            RequestBody requestBody = RequestBody.create(JSON, "{\"hue\" :10000}");


            Request request = null;
            try {
                request = new Request.Builder()
                        .url("http://192.168.43.165/api/wI1ctifgI71yETIBvGZfa7ercS5BIetYxqxfZuQL/lights/4/state")
//                        .method("PUT", requestBody)
                        .put(requestBody)
                        .build();
                Response resp = client.newCall(request).execute();
                System.out.println(resp.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
    private class WatsonUnderstandTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                    NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
                    "9af3dd26-8450-48fb-8878-c1b697a7330e",
                    "esyU4rPHDcMN"
            );
            String toAnalyze =text;
            KeywordsOptions keywords= new KeywordsOptions.Builder()
                    .sentiment(true)
                    .emotion(true)
                    .limit(3)
                    .build();

            Features features = new Features.Builder()
                    .keywords(keywords)
                    .build();

            AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                    .text(toAnalyze)
                    .features(features)
                    .build();

            AnalysisResults response = service
                    .analyze(parameters)
                    .execute();
         //   System.out.println(response.getKeywords().toString());
           // System.out.println(response.getEmotion().toString());

            return response.getKeywords().toString();
        }

        protected void onPostExecute(String response) {
         //   System.out.println(response);

            try {
                JSONArray arr = new JSONArray(response);
                for (int i = arr.length() - 1; i >= 0; i--){
                    JSONObject object = arr.getJSONObject(i);
                    new DownloadTask().execute(api_path + query + object.getString("text")); //emotion

                    RelativeLayout tvCard = (RelativeLayout) findViewById(R.id.myid);
                    tvCard.setBackgroundColor(android.graphics.Color.CYAN);
                    
/*****this changes the background color when watson finishes processing***********/
                    LinearLayout lLayout = (LinearLayout) findViewById(R.id.linearLayout);
                    lLayout.setBackgroundColor(Color.CYAN);
                    WebView lLayout1 = (WebView) findViewById(R.id.bckgrnd);
                    lLayout1.setBackgroundColor(android.graphics.Color.CYAN);
                }
                new doStuff().execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//
//    String post(String url, String json) throws IOException {
//        RequestBody body = RequestBody.create(JSON, json);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//        Response response = client.newCall(request).execute();
//        return response.body().string();
//    }
    private class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params){
            String gif_url ="";
            InputStream is;
            URL url;
            try {
                url = new URL(params[0]);
                is = url.openStream();
                JsonReader rdr = Json.createReader(is);
                JsonObject obj = rdr.readObject();
                JsonArray results = obj.getJsonArray("data");
                JsonObject result = results.getValuesAs(JsonObject.class).get(0);
                gif_url = result.getJsonObject("images").getJsonObject("original").getString("mp4");

            }catch (Exception e){
                e.printStackTrace();
            }
            return gif_url;
        }

        @Override
        protected void onPostExecute(String gif_url){
            super.onPostExecute(gif_url);
            try {
                back.getSettings().setJavaScriptEnabled(true);
                back.getSettings().setDomStorageEnabled(true);
                back.setWebViewClient(new WebViewClient());
                back.loadUrl(gif_url);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtOutput = (TextView) findViewById(R.id.txt_output);
        btnMicrophone = (ImageButton) findViewById(R.id.btn_mic);

        back = (WebView) findViewById(R.id.bckgrnd);
        //btn = (Button) findViewById(R.id.tmpBtn);
        tmpIn = (EditText) findViewById(R.id.tempinput);

        btnMicrophone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

               // setActivityBackgroundColor(0xFF00FF00);

                startSpeechToText();
               // text = tmpIn.getText().toString();
               //txtOutput.setText(text);
                //if text contains "mother" replace with daughter
                 //text = "happy";
            }
        });

       /* btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tmpIn.getText().toString();
                txtOutput.setText(text);
                //if text contains "mother" replace with daughter
                new WatsonUnderstandTask().execute(text);
            }
        });*/
    }
    /**
     * Start speech to text intent. This opens up Google Speech Recognition API dialog box to listen the speech input.
     * */
    private void startSpeechToText() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
         intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported on this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback for speech recognition activity
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    text = result.get(0);
                   // text = "mary had a lamb";
                    //txtOutput.setText(text);
                    tmpIn.setText(text);
                    new WatsonUnderstandTask().execute(text);

          /*         try {
                        new DownloadTask().execute(api_path + query + tmpIn.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
                break;
            }
        }
    }
}