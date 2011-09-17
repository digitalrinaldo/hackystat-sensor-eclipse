package org.hackystat.sensor.eclipse.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.hackystat.sensor.eclipse.EclipseSensorPlugin;

/**
 * Initializes the property values.
 * 
 * @author hongbing, seninp
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

  /**
   * Initialize the default setting for Hackystat Sensor.
   */
  @Override
  public void initializeDefaultPreferences() {

    EclipseSensorPlugin plugin = EclipseSensorPlugin.getDefault();

    IPreferenceStore store = plugin.getPreferenceStore();

    store.setDefault(PreferenceConstants.P_SENSORBASE,
        "http://localhost:9876/sensorbase/");
    store.setDefault(PreferenceConstants.P_EMAIL, "senin@hawaii.edu");
    store.setDefault(PreferenceConstants.P_PASSWORD, "H789YGqp3hTp");

    store.setDefault(PreferenceConstants.P_AUTOSEND_INTERVAL, "30");
    
    store.setDefault(PreferenceConstants.P_SONAR_HOST, "http://localhost:9000");
    store.setDefault(PreferenceConstants.P_SONAR_LOGIN, "seninp");
    store.setDefault(PreferenceConstants.P_SONAR_PASSWORD, "seninp_YAG");

    // store.setDefault(PreferenceConstants.P_ENABLE_AUTOUPDATE, true);
    // store.setDefault(PreferenceConstants.P_UPDATE_SITE,
    // "http://hackystat-sensor-eclipse.googlecode.com/svn/trunk/site/site.xml");
  }
}
