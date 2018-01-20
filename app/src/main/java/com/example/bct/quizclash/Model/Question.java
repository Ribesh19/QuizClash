package com.example.bct.quizclash.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nischal on 5/31/2017.
 */

public class Question implements Parcelable{
    private int ID;
    private String Question;
    private String AnswerA;
    private String AnswerB;
    private String AnswerC;
    private String AnswerD;
    private String CorrectAnswer;

    public Question(int id, String question, String answerA, String answerB, String answerC, String answerD, String correctAnswer) {
        ID = id;
        Question = question;
        AnswerA = answerA;
        AnswerB = answerB;
        AnswerC = answerC;
        AnswerD = answerD;
        CorrectAnswer = correctAnswer;
    }

    public Question(Parcel par){
        this.ID = par.readInt();
        this.Question = par.readString();
        this.AnswerA = par.readString();
        this.AnswerB = par.readString();
        this.AnswerC = par.readString();
        this.AnswerD = par.readString();
        this.CorrectAnswer = par.readString();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswerA() {
        return AnswerA;
    }

    public void setAnswerA(String answerA) {
        AnswerA = answerA;
    }

    public String getAnswerB() {
        return AnswerB;
    }

    public void setAnswerB(String answerB) {
        AnswerB = answerB;
    }

    public String getAnswerC() {
        return AnswerC;
    }

    public void setAnswerC(String answerC) {
        AnswerC = answerC;
    }

    public String getAnswerD() {
        return AnswerD;
    }

    public void setAnswerD(String answerD) {
        AnswerD = answerD;
    }

    public String getCorrectAnswer() {
        return CorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        CorrectAnswer = correctAnswer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(this.ID);
        parcel.writeString(this.Question);
        parcel.writeString(this.AnswerA);
        parcel.writeString(this.AnswerB);
        parcel.writeString(this.AnswerC);
        parcel.writeString(this.AnswerD);
        parcel.writeString(this.CorrectAnswer);

    }

    private void readFromParcel(Parcel in){
        ID = in.readInt();
        Question = in.readString();
        AnswerA = in.readString();
        AnswerB = in.readString();
        AnswerC = in.readString();
        AnswerD = in.readString();
        CorrectAnswer = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
