package com.letv.auto.keypad.interfaces;


import com.letv.auto.keypad.util.LetvLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangminghua on 15-4-3.
 */
public class StopServicesJobs {
    public static String TAG = "StopServicesJobs";
    public static StopServicesDone mStopServicesDone;
    public static List<String> mServiceClassNameList = new ArrayList<String>();

    public static void addServiceNameToListWhenStart(String serviceClassName) {
        if (!mServiceClassNameList.contains(serviceClassName)) {
            LetvLog.d(TAG,"addServiceNameToListWhenStart serviceClassName="+serviceClassName);
            mServiceClassNameList.add(serviceClassName);
        }
    }

    public static void removeServiceNameToListWhenDestory(String serviceClassName) {
        if (mServiceClassNameList.contains(serviceClassName)) {
            LetvLog.d(TAG,"removeServiceNameToListWhenDestory serviceClassName="+serviceClassName);
            mServiceClassNameList.remove(serviceClassName);
            LetvLog.d(TAG,"removeServiceNameToListWhenDestory mServiceClassNameList.size="+mServiceClassNameList.size());
        }
        if (mServiceClassNameList.size() == 0 && mStopServicesDone != null) {
            mStopServicesDone.serviceAllDestoried();
        }
    }

    public static void registInterfaceInstanceFromApplication(StopServicesDone callback) {
        mStopServicesDone = callback;
    }

    public abstract interface StopServicesDone {
        public abstract void serviceAllDestoried();
    }

}
