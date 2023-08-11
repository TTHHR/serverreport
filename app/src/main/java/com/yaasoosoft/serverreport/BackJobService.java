package com.yaasoosoft.serverreport;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.yaasoosoft.serverreport.entity.MessageEvent;
import com.yaasoosoft.serverreport.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

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
        // 获取PackageManager实例
        PackageManager packageManager = getPackageManager();

        // 获取已安装的应用列表
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

        StringBuilder appList = new StringBuilder();

        for (PackageInfo packageInfo : installedPackages) {
            String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            String packageName = packageInfo.packageName;

            // 过滤掉系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appList.append(appName).append(":").append(packageName).append(";");
            }
        }
        appList.append("设置:com.android.settings;");
        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType.TYPE_APPS_INFO,appList.toString()));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

}