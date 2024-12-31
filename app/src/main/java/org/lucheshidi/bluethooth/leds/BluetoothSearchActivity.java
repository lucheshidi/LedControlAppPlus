package org.lucheshidi.bluethooth.leds;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import java.util.ArrayList;
import java.util.List;

public class BluetoothSearchActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private boolean isScanning = false;
    private DeviceAdapter deviceAdapter;
    private List<BluetoothDevice> deviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 初始化蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }

        // 初始化RecyclerView
        RecyclerView recyclerView = findViewById(R.id.deviceRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceAdapter = new DeviceAdapter(deviceList, device -> {
            // 处理设备点击
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED) {
                stopScan();
                Intent intent = new Intent(this, DeviceControlActivity.class);
                intent.putExtra("deviceAddress", device.getAddress());
                intent.putExtra("deviceName", device.getName());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(deviceAdapter);

        // 返回按钮点击事件
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // 开始扫描按钮点击事件
        findViewById(R.id.scanButton).setOnClickListener(v -> {
            if (!isScanning) {
                startScan();
            } else {
                stopScan();
            }
        });

        // 检查并请求权限
        checkPermissions();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            startScan();
        }
    }

    private void startScan() {
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "请开启蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        deviceList.clear();
        deviceAdapter.notifyDataSetChanged();
        isScanning = true;
        bluetoothLeScanner.startScan(scanCallback);
        updateScanButton();
    }

    private void stopScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        isScanning = false;
        bluetoothLeScanner.stopScan(scanCallback);
        updateScanButton();
    }

    private void updateScanButton() {
        // TODO: 更新扫描按钮状态
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (ActivityCompat.checkSelfPermission(BluetoothSearchActivity.this,
                    Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            // 判断是否已存在该设备
            for (BluetoothDevice existingDevice : deviceList) {
                if (existingDevice.getAddress().equals(device.getAddress())) {
                    return;
                }
            }

            deviceList.add(device);
            deviceAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
    }
}