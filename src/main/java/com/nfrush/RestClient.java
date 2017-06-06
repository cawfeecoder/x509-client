package com.nfrush;

/**
 * Created by nfrush on 6/5/17.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import javax.net.ssl.SSLContext;

import java.io.FileInputStream;
import java.security.KeyStore;

public class RestClient {

    private RestTemplate rest;
    private String server = "https://localhost:8443/user";
    private HttpHeaders headers;
    private HttpStatus status;

    public RestClient(Boolean ssl_enabled) throws Exception{
        this.headers = new HttpHeaders();
        if (ssl_enabled) {
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(ClassLoader.getSystemResourceAsStream("jks/client-truststore.jks"),
                    "client-truststore-p455w0rd".toCharArray());
            if (keyStore == null) {
                throw new Exception("KeyStore failed to load.");
            }

            SSLContext sslContext = new SSLContextBuilder()
                    .loadKeyMaterial(keyStore, "client-truststore-p455w0rd".toCharArray()).build();

            if(sslContext == null) {
                throw new Exception("Failed to build SSLContext");
            }

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory
                    = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);

            this.rest = new RestTemplate(requestFactory);
        } else {
            this.rest = new RestTemplate();
        }
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
    }

    public String get(String uri) {
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        ResponseEntity<String> responseEntity = rest.exchange(server + uri, HttpMethod.GET, requestEntity, String.class);
        this.setStatus(responseEntity.getStatusCode());
        return responseEntity.getBody();
    }

    public String post(String uri, String json) {
        HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
        ResponseEntity<String> responseEntity = rest.exchange(server + uri, HttpMethod.POST, requestEntity, String.class);
        this.setStatus(responseEntity.getStatusCode());
        return responseEntity.getBody();
    }

    public void put(String uri, String json) {
        HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
        ResponseEntity<String> responseEntity = rest.exchange(server + uri, HttpMethod.PUT, requestEntity, String.class);
        this.setStatus(responseEntity.getStatusCode());
    }

    public void delete(String uri) {
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        ResponseEntity<String> responseEntity = rest.exchange(server + uri, HttpMethod.DELETE, requestEntity, String.class);
        this.setStatus(responseEntity.getStatusCode());
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(RestClient.class);
        //logger.info(args[0]);
        Boolean ssl_enabled = true;
        RestClient client = new RestClient(ssl_enabled);
        if (client == null) {
            throw new Exception("Client failed to build");
        }
        String get = client.get("");
        logger.info(get);
    }
}
