package com.sjl.blelibrarydemo;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sjl.blelibrary.BLEException;
import com.sjl.blelibrary.BLEManager;
import com.sjl.blelibrary.BLEScanner;
import com.sjl.blelibrary.listener.OnBLEConnectListener;
import com.sjl.blelibrary.listener.OnBLEReceiveDataListener;
import com.sjl.blelibrary.listener.OnBLEWriteDataListener;
import com.sjl.blelibrary.listener.OnBLEWriteDescriptorListener;
import com.sjl.blelibrary.util.BLEByteUtil;
import com.sjl.blelibrary.util.BLELogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙库的简单演示
 *
 * @author SJL
 * @date 2017/5/8
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOCATION_CODE = 100;
    //使用需要的uuid
    private String uuidDescriptorService;
    private String uuidDescriptorCharacteristic;
    private String uuidDescriptor;
    private String uuidWriteService;
    private String uuidWriteCharacteristics;

    private TextView tvCurrentMac;
    private ListView lv;

    private BLEManager bleManager;
    private List<String> macList = new ArrayList<>();
    private ArrayAdapter adapter;

    private String currentMac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tvCurrentMac = (TextView) findViewById(R.id.tvCurrentMac);
        lv = (ListView) findViewById(R.id.lv);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, macList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentMac = macList.get(position);
                tvCurrentMac.setText(currentMac);
            }
        });
        bleManager = BLEManager.getInstance(getApplication());
        PermisstionUtil.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE, "5.0之后使用蓝牙需要位置权限", new PermisstionUtil.OnPermissionResult() {
            @Override
            public void granted(int requestCode) {

            }

            @Override
            public void denied(int requestCode) {
                toast("位置权限被拒绝");
            }
        });
    }

    public void openBluetooth(View view) {
        if (bleManager.isSupportBluetooth()) {
            if (bleManager.isSupportBLE()) {
                if (!bleManager.isBluetoothEnable()) {
                    bleManager.enableBluetooth(this);
                }
            } else {
                toast("不支持低功耗蓝牙");
            }
        } else {
            toast("不支持蓝牙");
        }
    }

    /**
     * 扫描
     * @param view
     */
    public void scan(View view) {
        bleManager.startScan(10000, new BLEScanner.OnBLEScanListener() {

            @Override
            public void onScanResult(BluetoothDevice device, int rssi, byte[] scanRecord) {
                synchronized (MainActivity.this){
                    if(!macList.contains(device.getAddress())){
                        macList.add(device.getAddress());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onScanFailed(BLEException bleException) {
                toast(bleException.getMessage());
            }
        });
    }

    /**
     * 连接设备
     * @param view
     */
    public void connect(View view) {
        bleManager.stopScan();
        if (!checkCurrentMac()) {
            return;
        }
        bleManager.connect(tvCurrentMac.getText().toString(), new OnBLEConnectListener() {
            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status, int newState) {
                //设备连接成功，找服务
                toast("设备连接成功，开始找服务。。。");
                gatt.discoverServices();
            }

            @Override
            public void onConnectFailure(BluetoothGatt gatt, BLEException bleException) {
                //设备连接失败
                toast(bleException.getMessage());
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                //找到服务
                toast("找服务成功。。。");
            }
        });
    }

    /**
     * 接受通知，根据实际情况决定是否需要接受通知
     * @param view
     */
    public void receiveNotification(View view) {
        bleManager.stopScan();
        if (!checkCurrentMac()) {
            return;
        }
        bleManager.writeDescriptor(tvCurrentMac.getText().toString(), uuidDescriptorService, uuidDescriptorCharacteristic, uuidDescriptor, new OnBLEWriteDescriptorListener() {
            @Override
            public void onWriteDescriptorSuccess(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                toast("接受通知成功");
            }

            @Override
            public void onWriteDescriptorFailure(BLEException bleException) {
                toast(bleException.getMessage());
            }
        });
    }

    /**
     * 写数据
     * @param view
     */
    public void writeData(View view) {
        bleManager.stopScan();
        if (!checkCurrentMac()) {
            return;
        }
        //要写入的数据
        byte[] data=new byte[0];
        bleManager.writeData(tvCurrentMac.getText().toString(), uuidWriteService, uuidWriteCharacteristics, data, new OnBLEWriteDataListener() {
            @Override
            public void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                toast("写数据成功");
            }

            @Override
            public void onWriteDataFailure(BLEException exception) {
                toast(exception.getMessage());
            }
        }, new OnBLEReceiveDataListener() {
            @Override
            public void onReceiveData(byte[] data) {
                toast("接受到数据：" + BLEByteUtil.bytesToHexString(data));
            }
        });
    }

    private boolean checkCurrentMac() {
        if (tvCurrentMac.getText().length() == 0) {
            toast("请先选择要操作的设备");
            return false;
        }
        return true;
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        BLELogUtil.i(TAG,msg);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermisstionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
