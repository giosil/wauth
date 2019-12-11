# Wrapped Auth

LoginModule implementation template.

## Build

- `git clone https://github.com/giosil/wauth.git`
- `mvn clean install`

## Configuration example on JBoss EAP 6.0+ / Wildfly 8.0.0+ 

$JBOSS_HOME/modules/org/dew/auth/main/module.xml

```xml
<?xml version="1.0" ?>
<module xmlns="urn:jboss:module:1.1" name="org.dew.auth">
  <resources>
    <resource-root path="wauth-1.0.0.jar"/>
  </resources>
  
  <dependencies>
    <module name="javax.api"/>
    <module name="javax.resource.api"/>
    <module name="javax.security.auth.message.api"/>
    <module name="javax.security.jacc.api"/>
    <module name="javax.servlet.api"/>
    <module name="javax.transaction.api"/>
    <module name="javax.xml.bind.api"/>
    <module name="javax.xml.stream.api"/>
  </dependencies>
</module>
```

$JBOSS_HOME/standalone/configuration/standalone.xml

```xml
<?xml version='1.0' encoding='UTF-8'?>
<server xmlns="urn:jboss:domain:4.1">
...
 <profile>
  <subsystem xmlns="urn:jboss:domain:security:1.2">
   <security-domains>
    <security-domain name="wauth" cache-type="default">
     <authentication>
      <login-module code="org.dew.auth.WLoginModule" flag="optional" module="org.dew.auth"/>
     </authentication>
    </security-domain>
   </security-domains>
  </subsystem>
 </profile>
...
</server>
```

web-application.war/WEB-INF/web.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="2.5" 
  xmlns="http://java.sun.com/xml/ns/javaee"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
  <display-name>test</display-name>
  
  <welcome-file-list>
    <welcome-file>index.jsp</welcome-file>
  </welcome-file-list>
  
  <security-constraint>
    <web-resource-collection>
      <url-pattern>/*</url-pattern>
    </web-resource-collection>
    <auth-constraint>
      <role-name>admin</role-name>
    </auth-constraint>
  </security-constraint>
  
  <security-role>
    <role-name>admin</role-name>
  </security-role>
  
  <login-config>
    <auth-method>BASIC</auth-method>
    <realm-name>wauth</realm-name>
  </login-config>
</web-app>
```

web-application.war/WEB-INF/jboss-web.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jboss-web>
  <security-domain>wauth</security-domain>
</jboss-web>

```

## Contributors

* [Giorgio Silvestris](https://github.com/giosil)
