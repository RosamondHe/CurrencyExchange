package com.swufestu.currencyexchange;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MainActivity";

    EditText input;
    TextView resultshow;

    float dollar_rate = 0.1547f;
    float euro_rate = 0.1320f;
    float won_rate = 182.4343f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (EditText) findViewById(R.id.rmb);
        resultshow = (TextView) findViewById(R.id.result);
    }

    public void onClick(View btn){
        String str = input.getText().toString();
        Log.i(TAG ,"click:input=" + str);

        float rmb = 0;
        float rate = 0;
        if(str.length()>0){  //判断有输入
            rmb= Float.parseFloat(str);
        }else{
            Toast.makeText(this,"请输入金额后再进行转换", Toast.LENGTH_SHORT).show();
        }
        if(btn.getId()==R.id.dollar){
            rate = dollar_rate;
        }else if(btn.getId()==R.id.euro){
            rate = euro_rate;
        }else if(btn.getId()==R.id.won){
            rate = won_rate;
        }
        float result = rate * rmb;
        Log.i(TAG ,"click:result=" + result);

        //显示结果
        resultshow.setText(String.valueOf(result));
    }

    public void open(View btn){
        //打开一个页面Activity（例如京东）
        Log.i(TAG ,"open...");
        //Intent hello = new Intent(this, SecondActivity.class);
        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jd.com"));
        startActivity(web);
    }
}