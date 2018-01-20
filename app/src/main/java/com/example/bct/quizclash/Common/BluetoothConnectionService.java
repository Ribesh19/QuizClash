package com.example.bct.quizclash.Common;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.bct.quizclash.Model.Question;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Dell on 18/07/2017.
 */

public class BluetoothConnectionService {

    private static final String TAG = "BLUETOOTH CONN";
    private static final String appName = "MeroApp";

    private static String state = "Initial";

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    private static boolean alreadyConnected = false;

    private AcceptThread mInsecureAcceptThread;

    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;

    private ConnectedThread mConnectedThread;


    ArrayList<Question> question = new ArrayList<>();

    public BluetoothConnectionService(Context context) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = context;
        start();
    }


    private class AcceptThread extends Thread {
        //local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            //creating listening serversocket
            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);

                Log.i(TAG, "ACCEPTTHRREA:  Setting upserver Using" + MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.i(TAG, "ACCeptThread IOEXCEPTION " + e.getMessage());
            }


            mmServerSocket = tmp;

        }

        public void run() {

            Log.i(TAG, "RUN: AcceptThread running");

            BluetoothSocket socket = null;

            try {
                Log.i(TAG, "RUN: running server socket start.......");

                socket = mmServerSocket.accept();

                Log.i(TAG, "RUN: server socket accept connection");
            } catch (IOException e) {
                Log.i(TAG, "ACCeptThread IOEXCEPTION " + e.getMessage());
            }

            if (socket != null) {
                connected(socket, mmDevice);
            }

            Log.i(TAG, "ACCeptThread end");

        }

        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }
    }


    private class ConnectThread extends Thread {

        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {

            Log.d(TAG, "ConnectThread Started");
            mmDevice = device;
            deviceUUID = uuid;

        }

        public void run() {
            BluetoothSocket tmp = null;

            Log.d(TAG, "Run  mConnectedThread");

            try {

                tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.d(TAG, "ConnectThread couldnt connect InsecureRfCommSocket" + e.getMessage());
            }

            mmSocket = tmp;


            mBluetoothAdapter.cancelDiscovery();


            try {
                mmSocket.connect();

                Log.d(TAG, "Run ConnectThread COnnected");
            } catch (IOException e) {
                try {
                    mmSocket.close();
                    Log.d(TAG, "Socket Closed");
                } catch (IOException e1) {
                    Log.d(TAG, "mmConnectThread unable to close connection" + e1.getMessage());
                }

                Log.d(TAG, "run Couldnt connect to uuid" + MY_UUID_INSECURE);
            }

            connected(mmSocket, mmDevice);
        }


        public void cancel() {
            Log.d(TAG, "cancel: Closing Client socket.");
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close() of mmSocket failed. " + e.getMessage());
            }
        }
    }

    public synchronized void start() {
        Log.i(TAG, "Start");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    public boolean startClient(BluetoothDevice device, UUID uuid) {

        if (!alreadyConnected) {
            Log.i(TAG, "startClient :  Started");

            //mProgressDialog = ProgressDialog.show(mContext, "Connecting bluetooth", "Please Wait...", true);

            mConnectThread = new ConnectThread(device, uuid);
            mConnectThread.start();
            return false;
        } else {
            return true;
        }


    }


    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInputStream;
        private final OutputStream mmOutputStream;
        ObjectOutputStream objectOutputStream;
        ObjectInputStream objectInputStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.i(TAG, "ConnectedThread : Starting ");
            alreadyConnected = true;

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            objectOutputStream = null;
            objectInputStream = null;

            try {
                mProgressDialog.dismiss();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();

            } catch (IOException e) {

            }

            mmInputStream = tmpIn;
            mmOutputStream = tmpOut;
            try {
                objectOutputStream = new ObjectOutputStream(mmSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                objectInputStream = new ObjectInputStream(mmSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent incomingMessageIntent = new Intent("connected");
            incomingMessageIntent.putExtra("theMessage", "TRUE");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;


            while (true) {
                try {

                    if (Objects.equals(state, "Initial")) {//suru ma question select gardai xa bhanna ko lagi
                        bytes = mmInputStream.read(buffer);
                        String incomingMessage = new String(buffer, 0, bytes);
                        Log.i(TAG, "InputStream " + incomingMessage);
                        state = "Play";
                        Intent incomingMessageIntent = new Intent("incoming messages");
                        incomingMessageIntent.putExtra("theMessage", incomingMessage);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);
                    }
                    else if(Objects.equals(state,"Play")){//question pathauna ko lagi
                        try {
                            question = (ArrayList<Question>)objectInputStream.readObject();
                            Log.i(TAG, "InputStream ma questoion array");
                            Intent incomingMessageIntent = new Intent("question aayo");
                            incomingMessageIntent.putParcelableArrayListExtra("theMessage", question);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }


                    }
                    //Intent incomingMessageIntent = new Intent("incoming messages");
                    //incomingMessageIntent.putExtra("theMessage", incomingMessage);
                    //LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);

                } catch (IOException e) {
                    Log.i(TAG, "Write : error readiong input" + e.getMessage());
                    break;
                }


            }
        }

        public void write(byte[] bytes) {

            String text = new String(bytes, Charset.defaultCharset());
            Log.i(TAG, "write : Writing to output stream" + text);
            try {
                mmOutputStream.write(bytes);

            } catch (IOException e) {
                Log.i(TAG, "write : error writing to outputstram " + e.getMessage());
            }


        }

        public void write(ArrayList<Question> questionPlay) {

            try {
                objectOutputStream.writeObject(questionPlay);

            } catch (IOException e) {
                Log.i(TAG, "write : error writing to outputstram " + e.getMessage());
            }


        }


        public void cancel() {
            try {
                Log.i("LKHH", "Connected thread ko cancelld.");
                mmSocket.close();
            } catch (IOException e) {
            }
        }

    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {

        Log.i(TAG, "Connected : Starting ");

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();

    }

    public void write(byte[] out, String mode) {

        ConnectedThread r;

        Log.i(TAG, "Write : write called");
        r = mConnectedThread;

        state = "Play";
        mConnectedThread.write(out);


    }

    public void write(ArrayList<Question> questionPlay, String mode) {

        ConnectedThread r;

        Log.i(TAG, "Write : write called");
        r = mConnectedThread;

        state = "Playing";
        mConnectedThread.write(questionPlay);


    }


}
