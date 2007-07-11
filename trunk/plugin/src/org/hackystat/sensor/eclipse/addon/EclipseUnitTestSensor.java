package org.hackystat.sensor.eclipse.addon;

import java.util.Date;

import org.eclipse.jdt.junit.ITestRunListener;
import org.hackystat.core.kernel.sensordata.SensorDataPropertyMap;
import org.hackystat.sensor.eclipse.EclipseSensor;


/**
 * Provides a JUnit listener implementing class to get JUnit result information. 
 * A client must add this implementing class to the JUnitPlugin class in such a way that:
 * 
 * <pre>
 * ITestRunListener eclipseListener = new EclipseJUnitListener(EclipseSensor.getInstance());
 * JUnitPlugin.getDefault().addTestRunListener(eclipseListener);
 * </pre>
 * 
 *
 * @author Takuya Yamashita, Hongbing Kou
 * @version $Id: EclipseUnitTestSensor.java,v 1.1.1.1 2005/10/20 23:56:56 johnson Exp $
 */
public class EclipseUnitTestSensor implements ITestRunListener {
  /** The EclipseSensor. */
  private EclipseSensor sensor;
  
  /** Test. */
  private String test;
  /** Run time. */
  private Date runTime;
  /** Number of tests in one test run. */
  private int testCount;
  /** Indice of the test in one test run. */
  private int testIndice;
  
  /** The starting time of each test case. */
  private Date startTime;
  /** The failure stack trace message. */
  private String failureMessage;
  /** The error stack trace message. */
  private String errorMessage;
 
  /**
   * Constructor for the EclipseJUnitListener object. Adds this lister instance to the
   * <code>org.eclipse.jdt.internal.junit.ui.JUnitPlugin</code> instance.
   *
   * @param sensor The EclipseSensor instance.
   */
  public EclipseUnitTestSensor() {
    this(EclipseSensor.getInstance());
  }
  
  /**
   * Constructor for the EclipseJUnitListener object. Adds this lister instance to the
   * <code>org.eclipse.jdt.internal.junit.ui.JUnitPlugin</code> instance.
   *
   * @param sensor The EclipseSensor instance.
   */
  public EclipseUnitTestSensor(EclipseSensor sensor) {
    this.sensor = sensor;
    init();
  }

  /**
   * Initializes all the instance fields related to the fields in the JUnitResource instance.
   */
  private void init() {
    this.failureMessage = "";
    this.errorMessage = "";
  }

  /**
   * A single test was reran.
   *
   * @param testClass the name of the test class.
   * @param testName the name of the test.
   * @param status the status of the run
   * @param trace the stack trace in the case of a failure.
   *
   */
  public void testReran(String testClass, String testName, int status, String trace) {
  }

  /**
   * A test run has started.
   *
   * @param testCount the number of tests that will be run.
   *
   * @see org.eclipse.jdt.junit.ITestRunListener#testRunStarted(int)
   */
  public void testRunStarted(int testCount) {
    this.runTime = new Date();
    this.testCount = testCount;
    this.testIndice = 1;
  }
  
   /**
   * A test run ended.
   *
   * @param elapsedTime the elapsed time of the test run.
   *
   * @see org.eclipse.jdt.junit.ITestRunListener#testRunEnded(long)
   */
  public void testRunEnded(long elapsedTime) {
    String testName = getTestNameOf(this.test);
    SensorDataPropertyMap pMap = new SensorDataPropertyMap();
    pMap.put("subtype", "UnitTest");
    pMap.put("test-name", testName);
    pMap.put("test-count=", String.valueOf(this.testCount));
    pMap.put("elapsed-time", String.valueOf(elapsedTime));
    
    if (this.failureMessage != null && !this.failureMessage.equals("")) {
      pMap.put("success", "failure");
    }
    else if (this.errorMessage != null && !this.errorMessage.equals("")) {
      pMap.put("success", "error");
    }
    else {
      pMap.put("success", "true");
    }
    
    // Process DevEvent
    String testFile = this.sensor.getObjectFile(testName);
    if (testFile == null) {
      testFile = this.sensor.getActiveFile();
    }
    this.sensor.processDevEvent("Test", testFile, pMap);
  }

  /**
   * A test run was stopped before it ended.
   *
   * @param elapsedTime the elapsed time of the test run.
   *
   * @see org.eclipse.jdt.junit.ITestRunListener#testRunStopped(long)
   */
  public void testRunStopped(long elapsedTime) {
  }

  /**
   * The test runner VM has terminated.
   *
   * @see org.eclipse.jdt.junit.ITestRunListener#testRunTerminated()
   */
  public void testRunTerminated() {
  }

