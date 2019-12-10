package org.dew.test;

import org.dew.auth.WLoginModule;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestWAuth extends TestCase {
  
  public TestWAuth(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    return new TestSuite(TestWAuth.class);
  }
  
  public void testApp() {
    System.out.println(WLoginModule.class.getCanonicalName() + " " + WLoginModule.VERSION);
  }
  
}
