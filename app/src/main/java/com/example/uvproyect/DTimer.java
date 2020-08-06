package com.example.uvproyect;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;


public class DTimer {
    private ImageView[] timerDisplay;
    private int[] numbers;
    private int second, minute, hour;
    Activity activity;
    Timer timer;
    boolean timerOn;
    BluetoothSocket actualTower;

    DTimer(ImageView[] timerDisplay, Activity activity) {
        second = minute = hour = 0;
        timerOn = false;
        this.activity = activity;
        numbers = new int[]{
                R.drawable.n0, R.drawable.n1, R.drawable.n2,
                R.drawable.n3, R.drawable.n4, R.drawable.n5,
                R.drawable.n6, R.drawable.n7, R.drawable.n8,
                R.drawable.n9};
        for (int i = 0; i < timerDisplay.length; i++) {
            if (i == 2 || i == 5)
                continue;
            timerDisplay[i].setImageResource(numbers[0]);
        }
        this.timerDisplay = timerDisplay;
    }

    public void addTime() {
        if (second < 60) {
            second++;
            setDisplaySecond();
        }
        if (second == 60) {
            second = 0;
            minute++;
            setDisplaySecond();
            setDisplayMinute();
        }
        if (minute == 60) {
            minute = 0;
            hour++;
            setDisplayHour();
        }
    }

    private void setDisplayHour() {
        String num = "" + hour;
        if (num.length() < 2) {
            timerDisplay[1].setImageResource(numbers[hour]);
            timerDisplay[0].setImageResource(numbers[0]);
        } else {
            timerDisplay[1].setImageResource(numbers[Integer.parseInt("" + num.charAt(1))]);
            timerDisplay[0].setImageResource(numbers[Integer.parseInt("" + num.charAt(0))]);
        }
    }

    private void setDisplayMinute() {
        String num = "" + minute;
        if (num.length() < 2) {
            timerDisplay[4].setImageResource(numbers[minute]);
            timerDisplay[3].setImageResource(numbers[0]);
        } else {
            timerDisplay[4].setImageResource(numbers[Integer.parseInt("" + num.charAt(1))]);
            timerDisplay[3].setImageResource(numbers[Integer.parseInt("" + num.charAt(0))]);
        }
    }

    private void setDisplaySecond() {
        String num = "" + second;
        if (num.length() < 2) {
            timerDisplay[7].setImageResource(numbers[second]);
            timerDisplay[6].setImageResource(numbers[0]);
        } else {
            timerDisplay[7].setImageResource(numbers[Integer.parseInt("" + num.charAt(1))]);
            timerDisplay[6].setImageResource(numbers[Integer.parseInt("" + num.charAt(0))]);
        }
    }

    public void takeTime() {
        if (second != 0 || minute != 0 || hour != 0) {
            if (second == 0) {
                second = 59;
                minute--;
                setDisplaySecond();
                setDisplayMinute();
            } else {
                second--;
                setDisplaySecond();
            }
            if (minute < 0 && hour > 0) {
                hour--;
                minute = 59;
                setDisplayMinute();
                setDisplayHour();
            }
        }
    }

    public void countDown() {
        timer = new Timer();
        timerOn = true;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            if(second == 0 && minute == 0 && hour == 0){
                                try {
                                    OutputStream outputStream = actualTower.getOutputStream();
                                    outputStream.write("*10".getBytes());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                timerOn = false;
                                cancel();
                            }else
                                takeTime();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 500,1000);
    }

    public void stoptimer(){
        if(timerOn)
            timer.cancel();
    }

    public boolean isTimerOn() {
        return timerOn;
    }

    public  boolean isReady(){
        return second != 0 || minute != 0 || hour != 0;
    }

    public void setActualTower(BluetoothSocket actualTower){
        this.actualTower = actualTower;
    }
}

