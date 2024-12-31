package org.lucheshidi.bluethooth.leds;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import androidx.fragment.app.Fragment;
import android.bluetooth.BluetoothGattCharacteristic;

public class AnimationFragment extends Fragment implements View.OnClickListener {
    private BluetoothGattCharacteristic characteristic;
    private Button[] animationButtons = new Button[6];
    private SeekBar speedSeekBar;
    private int currentAnimation = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_animation, container, false);

        // 初始化所有按钮
        for (int i = 0; i < 6; i++) {
            int buttonId = getResources().getIdentifier("animation" + (i + 1), "id",
                    requireActivity().getPackageName());
            animationButtons[i] = view.findViewById(buttonId);
            animationButtons[i].setOnClickListener(this);
        }

        speedSeekBar = view.findViewById(R.id.speedSeekBar);
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && currentAnimation != -1) {
                    sendAnimationCommand(currentAnimation, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        // 获取被点击的按钮索引
        int index = -1;
        for (int i = 0; i < animationButtons.length; i++) {
            if (v == animationButtons[i]) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            currentAnimation = index;
            sendAnimationCommand(index, speedSeekBar.getProgress());
            updateButtonStates(index);
        }
    }

    private void updateButtonStates(int selectedIndex) {
        // 更新按钮状态，选中的按钮显示为激活状态
        for (int i = 0; i < animationButtons.length; i++) {
            animationButtons[i].setSelected(i == selectedIndex);
        }
    }

    private void sendAnimationCommand(int animationIndex, int speed) {
        if (characteristic != null) {
            // 构建动画命令
            byte[] command = new byte[4];
            command[0] = (byte) 0xA6;  // 动画模式命令头
            command[1] = (byte) animationIndex;  // 动画索引
            command[2] = (byte) speed;  // 动画速度
            command[3] = (byte) 0xFF;  // 结束符

            characteristic.setValue(command);
            ((DeviceControlActivity) getActivity()).writeCharacteristic(characteristic);
        }
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }
}