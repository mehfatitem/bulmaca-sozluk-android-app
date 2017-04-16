package com.example.mehfa.bulmacaszlk;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.net.URLEncoder;

import android.content.DialogInterface;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import android.util.Log;

import android.graphics.Typeface;

import android.net.ConnectivityManager;

import android.support.v7.app.AlertDialog;
import android.content.Context;

import android.view.ViewGroup;
import android.content.Intent;

import android.widget.AdapterView.OnItemClickListener;

import android.view.inputmethod.InputMethodManager;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "UnsupportedEncodingException";
    private static final String SECOND_TAG = "StringBuilding & BufferedReader\", \"Error converting result ";
    public String serverUrl = "";

    public void init(){
        if(this.isNetworkConnected()){
                Button searchButton = (Button) findViewById(R.id.searchButton);
                final EditText searchText = (EditText) findViewById(R.id.searchText);
                final TextView resultText = (TextView) findViewById(R.id.resultText);
                final Spinner spinner = (Spinner) findViewById(R.id.spinner);
                final ListView resultList = (ListView) findViewById(R.id.resultList);

                String [] listItem = new String[]{"eşit" , "içeren" , "ile başlayan" , "ile biten"};

                List<String> mylist = Arrays.asList(listItem);
                ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this, R.layout.spinner_item, mylist);
                spinner.setAdapter(adp1);
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        resultList.setAdapter(null);
                        Editable textVal = searchText.getText();
                        String searchText = textVal.toString();
                        searchText = searchText.trim();
                        if (searchText.length() == 0) {
                            resultText.setText("Soru alanını boş bırakmayınız !");
                            resultText.setTextColor(Color.WHITE);
                        } else {
                            int comparePosition = spinner.getSelectedItemPosition() + 1;
                            String cmprPosition = Integer.toString(comparePosition);
                            resultText.setText("");
                            try {
                                String url = "http://mehfatitem.comxa.com/bulmaca_sozluk/webservice/server.php?operand=" + cmprPosition + "&question=" + URLEncoder.encode(searchText, "UTF-8") + "&format=json";
                                System.out.println(url);
                                MyAsyncTask mat = new MyAsyncTask();
                                mat.execute(url);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                resultList.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String value = resultList.getItemAtPosition(position).toString();
                        int query = position + 1;
                        showAlertMessage("İÇERİK " + query , value , "KAPAT" , false);
                    }
                });
        }else{
            this.showAlertMessage("Uyarı !" , "Lütfen internet bağlantınızı kontrol ediniz... !" , "TAMAM" , true);
        }
    }

    public void showAlertMessage(String title , String messageText , String buttonText , final Boolean refresh){
        AlertDialog alertMessage = new AlertDialog.Builder(this).create();
        alertMessage.setTitle(title);
        alertMessage.setMessage(messageText);
        alertMessage.setButton(AlertDialog.BUTTON_NEUTRAL, buttonText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(refresh){
                            restartActivity();
                        }
                    }
                });
        alertMessage.show();
    }

    public void restartActivity(){
        finish();
        startActivity(getIntent());
    }

    public void logLargeString(String str , String staticVal) {
        if(str.length() > 3000) {
            Log.i(staticVal, str.substring(0, 3000));
            logLargeString(str.substring(3000) , staticVal);
        } else {
            Log.i(staticVal, str); // continuation
        }
    }

    private static String replaceSpaceWithHypn(String str) {
        if (str != null && str.trim().length() > 0) {
            String patternStr = "\\s+";
            String replaceStr = "-";
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(str);
            str = matcher.replaceAll(replaceStr);
            patternStr = "\\s";
            replaceStr = "-";
            pattern = Pattern.compile(patternStr);
            matcher = pattern.matcher(str);
            str = matcher.replaceAll(replaceStr);
        }
        return str;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.init();
    }

    public class MyAsyncTask extends AsyncTask<String, String, String>{
        private MainActivity ma = new MainActivity();
        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        InputStream inputStream = null;
        String result = "";

        @Override
        protected String doInBackground(String... url) {
            String url_select = (String)url[0];
            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

            try {
                // Set up HTTP post

                // HttpClient is more then less deprecated. Need to change to URLConnection
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(url_select);
                httpPost.setEntity(new UrlEncodedFormEntity(param));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                // Read content & Log
                inputStream = httpEntity.getContent();
            } catch (UnsupportedEncodingException e1) {
                //Log.e("UnsupportedEncodingException" , e1.toString());
                logLargeString(TAG , e1.toString());
                e1.printStackTrace();
            } catch (ClientProtocolException e2) {
                Log.e("ClientProtocolException", e2.toString());
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                Log.e("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.e("IOException", e4.toString());
                e4.printStackTrace();
            }
            // Convert response to string using String Builder
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();

            } catch (Exception e) {
                logLargeString(SECOND_TAG , e.toString());
                //Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Verileriniz yükleniyor ...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    MyAsyncTask.this.cancel(true);
                }
            });
        }


        @Override
        protected void onPostExecute(String  result) {
            final ListView listView = (ListView) findViewById(R.id.resultList);
            final TextView resultText = (TextView) findViewById(R.id.resultText);
            super.onPostExecute(result);
            List answer = new ArrayList<String>();
            try {
                JSONObject obj = new JSONObject(result);
                JSONArray jArray = obj.getJSONArray("posts");
                for(int i=0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    String question = jObject.getString("question");
                    int priority = i+1;
                    String lastString = Integer.toString(priority) + ". SORU : " + question + "\n\nCEVAP : " + ma.replaceSpaceWithHypn(jObject.getString("answer"));
                    answer.add(lastString);

                } // End Loop
                if(answer.size() == 0 ) {
                    resultText.setText("Sonuç Bulunamadı!");
                    resultText.setTypeface(null, Typeface.BOLD);
                    resultText.setTextColor(Color.WHITE);
                }else{
                    resultText.setText(answer.size() + " adet kayıt listelendi.");
                    resultText.setTypeface(null, Typeface.BOLD);
                    resultText.setTextColor(Color.WHITE);
                }
                this.progressDialog.dismiss();
            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }
            listView.setAdapter( new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item,  answer){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = ((TextView) view.findViewById(android.R.id.text1));
                    textView.setTextColor(Color.WHITE);
                    textView.setMinHeight(0); // Min Height
                    textView.setMinimumHeight(0); // Min Height
                    textView.setHeight(100);
                    return view;
                }
            });
            this.progressDialog.dismiss();
        }
    }
}
