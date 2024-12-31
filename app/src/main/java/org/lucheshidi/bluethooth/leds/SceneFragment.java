package org.lucheshidi.bluethooth.leds;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import android.bluetooth.BluetoothGattCharacteristic;

public class SceneFragment extends Fragment implements View.OnClickListener {
    private BluetoothGattCharacteristic characteristic;
    private Button[] sceneButtons = new Button[6];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scene, container, false);

        // 初始化所有按钮
        for (int i = 0; i < 6; i++) {
            int buttonId = getResources().getIdentifier("scene" + (i + 1), "id",
                    requireActivity().getPackageName());
            sceneButtons[i] = view.findViewById(buttonId);
            sceneButtons[i].setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        // 获取被点击的按钮索引
        int index = -1;
        for (int i = 0; i < sceneButtons.length; i++) {
            if (v == sceneButtons[i]) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            sendSceneCommand(index);
        }
    }

    private void sendSceneCommand(int index) {
        if (characteristic != null) {
            // 构建场景命令
            byte[] command = new byte[3];
            command[0] = (byte) 0xA5;  // 场景模式命令头
            command[1] = (byte) index;  // 场景索引
            command[2] = (byte) 0xFF;  // 结束符

            characteristic.setValue(command);
            ((DeviceControlActivity) getActivity()).writeCharacteristic(characteristic);
        }
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }
}