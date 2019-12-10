package org.dew.auth;

import java.security.Principal;
import java.security.acl.Group;

import java.util.Map;
import java.util.Objects;

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
      
      log("WLoginModule.check(" + username + ",*)...");
      String role = check(username, password);
      log("WLoginModule.check(" + username + ",*) -> " + role);
      
      if(role != null && role.length() > 0) {
        userPrincipal = new WPrincipal(username);
        
        Group group = new WGroup("Roles");
        group.addMember(new WPrincipal(role));
        
        rolePrincipal = group;
        
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
    if (Objects.isNull(userPrincipal) || Objects.isNull(rolePrincipal)) {
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
    System.out.println(message);
  }
  
  protected void log(String message, Exception exception) {
    System.out.println(message + ": " + exception);
  }
  
  protected String check(String username, String password) throws Exception {
    if(username == null || username.length() == 0) return null;
    if(password == null || password.length() == 0) return null;
    boolean check = username.equals(password);
    if(!check) return null;
    if(username.toLowerCase().startsWith("adm")) return "admin";
    return "oper";
  }
}
