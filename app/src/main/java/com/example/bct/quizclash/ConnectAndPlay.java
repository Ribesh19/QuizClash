package com.example.bct.quizclash;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bct.quizclash.Common.BluetoothConnectionService;
import com.example.bct.quizclash.Common.DeviceListAdapter;
import com.example.bct.quizclash.Model.Question;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ConnectAndPlay extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "ConnectAndPlay";

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;

    BluetoothAdapter mBluetoothAdapter;

    ArrayList<Question> questionPlay = new ArrayList<>();

    Intent intentss;

    BluetoothConnectionService mBluetoothConnection;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    BluetoothDevice mBTDevice;

    Button discoverable, scan, start;
    ListView deviceList;

    private MyService myService;
    private boolean isServiceBound;
    private ServiceConnection serviceConnection;

    private Intent serviceIntent;

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        public String TAG = "lksd";

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);

                deviceList.setAdapter(mDeviceListAdapter);
            }
        }
    };

    private boolean hasConnected = false;
    private boolean noSend = false;
     String checkSend = "False";


    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d("POI", "BroadcastReceiver: BOND_BONDED.");
                    mBTDevice = mDevice;

                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d("POI", "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d("POI", "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.i("LKHH", "onDestroy: called.");
        super.onDestroy();


        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);

        mBluetoothAdapter.cancelDiscovery();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_select);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        discoverable = (Button) findViewById(R.id.discoverable);
        scan = (Button) findViewById(R.id.scan);
        start = (Button) findViewById(R.id.startPlay);
        deviceList = (ListView) findViewById(R.id.newdevices);
        mBTDevices = new ArrayList<>();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        serviceIntent = new Intent(getApplicationContext(), MyService.class);

        startService(serviceIntent);
        bindService();

        deviceList.setOnItemClickListener(ConnectAndPlay.this);

        //startingMa();

        LocalBroadcastManager.getInstance(this).registerReceiver(changeState, new IntentFilter("incoming messages"));
        LocalBroadcastManager.getInstance(this).registerReceiver(connectedState, new IntentFilter("connected"));
        LocalBroadcastManager.getInstance(this).registerReceiver(questionCome, new IntentFilter("question aayo"));

        intentss = new Intent("finish_activity");




        discoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent discover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discover.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discover);


            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DSA", "btnDiscover looking for unpaired devices");
                //mBTDevices = null;
                mBTDevices.clear();
                deviceList.setAdapter(null);


                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                    Log.d("DSA", "btnDiscover cancelling discvery");

                    checkBTPermissions();

                    mBluetoothAdapter.startDiscovery();
                    IntentFilter discoverDeviceIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver3, discoverDeviceIntent);
                }
                if (!mBluetoothAdapter.isDiscovering()) {

                    checkBTPermissions();
                    mBluetoothAdapter.startDiscovery();
                    IntentFilter discoverDeviceIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver3, discoverDeviceIntent);
                }


            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                if (hasConnected && Objects.equals(checkSend,"False")) {
                    String ok = "NOSEND";
                    byte[] byts = ok.getBytes(Charset.defaultCharset());
                    myService.sendByte(byts, "Extra");
                    unbindService();
                    Intent intent = new Intent(getApplicationContext(), MultiCategory.class);
                    startActivity(intent);
                }
                else if (hasConnected && Objects.equals(checkSend,"True"))
                    Toast.makeText(getApplicationContext(),"Please wait other player is choosing Category",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(),"Please Connect First",Toast.LENGTH_SHORT).show();

            }
        });
    }



    private void unbindService() {
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    private void bindService() {
        if (serviceConnection == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    MyService.MyServiceBinder myServiceBinder = (MyService.MyServiceBinder) iBinder;
                    myService = myServiceBinder.getService();
                    isServiceBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    isServiceBound = false;
                }
            };
        }

        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }


    BroadcastReceiver changeState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //setContentView(R.layout.activity_category);
            String asdf = intent.getStringExtra("theMessage");


            if(Objects.equals(asdf,"NOSEND"))
            checkSend = "True";

        }
    };

    BroadcastReceiver questionCome = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //setContentView(R.layout.activity_category);
            questionPlay = intent.getParcelableArrayListExtra("theMessage");
            Intent i = new Intent(getApplicationContext(),playing.class);
            i.putParcelableArrayListExtra("Category",questionPlay);
            unbindService();
            startActivity(i);
            finish();

            sendBroadcast(intentss);


        }
    };


    BroadcastReceiver connectedState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //setContentView(R.layout.activity_category);
            String asdf = intent.getStringExtra("theMessage");


            if(Objects.equals(asdf,"TRUE"))
                hasConnected = true;

        }
    };



    /*public void startConnection() {

        startBTConnection(mBTDevice, MY_UUID_INSECURE);
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "StartConnection: Iniitalizing bt RFFCON BT connection");

        if (mBluetoothConnection.startClient(device, uuid)) {
            Toast.makeText(getApplicationContext(), "AlreadyConnected", Toast.LENGTH_LONG).show();
        }


        Intent intent = new Intent(getApplicationContext(), MultiCategory.class);
        startActivity(intent);

    }*/

    private void checkBTPermissions() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d("HAHA", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
        mBluetoothAdapter.cancelDiscovery();
        Log.d("TYU", "OnitemClick: clicked device");

        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d("VCC", "onItemClick: deviceName = " + deviceName);
        Log.d("VCC", "onItemClick: deviceAddress = " + deviceAddress);

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if (true) {
            Log.d("CV", "Trying to pair with " + deviceName);
            mBTDevices.get(i).createBond();

            mBTDevice = mBTDevices.get(i);
            myService.startBTConnection(mBTDevice, MY_UUID_INSECURE);


        }
    }


}
