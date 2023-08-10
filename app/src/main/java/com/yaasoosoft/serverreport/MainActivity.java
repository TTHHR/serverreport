package com.yaasoosoft.serverreport;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.yaasoosoft.serverreport.entity.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        // Register the activity to the event bus
        EventBus.getDefault().register(this);
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void initView()
    {
        // 设置屏幕方向为横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 隐藏状态栏和导航栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        webView=findViewById(R.id.htmlView);
        // 获取WebView的WebSettings
        WebSettings webSettings = webView.getSettings();

        // 允许JavaScript执行
        webSettings.setJavaScriptEnabled(true);

        // 加载assets目录下的HTML文件
        webView.loadUrl("file:///android_asset/test.html");
        // 设置WebViewClient以确保网页在WebView中打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                scheduleJob();
            }
        });

        // 设置WebChromeClient以支持JavaScript的弹出窗口等
        webView.setWebChromeClient(new WebChromeClient());
    }
    private void setIpInfo(String message) {
        Log.i(TAG,"setIpInfo "+message);
        if(webView!=null)
        {
            // 调用 WebView 中的 JavaScript 函数
            webView.loadUrl("javascript:setIpInfo('"+message+"');");
        }
    }
    private void scheduleJob() {
        ComponentName componentName = new ComponentName(this, BackJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(1, componentName);

        // Set minimum delay to approximate a one-minute interval
        builder.setPeriodic(15*60*1000); // 60,000 milliseconds = 1 minute

        JobScheduler jobScheduler = getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        MessageEvent.EventType type=event.getEventType();
        String message = event.getMessage();
        if(type== MessageEvent.EventType.TYPE_IP_CHANGE)
        {
            Log.d(TAG,"ipchange event");
            setIpInfo(message);
        } else if (type== MessageEvent.EventType.TYPE_NET_ERROR) {
            Log.d(TAG,"net error event");
            setIpInfo(message);
        }
    }
}
