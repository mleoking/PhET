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
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.*;

/**
 * This defines static methods for loading resources.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class ResourceLoader {
  protected static ArrayList searchPaths = new ArrayList(); // search paths
  protected static int maxPaths = 20;                       // max number of paths in history
  protected static Hashtable resources = new Hashtable();   // cached resources
  protected static boolean cacheEnabled = false;
  protected static URLClassLoader zipLoader;
  protected static String launchJarName;

  /**
   * Private constructor to prevent instantiation.
   */
  private ResourceLoader() {}

  /**
  * Gets a resource specified by name. If no resource is found using the name
  * alone, the searchPaths are searched.
  *
  * @param name the file or URL name
  * @return the Resource, or null if none found
  */
  public static Resource getResource(String name) {
    return getResource(name, Resource.class);
  }

  /**
  * Gets a resource specified by name and Class. If no resource is found using
  * the name alone, the searchPaths are searched.
  *
  * @param name the file or URL name
  * @param type the Class providing ClassLoader resource loading
  * @return the Resource, or null if none found
  */
  public static Resource getResource(String name, Class type) {
    // look for resource with name only
    Resource res = findResource(name, type);
    if(res!=null) {
      return res;
    }
    StringBuffer err = new StringBuffer("Not found: "+name);
    err.append(" [searched "+name);
    if(OSPFrame.applet!=null) { // applet mode
      JApplet applet = OSPFrame.applet;
      String path = getPath(applet.getCodeBase().toExternalForm(), name);
      res = findResource(path, type);
      if(res!=null) {
        return res;
      }
      err.append(";"+path);
      path = getPath(applet.getDocumentBase().toExternalForm(), name);
      res = findResource(path, type);
      if(res!=null) {
        return res;
      }
      err.append(";"+path);
    }
    // look for resource with name and searchPaths
    for(Iterator it = searchPaths.iterator();it.hasNext();) {
      String path = getPath((String) it.next(), name);
      res = findResource(path, type);
      if(res!=null) {
        return res;
      }
      err.append(";"+path);
    }
    err.append("]");
    OSPLog.fine(err.toString());
    return null;
  }

  /**
  * Gets a resource specified by base path and name. No additional searchpaths
  * are searched.
  *
  * @param basePath the base path
  * @param name the file or URL name
  * @return the Resource, or null if none found
  */
  public static Resource getResource(String basePath, String name) {
    return getResource(basePath, name, Resource.class);
  }

  /**
  * Gets a resource specified by base path, name and class. No additional
  * searchpaths are searched.
  *
  * @param basePath the base path
  * @param name the file or URL name
  * @param type the Class providing ClassLoader resource loading
  * @return the Resource, or null if none found
  */
  public static Resource getResource(String basePath, String name, Class type) {
    String path = getPath(basePath, name);
    Resource res = findResource(path, type);
    if(res==null) {
      OSPLog.info("Not found: "+path);
    }
    return res;
  }

  /**
  * Adds a path at the beginning of the searchPaths list.
  *
  * @param base the base path to add
  */
  public static void addSearchPath(String base) {
    if(base==null||base.equals("")||maxPaths<1) {
      return;
    }
    synchronized(searchPaths) {
      if(searchPaths.contains(base)) {
        searchPaths.remove(base);
      } else {
        OSPLog.fine("Added path: "+base);
      }
      searchPaths.add(0, base);
      while(searchPaths.size()>Math.max(maxPaths, 0)) {
        base = (String) searchPaths.get(searchPaths.size()-1);
        OSPLog.fine("Removed path: "+base);
        searchPaths.remove(base);
      }
    }
  }

  /**
   * Removes a path from the searchPaths list.
   *
   * @param base the base path to remove
   */
  public static void removeSearchPath(String base) {
    if(base==null||base.equals("")) {
      return;
    }
    synchronized(searchPaths) {
      if(searchPaths.contains(base)) {
        OSPLog.fine("Removed path: "+base);
        searchPaths.remove(base);
      }
    }
  }

  /**
   * Sets the cacheEnabled property.
   *
   * @param enabled true to enable the cache
   */
  public static void setCacheEnabled(boolean enabled) {
    cacheEnabled = enabled;
  }

  /**
   * Gets the cacheEnabled property.
   *
   * @return true if the cache is enabled
   */
  public static boolean isCacheEnabled() {
    return cacheEnabled;
  }

  // ___________________________ convenience methods ______________________________
  public static InputStream openInputStream(String path) {
    Resource res = getResource(path);
    return res==null ? null : res.openInputStream();
  }

  public static Reader openReader(String path) {
    Resource res = getResource(path);
    return res==null ? null : res.openReader();
  }

  public static String getString(String path) {
    Resource res = getResource(path);
    return res==null ? null : res.getString();
  }

  public static ImageIcon getIcon(String path) {
    Resource res = getResource(path);
    return res==null ? null : res.getIcon();
  }

  public static Image getImage(String path) {
    Resource res = getResource(path);
    return res==null ? null : res.getImage();
  }

  public static BufferedImage getBufferedImage(String path) {
    Resource res = getResource(path);
    return res==null ? null : res.getBufferedImage();
  }

  public static AudioClip getAudioClip(String path) {
    Resource res = getResource(path);
    return res==null ? null : res.getAudioClip();
  }

  // ______________________________ private methods _______________________________

  /**
   * Creates a Resource from a file.
   *
   * @param path the file path
   * @return the resource, if any
   */
  static private Resource createFileResource(String path) {
    // don't create resource for zip or jar files
    if(path.endsWith(".zip")||path.endsWith(".jar")) {
      return null;
    }
    File file = new File(path);
    if(file.exists()&&file.canRead()) {
      OSPLog.finest("Created from file: "+path);
      return new Resource(file);
    }
    return null;
  }

  /**
   * Creates a Resource from within a zip file.
   *
   * @param path the file path relative to the zip file root
   * @return the resource, if any
   */
  static private Resource createZipResource(String path) {
    // remove zip base path and leading slash from path
    int i = path.indexOf("zip!/");
    if(i>-1) {
      path = path.substring(i+5);
    }
    URL url = null;
    if(path.endsWith(".zip")||path.endsWith(".xset")) {
      try {
        // create single-zip loader and look for xset of same name within zip file
        String name = XML.getName(path);
        // replace xset with zip extension if nec
        if(path.endsWith(".xset")) {
          name = name.substring(0, name.length()-4)+"zip";
        }
        URL[] urls = new URL[] {new URL("file:"+name)};
        URLClassLoader loader = new URLClassLoader(urls);
        // replace zip with xset extension
        name = name.substring(0, name.length()-3)+"xset";
        url = loader.findResource(name);
      } catch(MalformedURLException ex) {}
    } else {
      // create zip loader if null
      if(zipLoader==null) {
        // find zip files in local directory
        File dir = new File(".");
        String[] fileNames = dir.list(new FilenameFilter() {
          public boolean accept(File dir, String name) {
            return name.endsWith(".zip");
          }
        });
        URL[] urls = new URL[fileNames.length];
        try {
          for(i = 0;i<fileNames.length;i++) {
            urls[i] = new URL("file:"+fileNames[i]);
          }
        } catch(MalformedURLException ex) {}
        zipLoader = new URLClassLoader(urls);
      }
      url = zipLoader.findResource(path);
    }
    if(url!=null) {
      try {
        Resource res = createResource(url);
        OSPLog.finest("Created from zip file: "+url.toString());
        return res;
      } catch(IOException ex) {}
    }
    return null;
  }

  /**
   * Creates a Resource from a class resource, typically in a jar file.
   *
   * @param name the resource name
   * @param type the class providing the classloader
   * @return the resource, if any
   */
  static private Resource createJarResource(String name, Class type) {
    // ignore any name that has a protocol
    if(name.indexOf(":/")!=-1) {
      return null;
    }
    Resource res = null;
    try { // check relative to root of jarfile containing specified class
      URL url = type.getResource("/"+name);
      res = createResource(url);
    } catch(Exception ex) {}
    if(res==null) {
      try { // check relative to specified class
        URL url = type.getResource(name);
        res = createResource(url);
      } catch(Exception ex) {}
    }
    // if resource is found, log and set launchJarName if not yet set
    if(res!=null) {
      String path = XML.forwardSlash(res.getAbsolutePath());
      OSPLog.finest("Created from jar: "+path);
      if(launchJarName==null) {
        int n = path.indexOf(".jar!");
        if(n>-1) {
          path = path.substring(0, n+4);
          launchJarName = path.substring(path.lastIndexOf("/")+1);
        }
      }
    }
    return res; // may be null
  }

  /**
   * Creates a Resource from a URL.
   *
   * @param path the url path, typically web address but may be any valid url
   * @return the resource, if any
   */
  static private Resource createURLResource(String path) {
    try {
      URL url = new URL(path);
      Resource res = createResource(url);
      OSPLog.finest("Created from URL: "+path);
      return res;
    } catch(Exception ex) {
      return null;
    }
  }

  /**
   * Creates a Resource.
   *
   * @param url the URL
   * @return the resource, if any
   * @throws IOException
   */
  static private Resource createResource(URL url) throws IOException {
    // check that url is accessible
    InputStream stream = url.openStream();
    stream.close();
    return new Resource(url);
  }

  private static Resource findResource(String path, Class type) {
    Resource res = null;
    // look for cached resource
    if(cacheEnabled) {
      res = (Resource) resources.get(path);
      if(res!=null) {
        OSPLog.finest("Found in cache: "+path);
        return res;
      }
    }
    // try to load resource in file/zip/web/jar order
    if((res = createFileResource(path))!=null||(res = createZipResource(path))!=null||(res = createURLResource(path))!=null||(res = createJarResource(path, type))!=null) {
      if(cacheEnabled) {
        resources.put(path, res);
      }
      return res;
    }
    return null;
  }

  /**
   * Gets a path from the basePath and file name.
   *
   * @param basePath the base path
   * @param name the file name
   * @return the path
   */
  static String getPath(String basePath, String name) {
    if(!basePath.equals("")&&!basePath.endsWith("/")) {
      basePath += "/";
    }
    // corrects the path so that it works with Mac
    if(basePath.startsWith("file:/")&&!basePath.startsWith("file:///")) {
      basePath = "file:///"+basePath.substring(6);
    }
    return basePath+name;
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
