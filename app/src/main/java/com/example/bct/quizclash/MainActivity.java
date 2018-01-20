package com.example.bct.quizclash;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.bct.quizclash.DbHelper.DbHelper;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button play,multi,setting;
    DbHelper db;
    static Activity activityA;

    private static final String TAG = "MainActivity";

    BluetoothAdapter mBluetoothAdapter;


    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = (Button) findViewById(R.id.btnPlay);
        multi = (Button) findViewById(R.id.btnMulti);
        setting = (Button) findViewById(R.id.btnSetting);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        activityA = this;
        BroadcastReceiver broadcast_reciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    finish();
                    // DO WHATEVER YOU WANT.
                }
            }
        };
        registerReceiver(broadcast_reciever, new IntentFilter("finish_activity"));

        db = new DbHelper(this);
        try{
            db.createDatabase();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),category.class);
                startActivity(intent);

            }
        });
        multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBluetoothAdapter == null){
                    Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
                }
                if(!mBluetoothAdapter.isEnabled()){
                    Log.d(TAG, "enableDisableBT: enabling BT.");
                    mBluetoothAdapter.enable();
                    //Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    //startActivity(enableBTIntent);

                    //IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    //registerReceiver(mBroadcastReceiver1, BTIntent);
                }
                /*if(mBluetoothAdapter.isEnabled()){
                    Log.d(TAG, "enableDisableBT: disabling BT.");
                    mBluetoothAdapter.disable();

                    IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(mBroadcastReceiver1, BTIntent);
                }*/

                Intent intent = new Intent(getApplicationContext(),ConnectAndPlay.class);
                startActivity(intent);

            }
        });



    }


}
