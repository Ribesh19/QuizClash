package com.example.bct.quizclash;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bct.quizclash.DbHelper.DbHelper;
import com.example.bct.quizclash.Model.Question;
import com.irozon.sneaker.Sneaker;

import java.util.ArrayList;

public class MultiPlaying extends AppCompatActivity implements View.OnClickListener {

    final static long INTERVAL = 100; // 0.1 second
    final static long TIMEOUT = 9200; // 9.2 sconds
    int progressValue;
    static boolean pause;

    CountDownTimer mCountDown, mPauseforSneaker; // for progressbar

    ArrayList<Question> questionPlay = new ArrayList<>(); //total Question
    DbHelper db;
    int index = 0, score = 0, thisQuestion = 0, totalQuestion, correctAnswer;
    String mode = "";

    //Control
    ProgressBar progressBar;
    ImageView imageView;
    Button btnA, btnB, btnC, btnD;
    TextView txtScore, txtQuestion, textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_playing);
        progressValue = 0;
        //Get Data from MainActivity
        Bundle extra = getIntent().getExtras();
        if (extra != null)
            questionPlay = extra.getParcelableArrayList("Category");


        Log.i("Sgjkhkjk", mode);
        db = new DbHelper(this);

        textView = (TextView) findViewById(R.id.MultitxtQuestion);
        txtScore = (TextView) findViewById(R.id.MultitxtScore);
        txtQuestion = (TextView) findViewById(R.id.MultitxtQuestion);
        progressBar = (ProgressBar) findViewById(R.id.multiprogressBar);
        progressBar.setMax(90);

        btnA = (Button) findViewById(R.id.multibtnAnswerA);
        btnB = (Button) findViewById(R.id.multibtnAnswerB);
        btnC = (Button) findViewById(R.id.multibtnAnswerC);
        btnD = (Button) findViewById(R.id.multibtnAnswerD);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);

        mPauseforSneaker = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long micro) {
                pause = true;
            }

            @Override
            public void onFinish() {
                mPauseforSneaker.cancel();
                pause = false;
                showQuestion(++index);
            }
        };


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            Toast.makeText(getApplicationContext(), "Can't go back", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //questionPlay = db.getQuestion(mode);
        totalQuestion = questionPlay.size();

        mCountDown = new CountDownTimer(TIMEOUT, INTERVAL) {
            @Override
            public void onTick(long micro) {

                progressBar.setProgress(progressValue);
                progressValue++;
            }

            @Override
            public void onFinish() {
                mCountDown.cancel();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }

    private void showQuestion(int index) {
        if (index < totalQuestion) {
            thisQuestion++;
            txtQuestion.setText(String.format("%d/%d", thisQuestion, totalQuestion));
            progressBar.setProgress(0);
            progressValue = 0;

            textView.setText(questionPlay.get(index).getQuestion());
            btnA.setText(questionPlay.get(index).getAnswerA());
            btnB.setText(questionPlay.get(index).getAnswerB());
            btnC.setText(questionPlay.get(index).getAnswerC());
            btnD.setText(questionPlay.get(index).getAnswerD());

            mCountDown.start();
        } else {
            Intent intent = new Intent(this, done.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE", score);
            dataSend.putInt("TOTAL", totalQuestion);
            dataSend.putInt("CORRECT", correctAnswer);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (!pause) {
            mCountDown.cancel();
            if (index < totalQuestion) {
                Button clickedButton = (Button) v;
                if (clickedButton.getText().equals(questionPlay.get(index).getCorrectAnswer())) {
                    Sneaker.with(MultiPlaying.this)
                            .setTitle("Correct")
                            .setDuration(3000)
                            .sneakSuccess();
                    score += 10; // increase score
                    correctAnswer++; //increase correct answer
                    mPauseforSneaker.start();


                } else {
                    Sneaker.with(MultiPlaying.this)
                            .setTitle("InCorrect")
                            .setDuration(3000)
                            .sneakError();
                    mPauseforSneaker.start();
                    //showQuestion(++index); // If choose right , just go to next question


                }
                txtScore.setText(String.format("%d", score));

            }
        }
    }
}




