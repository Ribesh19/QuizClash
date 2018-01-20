package com.example.bct.quizclash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.bct.quizclash.DbHelper.DbHelper;
import com.example.bct.quizclash.Model.Question;

import java.io.IOException;
import java.util.ArrayList;

public class category extends AppCompatActivity {

    Button general,science,sports,chemistry,physics,geography,football;
    DbHelper db;
    Intent intentss;
    ArrayList<Question> questionPlay = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        general = (Button) findViewById(R.id.btnGeneral);
        science = (Button) findViewById(R.id.btnScience);
        sports = (Button) findViewById(R.id.btnSports);
        chemistry = (Button) findViewById(R.id.btnChemistry);
        physics = (Button) findViewById(R.id.btnPhysics);
        geography = (Button) findViewById(R.id.btnGeography);
        football = (Button) findViewById(R.id.btnFootball);
        db = new DbHelper(this);
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
                Intent intent = new Intent(getApplicationContext(),playing.class);
                intent.putParcelableArrayListExtra("Category",questionPlay);
                startActivity(intent);
                finish();
                sendBroadcast(intentss);
            }
        });

    }


    /**
     * Created by Dell on 01/08/2017.
     */


}
