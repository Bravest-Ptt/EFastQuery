package bravest.ptt.efastquery.data.wordbook;

import java.util.Date;

/**
 * Created by pengtian on 2017/2/13.
 */

public class WordBook extends Word{
    private String phonetic;
    private String tags;
    private String progress = "1";

    public WordBook(WordBook wordBook) {
        word = wordBook.getWord();
        trans = wordBook.getTrans();
        phonetic = wordBook.getPhonetic();
        tags = wordBook.getTags();
    }

    public WordBook() {
        super();
    }

    public WordBook(String w, String t, String p, String tagss) {
        super(w,t);
        word = w;
        trans = t;
        phonetic = p;
        tags = tagss;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getProgress() {
        return progress;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    @Override
    public String toString() {
        return "WordBook: " + "word = " + word
                + "\n" + "trans = " + trans
                + "\n" + "phonetic = " + phonetic
                + "\n" + "tags = " + tags
                + "\n" + "progress = " + progress;
    }
}
