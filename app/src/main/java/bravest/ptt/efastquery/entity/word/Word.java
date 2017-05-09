package bravest.ptt.efastquery.entity.word;

import java.util.Date;

import bravest.ptt.efastquery.interfaces.IWord;

/**
 * Created by root on 3/1/17.
 */

public class Word implements IWord {
    protected String word;
    protected String trans;
    protected String tags;
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

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
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

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", trans='" + trans + '\'' +
                ", tags='" + tags + '\'' +
                ", date=" + date +
                '}';
    }
}
