package com.swufestu.currencyexchange;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RateActivity extends AppCompatActivity {
    private static final String TAG ="RateActivity";

    EditText input;
    TextView resultshow;

    //初始汇率
    float dollar_rate = 0.1547f;
    float euro_rate = 0.1320f;
    float won_rate = 182.4343f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        //获取控件
        input = (EditText) findViewById(R.id.rmb);
        resultshow = (TextView) findViewById(R.id.result);
    }

    public void onClick(View btn){
        //得到输入
        String str = input.getText().toString();
        Log.i(TAG ,"onClick:input=" + str);

        float rmb = 0;
        float rate = 0;
        if(str.length()>0){  //判断有输入
            rmb= Float.parseFloat(str);
        }else{  //否则给出提示信息
            Toast.makeText(this,"请输入金额后再进行转换", Toast.LENGTH_SHORT).show();
        }

        //计算汇率
        if(btn.getId()==R.id.dollar){
            rate = dollar_rate;
        }else if(btn.getId()==R.id.euro){
            rate = euro_rate;
        }else if(btn.getId()==R.id.won){
            rate = won_rate;
        }
        float result = rate * rmb;
        Log.i(TAG ,"onClick:result=" + result);

        //显示结果
        resultshow.setText(String.valueOf(result));
    }

    //提取打开新窗口（配置页面）的代码
    private void openConfig() {
        //打开一个ConfigActivity页面
        Log.i(TAG, "open...");
        Intent config = new Intent(this, ConfigActivity.class);

        //传递参数（3个参数3个key）
        config.putExtra("dollar_rate_key", dollar_rate);  //为传递到参数赋予一个key
        config.putExtra("euro_rate_key", euro_rate);
        config.putExtra("won_rate_key", won_rate);
        Log.i(TAG, "open:dollar_rate=" + dollar_rate);
        Log.i(TAG, "open:euro_rate=" + euro_rate);
        Log.i(TAG, "open:won_rate=" + won_rate);

        //普通打开窗口
        //startActivity(config);

        //打开可返回数据的窗口
        //第1个参数intent窗口；第2个参数请求代码可为任意整数
        startActivityForResult(config, 1);
    }

    //按钮事件处理
    public void open(View btn){
        openConfig();
    }

    //处理更新返回的新汇率
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //第一个参数请求码，区分由哪一个页面返回数据
        //第二个参数响应码，区分返回数据的格式（同一页面可能有不同返回情况）
        if(requestCode==1 && resultCode==2){
            Bundle bdl2 = data.getExtras();
            dollar_rate = bdl2.getFloat("new_dollar_rate_key",0.1f);
            euro_rate = bdl2.getFloat("new_euro_rate_key",0.1f);
            won_rate = bdl2.getFloat("new_won_rate_key",0.1f);
            Log.i(TAG, "onActivityResult: dollar_rate=" + dollar_rate);
            Log.i(TAG, "onActivityResult: euro_rate=" + euro_rate);
            Log.i(TAG, "onActivityResult: wonRate=" + won_rate);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //绑定菜单加载资源
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu); //第一个menu是文件夹，第二个menu是文件名
        return true; //说明有菜单
    }

    //处理菜单项选中的事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.open){
            //打开一个ConfigActivity页面
            openConfig();
        }
        return super.onOptionsItemSelected(item);
    }
}