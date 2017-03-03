package bravest.ptt.efastquery.data;

/**
 * Created by root on 1/22/17.
 */

class Key {
    String keyId;
    String keySecret;

    public Key(){

    }

    Key(String id, String secret) {
        this.keyId = id;
        this.keySecret = secret;
    }
}
