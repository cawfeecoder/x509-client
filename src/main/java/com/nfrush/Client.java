package com.nfrush;

/**
 * Created by nfrush on 6/6/17.
 */
public interface Client {
    public void createClient();
    public String get(String uri);
    public String post(String uri, String json);
    public void put(String uri, String json);
    public void delete(String uri);
}
