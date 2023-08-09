package com.yaasoosoft.serverreport;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.yaasoosoft.serverreport.entity.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView ipv6TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipv6TextView = findViewById(R.id.ipv6Text);
// Register the activity to the event bus
        EventBus.getDefault().register(this);
        scheduleJob();
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
            ipv6TextView.setText(message);
        }
    }
}
