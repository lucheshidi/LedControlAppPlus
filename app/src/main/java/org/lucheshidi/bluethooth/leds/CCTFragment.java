package org.lucheshidi.bluethooth.leds;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.bluetooth.BluetoothGattCharacteristic;

public class CCTFragment extends Fragment {
    private SeekBar warmSeekBar;
    private SeekBar coolSeekBar;
    private TextView warmValueText;
    private TextView coolValueText;
    private BluetoothGattCharacteristic characteristic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cct, container, false);

        warmSeekBar = view.findViewById(R.id.warmSeekBar);
        coolSeekBar = view.findViewById(R.id.coolSeekBar);
        warmValueText = view.findViewById(R.id.warmValue);
        coolValueText = view.findViewById(R.id.coolValue);

        // 设置暖光滑块
        warmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                warmValueText.setText(progress + "%");
                if(fromUser) {
                    sendCCTCommand(progress, coolSeekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // 设置冷光滑块
        coolSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                coolValueText.setText(progress + "%");
                if(fromUser) {
                    sendCCTCommand(warmSeekBar.getProgress(), progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return view;
    }

    private void sendCCTCommand(int warmValue, int coolValue) {
        if (characteristic != null) {
            // 构建命令数据
            byte[] command = new byte[4];
            command[0] = (byte) 0xA1;  // CCT模式命令头
            command[1] = (byte) warmValue;  // 暖光值
            command[2] = (byte) coolValue;  // 冷光值
            command[3] = (byte) 0xFF;  // 结束符

            characteristic.setValue(command);
            // 通过DeviceControlActivity发送数据
            ((DeviceControlActivity) getActivity()).writeCharacteristic(characteristic);
        }
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }
}