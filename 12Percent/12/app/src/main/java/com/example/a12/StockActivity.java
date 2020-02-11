package com.example.a12;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StockActivity extends AppCompatActivity {
    private Button saveButt;
    private double tmp = 0.0;
    //final Intent introActivity = new Intent(StockActivity.this, IntroActivity.class);
    private int totalTextViews = 10, numViewTracker = 0;
    private TextView[] myTextViews = new TextView[totalTextViews];
    private LinearLayout verLayout;
    private String TCKR;
    private SeekBar bar1;
    private int accumulatedPercentage = 0;
    private int percentage = 0;
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        verLayout = findViewById(R.id.percenty);
        bar1 = findViewById(R.id.seekBar1);
        bar1.setOnSeekBarChangeListener(new onSeekBarChangeListener());
        final EditText ET = (EditText) findViewById(R.id.user_in);
        Button B1 = (Button) findViewById(R.id.add);
        B1.setOnClickListener(new View.OnClickListener() {
            int numberOfTickers = 1;
            public void onClick(View v) {
                TextView stockNpercents = (TextView) findViewById(R.id.stocksAndPercentage);
                stockNpercents.append(numberOfTickers +". " + ET.getText() + "\n");
                numberOfTickers++;
                TCKR = ET.getText().toString();
                new MyTask().execute();

                TextView rowTextView = new TextView(getApplicationContext());
                accumulatedPercentage += percentage;
                rowTextView.setText("Percentage: " + percentage);
                verLayout.addView(rowTextView);
                myTextViews[numViewTracker] = rowTextView;
            } });

        //WHEN COMPLETE BUTTON IS CLICKED
        saveButt = findViewById(R.id.completeButton);
        saveButt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                int savedValue = percentage;
                //ONCE COMPLETE IS CLICKED, UPDATE
                final Intent introActivity = new Intent(StockActivity.this, IntroActivity.class);

                //ADD ALL PERCENTAGES AND SEND THEM TO INTRO SCREEN
                String per = Integer.toString(accumulatedPercentage);
                introActivity.putExtra("PER", per);

                //SEND AVGS BACK TO INTRO SCR
                String a = Double.toString(tmp);
                introActivity.putExtra("AVG", a);
                StockActivity.this.startActivity(introActivity);

            }
        });
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            //must be changed later
            String urlString = "https://www.blackrock.com/tools/hackathon/portfolio-analysis?calculateExpectedReturns=true&calculateExposures=true"
                    +"&calculatePerformance=true&calculateRisk=true&endDate=20200101&fullCalculation=true&includeAllBreakdowns=false&positions="+TCKR+"~100&startDate=20150101";
            try {
                URL url = new URL(urlString);
                URLConnection urlConnection = url.openConnection();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                //converts HTTP request to an json string
                String inputLine;
                String json ="";
                while ((inputLine = bufferedReader.readLine()) != null) {
                    json += inputLine;
                }

                try {
                    JSONObject obj = new JSONObject(json);
                    JSONArray p = obj.getJSONObject("resultMap").getJSONArray("PORTFOLIOS");
                    JSONArray portfolios = p.getJSONObject(0).getJSONArray("portfolios");
                    JSONObject returnsMap = portfolios.getJSONObject(0).getJSONObject("returns").getJSONObject("returnsMap");

                    //take average of "oneMonth" fields

                    String dates[] = new String[returnsMap.length()];
                    Iterator<String> keys = returnsMap.keys();
                    int k = 0;
                    while(keys.hasNext()) {
                        String key = keys.next();
                        if (returnsMap.get(key) instanceof JSONObject) {
                            dates[k] = key;
                            //System.out.println(dates[k]);
                        }
                        k++;
                    }

                    double sum = 0;
                    for (int i = 0; i <= returnsMap.length()-1; i++) {
                        String s[] = returnsMap.getJSONObject(dates[i]).toString().split(",");
                        for (int y = 0; y <= s.length-1; y++){
                            if(s[y].length() >= 12) {
                                if ((s[y].substring(0, 11).equals("\"oneMonth\":")) && (!s[y].substring(12).equals(""))) {
                                    System.out.println(Double.parseDouble(s[y].substring(12)));
                                    sum += Double.parseDouble(s[y].substring(12));
                                }
                            }
                        }
                    }
                    double avg = sum/returnsMap.length();
                    System.out.println("AVG: " + avg);
                    tmp += avg;
                  /**  saveButt = findViewById(R.id.completeButton);
                    saveButt.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v)
                        {
                            int savedValue = percentage;
                            //ONCE COMPLETE IS CLICKED, UPDATE
                            final Intent introActivity = new Intent(StockActivity.this, IntroActivity.class);

                            //ADD ALL PERCENTAGES AND SEND THEM TO INTRO SCREEN
                            String per = Integer.toString(accumulatedPercentage);
                            introActivity.putExtra("PER", per);

                            //SEND AVGS BACK TO INTRO SCR
                            String a = Double.toString(tmp);
                            introActivity.putExtra("AVG", a);
                            StockActivity.this.startActivity(introActivity);

                        }
                    });*/

                }
                catch(JSONException io){
                     io.printStackTrace();
                }
                bufferedReader.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
            return null;
        }
    }
    private class onSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener
    {
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        public void onStopTrackingTouch(SeekBar seekBar) { }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            //ticker1.setText("Ticker: " + progress + "%");
            percentage = progress;
        }
    }

}
