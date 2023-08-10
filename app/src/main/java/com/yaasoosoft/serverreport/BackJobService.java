package com.yaasoosoft.serverreport;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.yaasoosoft.serverreport.entity.MessageEvent;
import com.yaasoosoft.serverreport.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

public class BackJobService extends JobService {
    private static final String TAG = "BackJobService";
    private String lastIp="";
    private boolean threadRun=false;
    public BackJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.e(TAG, "Job started");
        if(threadRun)
            return false;
        threadRun=true;
        new Thread(()->{
            String ip=NetworkUtils.getIPV4V6Address();
            if(!ip.equals(""))
            {
                if(!ip.equals(lastIp))
                {
                    lastIp=ip;
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType.TYPE_IP_CHANGE,lastIp));
                    NetworkUtils.postIPV4V6Address(lastIp);
                }
            }else
            {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType.TYPE_NET_ERROR,"获取IP异常"));
            }

            threadRun=false;
        }).start();

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

}