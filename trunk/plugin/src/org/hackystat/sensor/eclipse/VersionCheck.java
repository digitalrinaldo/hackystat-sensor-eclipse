package org.hackystat.sensor.eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.update.internal.ui.UpdateUI;
import org.eclipse.update.internal.ui.wizards.InstallWizard;
import org.eclipse.update.internal.ui.wizards.ResizableInstallWizardDialog;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Provides the version check function. Clients may call <code>processUpdateDialog(String)</code>
 * to check new version on the server, whose url is specified in the preference page, and
 * bring update info dialog if the new version is available. Supoorts for both 2x and 3x stream
 * version information.
 * 
 * @author Takuya Yamashita
 * @version $Id: VersionCheck.java,v 1.1.1.1 2005/10/20 23:56:56 johnson Exp $
 *
 */
public class VersionCheck {
  /** Bundle object. */
  private Bundle bundle;
  
  /**
   * Instantiates a version check to a given bundle.
   * 
   * @param bundle A resource bundle or plugin.
   */
  public VersionCheck(Bundle bundle) {
    this.bundle = bundle;
  }
  
  /**
   * Creates an image descriptor form the given path. The path should be the relative path from a
   * project root.
   *
   * @param path the relative path from a project root.
   *
   * @return the newly created image descriptor.
   */
  public ImageDescriptor createImageDescriptor(String path) {
    try {
      URL url = this.bundle.getEntry("/");
      return ImageDescriptor.createFromURL(new URL(url, path));
    }
    catch (MalformedURLException e) {
      return ImageDescriptor.getMissingImageDescriptor();
    }
    catch (Exception e) {
      e.printStackTrace();
      return ImageDescriptor.getMissingImageDescriptor();
    }
  }
  
  /**
   * Processes update dialog to the user. The current version and new version appear in the dialog.
   * The element of the messages array is follows:
   * <ul>
   * <li>The element 0: first message.
   * <li>The element 1: the message between local version and server version.
   * <li>The element 2: last message.
   * </ul>  
   * @param updateUrl the url for the update site, which ends with "site.xml".
   * @param title the title to be shown in the update window page.
   * @param messages the messages to be shown in the update window page. 
   */
  public void processUpdateDialog(String updateUrl, final String title, final String[] messages) {
    if (updateUrl != null) {
      try { // Gets current version info.
        Version localVerIdentifier = getLocalPluginVersionIdentifier();
        String localVerString = localVerIdentifier.toString();
        final String localVersionId = localVerString.substring(0, localVerString.length() - 2);
        final String qualifierVersion = localVerIdentifier.getQualifier();

        // Gets new version info.
        URL url = new URL(updateUrl);
        Document serverDocument = parseXml(url.openStream());
        final Version serverVerIdentifier = getVersionIdentifier(serverDocument, 
            qualifierVersion);
        // Check if current version is not new version, then show pop up udate dialog.
        if (serverVerIdentifier != null && serverVerIdentifier.compareTo(localVerIdentifier) > 0) {
          String serverVerString = serverVerIdentifier.toString();
          final String  serverVerId = serverVerString.substring(0, serverVerString.length() - 2);
          
          IWorkbench workbench = EclipseSensorPlugin.getInstance().getWorkbench();
          final IWorkbenchWindow window = workbench.getWorkbenchWindows()[0];
          final String HACKYSTAT_ICON = EclipseSensorI18n.getString("VersionCheck.hackystatIcon");
          Display.getDefault().asyncExec(new Runnable() {
            public void run() {
              MessageDialog dialog = new MessageDialog(window.getShell(),
              title,
              createImageDescriptor(HACKYSTAT_ICON).createImage(), 
              messages[0] + localVersionId + messages[1] + serverVerId + 
                messages[2],
              MessageDialog.QUESTION,
              new String[] {
                EclipseSensorI18n.getString("VersionCheck.messageDialogButtonUpdate"),
                EclipseSensorI18n.getString("VersionCheck.messageDialogButtonCancel")
              },
              0);
                
              int result = dialog.open();
              if (result == 0) {
                openNewUpdatesWizard();
              }
            }
           });
        }
      }
      catch (Exception e) {
        e.printStackTrace();
        EclipseSensorPlugin.getInstance().log(e);
        //couldn't parse xml file, or even though xml is parsed, version attribute is not found.
      }
    }
  }
  
  /**
   * Gets the local plugin version identifier.
   * 
   * @return the local plugin version identifier.
   */
  private Version getLocalPluginVersionIdentifier() {
    //Bundle bundle = EclipseSensorPlugin.getInstance().getBundle();
    String version = (String) this.bundle.getHeaders().get(Constants.BUNDLE_VERSION);
    return new Version(version);    
  }

  /**
   * Gets version information from the given Document object, checking the qualifier version. The
   * qualifier version is the last token of the version identifier. For example, if the version
   * identifier is 1.4.204.2x, the qualifier version is 2x.
   *
   * @param document The Document object parsed from XML file.
   * @param qualifierVersion The qualifier version. e.g 2x for the 1.4.204.2x
   *
   * @return The PluginVersionIdentifier instance if it's found.Returns null if the version
   *         information is not found.
   */
  private static Version getVersionIdentifier(Document document,
    String qualifierVersion) {
    Element root = document.getDocumentElement();
    NodeList list = root.getChildNodes();
    final String FEATURE = "feature";
    final String VERSION = "version";
    for (int i = 0; i < list.getLength(); i++) {
      if ((list.item(i).getNodeType() == Node.ELEMENT_NODE)
          && list.item(i).getNodeName().equalsIgnoreCase(FEATURE)) {
        Element element = (Element) list.item(i);
        if (element.hasAttribute(VERSION)) {
          String version = element.getAttribute(VERSION);
          String[] versions = version.split("\\.");
          String newVersion = versions[0] + "." + versions[1];   
          Version identifier = new Version(newVersion);
          if (identifier.getQualifier().equals(qualifierVersion)) {
            return identifier;
          }
        }
      }
    }
    return null;
  }

  /**
   * Parses xml file to generate Document object from a given input stream.
   *
   * @param input The given input stream from which xml file is read.
   *
   * @return The Documentation object which contains the content parsed from the XML file. Returns
   *         null if parse error occurs.
   */
  private static Document parseXml(InputStream input) {
    try {
      // Create a builder factory
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(false);
      // Create the builder and parse the file
      Document document = factory.newDocumentBuilder().parse(input);
      input.close();
      return document;
    }
    catch (SAXException e) {
      // A parsing error occurred; the xml input is not valid
    }
    catch (ParserConfigurationException e) {
      // ignored.
    }
    catch (IOException e) {
      // ignored.
    }
    return null;
  }
  
  /**
   * Openes new update wizard. Uses reflection depending upon eclipse verison stream.
   * So that each stream can implement different class to open new updat wizard. 
   */
  private void openNewUpdatesWizard() {
    IWorkbench workbench = EclipseSensorPlugin.getInstance().getWorkbench();
    IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
    if (activeWindow != null) {
      Shell shell = workbench.getActiveWorkbenchWindow().getShell();
      IWizard wizard = new InstallWizard(null);
      WizardDialog dialog = new ResizableInstallWizardDialog(shell, wizard, "");
      dialog.create();
      //dialog.getShell().setText(UpdateUI.getString("InstallWizardAction.title")); //$NON-NLS-1$
      dialog.getShell().setText(UpdateUI.getPluginId());
      dialog.getShell().setSize(600, 500);
      dialog.open();
    }
  }
}
