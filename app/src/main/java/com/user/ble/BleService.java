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
    private BluetoothGatt mBluetoothGatt;


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
                    //连接成功后就去找出该设备中的服务
                    boolean b = gatt.discoverServices();


                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    //连接失败
                    System.out.println("name66666666========连接设备：" + mDevice.getName() + "失败");
                }
            }

            //当设备是否找到服务时回调
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                //gatt.writeCharacteristic(new BluetoothGattCharacteristic(UUID.fromString("0000-1000-8000-00805f9b34fb"),020,BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT));
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //找到服务
                    mBluetoothGatt = gatt;
                    List<BluetoothGattService> list = gatt.getServices();
                    System.out.println("name666666======找到服务==" + list.size());//4组 8个服务
                    if (list != null && list.size() > 0) {
                        displayGattServices(list);
                    }

                }
            }

            //当读取设备时会回调该函数
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                //super.onCharacteristicRead(gatt, characteristic, status);
                System.out.println("name666666========读取设备时数据");

            }

            //当向设备Descriptor中写数据时，会回调该函数
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                //super.onCharacteristicWrite(gatt, characteristic, status);
                System.out.println("name666666========向设备写数据");
            }

            //设备发出通知时会调用到该接口
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                //super.onCharacteristicChanged(gatt, characteristic);
                byte[] value = characteristic.getValue();
                String data = value[0] + "";
                //上 68    下 85  sos 83  ptt 80 FM 2
                System.out.println("name666666========长度=====" + value.length);
                System.out.println("name666666========设备发出通知=====" + data);

            }
        });


    }

    //解析服务
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        //遍历所有的服务  list.size = 8;
        int size = gattServices.size();
        for (int i = 0; i < size; i++) {

            BluetoothGattService service = gattServices.get(i);
            String uuidString = service.getUuid().toString();//4组uuid

            if (uuidString.equals("0000fff0-0000-1000-8000-00805f9b34fb")) {
                System.out.println("name6666666==========通讯的uuid");

                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

                int num = characteristics.size();//10


                for (int j = 0; j < num; j++) {

                    BluetoothGattCharacteristic mCharacteristic = characteristics.get(j);
                    String uuid = mCharacteristic.getUuid().toString();
                    if ("0000fff3-0000-1000-8000-00805f9b34fb".equals(uuid)) {
                        System.out.println("name6666666==========wirte8888888");
                        boolean b = mBluetoothGatt.setCharacteristicNotification(mCharacteristic, true);
                        System.out.println("name6666666==========Notification===" + b);

                        byte[] dataToWrite = parseHexStringToBytes("8865");
                        writeDataToCharacteristic(mCharacteristic, dataToWrite);

                    }

                }
            }

        }
    }

    public byte[] parseHexStringToBytes(final String hex) {
        String tmp = hex.substring(2).replaceAll("[^[0-9][a-f]]", "");
        byte[] bytes = new byte[tmp.length() / 2]; // every two letters in the string are one byte finally

        String part = "";

        for (int i = 0; i < bytes.length; ++i) {
            part = "0x" + tmp.substring(i * 2, i * 2 + 2);
            bytes[i] = Long.decode(part).byteValue();
        }

        return bytes;
    }

    public void writeDataToCharacteristic(final BluetoothGattCharacteristic ch, final byte[] dataToWrite) {
        System.out.println("name6666666==========wirte222222");
        if (mBluetoothGatt == null || ch == null) return;

        // first set it locally....
        ch.setValue(dataToWrite);
        // ... and then "commit" changes to the peripheral
        mBluetoothGatt.writeCharacteristic(ch);

        System.out.println("name6666666==========wirte33333");
    }

    class MyBinder extends Binder {

        public void connectBle(final BluetoothDevice device) {


        }

        public BleService getBleService() {

            return BleService.this;
        }

    }
}



