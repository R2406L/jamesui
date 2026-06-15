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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import jdk.internal.org.jline.utils.Log;

import org.apache.james.jamesui.backend.configuration.bean.JamesuiConfiguration;
import org.apache.james.jamesui.backend.configuration.bean.RecipientRewriteMapping;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author r2406
 */
public class Client {
    
    private JamesuiConfiguration jamesuiConfiguration;
    private final static Logger LOG = LoggerFactory.getLogger(Client.class);
    
    /**
    * Constructor
    */

    public Client() {
    }
    
    public void setConfiguration(JamesuiConfiguration jamesuiConfiguration) {
        this.jamesuiConfiguration = jamesuiConfiguration;
    }
    
    private static class Response {
        public static int code;
        public static String text;
        
        public Response(int code, String text) {
            this.code = code;
            this.text = text;
        }
    }
    
    private Response Send(String method, String path, String body) {
        URI url;
       
        try {
            url = new URI(jamesuiConfiguration.getJamesApiUrl() + "/" + path);
        } catch (URISyntaxException e) {
            LOG.error(e.toString());
            return new Response(500, "{}");
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
            return new Response(500, "{}");
        } catch(InterruptedException e) {
            LOG.error(e.toString());
            return new Response(500, "{}");
        }

        if (response.statusCode() < 200 && response.statusCode() >= 300) {
            LOG.error("Apache James response error " + response.statusCode() + ": " + response.body());
            return new Response(500, "{}");
        }
        
        LOG.debug("Server response: " + response.body().toString());
        return new Response(response.statusCode(), response.body().toString());
    }
    
    public boolean isConnectionValid() {
        return true;
    }
    
    public JSONObject getHealthCheck() {
        Response source = Send("GET", "healthcheck", "");
        JSONObject health = new JSONObject();

        try {
            health = new JSONObject(source.text);
        } catch(JSONException e) {
            LOG.error(e.toString());
        }
        
        return health;
    }
    
    // Domains
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
    
    // Users and mailboxes
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
    
    public String[][] getUserMailboxes(String user) {
        Response source = Send("GET", "users/" + user + "/mailboxes", "");
        try {
            JSONArray result = new JSONArray(source.text);
            String[][] mailboxes = new String[result.length()][2];
            for (int i = 0; i < result.length(); i++) {
                mailboxes[i][0] = result.getJSONObject(i).getString("mailboxId");
                mailboxes[i][1] = result.getJSONObject(i).getString("mailboxName");
            }
            return mailboxes;
        } catch(JSONException e) {
            LOG.error(e.toString());
            return new String[0][0];
        }
    }
    
    public boolean reindexUserMailboxes(String user) {
        Response source = Send("POST", "users/" + user + "/mailboxes?task=reIndex&messagesPerSecond=100&mode=fixOutdated", "");
        return source.code == 201;
    }
    
    public boolean setUserQuota(String user, int size, int count){
        JSONObject quota = new JSONObject();
        try {
            quota.put("count", count);
            quota.put("size", size);
        } catch(JSONException e) {
            LOG.error(e.toString());
            return false;
        }
        Response resp = Send("PUT", "/quota/users/"+user, quota.toString());
        return resp.code == 204;
    }
    
    public int[][] getUserQuota(String user) {
        Response source = Send("GET", "quota/users/"+user, "");
        int[][] response = new int[2][3];
        JSONObject quotas = new JSONObject();
        try {
            quotas = new JSONObject(source.text);
            JSONObject userQuota = quotas.getJSONObject("user");
            response[0][0] = userQuota.getInt("size");
            response[1][0] = userQuota.getInt("count");
            
            JSONObject userOccQuota = quotas.getJSONObject("occupation");
            response[0][1] = userOccQuota.getInt("size");
            response[1][1] = userOccQuota.getInt("count");
            
            JSONObject ratio = userOccQuota.getJSONObject("ratio");
            response[0][2] = (int) (ratio.getFloat("size") * 100);
            response[1][2] = (int) (ratio.getFloat("count") * 100);
        } catch(JSONException e) {
            LOG.error(e.toString());
        }
        return response;
    }
    
    // Extension methods
    public List<RecipientRewriteMapping> listMappings() {
        List<RecipientRewriteMapping> mappings = new ArrayList<RecipientRewriteMapping>();
        Response source = Send("GET", "mappings", "");
        try {
            JSONObject result = new JSONObject(source.text);
            Iterator<String> keys = result.keys();
            
            while(keys.hasNext()) {
                String key = keys.next();
                Set<String> recipients = new HashSet<>();
                JSONArray recipientSources = result.getJSONArray(key);
                for (int i = 0; i < recipientSources.length(); i++) {
                    recipients.add(recipientSources.getJSONObject(i).getString("mapping"));
                }
                RecipientRewriteMapping mapping = new RecipientRewriteMapping(key, recipients);
                mappings.add(mapping);
            }
            return mappings;
        } catch(JSONException e) {
            LOG.error(e.toString());
            return mappings;
        }
    }
    
    public Collection<String> getUserDomainMappings(String user, String domain) throws Exception {
        Response source = Send("GET", "mappings", "");
        Collection<String> mappingList = Collections.emptyList();
        try {
            JSONArray domainMappings = new JSONArray(source.text);
            for (int i = 0; i < domainMappings.length(); i++) {
                mappingList.add(domainMappings.getString(i));
            }
        } catch(JSONException e) {
            LOG.error(e.toString());
        }
        return mappingList;
    }
    
    public boolean addAddressMapping(String user, String domain, String address) throws Exception {
        Response source = Send("POST", "/mappings/address/" + user + "@" + domain + "/targets/" + address, "");
        return source.code == 204;
    }
    
    public boolean addRegexMapping(String user, String domain, String aregexp) throws Exception {
        Response source = Send("POST", "/mappings/regex/" + user + "@" + domain + "/targets/" + aregexp, "");
        return source.code == 204;
    }
    
    public boolean removeAddressMapping(String user, String domain, String address) throws Exception {
        Response source = Send("DELETE", "/mappings/address/" + user + "@" + domain + "/targets/" + address, "");
        return source.code == 204;
    }
    
    public boolean removeRegexMapping(String user, String domain, String regex) throws Exception {
        Response source = Send("DELETE", "/mappings/regex/" + user + "@" + domain + "/targets/" + regex, "");
        return source.code == 204;
    }
    
   public boolean addDomainMapping(String sourcedomain, String targetDomain) throws Exception {
       Response source = Send("PUT", "domainMappings/" + sourcedomain, targetDomain);
       return source.code == 204;
   }
   
   public boolean removeDomainMapping(String domain, String targetDomain) throws Exception {
        Response source = Send("DELETE", "/domainMappings/" + domain, targetDomain);
        return source.code == 204;
   }
   
}
