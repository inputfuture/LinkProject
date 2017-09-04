package com.letv.auto.keypad.service;

import android.os.Bundle;

/**
 * Created by ZhangHaoyi on 15-2-10.
 */
public interface BtGattCallback {
    void onGattReceive(int event, Bundle bundle);
}
