package com.user.ble;

import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;

import java.util.List;
import java.util.UUID;

import util.SampleGattAttributes;

public class BleService extends Service {

    private MyBinder mBinder;
    private BluetoothDevice mDevice;


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

        System.out.println("name666666==========create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("name666666==========onStartCommand");
        mDevice = intent.getExtras().getParcelable("ble_service");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection conn, int flags) {
        System.out.println("name666666==========bindservice");
        return super.bindService(intent, conn, flags);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        System.out.println("name666666==========unbindService");
    }

    @Override
    public boolean stopService(Intent name) {
        System.out.println("name666666==========stopService");
        return super.stopService(name);

    }

    public void connectBle() {
        mDevice.connectGatt(BleService.this, true, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    //连接成功
                    System.out.println("name66666666========连接设备：" + mDevice.getName() + "成功");

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    //连接失败
                    System.out.println("name66666666========连接设备：" + mDevice.getName() + "失败");
                }
            }

            //当设备是否找到服务时回调
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //找到服务
                    List<BluetoothGattService> list = gatt.getServices();
                    System.out.println("name666666======servicesize==" + list.size());

                }
            }

            //当读取设备时会回调该函数
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                System.out.println("name666666========读取设备时数据");

            }

            //当向设备Descriptor中写数据时，会回调该函数
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                System.out.println("name666666========向设备写数据");
            }
            //设备发出通知时会调用到该接口
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                System.out.println("name666666========设备发出通知");
            }
        });


    }

    //解析服务
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        //遍历所有的服务
        for (BluetoothGattService service : gattServices) {

            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

            //遍历每条服务里所有的characteristic
            for (int i = 0; i < characteristics.size(); i++) {
                BluetoothGattCharacteristic characteristic = characteristics.get(i);
                String s = characteristic.getUuid().toString();
                //System.out.println("name66666======characteristics===id==" + s);
                //if (s.equals("00002a06-0000-1000-8000-00805f9b34fb")) {//需要通信的uuid
                System.out.println("name66666======可以通信的id" + s);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                        UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                mGatt.writeDescriptor(descriptor);
//                mGatt.readCharacteristic(characteristic);

                //}
            }
        }
    }




    class MyBinder extends Binder {

        public void connectBle(final BluetoothDevice device) {


        }

        public BleService getBleService() {

            return BleService.this;
        }

    }
}



