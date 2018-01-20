package com.example.bct.quizclash.DbHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bct.quizclash.Model.Question;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Nischal on 5/31/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "QClash.db";
    private static String DATABASE_PATH = "";
    private SQLiteDatabase mDatabase;
    private Context mContext = null;
    private static String TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        DATABASE_PATH = context.getApplicationInfo().dataDir+"/databases/";
        this.mContext = context;
    }

    public void openDatabase(){
        String myPath = DATABASE_PATH+DATABASE_NAME;
        mDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    public void copyDatabase() throws IOException{
        try{
            InputStream myInput = mContext.getAssets().open(DATABASE_NAME);
            String outputFileName = DATABASE_PATH+DATABASE_NAME;
            OutputStream myOutput = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int length;
            while((length = myInput.read(buffer)) > 0)
                myOutput.write(buffer, 0 , length);

            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean checkDatabase(){
        SQLiteDatabase tempDB = null;
        try {
            String myPath = DATABASE_PATH+DATABASE_NAME;
            tempDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }
        if( tempDB != null )
            tempDB.close();
        return tempDB!=null?true:false;
    }

    public void createDatabase() throws IOException{
        boolean isDBExists = checkDatabase();
        if(isDBExists){

        }
        else{
            this.getReadableDatabase();
            try{
                copyDatabase();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public synchronized void close() {
        if(mDatabase != null)
            mDatabase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //CRUD for questions
    public ArrayList<Question> getQuestion(String TABLE_NAME){
        ArrayList<Question> listQuestion = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c;

        try{
            c = db.rawQuery("SELECT * FROM "+TABLE_NAME+ " ORDER BY RANDOM() LIMIT 5 ",null);
            if(c == null ) return null;
            c.moveToFirst();
            do {
                int Id = c.getInt(c.getColumnIndex("ID"));
                String Question = c.getString(c.getColumnIndex("Question"));
                String AnswerA = c.getString(c.getColumnIndex("AnswerA"));
                String AnswerB = c.getString(c.getColumnIndex("AnswerB"));
                String AnswerC = c.getString(c.getColumnIndex("AnswerC"));
                String AnswerD = c.getString(c.getColumnIndex("AnswerD"));
                String CorrectAnswer = c.getString(c.getColumnIndex("CorrectAnswer"));

                Question question = new Question(Id, Question, AnswerA, AnswerB, AnswerC, AnswerD, CorrectAnswer);
                listQuestion.add(question);
            }
            while(c.moveToNext());
            c.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return listQuestion;

    }



}
