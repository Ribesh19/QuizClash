package com.example.bct.quizclash;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.bct.quizclash.Common.BluetoothConnectionService;
import com.example.bct.quizclash.Model.Question;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Dell on 01/08/2017.
 */

public class MyService extends Service {

    BluetoothAdapter mBluetoothAdapter;

    BluetoothConnectionService mBluetoothConnection;

    private static int mode;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    BluetoothDevice mBTDevice;


    class MyServiceBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    private IBinder mBinder = new MyServiceBinder();


    @Override
    public void onCreate() {
        startingConnection();
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SERVICES: ", "Service started");

        //startingConnection();
        return START_REDELIVER_INTENT;
    }

    public void startingConnection() {
        mBluetoothConnection = new BluetoothConnectionService(MyService.this);
    }

    @Override
    public void onDestroy() {

        Log.i("SERVICES: ", "Service Destroyed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("SERVICES: ", "Binding Done");
        return mBinder;
    }


    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d("Service Bata", "StartConnection: Iniitalizing bt RFFCON BT connection");

        if (mBluetoothConnection.startClient(device, uuid)) {
            Toast.makeText(getApplicationContext(), "AlreadyConnected", Toast.LENGTH_LONG).show();

        }

    }

    public boolean onUnbind(Intent intent) {
        Log.i("SERVICE", "In onUnbind");
        return super.onUnbind(intent);
    }

    public void sendByte(byte[] bytes, String mode) {
        mBluetoothConnection.write(bytes,mode);
    }

    public void sendQuestion(ArrayList<Question> question, String mode) {
        mBluetoothConnection.write(question,mode);
    }


}
