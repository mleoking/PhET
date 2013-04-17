package org.jmol.api;

import java.io.IOException;
import java.io.OutputStream;

public interface JmolImageCreatorInterface {

  abstract public void setViewer(JmolViewer viewer, double privateKey);
  
  abstract public String clipImage(String text);
  
  abstract public String getClipboardText();
  
  /**
   * 
   * @param fileName
   * @param type
   * @param text_or_bytes
   * @param quality
   * @return          null (canceled) or a message starting with OK or an error message
   */
  abstract public Object createImage(String fileName, String type, Object text_or_bytes, int quality);
  
  abstract public Object getImageBytes(String type, int quality, String fileName, Object appendtext_or_bytes, 
                                       OutputStream os) throws IOException;

}
