package com.user.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import util.ProgressHUD;
import util.SampleGattAttributes;

/**
 * http://m.2cto.com/kf/201601/486609.html
 */
public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    private String TAG = "bledemo";
    private BluetoothManager mManager;
    private BluetoothAdapter mAdapter;
    private BluetoothAdapter.LeScanCallback mLeCallback;
    private boolean mScanning;
    private Handler mHandler;
    private BluetoothGatt mGatt;

    private ListView mListView;
    private ArrayList<BluetoothDevice> mList;
    private DeviceAdapter mDeviceAdapter;


    private BleService.MyBinder mBinder;
    private BleService mBleService;
    private ServiceConnection mBleConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBinder = (BleService.MyBinder) service;
            mBleService = mBinder.getBleService();
            System.out.println("name666666666=========服务已连接===" + mBleService);
            mBleService.connectBle();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("name666666666=========服务断开连接====" + mBleService);
            mBinder = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list_view);
        mList = new ArrayList<>();
        mDeviceAdapter = new DeviceAdapter(MainActivity.this);
        mListView.setAdapter(mDeviceAdapter);
        mListView.setOnItemClickListener(this);


        mHandler = new Handler();
        mManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = mManager.getAdapter();

        mLeCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

                if (!mList.contains(device)) {
                    mList.add(device);
                    mDeviceAdapter.clearItems();
                    mDeviceAdapter.addItems(mList);
                }
                System.out.println("name66666666=========发现设备===" + device.getName());
            }
        };

        //initData();
    }

    private void initData() {

//        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
//        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//        startService(gattServiceIntent);


        //mAdapter = BluetoothAdapter.getDefaultAdapter();
        mLeCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                String name = device.getName();
                System.out.println("name666666======name==" + name);
                if (!mList.contains(device)) {
                    mList.add(device);
                    mDeviceAdapter.clearItems();
                    mDeviceAdapter.addItems(mList);
                }
            }
        };
    }

    public void openBle(View view) {

        if (mAdapter == null || !mAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Toast.makeText(this, "蓝牙已启用", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "蓝牙未启用", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 因为扫描BLE设备是电源密集型操作，浪费电量，因此要保证以下原则：
     * 1）扫描到需要的设备后，马上停止扫描；
     * 2）给扫描一个时间限制
     */
    public void beginScan(View view) {
        scanLeDevice(true);

    }

    //搜索ble设备
    //启动搜索的操作最好放在Activity的onResume里面或者服务里面，我有发现放在onCreate有时响应不及时
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    System.out.println("name6666666========stopSearch");
                    mAdapter.stopLeScan(mLeCallback);
                }
            }, 10000); //10秒后停止搜索
            mScanning = true;
            System.out.println("name6666666========startSearch");
            mAdapter.startLeScan(mLeCallback); //开始搜索
        } else {
            mScanning = false;
            mAdapter.stopLeScan(mLeCallback);//停止搜索
        }
    }

    //开启服务
    public void openService(View view) {

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
                //if (s.equals("00002a06-0000-1000-8000-00805f9b34fb")) {//需要通信的uuid
                System.out.println("name66666======可以通信的id" + s);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                        UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mGatt.writeDescriptor(descriptor);
                mGatt.readCharacteristic(characteristic);


                //}
            }
        }
    }

    //设置可接收通知的uuid
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mAdapter == null || mGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BluetoothDevice device = mList.get(position);
        final String name = device.getName();
        final String address = device.getAddress();

        //绑定服务
        Intent intent = new Intent(MainActivity.this, BleService.class);
        intent.putExtra("ble_service", device);
        startService(intent);
        bindService(intent, mBleConnection, BIND_AUTO_CREATE);

    }

    //设置可读的uuid


    //设置可写的uuid


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent(MainActivity.this, BleService.class);
        unbindService(mBleConnection);
        stopService(intent);
        mBleService.stopSelf();
        mBleService = null;

        System.out.println("name666666========关闭mainactivity");

    }
}



