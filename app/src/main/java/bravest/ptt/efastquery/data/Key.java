package bravest.ptt.efastquery.data;

/**
 * Created by root on 1/22/17.
 */

public class Key {
    public String keyId;
    public String keySecret;

    public Key(){

    }

    public Key(String id, String secret) {
        this.keyId = id;
        this.keySecret = secret;
    }
}
