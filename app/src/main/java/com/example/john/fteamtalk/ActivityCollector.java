package com.example.john.fteamtalk;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangRui on 2016/10/10.
 * Activity控制，便于管理（销毁等）
 */

public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for(Activity activity : activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
