package com.sjl.blelibrarydemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanRecord;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sjl.blelibrary.BLibManager;
import com.sjl.blelibrary.constant.BLibCode;
import com.sjl.blelibrary.core.BLibAdvertiser;
import com.sjl.blelibrary.core.BLibScanner;
import com.sjl.blelibrary.listener.OnBLibConnectListener;
import com.sjl.blelibrary.listener.OnBLibReceiveDataListener;
import com.sjl.blelibrary.listener.OnBLibWriteDataListener;
import com.sjl.blelibrary.listener.OnBLibWriteDescriptorListener;
import com.sjl.blelibrary.util.BLibByteUtil;
import com.sjl.blelibrary.util.BLibLogUtil;
import com.sjl.blelibrary.util.BLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 蓝牙库的简单演示
 *
 * @author 林zero
 * @date 2017/5/8
 */
@SuppressLint("NewApi")
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

    private BLibManager bleManager;
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
        bleManager = BLibManager.getInstance();
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
                    bleManager.enableBluetooth();
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
     *
     * @param view
     */
    public void scan(View view) {
        macList.clear();
        adapter.notifyDataSetChanged();
        bleManager.startScan(10000, new BLibScanner.OnBLEScanListener() {

            @Override
            public void onScanResult(BluetoothDevice device, int rssi, ScanRecord scanRecord) {
                synchronized (MainActivity.this) {
                    Log.e(TAG,BLibByteUtil.bytesToHexString(scanRecord.getBytes()));
                    Log.e(TAG,scanRecord.toString());
                    if (!macList.contains(device.getAddress())) {
                        macList.add(device.getName() + "\n" + device.getAddress() + "\n" + BLibByteUtil.bytesToHexString(scanRecord.getBytes()));
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onScanFailed(int code) {
                BLibLogUtil.e(BLibCode.getError(code));
            }
        });
    }

    /**
     * 停止扫描
     *
     * @param view
     */
    public void stopScan(View view) {
        bleManager.stopScan();
    }

    /**
     * 广播
     *
     * @param view
     */
    public void startAdvertising(View view) {
        AdvertiseSettings advertiseSettings = BLibUtil.buildAdvertiseSettings(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY, AdvertiseSettings.ADVERTISE_TX_POWER_HIGH, false, 10000);
        AdvertiseData advertiseData = buildAdvertiseData();
        bleManager.startAdvertising(advertiseSettings, advertiseData, new BLibAdvertiser.OnBLEAdvertisingListener() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {

            }

            @Override
            public void onStartFailure(int errorCode) {

            }
        });
    }

    private UUID proximityUuid = UUID.fromString("fda50693-a4e2-4fb1-afcf-c6eb07647825");
    private short major = 0;
    private short minor = 0;
    private byte txPower = 0;

    @SuppressLint("NewApi")
    private AdvertiseData buildAdvertiseData() {
        String[] uuidstr = proximityUuid.toString().replaceAll("-", "").toLowerCase().split("");
        byte[] uuidBytes = new byte[16];
        for (int i = 1, x = 0; i < uuidstr.length; x++) {
            uuidBytes[x] = (byte) ((Integer.parseInt(uuidstr[i++], 16) << 4) | Integer.parseInt(uuidstr[i++], 16));
        }
        byte[] majorBytes = {(byte) (major >> 8), (byte) (major & 0xff)};
        byte[] minorBytes = {(byte) (minor >> 8), (byte) (minor & 0xff)};
        byte[] mPowerBytes = {txPower};
        byte[] manufacturerData = new byte[0x07];
        byte[] flagibeacon = {0x02, 0x15};

        System.arraycopy(flagibeacon, 0x0, manufacturerData, 0x0, 0x2);
        System.arraycopy(majorBytes, 0x0, manufacturerData, 0x2, 0x2);
        System.arraycopy(minorBytes, 0x0, manufacturerData, 0x4, 0x2);
        System.arraycopy(mPowerBytes, 0x0, manufacturerData, 0x6, 0x1);

        /*byte[] manufacturerData = new byte[0x17];
        byte[] flagibeacon = {0x02, 0x15};

        System.arraycopy(flagibeacon, 0x0, manufacturerData, 0x0, 0x2);
        System.arraycopy(uuidBytes, 0x0, manufacturerData, 0x2, 0x10);
        System.arraycopy(majorBytes, 0x0, manufacturerData, 0x12, 0x2);
        System.arraycopy(minorBytes, 0x0, manufacturerData, 0x14, 0x2);
        System.arraycopy(mPowerBytes, 0x0, manufacturerData, 0x16, 0x1);*/

        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.addManufacturerData(0x004c, manufacturerData);
        builder.addServiceUuid(ParcelUuid.fromString(proximityUuid.toString()));
        builder.setIncludeDeviceName(false);
        AdvertiseData advertiseData = builder.build();
        Log.e(TAG, advertiseData.toString());
        //AdvertiseData [mServiceUuids=[],
        // mManufacturerSpecificData={76=[2, 21, -3, -91, 6, -109, -92, -30, 79, -79, -81, -49, -58, -21, 7, 100, 120, 37, 0, 0, 0, 0, 0]},
        // mServiceData={},
        // mIncludeTxPowerLevel=false,
        // mIncludeDeviceName=false]
        return advertiseData;
    }

    /**
     * 停止广播
     *
     * @param view
     */
    public void stopAdvertising(View view) {
        bleManager.stopAdvertising();
    }

    /**
     * 连接设备
     *
     * @param view
     */
    public void connect(View view) {
        bleManager.stopScan();
        if (!checkCurrentMac()) {
            return;
        }
        bleManager.connect(currentMac, new OnBLibConnectListener() {
            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status, int newState) {
                //设备连接成功，找服务
                toast("设备连接成功，开始找服务。。。");
                gatt.discoverServices();
            }

            @Override
            public void onConnectFailure(BluetoothGatt gatt, int code) {
                //设备连接失败
                toast(BLibCode.getError(code));
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
     *
     * @param view
     */
    public void receiveNotification(View view) {
        bleManager.stopScan();
        if (!checkCurrentMac()) {
            return;
        }
        bleManager.writeDescriptor(currentMac, uuidDescriptorService, uuidDescriptorCharacteristic, uuidDescriptor, new OnBLibWriteDescriptorListener() {
            @Override
            public void onWriteDescriptorSuccess(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                toast("接受通知成功");
            }

            @Override
            public void onWriteDescriptorFailure(int code) {
                toast(BLibCode.getError(code));
            }
        });
    }

    /**
     * 写数据
     *
     * @param view
     */
    public void writeData(View view) {
        bleManager.stopScan();
        if (!checkCurrentMac()) {
            return;
        }
        //要写入的数据
        byte[] data = new byte[0];
        bleManager.writeData(currentMac, uuidWriteService, uuidWriteCharacteristics, data, new OnBLibWriteDataListener() {
            @Override
            public void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                toast("写数据成功");
            }

            @Override
            public void onWriteDataFailure(int code) {
                toast(BLibCode.getError(code));
            }
        }, new OnBLibReceiveDataListener() {
            @Override
            public void onReceiveData(byte[] data) {
                toast("接受到数据：" + BLibByteUtil.bytesToHexString(data));
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
        BLibLogUtil.i(TAG, msg);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermisstionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