  /**
   * Gets the fully qualified package class name from the testName string. For example, returns
   * "org.hackystat.sensor.eclipse.junit.EclipseJUnitListener".
   *
   * @param testName The test name string to be passed.
   *
   * @return The fully qualified package class name.
   */
  private String getTestNameOf(String testName) {
    int startIndex = testName.indexOf('(');
    int endIndex = testName.indexOf(')');

    // There is no test case
    if (startIndex == -1 || endIndex == -1) {
      return "";
    }
    
    return testName.substring(startIndex + 1, endIndex);
  }

  /**
   * Gets the test case name (method name) from the testName string. For example, returns
   * "testMethod".
   *
   * @param test The test name string to be passed.
   *
   * @return The test case name (method name).
   */
  private String getTestCaseOf(String test) {
    int endIndex = test.indexOf('(');
    
    // There is no test but test case name.
    if (endIndex == -1) {
      return test;
    }
    
    return test.substring(0, endIndex);
  }

  /**
   * An individual test has started. Supports the method for the 2.1 stream from RC2.
   *
   * @param testId a unique Id identifying the test
   * @param test the name of the test that started
   *
   * @see org.eclipse.jdt.junit.ITestRunListener#testStarted(java.lang.String, java.lang.String)
   * @since 2.1 RC2
   **/
  public void testStarted(String testId, String test) {
    this.test = test;
    this.startTime = new Date();
    init();
  }

  /**
   * An individual test has ended. Supports the method for the 2.1 stream from RC2.
   *
   * @param testId a unique Id identifying the test
   * @param test the name of the test that ended
   *
   * @see org.eclipse.jdt.junit.ITestRunListener#testEnded(java.lang.String, java.lang.String)
   * @since 2.1 RC2
   **/
  public void testEnded(String testId, String test) {
    Date endTime = new Date();
    String testName = getTestNameOf(test);
    String testCase = getTestCaseOf(test);
    
    String name = testName + "." + testCase;
    // Incase there is compilation error so that there is not test name
    if ("".equals(testName)) {
      name = testCase;
    }
    
    // Run test
    SensorDataPropertyMap pMap = new SensorDataPropertyMap();
    pMap.put("runtime", String.valueOf(this.runTime.getTime()));

    // Elapsed time
    long elapsedTime = endTime.getTime() - this.startTime.getTime();
    elapsedTime = (elapsedTime >= 0) ? elapsedTime : 0;
    pMap.put("elapsedTime", String.valueOf(elapsedTime));
    
    String result = "pass";
    if (this.failureMessage.length() > 0) {
      result = "fail";
      pMap.put("failureString", this.failureMessage);      
    }
    else if (this.errorMessage.length() > 0) {
      result = "fail";
      pMap.put("errorString" , this.errorMessage);      
    }
    pMap.put("testCase", testCase); // Test case
    pMap.put("testName", testName); // Test name
    
    pMap.put("testcount", String.valueOf(this.testCount));
    pMap.put("testindice", String.valueOf(this.testIndice));

    String testFile = this.sensor.getObjectFile(testName);
    if (testFile == null) {
      testFile = this.sensor.getActiveFile();
    }
    
    // Increment indice number by one. 
    this.testIndice++;
    // Process UnitTest
    this.sensor.processUnitTest(testFile, name, result, pMap);
  }

  /**
   * An individual test has failed with a stack trace.
   * Supports the method for the 2.1 stream from RC2.
   *
   * @param testId a unique Id identifying the test
   * @param test the name of the test that failed
   * @param status the outcome of the test; one of
   * {@link #STATUS_ERROR STATUS_ERROR} or
   * {@link #STATUS_FAILURE STATUS_FAILURE}
   * @param trace the stack trace
   *
   * @see org.eclipse.jdt.junit.ITestRunListener#testFailed(int, java.lang.String,
   * java.lang.String, java.lang.String)
   * @since 2.1 RC2
   */
  public void testFailed(int status, String testId, String test, String trace) {
    if (status == ITestRunListener.STATUS_FAILURE) {
      this.failureMessage = trace;
    }
    else if (status == ITestRunListener.STATUS_ERROR) {
      this.errorMessage = trace;
    }
  }

  /**
   * An individual test has been rerun.
   * Supports the method for the 2.1 stream from RC2.
   *
   * @param testId a unique Id identifying the test
   * @param testClass the name of the test class that was rerun
   * @param test the name of the test that was rerun
   * @param status the outcome of the test that was rerun; one of
   * {@link #STATUS_OK STATUS_OK}, {@link #STATUS_ERROR STATUS_ERROR},
   * or {@link #STATUS_FAILURE STATUS_FAILURE}
   * @param trace the stack trace in the case of abnormal termination,
   * or the empty string if none
   *
   * @see org.eclipse.jdt.junit.ITestRunListener#testReran(java.lang.String, java.lang.String,
   *  java.lang.String, int, java.lang.String)
   * @since 2.1 RC2
   */
  public void testReran(String testId, String testClass,
                        String test, int status, String trace) {
    testReran(testClass, test, status, trace);
  }
}