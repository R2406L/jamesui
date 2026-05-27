/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.apache.james.jamesui.backend.api;

import java.io.IOException;
import java.lang.InterruptedException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.james.jamesui.backend.configuration.bean.RecipientRewriteMapping;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author r2406l
 */
public class Client {
    
    private static class Response {
        public static int code;
        public static String text;
        
        public Response(int code, String text) {
            this.code = code;
            this.text = text;
        }
    }
    
    private final static Logger LOG = LoggerFactory.getLogger(Client.class);
    private final static String HOST = "http://127.0.0.1:8000";
    
    private Response Send(String method, String path, String body) {
        URI url;
       
        try {
            url = new URI(HOST + "/" + path);
        } catch (URISyntaxException e) {
            LOG.error(e.toString());
            return new Response(500, "");
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(url)
            .header("X-Applet", "java")
            .header("Content-Type", "application/json")
            .method(method, BodyPublishers.ofString(body))
            .build();
        
        HttpResponse response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch(IOException e) {
            LOG.error(e.toString());
            return new Response(500, "");
        } catch(InterruptedException e) {
            LOG.error(e.toString());
            return new Response(500, "");
        }
        
        if (response.statusCode() < 200 && response.statusCode() >= 300) {
            LOG.error("Apache James response error " + response.statusCode() + ": " + response.body());
            return new Response(500, "");
        }
        
        return new Response(response.statusCode(), response.body().toString());
    }
    
    public boolean isConnectionValid() {
        return true;
    }
    
    public String[] getDomains() {
        Response source = Send("GET", "domains", "");
        try {
            JSONArray domains = new JSONArray(source.text);
            String[] domainList = new String[domains.length()];
            for (int i = 0; i < domains.length(); i++) {
                domainList[i] = domains.getString(i);
            }
            return domainList;
        } catch(JSONException e) {
            LOG.error(e.toString());
            return new String[0];
        }
    }
    
    public boolean addDomain(String newDomain){
        Response source = Send("PUT", "domains/" + newDomain, "");
        return source.code == 204;
    }
    
    public boolean removeDomain(String domainToRemove){
        Response source = Send("DELETE", "domains/" + domainToRemove, "");
        return source.code == 204;
    }
    
    public boolean containsDomain(String domain){
        return true;
    }
    
    public String[] getAllusers() {
        Response source = Send("GET", "users", "");
        try {
            JSONArray users = new JSONArray(source.text);
            String[] userList = new String[users.length()];
            for (int i = 0; i < users.length(); i++) {
                userList[i] = users.getJSONObject(i).getString("username");
            }
            return userList;
        } catch(JSONException e) {
            LOG.error(e.toString());
            return new String[0];
        }
    }
    
    public boolean addUser(String newUser, String userPassword) {
        JSONObject password = new JSONObject();
        try {
            password.put("password", userPassword);
        } catch(JSONException e) {
            LOG.error(e.toString());
            return false;
        }
        Response source = Send("PUT", "users/" + newUser, password.toString());
        return source.code == 204;
    }
            
    public boolean deleteUser(String userToDelete) {
        Response source = Send("DELETE", "users/" + userToDelete, "");
        return source.code == 204;
    }
    
    public boolean verifyExistUser(String userToCheck) {
        return true;
    }
    
    public boolean changeUserPassword(String user, String newPassword) {
        JSONObject password = new JSONObject();
        try {
            password.put("password", newPassword);
        } catch(JSONException e) {
            LOG.error(e.toString());
            return false;
        }
        Response source = Send("PUT", "users/" + user + "?force", password.toString());
        return source.code == 204;
    }
    
    public boolean isImapServerStarted() throws Exception {
        return true;
    }
    
    public boolean stopImapServer() throws Exception {
        return true;
    }
    
    public boolean startImapServer() throws Exception {
        return true;
    }
    
    public boolean isPop3ServerStarted() throws Exception {
        return true;
    }
    
    public boolean startPop3Server() throws Exception {
        return true;
    }
    
    public boolean stopPop3Server() throws Exception {
        return true;
    }
    
    public boolean isSmtpServerStarted() throws Exception {
        return true;
    }
    
    public boolean startSmtpServer() throws Exception {
        return true;
    }
    
    public boolean stopSmtpServer() throws Exception {
        return true;
    }
    
    public List<RecipientRewriteMapping> listMappings() {
        return new ArrayList<RecipientRewriteMapping>();
    }
    
    public Collection<String> getUserDomainMappings(String user, String domain) throws Exception {
        return Collections.emptyList();
    }
    
    public void addAddressMapping(String user, String domain, String address) throws Exception {
        
    }
    
    public void addRegexMapping(String user, String domain, String aregexp) throws Exception {
        
    }
    
    public void removeAddressMapping(String user, String domain, String address) throws Exception {
        
    }
    
    public void removeRegexMapping(String user, String domain, String regex) throws Exception {
        
    }
    
    public void addErrorMapping(String user, String domain, String errorMapping) throws Exception {
        
    }
    
   public void removeErrorMapping(String user, String domain, String error) throws Exception {
       
   }
   
   public void addDomainMapping(String sourcedomain, String targetDomain) throws Exception {
       
   }
   
   public void removeDomainMapping(String domain, String targetDomain) throws Exception {
       
   }
   
}
