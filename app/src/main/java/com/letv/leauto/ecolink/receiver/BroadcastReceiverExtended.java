/******************************************************************************
 *  Copyright (C) Cambridge Silicon Radio Limited 2015
 *
 *  This software is provided to the customer for evaluation
 *  purposes only and, as such early feedback on performance and operation
 *  is anticipated. The software source code is subject to change and
 *  not intended for production. Use of developmental release software is
 *  at the user's own risk. This software is provided "as is," and CSR
 *  cautions users to determine for themselves the suitability of using the
 *  beta release version of this software. CSR makes no warranty or
 *  representation whatsoever of merchantability or fitness of the product
 *  for any particular purpose or use. In no event shall CSR be liable for
 *  any consequential, incidental or special damages whatsoever arising out
 *  of the use of or inability to use this software, even if the user has
 *  advised CSR of the possibility of such damages.
 *
 ******************************************************************************/

package com.letv.leauto.ecolink.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;

/**
 * This class allows to receive information from the system. We use it to have information about the Bluetooth state.
 */

public class BroadcastReceiverExtended extends BroadcastReceiver {

    /**
     * To have an access to the main activity from which this receiver is initiated.
     */
    private WeakReference<BroadcastReceiverListener> mListener;

    /**
     * The constructor of this class.
     *
     * @param listener
     *            the main activity from which the receiver is initiated.
     */
    public BroadcastReceiverExtended(BroadcastReceiverListener listener) {
        this.mListener = new WeakReference<BroadcastReceiverListener>(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            if (state == BluetoothAdapter.STATE_OFF) {
                BroadcastReceiverListener broadcastReceiverListener=mListener.get();
                if (broadcastReceiverListener!=null)

                    broadcastReceiverListener.onBluetoothDisabled();
            }
            else if (state == BluetoothAdapter.STATE_ON) {
                BroadcastReceiverListener broadcastReceiverListener=mListener.get();
                if (broadcastReceiverListener!=null){
                    broadcastReceiverListener.onBluetoothEnabled();}
            }
        }
    }

    public void destory() {
        mListener=null;
    }

    /**
     * The interface used to communicate with this object.
     */
    public interface BroadcastReceiverListener {
        /**
         * When the application is informed that the Bluetooth is disabled, this method is called.
         */
        public void onBluetoothDisabled();
        /**
         * When the application is informed that the Bluetooth is enabled, this method is called.
         */
        public void onBluetoothEnabled();
    }

}
