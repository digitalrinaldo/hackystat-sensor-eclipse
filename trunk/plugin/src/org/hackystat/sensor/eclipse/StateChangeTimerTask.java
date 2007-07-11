package org.hackystat.sensor.eclipse;

import java.util.TimerTask;

/**
 * Implements Eclipse sensor timer task that can be executed by a timer. The timer task checks
 * whether the state of Eclipse buffer has been changed since its last invocation, and also
 * process the FileMetric.
 *
 * @author Takuya Yamashita
 * @version $Id: StateChangeTimerTask.java,v 1.1.1.1 2005/10/20 23:56:56 johnson Exp $
 */
public class StateChangeTimerTask extends TimerTask {
  /**
   * Processes the state change activity and computes file metrics in a time based interval.
   */
  public void run() {
    EclipseSensor sensor = EclipseSensor.getInstance();
    // process FileMetric.
    // sensor.processFileMetric();
    // Chnaged to call processFileMetric() inside state change activity
    // process state change Activity.
    sensor.processStateChangeActivity();
  }
}