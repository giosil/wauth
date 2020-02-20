package org.dew.auth;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Calendar;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class WLoginModule implements LoginModule {
  
  public static final String VERSION = "1.0.0";
  
  private CallbackHandler handler;
  private Subject subject;
  private Principal userPrincipal;
  private Principal rolePrincipal;
  
  @Override
  public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
    log("WLoginModule.initialize(" + subject + "," + callbackHandler + "," + sharedState + ", " + options + ")...");
    this.handler = callbackHandler;
    this.subject = subject;
  }
  
  @Override
  public boolean login() throws LoginException {
    log("WLoginModule.login()...");
    if(handler == null) {
      log("WLoginModule.login() -> " + this.getClass().getName() + " not initialized");
      throw new LoginException(this.getClass().getName() + " not initialized");
    }
    
    NameCallback     nameCallback = new NameCallback("login");
    PasswordCallback passCallback = new PasswordCallback("password", true);
    try {
      handler.handle(new Callback[]{nameCallback, passCallback});
      
      String username = nameCallback.getName();
      String password = String.valueOf(passCallback.getPassword());
      
      log("WCache.read(" + username + ",*)...");
      String role = WCache.read(username, password);
      log("WCache.read(" + username + ",*) -> " + role);
      
      if(role == null || role.length() == 0) {
        log("WLoginModule.check(" + username + ",*)...");
        role = check(username, password);
        log("WLoginModule.check(" + username + ",*) -> " + role);
      }
      
      if(role != null && role.length() > 0) {
        userPrincipal = new WPrincipal(username);
        
        Group group = new WGroup("Roles");
        group.addMember(new WPrincipal(role));
        
        rolePrincipal = group;
        
        WCache.write(username, password, role);
        
        log("WLoginModule.login() -> true");
        return true;
      }
    } 
    catch (Exception ex) {
      log("WLoginModule.login()", ex);
      throw new LoginException(ex.getMessage());
    }
    log("WLoginModule.login() -> false");
    return false;
  }
  
  @Override
  public boolean commit() throws LoginException {
    log("WLoginModule.commit()...");
    if (userPrincipal == null || rolePrincipal == null) {
      log("WLoginModule.commit() -> false");
      return false;
    }
    subject.getPrincipals().add(userPrincipal);
    subject.getPrincipals().add(rolePrincipal);
    log("WLoginModule.commit() -> true");
    return true;
  }
  
  @Override
  public boolean abort() throws LoginException {
    log("WLoginModule.abort()...");
    log("WLoginModule.abort() -> false");
    return false;
  }
  
  @Override
  public boolean logout() throws LoginException {
    log("WLoginModule.logout()...");
    subject.getPrincipals().remove(userPrincipal);
    subject.getPrincipals().remove(rolePrincipal);
    log("WLoginModule.logout() -> true");
    return true;
  }
  
  protected void log(String message) {
    Calendar cal = Calendar.getInstance();
    
    int iYear  = cal.get(Calendar.YEAR);
    int iMonth = cal.get(Calendar.MONTH) + 1;
    int iDay   = cal.get(Calendar.DATE);
    int iHour  = cal.get(Calendar.HOUR_OF_DAY);
    int iMin   = cal.get(Calendar.MINUTE);
    int iSec   = cal.get(Calendar.SECOND);
    String sYear  = String.valueOf(iYear);
    String sMonth = iMonth < 10 ? "0" + iMonth : String.valueOf(iMonth);
    String sDay   = iDay   < 10 ? "0" + iDay   : String.valueOf(iDay);
    String sHour  = iHour  < 10 ? "0" + iHour  : String.valueOf(iHour);
    String sMin   = iMin   < 10 ? "0" + iMin   : String.valueOf(iMin);
    String sSec   = iSec   < 10 ? "0" + iSec   : String.valueOf(iSec);
    
    String sDate  = sYear + "-" + sMonth + "-" + sDay;
    String sTime  = sHour + ":" + sMin   + ":" + sSec;
    
    System.out.println(sDate + " " + sTime + " " + message);
  }
  
  protected void log(String message, Exception exception) {
    log(message + ": " + exception);
  }
  
  protected String check(String username, String password) throws Exception {
    if(username == null || username.length() == 0) return null;
    if(password == null || password.length() == 0) return null;
    boolean check = username.equals(password);
    if(!check) return null;
    if(username.toLowerCase().startsWith("op")) return "oper";
    return "admin";
  }
}
