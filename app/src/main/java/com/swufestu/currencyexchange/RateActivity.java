package com.swufestu.currencyexchange;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class RateActivity extends AppCompatActivity implements Runnable{
    private static final String TAG ="RateActivity";

    EditText input;
    TextView resultshow;
    Handler handler; //定义成类变量

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

        //获取保存在sharedpreferences里的数据
        //第一个参数：sp文件名，第一次读取时不存在，默认创建
        //第二个参数：读取权限
        SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        //获取方法二；无法命名配置文件
        //尽量只将关键数据保存在配置文件中（1个配置文件）
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        dollar_rate = sp.getFloat("dollar_rate_spkey",0.0f); //第一次打开会默认显示这个默认值，而不是上方定义的0.1547f
        euro_rate = sp.getFloat("euro_rate_spkey",0.0f);
        won_rate = sp.getFloat("won_rate_spkey",0.0f);
        Log.i(TAG, "onCreate: sp dollar_rate=" + dollar_rate);
        Log.i(TAG, "onCreate: sp euro_rate=" + euro_rate);
        Log.i(TAG, "onCreate: sp wonRate=" + won_rate);

        //开启子线程（t代表这个子线程）
        //当前对象this已经实现了runnable接口，线程开启时会去调用run方法
        Thread t = new Thread(this);
        t.start();

        //处理收到的消息
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //由标识判断接收到的消息
                if(msg.what==5){
                    //String str = (String) msg.obj;  //强制转换的前提是可以被转换成这个类型
                    //Log.i(TAG, "handleMessage: getMessage msg = " + str);
                    //resultshow.setText(str);

                    Bundle bundle = (Bundle) msg.obj;
                    dollar_rate = bundle.getFloat("dollarRate");
                    euro_rate = bundle.getFloat("euroRate");
                    won_rate = bundle.getFloat("wonRate");
                    Log.i(TAG, "handleMessage: dollar_rate:" + dollar_rate);
                    Log.i(TAG, "handleMessage: euro_rate:" + euro_rate);
                    Log.i(TAG, "handleMessage: won_rate:" + won_rate);
                    Toast.makeText(RateActivity.this, "汇率已更新", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };  //这里面是对方法的重写（本来什么也没有）
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
            return;
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

            //将新设置的汇率写到SP里(也可以写在ConfigActivity的save方法里，这两块的数据是一样的，是传输过来的）
            //注意保存数据的文件名，一定和读取数据的文件名一致
            SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();  //编辑文件
            editor.putFloat("dollar_rate_spkey",dollar_rate);
            editor.putFloat("euro_rate_spkey",euro_rate);
            editor.putFloat("won_rate_spkey",won_rate);
            editor.commit();
            Log.i(TAG, "onActivityResult: 数据已保存到sharedPreferences");
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

    @Override
    public void run() {
        Log.i(TAG, "run: run()...");
//        for(int i=1;i<6;i++){
//            Log.i(TAG, "run: i=" + i);
//            try {
//                Thread.sleep(2000);  //当前停止2秒钟
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //用于保存获取到的汇率
        Bundle bdl3 = new Bundle();

//        URL url = null;
//        try {
//            url = new URL("http://www.usd-cny.com/icbc.htm");
//            HttpURLConnection http = (HttpURLConnection) url.openConnection(); //打开链接（需要强制转换）
//            InputStream in = http.getInputStream(); //获得一个输入流
//            String html = inputStream2String(in); //将输入流转换成字符串
//            Document doc = Jsoup.parse(html);
//            Log.i(TAG, "run: html=" + html);
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //直接用网址解析，就不用上面将输入流转换成字符串了
        Document doc = null;
        try {
            String url = "http://www.usd-cny.com/bankofchina.htm";
            doc = Jsoup.connect(url).get();
            Log.i(TAG, "run: " + doc.title());
            Elements tables = doc.getElementsByTag("table"); //集合

            //查看第几个table是需要的（只有一个）
//            int i = 1;
//            for(Element table : tables){
//                Log.i(TAG, "run: table["+i+"]=" + table);
//                i++;
//            }

            //解析网页
            Element table1 = tables.get(0);
//            Log.i(TAG, "run: table1=" + table1);

            //获取td中的数据
            Elements tds = table1.getElementsByTag("td");
            //查看td中的元素
//            for(Element td : tds){
//                Log.i(TAG, "run: td=" + td);
//                Log.i(TAG, "run: text=" + td.text());
//                Log.i(TAG, "run: html=" + td.html());
//            }

            for(int j=0; j<tds.size(); j+=6){
                Element td1 = tds.get(j);    //国家名
                Element td2 = tds.get(j+5);  //折算价
                String name = td1.text();
                String val = td2.text();
                Log.i(TAG, "run:" + name + "==>" + val);

                float urate = 100f / Float.parseFloat(val);
                if("美元".equals(name)){
                    bdl3.putFloat("dollarRate", urate);
                }else if("欧元".equals(name)){
                    bdl3.putFloat("euroRate", urate);
                }else if("韩元".equals(name)){
                    bdl3.putFloat("wonRate", urate);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        //arg用于传整数，其他类型可以用obj，what是整数用于标记比对msg
        //msg.what = 5; //这里写和上面参数what:5是一样的
        //msg.obj = "Hello from run()";
        msg.obj = bdl3; //bdl2中保存着从网页获取的汇率
        handler.sendMessage(msg);  //由handler将msg发送到消息队列中
    }

    //将输入流转换成字符串输出
    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312"); //这个编码要根据网页编码
        while (true) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}