package org.hackystat.sensor.eclipse.preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.hackystat.sensor.eclipse.EclipseSensor;
import org.hackystat.sensor.eclipse.EclipseSensorPlugin;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorshell.SensorShellException;
import org.sonar.wsclient.Sonar;

/**
 * Implements the preference page for Eclipse Sensor. It does some validation to make sure that
 * hackystat sensorbase host, email and password are correct.
 * 
 * @author Hongbing Kou, seninp
 */
public class SensorPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {
  /** SensorBase field. */
  private StringFieldEditor sensorbaseField;
  /** Email field. */
  private StringFieldEditor emailField;
  /** Password field. */
  private StringFieldEditor passwordField;
  /** Autosend Interval field. */
  private IntegerFieldEditor autosendIntervalField; // NOPMD

  /** SONAR host field. */
  private StringFieldEditor sonarHostField;
  /** SONAR login field. */
  private StringFieldEditor sonarLoginField;
  /** SONAR password field. */
  private StringFieldEditor sonarPasswordField;

  /** Autoupdate field. */
  // private BooleanFieldEditor autoUpdateEditor;
  /** Update site. */
  // private StringFieldEditor udpateSiteField;

  /**
   * Instantiate Eclipse plugin preference.
   */
  public SensorPreferencePage() {
    super(GRID);

    EclipseSensorPlugin plugin = EclipseSensorPlugin.getDefault();
    setPreferenceStore(plugin.getPreferenceStore());
  }

  /**
   * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
   * manipulate various types of preferences. Each field editor knows how to save and restore
   * itself.
   */
  public void createFieldEditors() {
    // hackystat sensorbase host
    this.sensorbaseField = new StringFieldEditor(PreferenceConstants.P_SENSORBASE,
        "&SensorbaseHost", getFieldEditorParent());
    this.sensorbaseField.setEmptyStringAllowed(false);
    super.addField(this.sensorbaseField);

    // User email
    this.emailField = new StringFieldEditor(PreferenceConstants.P_EMAIL, "&Email",
        getFieldEditorParent());
    this.emailField.setEmptyStringAllowed(false);
    super.addField(this.emailField);

    // Password
    this.passwordField = new StringFieldEditor(PreferenceConstants.P_PASSWORD, "&Password",
        getFieldEditorParent());
    this.passwordField.setEmptyStringAllowed(false);
    this.passwordField.getTextControl(getFieldEditorParent()).setEchoChar('*');
    super.addField(this.passwordField);

    // SONAR host
    this.sonarHostField = new StringFieldEditor(PreferenceConstants.P_SONAR_HOST,
        "&SONAR Host URI", getFieldEditorParent());
    this.sonarHostField.setEmptyStringAllowed(false);
    super.addField(this.sonarHostField);

    // SONAR user login
    this.sonarLoginField = new StringFieldEditor(PreferenceConstants.P_SONAR_LOGIN, "&SONAR login",
        getFieldEditorParent());
    this.sonarLoginField.setEmptyStringAllowed(false);
    super.addField(this.sonarLoginField);

    // SONAR user password
    this.sonarPasswordField = new StringFieldEditor(PreferenceConstants.P_SONAR_PASSWORD,
        "&SONAR password", getFieldEditorParent());
    this.sonarPasswordField.setEmptyStringAllowed(false);
    this.sonarPasswordField.getTextControl(getFieldEditorParent()).setEchoChar('*');
    super.addField(this.sonarPasswordField);

    this.autosendIntervalField = new IntegerFieldEditor(PreferenceConstants.P_AUTOSEND_INTERVAL,
        "&Autosend Interval (Minutes)", getFieldEditorParent());
    this.autosendIntervalField.setEmptyStringAllowed(false);
    super.addField(this.autosendIntervalField);

    // Whether or not allow auto update
    // this.autoUpdateEditor = new BooleanFieldEditor(
    // PreferenceConstants.P_ENABLE_AUTOUPDATE,
    // "Enable &AutoUpdate", getFieldEditorParent());
    // super.addField(this.autoUpdateEditor);

    // this.udpateSiteField = new StringFieldEditor(
    // PreferenceConstants.P_UPDATE_SITE, "&Update Site", getFieldEditorParent());
    // super.addField(this.udpateSiteField);
  }

  /**
   * Initialize the preference page.
   * 
   * @param workbench Eclipse workbench.
   */
  public void init(IWorkbench workbench) {
    // Initialize the preference store we wish to use
    setPreferenceStore(EclipseSensorPlugin.getDefault().getPreferenceStore());
  }

  /**
   * Perform okay to validate sensorbase/user/password.
   * 
   * @return True if inputs are valid.
   */
  public boolean performOk() {

    // IPreferenceStore store = super.getPreferenceStore();

    String sensorbase = this.sensorbaseField.getStringValue();
    if (!SensorBaseClient.isHost(sensorbase)) {
      super.setErrorMessage(sensorbase + " is an invalid HACKYSTAT host!");
      return false;
    }

    String email = this.emailField.getStringValue();
    // store.getString(PreferenceConstants.P_EMAIL);
    String password = this.passwordField.getStringValue();
    // String password = store.getDefaultString(PreferenceConstants.P_PASSWORD);
    if (!SensorBaseClient.isRegistered(sensorbase, email, password)) {
      super.setErrorMessage("Either HACKYSTAT email or password is incorrect!");
      return false;
    }

    // try to create a SONAR connector
    //
    String sonar = this.sonarHostField.getStringValue();
    Sonar sonarClient = Sonar.create(sonar);
    if (null == sonarClient) {
      super.setErrorMessage(sensorbase + " is an invalid SONAR host!");
      return false;
    }
    String login = this.sonarLoginField.getStringValue();
    String pass = this.sonarPasswordField.getStringValue();
    sonarClient = Sonar.create(sonar, login, pass);
    if (null == sonarClient) {
      super.setErrorMessage("Either SONAR login or password is incorrect!");
      return false;
    }

    // check if SONAR credentials are OK
    //

    // Ask sensor to enable the new settings.
    try {
      super.performOk();

      EclipseSensor sensor = EclipseSensor.getInstance();
      // Stop the sensor temporarily to let it send out previously collected sensor data.
      sensor.stop();
      sensor.hotDeploySensorShell();
    }
    catch (SensorShellException e) {
      super.setErrorMessage(e.getMessage());
      return false;
    }

    return true;
  }
}