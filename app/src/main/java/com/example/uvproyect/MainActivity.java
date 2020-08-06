package com.example.uvproyect;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{

    FrameLayout power_layout;
    DTimer dTimer;
    Button bt_up, bt_down, bt_power, bt_home;
    ImageView[] timerDisplay;
    DisplayManager dm;

    final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter adapter;
    int REQUEST_ENABLE_BT = 1;//request code for enable
    Intent enableBluetooth;
    BluetoothSocket actualTower;
    List<BluetoothDevice> devices;
    int index;

    //ServerSocket ss;
    //Socket actualTower;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemUI();
        hideReinforce();
        initTimer();
        initDisplay();
        adapter = BluetoothAdapter.getDefaultAdapter();
        enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        bt_power = findViewById(R.id.bt_power);
        power_layout = findViewById(R.id.imageFrame);
        bt_home = findViewById(R.id.bt_home);
        bt_home.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(actualTower !=null) {
                    try {
                        actualTower.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                devices = new ArrayList<>();
                bt_home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connect(v);
                    }
                });
                dm.setTextOnDisplay("connection off");
                return false;
            }
        });
        dm.setTextOnDisplay("Not connected");
    }

    private void initBluetooth() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if(permissionCheck != 0)
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1001);
        }
        if (adapter == null) {
            //Device does not support bluetooth
            Toast.makeText(getApplicationContext(), "Bluetooth does not support on this Device", Toast.LENGTH_LONG).show();
        } else {
            if (!adapter.isEnabled()) {
                //Code to enable bluetooth
                startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (requestCode == RESULT_OK) {
                //Bluetooth is enabled
                Toast.makeText(getApplicationContext(), "Bluetooth is enabled", Toast.LENGTH_LONG).show();
            } else if (requestCode == RESULT_CANCELED) {
                //Bluetooth is cancelled
                Toast.makeText(getApplicationContext(), "Bluetooth is cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void connect(View view){
        initBluetooth();
        if(mp != null && mp.isPlaying())
            mp.release();
        mp = MediaPlayer.create(this, R.raw.homebutton);
        mp.start();
        devices = new ArrayList<>();
        devices.addAll(adapter.getBondedDevices());
        if(!devices.isEmpty()) {
            dm.setTextOnDisplay("select tower");
            bt_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(v);
                }
            });
        }
        else
            dm.setTextOnDisplay("error");
        index = 0;
    }

    public void select(View view){
        dm.setTextOnDisplay("connecting");
        BluetoothDevice device = devices.get(index);
        BluetoothSocket socket = null;
        int count = 0;
        do {
            try {
                actualTower = device.createInsecureRfcommSocketToServiceRecord(uuid);
                actualTower.connect();
                count++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }while(!actualTower.isConnected() && count!=10);
        if(!actualTower.isConnected())
            dm.setTextOnDisplay("unable to connect");
        else
            dm.setTextOnDisplay("connected");
    }

    public void moveLeft(View view){
        if(mp != null && mp.isPlaying())
            mp.release();
        mp = MediaPlayer.create(this, R.raw.button);
        mp.start();
        if(!devices.isEmpty()){
            if(index!=0) {
                index--;
                dm.setTextOnDisplay(devices.get(index).getAddress().substring(0,14));
            }
        }
    }

    public void moveRight(View view){
        if(mp != null && mp.isPlaying())
            mp.release();
        mp = MediaPlayer.create(this, R.raw.button);
        mp.start();
        if(!devices.isEmpty()){
            if(devices.size() != index+1) {
                index++;
                dm.setTextOnDisplay(devices.get(index).getAddress().substring(0,14));
            }
        }
    }

    public void addTime(View view){
        if(mp != null && mp.isPlaying())
            mp.release();
        mp = MediaPlayer.create(this, R.raw.button);
        mp.start();
        dTimer.addTime();
    }
    public void takeTime(View view){
        if(mp != null && mp.isPlaying())
            mp.release();
        mp = MediaPlayer.create(this, R.raw.button);
        mp.start();
        dTimer.takeTime();}

    public void poweron(View view){
        if(actualTower == null){
            Toast.makeText(this, "No tower connected yet", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!dTimer.isReady()){
            Toast.makeText(this, "There is no time in the counter", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mp != null && mp.isPlaying())
            mp.release();
        mp = MediaPlayer.create(this, R.raw.poweron);
        mp.start();
        power_layout.setBackgroundResource(R.drawable.poweron);
        dTimer.setActualTower(actualTower);
        dTimer.countDown();
        bt_power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poweroff(v);
            }
        });
    }

    public void poweroff(View view){
        if(mp != null && mp.isPlaying())
            mp.release();
        mp = MediaPlayer.create(this, R.raw.poweroff);
        mp.start();
        if(dTimer.isTimerOn()) {
            dTimer.stoptimer();
        }else{
            try {
                OutputStream outputStream = actualTower.getOutputStream();
                outputStream.write("*20".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        power_layout.setBackgroundResource(R.drawable.poweroff);
        bt_power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poweron(v);
            }
        });
    }

    private void hideSystemUI(){
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void hideReinforce(){
        findViewById(R.id.centerframe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSystemUI();
            }
        });
        findViewById(R.id.powerButtonFrame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSystemUI();
            }
        });
    }

    private void initTimer() {
        timerDisplay =  new ImageView[8];
        timerDisplay[0] = findViewById(R.id.timer_slot1);
        timerDisplay[1] = findViewById(R.id.timer_slot2);
        timerDisplay[2] = findViewById(R.id.timer_points1);
        timerDisplay[3] = findViewById(R.id.timer_slot3);
        timerDisplay[4] = findViewById(R.id.timer_slot4);
        timerDisplay[5] = findViewById(R.id.timer_points2);
        timerDisplay[6] = findViewById(R.id.timer_slot5);
        timerDisplay[7] = findViewById(R.id.timer_slot6);
        bt_up = findViewById(R.id.bt_up);
        bt_down = findViewById(R.id.bt_down);
        dTimer = new DTimer(timerDisplay, this);
    }

    private void initDisplay() {
        ImageView[] slots = new ImageView[20];
        slots[0] = findViewById(R.id.dslot1);
        slots[1] = findViewById(R.id.dslot2);
        slots[2] = findViewById(R.id.dslot3);
        slots[3] = findViewById(R.id.dslot4);
        slots[4] = findViewById(R.id.dslot5);
        slots[5] = findViewById(R.id.dslot6);
        slots[6] = findViewById(R.id.dslot7);
        slots[7] = findViewById(R.id.dslot8);
        slots[8] = findViewById(R.id.dslot9);
        slots[9] = findViewById(R.id.dslot10);
        slots[10] = findViewById(R.id.dslot11);
        slots[11] = findViewById(R.id.dslot12);
        slots[12] = findViewById(R.id.dslot13);
        slots[13] = findViewById(R.id.dslot14);
        slots[14] = findViewById(R.id.dslot15);
        slots[15] = findViewById(R.id.dslot16);
        slots[16] = findViewById(R.id.dslot17);
        slots[17] = findViewById(R.id.dslot18);
        slots[18] = findViewById(R.id.dslot19);
        slots[19] = findViewById(R.id.dslot20);
        dm = new DisplayManager(slots);
    }

}

