package org.dew.auth;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

public class WCache {
  private static final int EXPIRY_IN = 15 * 60 * 1000;
  
  private static ConcurrentHashMap<String, AuthCacheEntry> map = new ConcurrentHashMap<String, AuthCacheEntry>();
  
  public static 
  String read(String username, String password)
  {
    final String key = username + ":" + password;
    
    final long currentTimeMillis = System.currentTimeMillis();
    
    final AuthCacheEntry authCacheEntry = map.get(key);
    
    if(authCacheEntry != null) {
      if(authCacheEntry.getExpiryIn() > currentTimeMillis) {
        authCacheEntry.setExpiryIn(currentTimeMillis + EXPIRY_IN);
        return authCacheEntry.getRole();
      }
    }
    
    return null;
  }
  
  public static 
  void write(String username, String password, String role)
  {
    final String key = username + ":" + password;
    
    final long currentTimeMillis = System.currentTimeMillis();
    
    map.put(key, new AuthCacheEntry(username, role, currentTimeMillis + EXPIRY_IN));
    
    Iterator<Map.Entry<String, AuthCacheEntry>> iterator = map.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<String, AuthCacheEntry> entry = iterator.next();
      
      final AuthCacheEntry authCacheEntry = entry.getValue();
      if(authCacheEntry.getExpiryIn() < currentTimeMillis) {
        iterator.remove();
        break;
      }
    }
  }
  
  static class AuthCacheEntry implements Serializable
  {
    private static final long serialVersionUID = 8167989138284912244L;
    
    private String username;
    private String role;
    private long   expiryIn;
    
    public AuthCacheEntry()
    {
    }
    
    public AuthCacheEntry(String username, String role, long expiryIn) {
      this.username = username;
      this.role     = role;
      this.expiryIn = expiryIn;
    }
    
    public String getUsername() {
      return username;
    }
    
    public void setUsername(String username) {
      this.username = username;
    }
    
    public String getRole() {
      return role;
    }
    
    public void setRoles(String role) {
      this.role = role;
    }
    
    public long getExpiryIn() {
      return expiryIn;
    }
    
    public void setExpiryIn(long expiryIn) {
      this.expiryIn = expiryIn;
    }
    
    @Override
    public boolean equals(Object object) {
      if(object instanceof AuthCacheEntry) {
        String sObjUsername = ((AuthCacheEntry) object).getUsername();
        if(sObjUsername == null && username == null) {
          return true;
        }
        else if(sObjUsername != null && username != null) {
          return username.equals(sObjUsername);
        }
      }
      return false;
    }
    
    @Override
    public int hashCode() {
      if(username == null) return 0;
      return username.hashCode();
    }
    
    @Override
    public String toString() {
      return "AuthCacheEntry(" + username + ")";
    }
  }
}
