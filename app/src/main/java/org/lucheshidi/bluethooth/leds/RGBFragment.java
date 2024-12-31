package org.lucheshidi.bluethooth.leds;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.bluetooth.BluetoothGattCharacteristic;

public class RGBFragment extends Fragment {
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private TextView redValueText;
    private TextView greenValueText;
    private TextView blueValueText;
    private BluetoothGattCharacteristic characteristic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rgb, container, false);

        redSeekBar = view.findViewById(R.id.redSeekBar);
        greenSeekBar = view.findViewById(R.id.greenSeekBar);
        blueSeekBar = view.findViewById(R.id.blueSeekBar);
        redValueText = view.findViewById(R.id.redValue);
        greenValueText = view.findViewById(R.id.greenValue);
        blueValueText = view.findViewById(R.id.blueValue);

        setupSeekBar(redSeekBar, redValueText);
        setupSeekBar(greenSeekBar, greenValueText);
        setupSeekBar(blueSeekBar, blueValueText);

        return view;
    }

    private void setupSeekBar(SeekBar seekBar, final TextView valueText) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueText.setText(String.valueOf(progress));
                if(fromUser) {
                    sendRGBCommand();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void sendRGBCommand() {
        if (characteristic != null) {
            // 构建RGB命令
            byte[] command = new byte[5];
            command[0] = (byte) 0xA2;  // RGB模式命令头
            command[1] = (byte) redSeekBar.getProgress();
            command[2] = (byte) greenSeekBar.getProgress();
            command[3] = (byte) blueSeekBar.getProgress();
            command[4] = (byte) 0xFF;  // 结束符

            characteristic.setValue(command);
            ((DeviceControlActivity) getActivity()).writeCharacteristic(characteristic);
        }
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }
}