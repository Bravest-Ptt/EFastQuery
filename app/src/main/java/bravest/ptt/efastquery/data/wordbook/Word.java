package bravest.ptt.efastquery.data.wordbook;

import java.util.Date;

/**
 * Created by root on 3/1/17.
 */

public class Word {
    protected String word;
    protected String trans;
    protected Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public Word(String word, String trans) {
        this.word = word;
        this.trans = trans;
    }

    public Word() {
    }

    public Word(String word, String trans, Date date) {
        this.word = word;
        this.trans = trans;
        this.date = date;
    }
}
