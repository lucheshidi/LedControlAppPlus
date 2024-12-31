package org.lucheshidi.bluethooth.leds;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.bluetooth.BluetoothGattCharacteristic;

public class HSIFragment extends Fragment {
    private SeekBar hueSeekBar;
    private SeekBar saturationSeekBar;
    private SeekBar intensitySeekBar;
    private TextView hueValueText;
    private TextView saturationValueText;
    private TextView intensityValueText;
    private BluetoothGattCharacteristic characteristic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hsi, container, false);

        hueSeekBar = view.findViewById(R.id.hueSeekBar);
        saturationSeekBar = view.findViewById(R.id.saturationSeekBar);
        intensitySeekBar = view.findViewById(R.id.intensitySeekBar);
        hueValueText = view.findViewById(R.id.hueValue);
        saturationValueText = view.findViewById(R.id.saturationValue);
        intensityValueText = view.findViewById(R.id.intensityValue);

        // 设置色调范围 0-360
        hueSeekBar.setMax(360);
        setupSeekBar(hueSeekBar, hueValueText, "°");

        // 设置饱和度和亮度范围 0-100
        setupSeekBar(saturationSeekBar, saturationValueText, "%");
        setupSeekBar(intensitySeekBar, intensityValueText, "%");

        return view;
    }

    private void setupSeekBar(SeekBar seekBar, final TextView valueText, final String unit) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueText.setText(progress + unit);
                if(fromUser) {
                    sendHSICommand();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void sendHSICommand() {
        if (characteristic != null) {
            // 构建HSI命令
            byte[] command = new byte[5];
            command[0] = (byte) 0xA3;  // HSI模式命令头
            command[1] = (byte) (hueSeekBar.getProgress() & 0xFF);  // 色调低8位
            command[2] = (byte) ((hueSeekBar.getProgress() >> 8) & 0xFF);  // 色调高8位
            command[3] = (byte) saturationSeekBar.getProgress();
            command[4] = (byte) intensitySeekBar.getProgress();
            command[5] = (byte) 0xFF;  // 结束符

            characteristic.setValue(command);
            ((DeviceControlActivity) getActivity()).writeCharacteristic(characteristic);
        }
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }
}