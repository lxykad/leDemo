package com.user.ble;

import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class BleService extends Service {

    private MyBinder mBinder;


    public BleService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        if (mBinder == null) {
            mBinder = new MyBinder();
            return mBinder;
        } else {
            return mBinder;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    class MyBinder extends Binder {

        public void connectBle(final BluetoothDevice device) {

           device.connectGatt(BleService.this, true, new BluetoothGattCallback() {
               @Override
               public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                   super.onConnectionStateChange(gatt, status, newState);
                   System.out.println("name66666666========正在连接设备："+device.getName());
               }
           });
        }

    }
}



