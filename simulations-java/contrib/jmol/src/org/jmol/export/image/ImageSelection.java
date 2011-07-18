/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *  02110-1301, USA.
 */

package org.jmol.export.image;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jmol.util.Logger;

/**
 * This class is used to transfer an {@link Image} into the clipboard.
 * 
 * @author Nicolas Vervelle
 */
public class ImageSelection implements Transferable {

  /**
   * The image to transfer into the clipboard.
   */
  private Image image;
  private String text;
  //boolean isText;
  
  /**
   * Transfers <code>image</code> into the clipboard.
   * 
   * @param image Image to transfer into the clipboard.
   */
  public static void setClipboard(Image image) {
    ImageSelection sel = new ImageSelection(image);
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
  }

  /**
   * Transfers <code>text</code> into the clipboard.
   * 
   * @param text to transfer into the clipboard.
   */
  public static void setClipboard(String text) {
    ImageSelection sel = new ImageSelection(text);
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
  }
  

  /**
   * Constructs a <code>ImageSelection</code>.
   * 
   * @param image The real Image.
   */
  public ImageSelection(Image image) {
    this.image = image;
  }
  
  /**
   * Constructs a <code>ImageSelection</code>.
   * 
   * @param text The text to transfer
   */
  public ImageSelection(String text) {
    this.text = text;
  }

  /* (non-Javadoc)
   * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
   */
  public DataFlavor[] getTransferDataFlavors() {
    return (text == null ? 
        new DataFlavor[]{ DataFlavor.imageFlavor }
      : new DataFlavor[]{ DataFlavor.stringFlavor });
  }

  /* (non-Javadoc)
   * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
   */
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return DataFlavor.imageFlavor.equals(flavor);
  }

  /* (non-Javadoc)
   * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
   */
  public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException, IOException {
    if (DataFlavor.imageFlavor.equals(flavor)) {
      return image;
    } else     if (DataFlavor.stringFlavor.equals(flavor)) {
      return text;
    }
    throw new UnsupportedFlavorException(flavor);
  }

  /**
   * Get the String residing on the clipboard. Or, if it is a file list,
   * get the load command associated with that.
   * from http://www.javapractices.com/Topic82.cjp
   * @return any text found on the Clipboard; if none found, return an
   * empty String.
   */
  @SuppressWarnings("unchecked")
  public static String getClipboardText() {
    String result = null;
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable contents = clipboard.getContents(null);
    if (contents == null)
      return null;
      try {
        if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        result = (String) contents.getTransferData(DataFlavor.stringFlavor);
        } else if (contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
          List<File> fileList = (List<File>) contents.getTransferData(DataFlavor.javaFileListFlavor);
          final int length = fileList.size();
          if (length == 0)
            return null;
          result = "LOAD files ";
          for (int i = 0; i < length; i++)
            result += " \"" + (fileList.get(i)).getAbsolutePath().replace('\\','/') + "\"";
        }
      } catch (UnsupportedFlavorException ex) {
        //highly unlikely since we are using a standard DataFlavor
        Logger.error("Clipboard problem", ex);
        ex.printStackTrace();
      } catch (IOException ex) {
        Logger.error("Clipboard problem", ex);
        ex.printStackTrace();
      }
    return result;
  }
}
