package bravest.ptt.efastquery.data.wordbook;

/**
 * Created by pengtian on 2017/2/13.
 */

public class WordBook {
    private String word;
    private String trans;
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
    }

    public WordBook(String w, String t, String p, String tagss) {
        word = w;
        trans = t;
        phonetic = p;
        tags = tagss;
    }

    public void setWord(String w) {
        this.word = w;
    }

    public String getWord() {
        return this.word;
    }

    public void setTrans(String t) {
        this.trans = t;
    }

    public String getTrans() {
        return trans;
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
