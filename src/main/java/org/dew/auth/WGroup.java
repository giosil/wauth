package org.dew.auth;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public 
class WGroup extends WPrincipal implements Group
{
  private static final long serialVersionUID = 4548161151770776798L;
  
  private Map<String, Principal> members;
  
  public 
  WGroup(String groupName)
  {
    super(groupName);
    members = new HashMap<String, Principal>();
  }
  
  public 
  boolean addMember(Principal user)
  {
    if (members.containsKey(user.getName())){
      return false;
    }
    members.put(user.getName(), user);
    return true;
  }
  
  public 
  boolean isMember(Principal member)
  {
    if (members.containsKey(member.getName())){
      return true;
    }
    for (Principal principal : members.values()){
      if (principal instanceof Group && Group.class.cast(principal).isMember(member)){
        return true;
      }
    }
    return false;
  }
  
  public 
  Enumeration<Principal> members()
  {
    return Collections.enumeration(members.values());
  }
  
  public 
  boolean removeMember(Principal user)
  {
    return !Objects.isNull(members.remove(user.getName()));
  }
}
