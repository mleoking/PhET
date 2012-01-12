package org.jmol.api;

import java.awt.Image;

public interface JmolPdfCreatorInterface {

  public String createPdfDocument(String fileName, Image image);
}
