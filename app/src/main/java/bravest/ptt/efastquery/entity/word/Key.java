package bravest.ptt.efastquery.entity.word;

/**
 * Created by root on 1/22/17.
 */

public class Key {
    private String keyId;
    private String keySecret;

    public Key(){

    }

    public Key(String id, String secret) {
        this.keyId = id;
        this.keySecret = secret;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getKeySecret() {
        return keySecret;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public void setKeySecret(String keySecret) {
        this.keySecret = keySecret;
    }

    @Override
    public String toString() {
        return "Key{" +
                "keyId='" + keyId + '\'' +
                ", keySecret='" + keySecret + '\'' +
                '}';
    }
}
