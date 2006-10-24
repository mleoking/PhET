/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.applet.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import org.opensourcephysics.controls.*;
import java.awt.image.BufferedImage;

/**
 * This represents a resource obtained from a URL or File.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class Resource {
  private URL url;
  private File file;
  private boolean isAnImage = true;
  private ImageIcon icon;
  private String string;
  private AudioClip clip;
  private BufferedImage image;

  /**
   * Constructs a resource from a url.
   *
   * @param url the URL
   */
  public Resource(URL url) {
    this.url = url;
  }

  /**
   * Constructs a resource from a file.
   *
   * @param file the file
   */
  public Resource(File file) {
    this.file = file;
  }

  /**
   * Gets the absolute path.
   *
   * @return the absolute path
   */
  public String getAbsolutePath() {
    if(getFile()!=null) {
      try {
        return XML.forwardSlash(getFile().getCanonicalPath());
      } catch(IOException ex) {}
      return getFile().getAbsolutePath();
    }
    if(getURL()!=null) {
      URL url = getURL();
      String path = url.getPath();
      // remove file protocol, if any
      if(path.startsWith("file:")) {
        path = path.substring(5);
      }
      // remove leading slash if drive is specified
      if(path.startsWith("/")&&path.indexOf(":")>-1) {
        path = path.substring(1);
      }
      // replace "%20" with space
      int i = path.indexOf("%20");
      while(i>-1) {
        String s = path.substring(0, i);
        path = s+" "+path.substring(i+3);
        i = path.indexOf("%20");
      }
      return path;
    }
    return null;
  }

  /**
   * Gets the url associated with this resource.
   *
   * @return the URL
   */
  public URL getURL() {
    if(url==null&&file!=null) {
      try {
        String fileName = getAbsolutePath();
        if(fileName.startsWith("/")) {
          url = new URL("file:"+fileName);
        } else {
          url = new URL("file:/"+fileName);
        }
      } catch(MalformedURLException ex) {}
    }
    return url;
  }

  /**
   * Gets the file associated with this resource.
   *
   * @return the File
   */
  public File getFile() {
    return file;
  }

  /**
   * Gets an object of the specified type. Currently the only types
   * recognized are String and ImageIcon.
   *
   * @param type the desired class type
   * @return the object, or null
   */
  public Object getObject(Class type) {
    if(ImageIcon.class.equals(type)) {
      return getIcon();
    }
    if(String.class.equals(type)) {
      return getString();
    }
    return null;
  }

  /**
   * Opens an InputStream.
   *
   * @return the stream
   */
  public InputStream openInputStream() {
    if(getURL()!=null) {
      try {
        return getURL().openStream();
      } catch(IOException ex) {}
    }
    return null;
  }

  /**
   * Opens a BufferedReader.
   *
   * @return the reader
   */
  public BufferedReader openReader() {
    if(getFile()!=null) {
      try {
        return new BufferedReader(new FileReader(file));
      } catch(FileNotFoundException ex) {}
    }
    if(getURL()!=null) {
      return new BufferedReader(new InputStreamReader(openInputStream()));
    }
    return null;
  }

  /**
   * Gets an ImageIcon.
   *
   * @return the icon
   */
  public ImageIcon getIcon() {
    if(icon==null&&isAnImage) {
      icon = new ImageIcon(getURL());
      if(icon.getIconWidth()<1) {
        icon = null;
        isAnImage = false;
      }
    }
    return icon;
  }

  /**
   * Gets an Image.
   *
   * @return the image
   */
  public Image getImage() {
    ImageIcon icon = getIcon();
    if(icon!=null) {
      return icon.getImage();
    }
    return null;
  }

  /**
   * Gets a buffered image.
   *
   * @return the image
   */
  public BufferedImage getBufferedImage() {
    if(image==null&&isAnImage) {
      Image im = getImage();
      if(im==null) {
        isAnImage = false;
      } else {
        image = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.drawImage(im, 0, 0, null);
      }
    }
    return image;
  }

  /**
   * Gets a String.
   *
   * @return the string
   */
  public String getString() {
    if(string==null) {
      StringBuffer buffer = new StringBuffer();
      try {
        BufferedReader in = new BufferedReader(openReader());
        String line = in.readLine();
        while(line!=null) {
          buffer.append(line+XML.NEW_LINE);
          line = in.readLine();
        }
        in.close();
      } catch(IOException ex) {}
      string = buffer.toString();
    }
    return string;
  }

  /**
   * Gets an AudioClip.
   *
   * @return the audio clip
   */
  public AudioClip getAudioClip() {
    if(clip==null&&getURL()!=null) {
      clip = Applet.newAudioClip(getURL());
    }
    return clip;
  }
}

/* 
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */
