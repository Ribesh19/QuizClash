package com.example.bct.quizclash;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.bct.quizclash.Common.BluetoothConnectionService;
import com.example.bct.quizclash.DbHelper.DbHelper;
import com.example.bct.quizclash.Model.Question;

import java.io.IOException;
import java.util.ArrayList;

public class MultiCategory extends AppCompatActivity {

    private MyService myService;
    private boolean isServiceBound = false;
    private ServiceConnection serviceConnection;

    private Intent serviceIntent;

    Button general,science,sports,chemistry,physics,geography,football;
    DbHelper db;
    Intent intentss;
    BluetoothConnectionService mBluetoothConnection;

    ArrayList<Question> questionPlay = new ArrayList<>();

    //BluetoothConnectionService mBluetoothConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_category);
        general = (Button) findViewById(R.id.btnMultiGeneral);
        science = (Button) findViewById(R.id.btnMultiScience);
        sports = (Button) findViewById(R.id.btnMultiSports);
        chemistry = (Button) findViewById(R.id.btnMultiChemistry);
        physics = (Button) findViewById(R.id.btnMultiPhysics);
        geography = (Button) findViewById(R.id.btnMultiGeography);
        football = (Button) findViewById(R.id.btnMultiFootball);
        db = new DbHelper(this);
        serviceIntent = new Intent(getApplicationContext(), MyService.class);

        bindService();

        intentss = new Intent("finish_activity");
        try{
            db.createDatabase();
        }
        catch (IOException e) {
            e.printStackTrace();
        }



        general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionPlay = db.getQuestion("General");
                myService.sendQuestion(questionPlay,"Play");
                unbindService();
                Intent intent = new Intent(getApplicationContext(),playing.class);
                intent.putParcelableArrayListExtra("Category",questionPlay);
                startActivity(intent);
                finish();

                sendBroadcast(intentss);

            }
        });


        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionPlay = db.getQuestion("Sports");
                myService.sendQuestion(questionPlay,"Play");
                unbindService();
                Intent intent = new Intent(getApplicationContext(),playing.class);
                intent.putParcelableArrayListExtra("Category",questionPlay);
                startActivity(intent);
                finish();
                sendBroadcast(intentss);

            }
        });

        science.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionPlay = db.getQuestion("Science");
                myService.sendQuestion(questionPlay,"Play");
                unbindService();
                Intent intent = new Intent(getApplicationContext(),playing.class);
                intent.putParcelableArrayListExtra("Category",questionPlay);
                startActivity(intent);
                finish();
                sendBroadcast(intentss);
            }
        });
        physics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionPlay = db.getQuestion("Physics");
                myService.sendQuestion(questionPlay,"Play");
                unbindService();
                Intent intent = new Intent(getApplicationContext(),playing.class);
                intent.putParcelableArrayListExtra("Category",questionPlay);
                startActivity(intent);
                finish();
                sendBroadcast(intentss);
            }
        });
        geography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionPlay = db.getQuestion("Geography");
                myService.sendQuestion(questionPlay,"Play");
                unbindService();
                Intent intent = new Intent(getApplicationContext(),playing.class);
                intent.putParcelableArrayListExtra("Category",questionPlay);
                startActivity(intent);
                finish();
                sendBroadcast(intentss);
            }
        });
        football.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionPlay = db.getQuestion("Football");
                myService.sendQuestion(questionPlay,"Play");
                unbindService();
                Intent intent = new Intent(getApplicationContext(),playing.class);
                intent.putParcelableArrayListExtra("Category",questionPlay);
                startActivity(intent);
                finish();
                sendBroadcast(intentss);
            }
        });
        chemistry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionPlay = db.getQuestion("Chemistry");
                myService.sendQuestion(questionPlay,"Play");
                unbindService();
                Intent intent = new Intent(getApplicationContext(),playing.class);
                intent.putParcelableArrayListExtra("Category",questionPlay);
                startActivity(intent);
                finish();
                sendBroadcast(intentss);
            }
        });

    }


    private void unbindService(){
        if(isServiceBound){
            unbindService(serviceConnection);
            isServiceBound=false;
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



}
