package org.lucheshidi.bluethooth.leds;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import java.util.UUID;

public class DeviceControlActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private String deviceAddress;
    private String deviceName;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TabPagerAdapter pagerAdapter;
    private CCTFragment cctFragment;
    private RGBFragment rgbFragment;
    private HSIFragment hsiFragment;
    private PaperFragment paperFragment;
    private SceneFragment sceneFragment;
    private AnimationFragment animationFragment;

    // BLE服务和特征UUID
    private static final UUID SERVICE_UUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB");
    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");
    private BluetoothGattCharacteristic controlCharacteristic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        // 获取设备信息
        deviceAddress = getIntent().getStringExtra("deviceAddress");
        deviceName = getIntent().getStringExtra("deviceName");

        // 初始化蓝牙
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 初始化Fragment
        initFragments();

        // 初始化ViewPager和TabLayout
        initViews();

        // 连接到设备
        connectToDevice();

        // 返回按钮点击事件
        findViewById(R.id.backButton).setOnClickListener(v -> {
            disconnect();
            finish();
        });
    }

    private void initFragments() {
        cctFragment = new CCTFragment();
        rgbFragment = new RGBFragment();
        hsiFragment = new HSIFragment();
        paperFragment = new PaperFragment();
        sceneFragment = new SceneFragment();
        animationFragment = new AnimationFragment();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // 设置ViewPager适配器
        pagerAdapter = new TabPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // 将TabLayout和ViewPager关联
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("色温CCT");
                    break;
                case 1:
                    tab.setText("RGB色彩");
                    break;
                case 2:
                    tab.setText("HSI色彩");
                    break;
                case 3:
                    tab.setText("色纸");
                    break;
                case 4:
                    tab.setText("场景特效");
                    break;
                case 5:
                    tab.setText("动画特效");
                    break;
            }
        }).attach();
    }

    private void connectToDevice() {
        if (deviceAddress == null) {
            Toast.makeText(this, "设备地址无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
    }

    private void disconnect() {
        if (bluetoothGatt != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (bluetoothGatt != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (ActivityCompat.checkSelfPermission(DeviceControlActivity.this,
                        Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                runOnUiThread(() -> Toast.makeText(DeviceControlActivity.this,
                        "已连接到设备", Toast.LENGTH_SHORT).show());
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread(() -> {
                    Toast.makeText(DeviceControlActivity.this,
                            "设备已断开连接", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(SERVICE_UUID);
                if (service != null) {
                    controlCharacteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
                    if (controlCharacteristic != null) {
                        // 设置各Fragment的特征值
                        runOnUiThread(() -> {
                            cctFragment.setCharacteristic(controlCharacteristic);
                            rgbFragment.setCharacteristic(controlCharacteristic);
                            hsiFragment.setCharacteristic(controlCharacteristic);
                            paperFragment.setCharacteristic(controlCharacteristic);
                            sceneFragment.setCharacteristic(controlCharacteristic);
                            animationFragment.setCharacteristic(controlCharacteristic);
                        });
                    }
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                runOnUiThread(() -> Toast.makeText(DeviceControlActivity.this,
                        "命令发送成功", Toast.LENGTH_SHORT).show());
            } else {
                runOnUiThread(() -> Toast.makeText(DeviceControlActivity.this,
                        "命令发送失败", Toast.LENGTH_SHORT).show());
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }
}