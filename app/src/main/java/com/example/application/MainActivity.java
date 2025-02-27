//package com.example.application;
//
//import android.annotation.SuppressLint;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.SwitchCompat;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.UUID;
//
//
//public class MainActivity extends AppCompatActivity {
//    private SwitchCompat switchControl;
//    private EditText speed;
//    private EditText ipAddress;
//    private ImageButton btnLeft, btnUp, btnRight, btnDown, btnOk, btn_wifi, btn_Bluetooth;
//    private TextView statusText, labeled, labeled_2;
//
//    private BluetoothAdapter bluetoothAdapter;
//    private BluetoothSocket bluetoothSocket;
//    private OutputStream outputStream;
//
//    private boolean isBluetoothMode = false; // true = Bluetooth, false = WiFi (Mặc định sẽ là Wifi)
//    private boolean isModeSelected = false; // Thực hiện kiểm tra đã được click hay chưa
//
//    private static final String DEVICE_NAME = "ESP32_BT"; // Tên Bluetooth của ESP32
//    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//
//    @SuppressLint("SetTextI18n")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//
//        switchControl = findViewById(R.id.switch_button);
//        labeled = findViewById(R.id.labeled);
//        labeled_2 = findViewById(R.id.labeled_2);
//        speed = findViewById(R.id.speed);
//        btnLeft = findViewById(R.id.btn_left);
//        btnUp = findViewById(R.id.btn_up);
//        btnRight = findViewById(R.id.btn_right);
//        btnDown = findViewById(R.id.btn_down);
//        statusText = findViewById(R.id.status_text);
//        ipAddress = findViewById(R.id.IP_address);
//        btnOk = findViewById(R.id.btn_ok);
//        btn_wifi = findViewById(R.id.wifi);
//        btn_Bluetooth = findViewById(R.id.Bluetooth);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        hideControls();
//
//
//        btn_Bluetooth.setOnClickListener(v -> {
//            isBluetoothMode = true;
//            isModeSelected = true; // Đánh dấu là đã click
//            btn_Bluetooth.setColorFilter(getResources().getColor(R.color.blue)); // Đổi màu
//            btn_wifi.setColorFilter(getResources().getColor(R.color.gray)); // Reset màu WiFi
//            showControls();
//            setControlsEnabled(false); // Vô hiệu hóa trước khi bật switchControl
//            statusText.setText("OFF");
//            switchControl.setChecked(false);
//        });
//
//        btn_wifi.setOnClickListener(v -> {
//            isBluetoothMode = false;
//            isModeSelected = true; // Đánh dấu là đã click
//            btn_wifi.setColorFilter(getResources().getColor(R.color.blue)); // Đổi màu
//            btn_Bluetooth.setColorFilter(getResources().getColor(R.color.gray)); // Reset màu Bluetooth
//            showControls();
//            setControlsEnabled(false); // Vô hiệu hóa toàn bộ điều khiển
//            statusText.setText("OFF");
//            switchControl.setChecked(false);
//        });
//
//        switchControl.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            try {
//                if (isChecked) {
//                    if(isBluetoothMode){
//                        statusText.setText("ON");
//                        connectBluetooth();
//                    }
//                    else{
//                        Toast.makeText(this, "Chạy của Wifi!", Toast.LENGTH_SHORT).show();
//                        statusText.setText("ON");
//                        connectWifi();
//                    }
//
//
//                    setControlsEnabled(true);
//                } else {
//                    statusText.setText("OFF");
//                    disconnectBluetooth();
//                    setControlsEnabled(false);
//                }
//            } catch (Exception e) {
//                showErrorDialog("Lỗi không tìm thấy thiết bị");
//                disconnectBluetooth();
//                setControlsEnabled(false);
//                e.printStackTrace();
//            }
//        });
//
//
//        ipAddress.setText("192.168.1.1");
//
//        if (btnUp != null) btnUp.setOnClickListener(v -> click_Up());
//        if (btnDown != null) btnDown.setOnClickListener(v -> click_Down());
//        if (btnLeft != null) btnLeft.setOnClickListener(v -> click_Left());
//        if (btnRight != null) btnRight.setOnClickListener(v -> click_Right());
//        if (btnOk != null) btnOk.setOnClickListener(v -> click_sendSpeed());
//        setControlsEnabled(false);
//    }
//
//    // Ẩn các điều khiển khi chưa chọn chế độ
//    private void hideControls() {
//        showErrorDialog("Vui lòng chọn Bluetooth hoặc WiFi trước!");
//        switchControl.setVisibility(View.GONE);
//        labeled.setVisibility(View.GONE);
//        labeled_2.setVisibility(View.GONE);
//        speed.setVisibility(View.GONE);
//        btnLeft.setVisibility(View.GONE);
//        btnUp.setVisibility(View.GONE);
//        btnRight.setVisibility(View.GONE);
//        btnDown.setVisibility(View.GONE);
//        statusText.setVisibility(View.GONE);
//        btnOk.setVisibility(View.GONE);
//    }
//
//    // Hiển thị các điều khiển sau khi chọn WiFi/Bluetooth
//    private void showControls() {
//        switchControl.setVisibility(View.VISIBLE);
//        labeled.setVisibility(View.VISIBLE);
//        labeled_2.setVisibility(View.VISIBLE);
//        speed.setVisibility(View.VISIBLE);
//        btnLeft.setVisibility(View.VISIBLE);
//        btnUp.setVisibility(View.VISIBLE);
//        btnRight.setVisibility(View.VISIBLE);
//        btnDown.setVisibility(View.VISIBLE);
//        statusText.setVisibility(View.VISIBLE);
//        btnOk.setVisibility(View.VISIBLE);
//    }
//
//    private void connectWifi(){
//
//    }
//
//
//    @SuppressLint("SetTextI18n")
//    private void connectBluetooth() {
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null) {
//            showErrorDialog("Thiết bị không hỗ trợ Bluetooth");
//            disconnectBluetooth();
//            setControlsEnabled(false);
//            switchControl.setChecked(false);
//            statusText.setText("OFF");
//            return;
//        }
//
//        if (!bluetoothAdapter.isEnabled()) {
//            showErrorDialog("Bluetooth chưa bật");
//            disconnectBluetooth();
//            setControlsEnabled(false);
//            switchControl.setChecked(false);
//            statusText.setText("OFF");
//            return;
//        }
//
//        BluetoothDevice device = null;
//        for (BluetoothDevice pairedDevice : bluetoothAdapter.getBondedDevices()) {
//            if (pairedDevice.getName().equals(DEVICE_NAME)) {
//                device = pairedDevice;
//                break;
//            }
//        }
//
//        if (device == null) {
//            showErrorDialog("Không tìm thấy ESP32");
//            disconnectBluetooth();
//            setControlsEnabled(false);
//            switchControl.setChecked(false);
//            statusText.setText("OFF");
//            return;
//        }
//
//        try {
//            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
//            bluetoothSocket.connect();
//            outputStream = bluetoothSocket.getOutputStream();
//            Toast.makeText(this, "Kết nối thành công!", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            showErrorDialog("Kết nối thất bại");
//            disconnectBluetooth();
//            setControlsEnabled(false);
//            switchControl.setChecked(false);
//            statusText.setText("OFF");
//            e.printStackTrace();
//        }
//    }
//
//
//    @SuppressLint("SetTextI18n")
//    private void sendData(String data) {
//
//        if(isBluetoothMode){
//            if (outputStream == null) {
//                showErrorDialog("Chưa kết nối Bluetooth!");
//                disconnectBluetooth();
//                setControlsEnabled(false);
//                switchControl.setChecked(false);
//                statusText.setText("OFF");
//                return;
//            }
//            try {
//                outputStream.write(data.getBytes());
//                // Toast.makeText(this, "Gửi: " + data, Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                showErrorDialog("Mất kết nối Bluetooth");
//                disconnectBluetooth();
//                setControlsEnabled(false);
//                switchControl.setChecked(false);
//                statusText.setText("OFF");
//                e.printStackTrace();
//            }
//        }
//        else{
//            Toast.makeText(this, "Dữ liệu: " + outputStream, Toast.LENGTH_SHORT).show();
//            if (outputStream == null) {
//                // Toast.makeText(this, "Chưa kết nối Bluetooth!", Toast.LENGTH_SHORT).show();
//                showErrorDialog("Đang tìm kết nối Wifi!");
//                statusText.setText("OFF");
//                return;
//            }
//            try {
//                outputStream.write(data.getBytes());
//                 Toast.makeText(this, "Gửi dữ liệu Wifi: " + data, Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                statusText.setText("OFF");
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void disconnectBluetooth() {
//        try {
//            if (outputStream != null) {
//                outputStream.close();
//                outputStream = null;
//            }
//            if (bluetoothSocket != null) {
//                bluetoothSocket.close();
//                bluetoothSocket = null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Đóng Bluetooth khi ứng dụng bị đóng
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        disconnectBluetooth();
//    }
//
//    // Hàm bật/tắt các điều khiển
//    private void setControlsEnabled(boolean isEnabled) {
//        btnUp.setEnabled(isEnabled);
//        btnDown.setEnabled(isEnabled);
//        btnLeft.setEnabled(isEnabled);
//        btnRight.setEnabled(isEnabled);
//        btnOk.setEnabled(isEnabled);
//        speed.setEnabled(isEnabled);
//    }
//
//    private void click_Up(){
//        sendData("U");
//        Toast.makeText(this, "Đã click hướng lên", Toast.LENGTH_SHORT).show();
//    }
//
//    private void click_Down(){
//        sendData("D");
//        Toast.makeText(this, "Đã click hướng xuống", Toast.LENGTH_SHORT).show();
//    }
//
//    private void click_Left(){
//        sendData("L");
//        Toast.makeText(this, "Đã click hướng trái", Toast.LENGTH_SHORT).show();
//    }
//
//    private void click_Right(){
//        sendData("R");
//        Toast.makeText(this, "Đã click hướng phải", Toast.LENGTH_SHORT).show();
//    }
//
//    private void click_sendSpeed(){
//        String speedValue = speed.getText().toString();
//        sendData(speedValue);
//        speed.setText("");
//    }
//
//    private void showErrorDialog(String text){
//        ConstraintLayout errorConstrainLayout = findViewById(R.id.errorConstrainLayout);
//        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.error_dialog, errorConstrainLayout);
//        Button errorClose = view.findViewById(R.id.errorClose);
//        TextView StringText = view.findViewById(R.id.errorDesc);
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setView(view);
//        final AlertDialog alertDialog = builder.create();
//        StringText.setText(text);
//        errorClose.findViewById(R.id.errorClose).setOnClickListener(v -> {
//            alertDialog.dismiss();
//            // Toast.makeText(MainActivity.this, "Close", Toast.LENGTH_SHORT).show();
//        });
//        if (alertDialog.getWindow()!= null){
//            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//        }
//        alertDialog.show();
//    }
//}













































package com.example.application;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private SwitchCompat switchControl;
    private EditText speed;
    private EditText ipAddress;
    private ImageButton btnLeft, btnUp, btnRight, btnDown, btnOk, btn_wifi, btn_Bluetooth;
    private TextView statusText, labeled, labeled_2;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    private Socket wifiSocket; // Socket cho WiFi

    private boolean isBluetoothMode = false; // true = Bluetooth, false = WiFi
    private boolean isModeSelected = false; // Kiểm tra đã chọn chế độ chưa

    private static final String DEVICE_NAME = "ESP32_BT"; // Tên Bluetooth của ESP32
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int WIFI_PORT = 80; // Cổng WiFi (phải khớp với ESP32)

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        switchControl = findViewById(R.id.switch_button);
        labeled = findViewById(R.id.labeled);
        labeled_2 = findViewById(R.id.labeled_2);
        speed = findViewById(R.id.speed);
        btnLeft = findViewById(R.id.btn_left);
        btnUp = findViewById(R.id.btn_up);
        btnRight = findViewById(R.id.btn_right);
        btnDown = findViewById(R.id.btn_down);
        statusText = findViewById(R.id.status_text);
        ipAddress = findViewById(R.id.IP_address);
        btnOk = findViewById(R.id.btn_ok);
        btn_wifi = findViewById(R.id.wifi);
        btn_Bluetooth = findViewById(R.id.Bluetooth);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        hideControls();

        btn_Bluetooth.setOnClickListener(v -> {
            isBluetoothMode = true;
            isModeSelected = true;
            btn_Bluetooth.setColorFilter(getResources().getColor(R.color.blue));
            btn_wifi.setColorFilter(getResources().getColor(R.color.gray));
            showControls();
            setControlsEnabled(false);
            statusText.setText("OFF");
            switchControl.setChecked(false);
        });

        btn_wifi.setOnClickListener(v -> {
            isBluetoothMode = false;
            isModeSelected = true;
            btn_wifi.setColorFilter(getResources().getColor(R.color.blue));
            btn_Bluetooth.setColorFilter(getResources().getColor(R.color.gray));
            showControls();
            setControlsEnabled(false);
            statusText.setText("OFF");
            switchControl.setChecked(false);
            ipAddress.setVisibility(View.VISIBLE);
        });

        switchControl.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                if (isChecked) {
                    if (isBluetoothMode) {
                        statusText.setText("ON");
                        connectBluetooth();
                    } else {
                        String ip_Check = ipAddress.getText().toString().trim();
                        // Kiểm tra xem địa chỉ IP đã được nhập chưa
                        if (ip_Check.isEmpty()) {
                            showErrorDialog("Chưa nhập địa chỉ IP ESP32");
                        } else {
                            // Nếu có dữ liệu, bạn có thể thực hiện các hành động khác
                            statusText.setText("ON");
                            connectWifi();
                            Toast.makeText(this, "Đã kết nối WiFi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    setControlsEnabled(true);
                } else {
                    statusText.setText("OFF");
                    if (isBluetoothMode) {
                        disconnectBluetooth();
                    } else {
                        disconnectWifi();
                    }
                    setControlsEnabled(false);
                }
            } catch (Exception e) {
                showErrorDialog("Lỗi kết nối tới thiết bị");
                if (isBluetoothMode) {
                    disconnectBluetooth();
                } else {
                    disconnectWifi();
                }
                setControlsEnabled(false);
                e.printStackTrace();
            }
        });

        // ipAddress.setText("192.168.0.102"); // Địa chỉ IP mặc định, thay đổi theo ESP32 của bạn

        if (btnUp != null) btnUp.setOnClickListener(v -> click_Up());
        if (btnDown != null) btnDown.setOnClickListener(v -> click_Down());
        if (btnLeft != null) btnLeft.setOnClickListener(v -> click_Left());
        if (btnRight != null) btnRight.setOnClickListener(v -> click_Right());
        if (btnOk != null) btnOk.setOnClickListener(v -> click_sendSpeed());
        setControlsEnabled(false);
    }

    // Ẩn các điều khiển khi chưa chọn chế độ
    private void hideControls() {
        showErrorDialog("Vui lòng chọn Bluetooth hoặc WiFi trước!");
        switchControl.setVisibility(View.GONE);
        labeled.setVisibility(View.GONE);
        labeled_2.setVisibility(View.GONE);
        speed.setVisibility(View.GONE);
        btnLeft.setVisibility(View.GONE);
        btnUp.setVisibility(View.GONE);
        btnRight.setVisibility(View.GONE);
        btnDown.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);
        ipAddress.setVisibility(View.GONE);
    }

    // Hiển thị các điều khiển sau khi chọn WiFi/Bluetooth
    private void showControls() {
        switchControl.setVisibility(View.VISIBLE);
        labeled.setVisibility(View.VISIBLE);
        labeled_2.setVisibility(View.VISIBLE);
        speed.setVisibility(View.VISIBLE);
        btnLeft.setVisibility(View.VISIBLE);
        btnUp.setVisibility(View.VISIBLE);
        btnRight.setVisibility(View.VISIBLE);
        btnDown.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.VISIBLE);
        btnOk.setVisibility(View.VISIBLE);
    }

    private void connectWifi() {
        String ip = ipAddress.getText().toString();
        Toast.makeText(this, "IP: " + ip, Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            try {
                // Log.d("WiFi", "Đang kết nối tới " + ip + ":" + WIFI_PORT);
                wifiSocket = new Socket(ip, WIFI_PORT);
                outputStream = wifiSocket.getOutputStream();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Kết nối WiFi thành công!", Toast.LENGTH_SHORT).show();
                    // Log.d("WiFi", "Kết nối thành công, outputStream sẵn sàng");
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    showErrorDialog("Không thể kết nối tới WiFi");
                    statusText.setText("OFF");
                    switchControl.setChecked(false);
                    setControlsEnabled(false);
                });
                // Log.e("WiFi", "Lỗi kết nối: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void disconnectWifi() {
        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            if (wifiSocket != null) {
                wifiSocket.close();
                wifiSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void connectBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            showErrorDialog("Thiết bị không hỗ trợ Bluetooth");
            disconnectBluetooth();
            setControlsEnabled(false);
            switchControl.setChecked(false);
            statusText.setText("OFF");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            showErrorDialog("Bluetooth chưa bật");
            disconnectBluetooth();
            setControlsEnabled(false);
            switchControl.setChecked(false);
            statusText.setText("OFF");
            return;
        }

        BluetoothDevice device = null;
        for (BluetoothDevice pairedDevice : bluetoothAdapter.getBondedDevices()) {
            if (pairedDevice.getName().equals(DEVICE_NAME)) {
                device = pairedDevice;
                break;
            }
        }

        if (device == null) {
            showErrorDialog("Không tìm thấy ESP32");
            disconnectBluetooth();
            setControlsEnabled(false);
            switchControl.setChecked(false);
            statusText.setText("OFF");
            return;
        }

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            Toast.makeText(this, "Kết nối thành công!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            showErrorDialog("Kết nối thất bại");
            disconnectBluetooth();
            setControlsEnabled(false);
            switchControl.setChecked(false);
            statusText.setText("OFF");
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void sendData(String data) {
        if (isBluetoothMode) {
            if (outputStream == null) {
                showErrorDialog("Chưa kết nối Bluetooth!");
                disconnectBluetooth();
                setControlsEnabled(false);
                switchControl.setChecked(false);
                statusText.setText("OFF");
                return;
            }
            try {
                // Toast.makeText(this, "Gửi dữ liệu Bluetooth", Toast.LENGTH_SHORT).show();
                outputStream.write(data.getBytes());
            } catch (IOException e) {
                showErrorDialog("Mất kết nối Bluetooth");
                disconnectBluetooth();
                setControlsEnabled(false);
                switchControl.setChecked(false);
                statusText.setText("OFF");
                e.printStackTrace();
            }
        } else {
            if (outputStream == null) {
                runOnUiThread(() -> {
                    showErrorDialog("Chưa kết nối WiFi!");
                    statusText.setText("OFF");
                    switchControl.setChecked(false);
                    setControlsEnabled(false);
                });
                // Log.e("WiFi", "outputStream là null, không thể gửi dữ liệu");
                return;
            }
            new Thread(() -> {
                try {
                    String dataWithNewline = data + "\n"; // Thêm \n
                    outputStream.write(dataWithNewline.getBytes());
                    outputStream.flush();
                    // runOnUiThread(() -> Toast.makeText(this, "Gửi dữ liệu WiFi: " + data, Toast.LENGTH_SHORT).show());
                } catch (IOException e) {
                    runOnUiThread(() -> {
                        showErrorDialog("Mất kết nối WiFi");
                        disconnectWifi();
                        statusText.setText("OFF");
                        switchControl.setChecked(false);
                        setControlsEnabled(false);
                    });
                    Log.e("WiFi", "Lỗi gửi dữ liệu: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void disconnectBluetooth() {
        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectBluetooth();
        disconnectWifi();
    }

    private void setControlsEnabled(boolean isEnabled) {
        btnUp.setEnabled(isEnabled);
        btnDown.setEnabled(isEnabled);
        btnLeft.setEnabled(isEnabled);
        btnRight.setEnabled(isEnabled);
        btnOk.setEnabled(isEnabled);
        speed.setEnabled(isEnabled);
    }

    private void click_Up() {
        sendData("U");
        // Toast.makeText(this, "Đã click hướng lên", Toast.LENGTH_SHORT).show();
    }

    private void click_Down() {
        sendData("D");
        // Toast.makeText(this, "Đã click hướng xuống", Toast.LENGTH_SHORT).show();
    }

    private void click_Left() {
        sendData("L");
        // Toast.makeText(this, "Đã click hướng trái", Toast.LENGTH_SHORT).show();
    }

    private void click_Right() {
        sendData("R");
        // Toast.makeText(this, "Đã click hướng phải", Toast.LENGTH_SHORT).show();
    }

    private void click_sendSpeed() {
        String speedValue = speed.getText().toString();
        sendData(speedValue);
        speed.setText("");
    }

    private void showErrorDialog(String text) {
        ConstraintLayout errorConstrainLayout = findViewById(R.id.errorConstrainLayout);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.error_dialog, errorConstrainLayout);
        Button errorClose = view.findViewById(R.id.errorClose);
        TextView StringText = view.findViewById(R.id.errorDesc);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        StringText.setText(text);
        errorClose.setOnClickListener(v -> alertDialog.dismiss());
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}