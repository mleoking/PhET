/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-06-02 12:14:13 -0500 (Sat, 02 Jun 2007) $
 * $Revision: 7831 $
 *
 * Copyright (C) 2000-2005  The Jmol Development Team
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jmol.export.image;

import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.jmol.api.Interface;
import org.jmol.api.JmolImageCreatorInterface;
import org.jmol.api.JmolPdfCreatorInterface;
import org.jmol.api.JmolViewer;
import org.jmol.util.Base64;
import org.jmol.util.JpegEncoder;
import org.jmol.util.Logger;
import org.jmol.viewer.Viewer;

public class ImageCreator implements JmolImageCreatorInterface {
  
  private Viewer viewer;
  private double privateKey;
  
  public ImageCreator() {
    // can set viewer later
  }
  
  public ImageCreator(JmolViewer viewer){
    // for clipImage only
    this.viewer = (Viewer) viewer;
  }
 
  public void setViewer(JmolViewer viewer, double privateKey) {
    this.viewer = (Viewer) viewer;
    this.privateKey = privateKey;
  }
  
  public String clipImage(String text) {
    String msg;
    try {
      if (text == null) {
        Image image = viewer.getScreenImage(null);
        ImageSelection.setClipboard(image);
        msg = "OK image to clipboard: " + (image.getWidth(null) * image.getHeight(null));
      } else {
        ImageSelection.setClipboard(text);
        msg = "OK text to clipboard: " + text.length();
      }
    } catch (Error er) {
      msg = viewer.getErrorMessage();
    } finally {
      if (text == null)
        viewer.releaseScreenImage();
    }
    return msg;
  }

  public String getClipboardText() {
    return ImageSelection.getClipboardText();
  }
  
  public static String getClipboardTextStatic() {
    return ImageSelection.getClipboardText();
  }

  /**
   * 
   * @param fileName
   * @param type
   * @param text_or_bytes
   * @param quality
   * @return          null (canceled) or a message starting with OK or an error message
   */
  public Object createImage(String fileName, String type, Object text_or_bytes,
                            int quality) {
    // this method may not be accessed, though public, unless 
    // accessed via viewer, which provides its private key.
    if (!viewer.checkPrivateKey(privateKey))
      return "NO SECURITY";
    // returns message starting with OK or an error message
    boolean isBytes = (text_or_bytes instanceof byte[] || text_or_bytes instanceof Image);
    boolean appendText = (text_or_bytes instanceof Object[]);
    if (appendText)
      text_or_bytes = ((Object[]) text_or_bytes)[0];
    String text = (isBytes ? null : (String) text_or_bytes);
    boolean isText = (quality == Integer.MIN_VALUE);
    FileOutputStream os = null;
    long len = -1;
    try {
      if ("OutputStream".equals(type))
        return new FileOutputStream(fileName);
      if ((isText || isBytes) && text_or_bytes == null)
        return "NO DATA";
      if (isBytes) {
        if (text_or_bytes instanceof Image) {
          getImageBytes(type, quality, fileName, text_or_bytes, null);
          return fileName;
        }
        len = ((byte[]) text_or_bytes).length;
        os = new FileOutputStream(fileName);
        os.write((byte[]) text_or_bytes);
        os.flush();
        os.close();
      } else if (isText) {
        os = new FileOutputStream(fileName);
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw, 8192);
        len = text.length();
        bw.write(text);
        bw.close();
        os = null;
      } else {
        len = 1;
        Object bytesOrError = getImageBytes(type, quality, fileName,
            (appendText ? text_or_bytes : null), null);
        if (bytesOrError instanceof String)
          return bytesOrError;
        byte[] bytes = (byte[]) bytesOrError;
        if (bytes != null)
          return new String(bytes);
        len = (new File(fileName)).length();
      }
    } catch (IOException exc) {
      if (exc != null) {
        Logger.error("IO Exception", exc);
        return exc.toString();
      }
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException e) {
          // ignore
        }
      }
    }
    return (len < 0 ? "Creation of " + fileName + " failed: "
        + viewer.getErrorMessageUntranslated() : "OK " + type + " " + len + " "
        + fileName
        + (quality == Integer.MIN_VALUE ? "" : "; quality=" + quality));
  }

  public Object getImageBytes(String type, int quality, String fileName,
                              Object appendText, OutputStream os) throws IOException {
    byte[] bytes = null;
    String errMsg = null;
    boolean isPDF = type.equalsIgnoreCase("PDF");
    boolean isOsTemp = (os == null && fileName != null && !isPDF);
    boolean asBytes = (os == null && fileName == null && !isPDF);
    boolean isImage = (appendText instanceof Image); 
    Image image = (isImage ? (Image) appendText : viewer.getScreenImage(null));
    try {
      if (image == null) {
        errMsg = viewer.getErrorMessage();
      }else {
        if (isOsTemp)
            os = new FileOutputStream(fileName);
        if (type.equalsIgnoreCase("JPEG") || type.equalsIgnoreCase("JPG")) {
          if (quality <= 0)
            quality = 75;
          if (asBytes) {
            bytes = JpegEncoder.getBytes(image, quality, Viewer.getJmolVersion());
          } else {
            JpegEncoder.write(image, quality, os, viewer.getWrappedState(true));
            bytes = null;
          }
        } else if (type.equalsIgnoreCase("JPG64") || type.equalsIgnoreCase("JPEG64")) {
          if (quality <= 0)
            quality = 75;
          bytes = JpegEncoder.getBytes(image, quality, Viewer.getJmolVersion());
          if (asBytes) {
            bytes = Base64.getBytes64(bytes);
          } else {
            Base64.write(bytes, os);
            bytes = null;
          }
        } else if (type.equalsIgnoreCase("PNG")) {
          if (quality < 0)
            quality = 2;
          else if (quality > 9)
            quality = 9;
          if (asBytes) {
            bytes = PngEncoder.getBytes(image, quality);
          } else {
            PngEncoder.write(image, quality, os);
            if (appendText == null)
              os.write(viewer.getWrappedState(true).getBytes());
            bytes = null;
          }
        } else if (type.equalsIgnoreCase("PPM")) {
          if (asBytes) {
            bytes = PpmEncoder.getBytes(image);
          } else {
            PpmEncoder.write(image, os);
            bytes = null;
          }
        } else if (type.equalsIgnoreCase("GIF")) {
          if (asBytes) {
            bytes = GifEncoder.getBytes(image);
          } else {
            GifEncoder.write(image, os);
            bytes = null;
          }
        } else if (type.equalsIgnoreCase("PDF")) {
          // applet will not have this interface
          // PDF is application-only because it is such a HUGE package
            JmolPdfCreatorInterface pci = (JmolPdfCreatorInterface) Interface
                .getApplicationInterface("jmolpanel.PdfCreator");
            errMsg = pci.createPdfDocument(fileName, image);
        }
        if (appendText != null && os != null) {
          byte[] b = (appendText instanceof byte[] ? 
                (byte[]) appendText 
              : appendText instanceof String ? ((String) appendText).getBytes() : null);
          if (b != null)
            os.write(b);
        }
        if (os != null)
          os.flush();
        if (isOsTemp)
          os.close();
      }
    } catch (IOException e) {
      if (!isImage)
        viewer.releaseScreenImage();
      throw new IOException("" + e);
    } catch (Error er) {
      if (!isImage)
        viewer.releaseScreenImage();
      throw new Error(er);
    }
    if (!isImage)
      viewer.releaseScreenImage();
    if (errMsg != null)
      return errMsg;
    return bytes;
  }
}
