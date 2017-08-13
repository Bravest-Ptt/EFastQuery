package bravest.ptt.efastquery.entity;

import java.util.Date;

/**
 * Created by pengtian on 2017/6/26.
 */

public class Article {
    private static final String TAG = "Article";

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_BIG_IMAGE = 1;
    public static final int TYPE_MULTI_IMAGE = 2;
    public static final int TYPE_NORMAL_GROUP = 3;
    public static final int TYPE_BIG_IMAGE_GROUP = 4;
    public static final int TYPE_MULTI_IMAGE_GROUP = 5;

    private String id;
    private int type;
    private String tag;
    private String title;
    private String imageUrl;
    private String contentEnglish;
    private String contentChina;
    private String contentAudio;
    private int readCount;
    private Date createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContentEnglish() {
        return contentEnglish;
    }

    public void setContentEnglish(String contentEnglish) {
        this.contentEnglish = contentEnglish;
    }

    public String getContentChina() {
        return contentChina;
    }

    public void setContentChina(String contentChina) {
        this.contentChina = contentChina;
    }

    public String getContentAudio() {
        return contentAudio;
    }

    public void setContentAudio(String contentAudio) {
        this.contentAudio = contentAudio;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
