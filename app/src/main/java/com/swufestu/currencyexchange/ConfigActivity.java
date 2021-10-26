package com.swufestu.currencyexchange;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {
    private static final String TAG ="ConfigActivity";

    EditText dollarText, euroText, wonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        //获取参数
        Intent intent = getIntent();
        //获取时要对应数据类型float
        //第一个参数key必须和前一页面传递的key一致；第二个参数是获取不到时的默认值
        float dollar_rate2 = intent.getFloatExtra("dollar_rate_key", 0.0f);
        float euro_rate2 = intent.getFloatExtra("euro_rate_key", 0.0f);
        float won_rate2 = intent.getFloatExtra("won_rate_key", 0.0f);
        Log.i(TAG ,"onCreate:dollar2" + dollar_rate2);
        Log.i(TAG ,"onCreate:euro2" + euro_rate2);
        Log.i(TAG ,"onCreate:won2" + won_rate2);

        //显示获取到的汇率
        dollarText = (EditText) findViewById(R.id.dollarRate);
        euroText = (EditText) findViewById(R.id.euroRate);
        wonText = (EditText) findViewById(R.id.wonRate);
        dollarText.setText(String.valueOf(dollar_rate2));
        euroText.setText(String.valueOf(euro_rate2));
        wonText.setText(String.valueOf(won_rate2));
    }

    public void save(View btn){
        //获取新的值
        float new_dollar_rate = Float.parseFloat(dollarText.getText().toString());
        float new_euro_rate = Float.parseFloat(euroText.getText().toString());
        float new_won_rate = Float.parseFloat(wonText.getText().toString());
        Log.i(TAG, "save: new_dollar_rate=" + new_dollar_rate);
        Log.i(TAG, "save: new_euro_rate=" + new_euro_rate);
        Log.i(TAG, "save: new_won_rate=" + new_won_rate);

        //传递参数(3个参数保存到Bundle再传递（封装）)
        Intent intent = getIntent();
        Bundle bdl = new Bundle();
        bdl.putFloat("new_dollar_rate_key",new_dollar_rate);
        bdl.putFloat("new_euro_rate_key",new_euro_rate);
        bdl.putFloat("new_won_rate_key",new_won_rate);
        intent.putExtras(bdl);
        setResult(2,intent);  //第一个参数是响应代码，第二个参数可以是任意类型

        //返回到调用页面
        finish();
    }

}