/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import java.awt.Color;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * This defines the ObjectLoader interface and static methods for managing and
 * accessing ObjectLoader implementations.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class XML {
  // static constants
  public static String NEW_LINE = "/n";
  public static final String CDATA_PRE = "<![CDATA[";
  public static final String CDATA_POST = "]]>";
  public static final int INDENT = 4;

  // static fields
  private static Map loaders = new HashMap();
  private static ObjectLoader defaultLoader;
  private static String dtdName;
  private static String dtd; // the dtd as a string
  private static String defaultName = "osp10.dtd";
  private static ClassLoader classLoader;

  // added by W. Christian
  static {
    try { // system properties may not be readable in some environments
      NEW_LINE = System.getProperty("line.separator", "/n");
    } catch(SecurityException ex) {}
  }

  /**
   * Private constructor to prevent instantiation.
   */
  private XML() {}

  /**
   * Sets the ObjectLoader for a specified class.
   *
   * @param classtype the class
   * @param loader the ObjectLoader
   */
  public static void setLoader(Class classtype, XML.ObjectLoader loader) {
    loaders.put(classtype, loader);
  }

  /**
   * Gets the ObjectLoader for the specified class.
   *
   * @param classtype the class
   * @return the ObjectLoader
   */
  public static XML.ObjectLoader getLoader(Class classtype) {
    // look for registered loader first
    ObjectLoader loader = (ObjectLoader) loaders.get(classtype);
    // if no registered loader, look for static getLoader() method in class
    if(loader==null) {
      try {
        Method method = classtype.getMethod("getLoader", (Class[]) null);
        if(method!=null&&Modifier.isStatic(method.getModifiers())) {
          loader = (ObjectLoader) method.invoke(null, (Object[]) null);
          if(loader!=null) {
            // register loader for future calls
            setLoader(classtype, loader);
          }
        }
      } catch(IllegalAccessException ex) {}
      catch(IllegalArgumentException ex) {}
      catch(InvocationTargetException ex) {}
      catch(NoSuchMethodException ex) {}
      catch(SecurityException ex) {}
    }
    // if still no loader found, use the default loader
    if(loader==null) {
      if(defaultLoader==null) {
        defaultLoader = new XMLLoader();
      }
      loader = defaultLoader;
    }
    return loader;
  }

  /**
   * Sets the default ObjectLoader. May be set to null.
   *
   * @param loader the ObjectLoader
   */
  public static void setDefaultLoader(XML.ObjectLoader loader) {
    defaultLoader = loader;
  }

  /**
   * Gets the datatype of the object.
   *
   * @param obj the object
   * @return the type
   */
  public static String getDataType(Object obj) {
    if(obj==null) {
      return null;
    }
    if(obj instanceof String) {
      return "string";
    } else if(obj instanceof Collection) {
      return "collection";
    } else if(obj.getClass().isArray()) {
      // make sure ultimate component class is acceptable
      Class componentType = obj.getClass().getComponentType();
      while(componentType.isArray()) {
        componentType = componentType.getComponentType();
      }
      String type = componentType.getName();
      if(type.indexOf(".")==-1&&"intdoubleboolean".indexOf(type)==-1) {
        return null;
      }
      return "array";
    } else if(obj instanceof Double) {
      return "double";
    } else if(obj instanceof Integer) {
      return "int";
    } else {
      return "object";
    }
  }

  /**
   * Gets an array containing all supported data types.
   *
   * @return an array of types
   */
  public static String[] getDataTypes() {
    return new String[] {
      "object", "array", "collection", "string", "int", "double", "boolean"
    };
  }

  /**
   * Determines whether the specified string requires CDATA tags.
   *
   * @param text the string
   * @return <code>true</code> if CDATA tags are required
   */
  public static boolean requiresCDATA(String text) {
    if(text.indexOf("\"")!=-1||text.indexOf("<")!=-1||text.indexOf(">")!=-1||text.indexOf("&")!=-1||text.indexOf("'")!=-1) {
      return true;
    }
    return false;
  }

  /**
   * Gets the DTD for the specified doctype file name.
   *
   * @param doctype the doctype file name (e.g., "osp10.dtd")
   * @return the DTD as a string
   */
  public static String getDTD(String doctype) {
    if(dtdName!=doctype) {
      // set to defaults in case doctype is not found
      dtdName = defaultName;
      try {
        String dtdPath = "/org/opensourcephysics/resources/controls/doctypes/";
        java.net.URL url = XML.class.getResource(dtdPath+doctype);
        if(url==null) {
          return dtd;
        }
        Object content = url.getContent();
        if(content instanceof InputStream) {
          BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) content));
          StringBuffer buffer = new StringBuffer(0);
          String line;
          while((line = reader.readLine())!=null) {
            buffer.append(line+NEW_LINE);
          }
          dtd = buffer.toString();
          dtdName = doctype;
        }
      } catch(IOException ex) {}
    }
    return dtd;
  }

  /**
   * Sets the ClassLoader.
   *
   * @param loader the classLoader
   */
  public static void setClassLoader(ClassLoader loader) {
    classLoader = loader;
  }

  /**
   * Gets the ClassLoader. May be null.
   *
   * @return the classLoader
   */
  public static ClassLoader getClassLoader() {
    return classLoader;
  }

  /**
   * Replaces backslashes with slashes.
   *
   * @param path the path
   * @return the path with forward slashes
   */
  public static String forwardSlash(String path) {
    if(path==null) {
      return "";
    }
    int i = path.indexOf("\\");
    while(i!=-1) {
      path = path.substring(0, i)+"/"+path.substring(i+1);
      i = path.indexOf("\\");
    }
    return path;
  }

  /**
   * Gets the name from the specified path.
   *
   * @param path the full path
   * @return the name alone
   */
  public static String getName(String path) {
    if(path==null) {
      return "";
    }
    // remove path
    int i = path.lastIndexOf("/");
    if(i==-1) {
      i = path.lastIndexOf("\\");
    }
    if(i!=-1) {
      return path.substring(i+1);
    }
    return path;
  }

  /**
   * Gets the extension of the specified file name.
   *
   * @param fileName the file name with or without path
   * @return the extension, or null if none
   */
  public static String getExtension(String fileName) {
    if(fileName==null) {
      return null;
    }
    int i = fileName.lastIndexOf('.');
    if(i>0&&i<fileName.length()-1) {
      return fileName.substring(i+1);
    }
    return null;
  }

  /**
   * Gets a simple class name for the specified class type.
   *
   * @param type the class
   * @return the simple class name
   */
  public static String getSimpleClassName(Class type) {
    String name = type.getName();
    // trim trailing semicolon, if any
    int i = name.indexOf(";");
    if(i>-1) {
      name = name.substring(0, i);
    }
    // add brackets for arrays
    while(name.startsWith("[")) {
      name = name.substring(1);
      name = name+"[]";
    }
    // eliminate leading package name, if any
    String ext = XML.getExtension(name);
    if(ext!=null) {
      name = ext;
    }
    // substitute int for I and double for D arrays
    i = name.indexOf("[");
    if(i>-1) {
      String s = name.substring(0, i);
      if(s.equals("I")) {
        s = "int";
      }
      if(s.equals("D")) {
        s = "double";
      }
      name = s+name.substring(i);
    }
    return name;
  }

  /**
   * Strips the extension from the specified file name.
   *
   * @param fileName the file name with or without path
   * @return the file name without extension
   */
  public static String stripExtension(String fileName) {
    if(fileName==null) {
      return null;
    }
    int i = fileName.lastIndexOf('.');
    if(i>0&&i<fileName.length()-1) {
      return fileName.substring(0, i);
    }
    return fileName;
  }

  /**
   * Gets the path relative to the specified base directory.
   *
   * @param absolutePath the absolute path
   * @param base the absolute base directory path
   * @return (with forward slashes) the path relative to the base,
   *   or the absolutePath if no relative path is found (eg, different drive)
   *   or the absolutePath if it is really a relative path
   */
  public static String getPathRelativeTo(String absolutePath, String base) {
    // if no base specified then base is current user directory
    if(base==null||base.equals("")) {
      base = getUserDirectory();
    }
    // make sure both paths use forward slashes
    absolutePath = forwardSlash(absolutePath);
    base = forwardSlash(base);
    // return absolutePath if either path not absolute
    if(!absolutePath.startsWith("/")&&absolutePath.indexOf(":")==-1) {
      return absolutePath;
    }
    if(!base.startsWith("/")&&base.indexOf(":")==-1) {
      return absolutePath;
    }
    // look for paths in jar files
    int jar = absolutePath.indexOf("jar!");
    if(jar>-1) {
      absolutePath = absolutePath.substring(jar+5);
      return absolutePath;
    }
    // construct relative path
    String relativePath = "";
    // search for base up containing hierarchy
    if(base.endsWith("/")) {
      base = base.substring(0, base.length()-1);
    }
    for(int j = 0;j<6;j++) {
      // move base up one level each iteration after the first
      if(j>0) {
        int k = base.lastIndexOf("/");
        if(k!=-1) {
          base = base.substring(0, k); // doesn't include the slash
          relativePath += "../";
        } else if(!base.equals("")) {
          base = "";
          relativePath += "../";
        } else {
          break;                       // no more levels
        }
      }
      // construct and return relative path once base is in the path
      if(!base.equals("")&&absolutePath.startsWith(base)) {
        String path = absolutePath.substring(base.length());
        // eliminate leading slash, if any
        int k = path.indexOf("/");
        if(k==0) {
          path = path.substring(1);
        }
        relativePath += path;
        return relativePath;
      }
    }
    // relative path not found
    return absolutePath;
  }

  /**
   * Gets a path relative to the default user directory.
   *
   * @param absolutePath the absolute path
   * @return the relative path, with forward slashes
   */
  public static String getRelativePath(String absolutePath) {
    return getPathRelativeTo(absolutePath, getUserDirectory());
    // todo: relative to doc base, code base, xml file?
  }

  /**
   * Gets the default user directory.
   *
   * @return the user directory
   */
  public static String getUserDirectory() {
    String userDir = System.getProperty("user.dir", ".");
    return userDir;
  }

  /**
   * Gets the path of the directory containing the specified file.
   *
   * @param fileName the full file name, including path
   * @return the directory path, with forward slashes
   */
  public static String getDirectoryPath(String fileName) {
    if(fileName==null) {
      return "";
    }
    fileName = forwardSlash(fileName);
    int slash = fileName.lastIndexOf("/");
    if(slash!=-1) {
      return fileName.substring(0, slash);
    } else {
      return "";
    }
  }

  /**
   * Resolves the name of a file specified relative to a base path.
   *
   * @param relativePath the relative file name
   * @param base the absolute base path
   * @return the resolved file name with forward slashes
   */
  public static String getResolvedPath(String relativePath, String base) {
    relativePath = forwardSlash(relativePath);
    // return relativePath if it is really absolute
    if(relativePath.startsWith("/")||relativePath.indexOf(":/")!=-1) {
      return relativePath;
    }
    base = forwardSlash(base);
    while(relativePath.startsWith("../")&&!base.equals("")) {
      if(base.indexOf("/")==-1) {
        base = "/"+base;
      }
      relativePath = relativePath.substring(3);
      base = base.substring(0, base.lastIndexOf("/"));
    }
    if(base.equals("")) {
      return relativePath;
    }
    if(base.endsWith("/")) {
      return base+relativePath;
    }
    return base+"/"+relativePath;
  }

  /**
   * Creates any missing folders in the specified path.
   *
   * @param path the path to construct
   */
  public static void createFolders(String path) {
    // work way up path to find existing folder
    File dir = new File(path);
    ArrayList dirs = new ArrayList(); // list of needed directories
    while(!dir.exists()) {
      dirs.add(0, dir);
      int j = path.lastIndexOf("/");
      if(j==-1) {
        break;
      }
      path = path.substring(0, j);
      dir = new File(path);
    }
    // work back down path and create needed directories
    Iterator it = dirs.iterator();
    while(it.hasNext()) {
      dir = (File) it.next();
      dir.mkdir();
    }
  }

  // _________________________ ObjectLoader interface _____________________________

  /**
   * This defines methods for moving xml data between an XMLControl and
   * a corresponding Java object.
   *
   * @author Douglas Brown
   * @version 1.0
   */
  public interface ObjectLoader {

    /**
     * Saves data from an object to an XMLControl. The object must
     * be castable to the class control.getObjectClass().
     *
     * @param control the xml control
     * @param obj the object
     */
    public void saveObject(XMLControl control, Object obj);

    /**
     * Creates an object from data in an XMLControl. The returned object must
     * be castable to the class control.getObjectClass().
     *
     * @param control the xml control
     * @return a new object
     */
    public Object createObject(XMLControl control);

    /**
     * Loads an object with data from an XMLControl. The object must
     * be castable to the class control.getObjectClass().
     *
     * @param control the xml control
     * @param obj the object
     * @return the loaded object
     */
    public Object loadObject(XMLControl control, Object obj);
  }

  // static initializer defines loaders for commonly used classes
  static {
    setLoader(Color.class, new XML.ObjectLoader() {
      public void saveObject(XMLControl control, Object obj) {
        java.awt.Color color = (java.awt.Color) obj;
        control.setValue("red", color.getRed());
        control.setValue("green", color.getGreen());
        control.setValue("blue", color.getBlue());
        control.setValue("alpha", color.getAlpha());
      }
      public Object createObject(XMLControl control) {
        int r = control.getInt("red");
        int g = control.getInt("green");
        int b = control.getInt("blue");
        int a = control.getInt("alpha");
        return new java.awt.Color(r, g, b, a);
      }
      public Object loadObject(XMLControl control, Object obj) {
        int r = control.getInt("red");
        int g = control.getInt("green");
        int b = control.getInt("blue");
        int a = control.getInt("alpha");
        return new java.awt.Color(r, g, b, a);
      }
    });
    setLoader(Double.class, new XML.ObjectLoader() {
      public void saveObject(XMLControl control, Object obj) {
        Double dbl = (Double) obj;
        control.setValue("value", dbl.doubleValue());
      }
      public Object createObject(XMLControl control) {
        double val = control.getDouble("value");
        return new Double(val);
      }
      public Object loadObject(XMLControl control, Object obj) {
        Double dbl = (Double) obj;
        double val = control.getDouble("value");
        if(dbl.doubleValue()==val) {
          return dbl;
        }
        return new Double(val);
      }
    });
    setLoader(Integer.class, new XML.ObjectLoader() {
      public void saveObject(XMLControl control, Object obj) {
        Integer i = (Integer) obj;
        control.setValue("value", i.intValue());
      }
      public Object createObject(XMLControl control) {
        int val = control.getInt("value");
        return new Integer(val);
      }
      public Object loadObject(XMLControl control, Object obj) {
        Integer i = (Integer) obj;
        int val = control.getInt("value");
        if(i.intValue()==val) {
          return i;
        }
        return new Integer(val);
      }
    });
    setLoader(Boolean.class, new XML.ObjectLoader() {
      public void saveObject(XMLControl control, Object obj) {
        Boolean bool = (Boolean) obj;
        control.setValue("value", bool.booleanValue());
      }
      public Object createObject(XMLControl control) {
        boolean val = control.getBoolean("value");
        return new Boolean(val);
      }
      public Object loadObject(XMLControl control, Object obj) {
        Boolean bool = (Boolean) obj;
        boolean val = control.getBoolean("value");
        if(bool.booleanValue()==val) {
          return bool;
        }
        return new Boolean(val);
      }
    });
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
