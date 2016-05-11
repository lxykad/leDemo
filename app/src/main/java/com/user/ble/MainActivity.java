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

    private BluetoothLeService mBluetoothLeService;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //从BluetoothLeService中获取的对象
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //System.out.println("name666666======service==断开服务连接");
            
            mBluetoothLeService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
    }

    private void initData() {

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        startService(gattServiceIntent);

        mListView = (ListView) findViewById(R.id.list_view);
        mList = new ArrayList<>();
        mDeviceAdapter = new DeviceAdapter(MainActivity.this);
        mListView.setAdapter(mDeviceAdapter);
        mListView.setOnItemClickListener(this);


        mHandler = new Handler();
        mManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = mManager.getAdapter();
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

                // && "MI1S".equals(name)
//                if (name != null && !name.equals("")) {
//                    String address = device.getAddress();
//                    ParcelUuid[] uuids = device.getUuids();
//                    System.out.println("name666666======address==" + address);//  C8:0F:10:27:71:46
//                    //System.out.println("name666666======uuid==" + uuids);//null
//                    device.connectGatt(MainActivity.this, true, new BluetoothGattCallback() {
//                        @Override//当连接上设备或者失去连接时会回调该方法
//                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//                            super.onConnectionStateChange(gatt, status, newState);
//                            if (newState == BluetoothProfile.STATE_CONNECTED) { //连接成功
//                                //连接成功后就去找出该设备中的服务
//                                boolean b = gatt.discoverServices();
//                                //Toast.makeText(MainActivity.this,"成功连接"+device.getName(),Toast.LENGTH_SHORT).show();
//                                List<BluetoothGattService> services = gatt.getServices();
//                                System.out.println("name666666======service==" + b);
//                            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {  //连接失败
//                                Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        //当设备是否找到服务时，会回调该函数
//                        @Override
//                        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//                            super.onServicesDiscovered(gatt, status);
//                            if (status == BluetoothGatt.GATT_SUCCESS) { //找到服务了
//                                mGatt = gatt;
//                                System.out.println("name666666======findservice==");
//                                //解析服务
//                                List<BluetoothGattService> list = gatt.getServices();
//                                System.out.println("name666666======servicesize==" + list.size());
//                                if (list != null && list.size() > 0) {
//
//                                    displayGattServices(list);
//                                }
////                                for(int i = 0;i<list.size();i++){
////                                    BluetoothGattService service = list.get(i);
////                                    System.out.println("name666666======service-id=="+service.getUuid());
////                                }
//
//                            } else {
//
//                            }
//                        }
//
//                        //当读取设备时会回调该函数
//                        @Override
//                        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//                            super.onCharacteristicRead(gatt, characteristic, status);
//                            if (status == BluetoothGatt.GATT_SUCCESS) {
//                                System.out.println("name666666======service==读取设备数据");
//                                //读取到的数据存在characteristic当中，可以通过characteristic.getValue();函数取出。
//                                byte[] bytes = characteristic.getValue();
//
//
//                            }
//                        }
//
//                        //当向设备Descriptor中写数据时，会回调该函数
//                        @Override
//                        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//                            super.onDescriptorWrite(gatt, descriptor, status);
//                        }
//
//                        //设备发出通知时会调用到该接口
//                        @Override
//                        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//                            super.onCharacteristicChanged(gatt, characteristic);
//                            if (characteristic.getValue() != null) {
//                                System.out.println("name666666======service-notice==" + characteristic.getStringValue(0));
//                            }
//                            System.out.println("--------onCharacteristicChanged-----");
//                        }
//
//                        //
//                        @Override
//                        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
//                            super.onReadRemoteRssi(gatt, rssi, status);
//                        }
//
//                        //当向Characteristic写数据时会回调该函数
//                        @Override
//                        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//                            super.onCharacteristicWrite(gatt, characteristic, status);
//                        }
//
//                    });
//                    mAdapter.stopLeScan(mLeCallback);
//
//                }

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

//        mAdapter.stopLeScan(mLeCallback);
//        boolean b = mAdapter.startLeScan(mLeCallback);
//        System.out.println("bbbbbbbbb======" + b);

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
                    mAdapter.stopLeScan(mLeCallback);
                }
            }, 10000); //10秒后停止搜索
            mScanning = true;
            mAdapter.startLeScan(mLeCallback); //开始搜索
        } else {
            mScanning = false;
            mAdapter.stopLeScan(mLeCallback);//停止搜索
        }
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

//        if (mBluetoothLeService != null) {
//            boolean flag = mBluetoothLeService.connect(address);
//        }

        ProgressHUD.showLoding(MainActivity.this);

        device.connectGatt(MainActivity.this, true, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    //连接成功
                    //连接成功后就去找出该设备中的服务
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressHUD.hidden();
                            //ProgressHUD.show(MainActivity.this, "设备" + name + "连接成功");
                        }
                    });
                    boolean b = gatt.discoverServices();

                    List<BluetoothGattService> services = gatt.getServices();
                    System.out.println("name666666======service==" + b);


                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {  //连接失败
                    //ProgressHUD.hidden();
                    //ProgressHUD.show(MainActivity.this,"设备"+name+"连接失败");
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);

                if (status == BluetoothGatt.GATT_SUCCESS) { //找到服务了
                    mGatt = gatt;
                    //解析服务
                    List<BluetoothGattService> list = gatt.getServices();
                    System.out.println("name666666======servicesize==" + list.size());
                    if (list != null && list.size() > 0) {

                        displayGattServices(list);
                    }
                    for (int i = 0; i < list.size(); i++) {
                        BluetoothGattService service = list.get(i);
                        //System.out.println("name666666======service-id==" + service.getUuid());
                    }

                } else {

                }

            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);

            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
            }

            //读取ble设备数据时回调
            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    System.out.println("name666666======service==读取设备数据");

                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
            }
        });
    }


    //设置可读的uuid


    //设置可写的uuid


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
}



