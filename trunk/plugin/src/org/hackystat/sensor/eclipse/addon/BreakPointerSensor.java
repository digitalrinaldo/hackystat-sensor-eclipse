package org.hackystat.sensor.eclipse.addon;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hackystat.sensor.eclipse.EclipseSensor;
import org.hackystat.sensor.eclipse.EclipseSensorPlugin;

/**
 * Listens to the break point changes to send activity data to Hackystat server.
 * 
 * @author Hongbing Kou
 * @version $Id: BreakPointerSensor.java,v 1.1.1.1 2005/10/20 23:56:56 johnson Exp $
 */
public class BreakPointerSensor implements IBreakpointListener {
  /** Eclipse sensor. */
  private EclipseSensor eclipseSensor;
  
  /**
   * Creates a breakpoint listener instance.
   * 
   * @param eclipseSensor Eclipse sensor.
   */
  public BreakPointerSensor(EclipseSensor eclipseSensor) {
    this.eclipseSensor = eclipseSensor;
  }

  /**
   * Listens to the break point added event.
   * 
   * @param breakpoint Break point.
   */
  public void breakpointAdded(IBreakpoint breakpoint) {
    // Ignores breakpoints other than line break point
    if (!(breakpoint instanceof ILineBreakpoint)) {
      return;
    }

    ITextEditor activeEditor = this.eclipseSensor.getActiveTextEditor();
    if (activeEditor == null) {
      return;
    }
    
    IFileEditorInput fileEditorInput = (IFileEditorInput) activeEditor.getEditorInput();
    IFile file = fileEditorInput.getFile();
    try {
      String fileNamePath = file.getLocation().toString();
      String lineNumberString = String.valueOf(((ILineBreakpoint) breakpoint).getLineNumber());
      
      // Also generate DevEvent data for the debug event
      Map<String, String> debugData = new HashMap<String, String>();
      debugData.put("subtype", "BreakPoint");
      debugData.put("set", "set");
      debugData.put("line", lineNumberString);
      String message = constructMessage(fileNamePath, "Debug:BreakPoint", lineNumberString);
      this.eclipseSensor.addDevEvent("Debug", fileNamePath, debugData, message);
    }
    catch (CoreException e) {
      EclipseSensorPlugin.getInstance().log(e);
    }
  }

  /**
   * Constructs message string to be displayed or logged.
   * 
   * @param fileName File name and path. 
   * @param type Set/unset break point.
   * @param lineno Line no.
   * @return Message string.
   */
  private String constructMessage(String fileName, String type, String lineno) {
    StringBuffer message = new StringBuffer("Debug");
    message.append(" : ").append(this.eclipseSensor.extractFileName(fileName));
    message.append(" [").append(type).append(",").append(lineno).append("]");
    
    return message.toString();
  }
  
  
  /**
   * Listens to break point changed event.
   * 
   * @param breakpoint Break point.
   * @param delta Delta change.
   */
  public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
  }

  /**
   * Listens to break point removed event.
   * 
   * @param breakpoint Breakpoint.
   * @param delta Marker changes.
   */
  public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
    // Ignores breakpoints other than line break point
    if (!(breakpoint instanceof ILineBreakpoint)) {
      return;
    }

    ITextEditor activeEditor = this.eclipseSensor.getActiveTextEditor();
    IFileEditorInput fileEditorInput = (IFileEditorInput) activeEditor.getEditorInput();
    IFile file = fileEditorInput.getFile();
    try {
      String fileNamePath = file.getLocation().toString();
      String lineNumberString = String.valueOf(((ILineBreakpoint) breakpoint).getLineNumber());

      // Also generate DevEvent data for the debug event
      Map<String, String> keyValueMap = new HashMap<String, String>();
      keyValueMap.put("subtype", "BreakPoint");
      keyValueMap.put("set", "unset");
      keyValueMap.put("line", lineNumberString);
      String message = constructMessage(fileNamePath, "Debug:BreakPoint", keyValueMap.get("line"));
      this.eclipseSensor.addDevEvent("Debug", fileNamePath, keyValueMap, message);
    }
    catch (CoreException e) {
      // Exception can be thrown when there is no associated line number
      // we do not need to worry about this in our sensor. 
      //EclipseSensorPlugin.getInstance().log(e);
    }
  }
}