package com.nfrush;

public class ClientFactory {

    public Client getClient(Boolean sslEnabled){
        if(sslEnabled){
            return new SSLClient();
        } else {
            return new HTTPClient();
        }

        return null;
    }
}