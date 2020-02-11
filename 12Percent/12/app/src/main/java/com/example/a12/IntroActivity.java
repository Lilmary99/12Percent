package com.example.a12;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.TextView;

import android.view.View;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ImageView stock = (ImageView) findViewById(R.id.egg1);
        ImageView etf = (ImageView) findViewById(R.id.egg2);
        ImageView mtf = (ImageView) findViewById(R.id.egg3);

        stock.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final Intent stockActivity = new Intent(IntroActivity.this, StockActivity.class);
                    IntroActivity.this.startActivity(stockActivity);
                }
            });

        TextView stockPercent = (TextView) findViewById(R.id.stockEgg);

        Bundle extras = getIntent().getExtras();
        String p = "";
        if (extras != null) {
            p = getIntent().getExtras().getString("PER");
            stockPercent.setText(p + " %");


            TextView t = (TextView) findViewById(R.id.growth);
            String a = getIntent().getExtras().getString("AVG");
            t.setText("Based on previous trends and models, your " + p + "% investment could increase by " + Double.parseDouble(a) * 100 + "% over a five year period.");
        }
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText myEdit = (EditText) findViewById(R.id.editText1);
                String myEditValue = myEdit.getText().toString();
                double myEditNum = Double.parseDouble(myEditValue);
                double percent =12;
                double my12Percent = (.01*(percent)) * myEditNum;

                String s = String.format("%.2f", my12Percent);

                TextView result = (TextView) findViewById(R.id.textView3);
                result.setText("$"+ s);

            }
        });

        TextView twelvePercent = findViewById(R.id.twelveClick);
        twelvePercent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView myEdit = findViewById(R.id.twelveInfo);
                myEdit.setText("It's recommended that those under 30 save 12% of their income. ");

            }
        });



    }
}
