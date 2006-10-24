/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import org.opensourcephysics.tools.*;

/**
 * This is a basic xml control for storing data.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class XMLControlElement implements XMLControl {
  // static fields
  public static int compactArraySize = 0;

  // instance fields
  protected String className = Object.class.getName();
  protected String name;
  protected Map counts = new HashMap(); // maps numbered names to counts
  protected Object object;
  protected XMLProperty parent;
  protected int level;
  protected ArrayList propNames = new ArrayList();
  protected ArrayList props = new ArrayList();
  protected BufferedReader input;
  protected BufferedWriter output;
  public boolean canWrite;
  protected boolean valid = false;
  protected boolean readFailed = false;
  protected String version;
  protected String doctype = "osp10.dtd";
  private String basepath;

  /**
   * Constructs a default element for the Object class.
   */
  public XMLControlElement() {}

  /**
   * Constructs an element for the specified class.
   *
   * @param type the class.
   */
  public XMLControlElement(Class type) {
    this();
    className = type.getName();
  }

  /**
   * Constructs an element for the specified object.
   *
   * @param obj the object.
   */
  public XMLControlElement(Object obj) {
    this();
    className = obj.getClass().getName();
    saveObject(obj);
  }

  /**
   * Constructs an element with the specified parent.
   *
   * @param parent the parent.
   */
  public XMLControlElement(XMLProperty parent) {
    this();
    this.parent = parent;
    level = parent.getLevel();
  }

  /**
   * Constructs an element from the specified named source.
   *
   * @param name the name
   */
  public XMLControlElement(String name) {
    this();
    if(name.startsWith("<?xml")) {
      readXML(name);
    } else {
      read(name);
    }
  }

  /**
   * Constructs a copy of the specified XMLControl.
   *
   * @param control the XMLControl to copy.
   */
  public XMLControlElement(XMLControl control) {
    this();
    readXML(control.toXML());
  }

  /**
   * Locks the control's interface. Values sent to the control will not
   * update the display until the control is unlocked. Not yet implemented.
   *
   * @param lock boolean
   */
  public void setLockValues(boolean lock) {}

  /**
   * Sets a property with the specified name and boolean value.
   *
   * @param name the name
   * @param value the boolean value
   */
  public void setValue(String name, boolean value) {
    if(name==null) {
      return;
    }
    setXMLProperty(name, "boolean", String.valueOf(value));
  }

  /**
   * Sets a property with the specified name and double value.
   *
   * @param name the name
   * @param value the double value
   */
  public void setValue(String name, double value) {
    if(name==null) {
      return;
    }
    setXMLProperty(name, "double", String.valueOf(value));
  }

  /**
   * Sets a property with the specified name and int value.
   *
   * @param name the name
   * @param value the int value
   */
  public void setValue(String name, int value) {
    if(name==null) {
      return;
    }
    setXMLProperty(name, "int", String.valueOf(value));
  }

  /**
   * Sets a property with the specified name and object value.
   *
   * @param name the name
   * @param obj the object
   */
  public void setValue(String name, Object obj) {
    if(name==null) {
      return;
    }
    // clear the property if obj is null
    if(obj==null) {
      Iterator it = props.iterator();
      while(it.hasNext()) {
        XMLProperty prop = (XMLProperty) it.next();
        if(name.equals(prop.getPropertyName())) {
          it.remove();
          propNames.remove(name);
          break;
        }
      }
      return;
    }
    String type = XML.getDataType(obj);
    if(type!=null) {
      if(type.equals("int")||type.equals("double")) {
        obj = obj.toString();
      }
      setXMLProperty(name, type, obj);
    }
  }

  /**
   * Gets the boolean value of the specified named property.
   *
   * @param name the name
   * @return the boolean value, or false if none found
   */
  public boolean getBoolean(String name) {
    XMLProperty prop = getXMLProperty(name);
    if(prop!=null&&prop.getPropertyType().equals("boolean")) {
      return "true".equals(prop.getPropertyContent().get(0));
    }
    return false;
  }

  /**
   * Gets the double value of the specified named property.
   *
   * @param name the name
   * @return the double value, or Double.NaN if none found
   */
  public double getDouble(String name) {
    XMLProperty prop = getXMLProperty(name);
    if(prop!=null&&prop.getPropertyType().equals("double")) {
      return Double.parseDouble((String) prop.getPropertyContent().get(0));
    }
    return Double.NaN;
  }

  /**
   * Gets the int value of the specified named property.
   *
   * @param name the name
   * @return the int value, or Integer.MIN_VALUE if none found
   */
  public int getInt(String name) {
    XMLProperty prop = getXMLProperty(name);
    if(prop!=null&&prop.getPropertyType().equals("int")) {
      return Integer.parseInt((String) prop.getPropertyContent().get(0));
    }
    return Integer.MIN_VALUE;
  }

  /**
   * Gets the string value of the specified named property.
   *
   * @param name the name
   * @return the string value, or null if none found
   */
  public String getString(String name) {
    XMLProperty prop = getXMLProperty(name);
    if(prop!=null&&prop.getPropertyType().equals("string")) {
      return(String) prop.getPropertyContent().get(0);
    } else if(name.equals("basepath")&&getRootControl()!=null) {
      return getRootControl().basepath;
    }
    return null;
  }

  /**
   * Gets the object value of the specified named property.
   *
   * @param name the name
   * @return the object, or null if not found
   */
  public Object getObject(String name) {
    XMLProperty prop = getXMLProperty(name);
    if(prop!=null) {
      String type = prop.getPropertyType();
      if(type.equals("object")) {
        return objectValue(prop);
      } else if(type.equals("array")) {
        return arrayValue(prop);
      } else if(type.equals("collection")) {
        return collectionValue(prop);
      } else if(type.equals("int")) {
        return new Integer(intValue(prop));
      } else if(type.equals("double")) {
        return new Double(doubleValue(prop));
      } else if(type.equals("boolean")) {
        return new Boolean(booleanValue(prop));
      } else if(type.equals("string")) {
        return stringValue(prop);
      }
    }
    return null;
  }

  /**
   * Gets the set of property names.
   *
   * @return a set of names
   */
  public Collection getPropertyNames() {
    synchronized(propNames) {
      return(List) propNames.clone();
    }
  }

  /**
   * Gets the type of the specified property. Returns null if the property
   * is not found.
   *
   * @param name the property name
   * @return the type
   */
  public String getPropertyType(String name) {
    XMLProperty prop = getXMLProperty(name);
    if(prop!=null) {
      return prop.getPropertyType();
    }
    return null;
  }

  /**
   * Reads data into this control from a named source.
   *
   * @param name the name
   * @return the path of the opened document or null if failed
   */
  public String read(String name) {
    OSPLog.finest("reading "+name);
    Resource res = ResourceLoader.getResource(name);
    if(res!=null) {
      read(res.openReader());
      String path = XML.getDirectoryPath(name);
      if(!path.equals("")) {
        ResourceLoader.addSearchPath(path);
        basepath = path;
      } else {
        basepath = XML.getDirectoryPath(res.getAbsolutePath());
      }
      File file = res.getFile();
      canWrite = (file!=null&&file.canWrite());
      return res.getAbsolutePath();
    } else {
      readFailed = true;
    }
    return null;
  }

  /**
   * Reads the control from an xml string.
   *
   * @param xml the xml string
   */
  public void readXML(String xml) {
    input = new BufferedReader(new StringReader(xml));
    readInput();
    if(!failedToRead()) {
      canWrite = false;
    }
  }

  /**
   * Reads the control from a Reader.
   *
   * @param in the Reader
   */
  public void read(Reader in) {
    if(in instanceof BufferedReader) {
      input = (BufferedReader) in;
    } else {
      input = new BufferedReader(in);
    }
    readInput();
    try {
      input.close();
    } catch(IOException ex) {}
  }

  /**
   * Returns true if the most recent read operation failed.
   *
   * @return <code>true</code> if the most recent read operation failed
   */
  public boolean failedToRead() {
    return readFailed;
  }

  /**
   * Writes this control as an xml file with the specified name.
   *
   * @param fileName the file name
   * @return the path of the saved document or null if failed
   */
  public String write(String fileName) {
    canWrite = true;
    int n = fileName.lastIndexOf("/");
    if(n<0) {
      n = fileName.lastIndexOf("\\");
    }
    if(n>0) {
      String dir = fileName.substring(0, n+1);
      File file = new File(dir);
      if(!file.exists()&&!file.mkdir()) {
        canWrite = false;
        return null;
      }
    }
    try {
      File file = new File(fileName);
      if(file.exists()&&!file.canWrite()) {
        JOptionPane.showMessageDialog(null, "File is read-only.");
        canWrite = false;
        return null;
      }
      write(new FileWriter(file));
      // add search path to ResourceLoader
      if(file.exists()) {
        String path = XML.getDirectoryPath(file.getCanonicalPath());
        ResourceLoader.addSearchPath(path);
      }
      // write dtd if valid
      if(isValid()) {
        // replace xml file name with dtd file name
        if(fileName.indexOf("/")!=-1) {
          fileName = fileName.substring(0, fileName.lastIndexOf("/")+1)+getDoctype();
        } else if(fileName.indexOf("\\")!=-1) {
          fileName = fileName.substring(0, fileName.lastIndexOf("\\")+1)+getDoctype();
        } else {
          fileName = doctype;
        }
        writeDocType(new FileWriter(fileName));
      }
      if(file.exists()) {
        return file.getAbsolutePath();
      }
    } catch(IOException ex) {
      canWrite = false;
      OSPLog.warning(ex.getMessage());
    }
    return null;
  }

  /**
   * Writes this control to a Writer.
   *
   * @param out the Writer
   */
  public void write(Writer out) {
    try {
      output = new BufferedWriter(out);
      output.write(toXML());
      output.flush();
      output.close();
    } catch(IOException ex) {
      OSPLog.info(ex.getMessage());
    }
  }

  /**
   * Writes the DTD to a Writer.
   *
   * @param out the Writer
   */
  public void writeDocType(Writer out) {
    try {
      output = new BufferedWriter(out);
      output.write(XML.getDTD(getDoctype()));
      output.flush();
      output.close();
    } catch(IOException ex) {
      OSPLog.info(ex.getMessage());
    }
  }

  /**
   * Returns this control as an xml string.
   *
   * @return the xml string
   */
  public String toXML() {
    return toString();
  }

  /**
   * Sets the valid property.
   *
   * @param valid <code>true</code> to write the DTD and DocType
   */
  public void setValid(boolean valid) {
    this.valid = valid;
  }

  /**
   * Gets the valid property. When true, this writes the DTD and defines
   * the DocType when writing an xml document. Note: the presence or absense
   * of the DocType header and DTD has no effect on the read() methods--this
   * will always read a well-formed osp document and ignore a non-osp document.
   *
   * @return <code>true</code> if this is valid
   */
  public boolean isValid() {
    return valid&&XML.getDTD(getDoctype())!=null;
  }

  /**
   * Sets the version.
   *
   * @param vers the version data
   */
  public void setVersion(String vers) {
    version = vers;
  }

  /**
   * Gets the version. May return null.
   *
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the doctype. Not yet implemented since only one doctype is defined.
   *
   * @param name the doctype resource name
   */
  public void setDoctype(String name) {
    if(XML.getDTD(name)!=null) {
      // check that name is accepted, etc
      // could make acceptable names be public String constants?
    }
  }

  /**
   * Gets the doctype. May return null.
   *
   * @return the doctype
   */
  public String getDoctype() {
    return doctype;
  }

  /**
   * Gets the class of the object for which this element stores data.
   *
   * @return the <code>Class</code> of the object
   */
  public Class getObjectClass() {
    if(className==null) {
      return null;
    }
    Class theClass = null;
    ClassLoader loader = XML.getClassLoader();
    if(loader!=null) {
      try {
        theClass = loader.loadClass(className);
      } catch(ClassNotFoundException ex) {}
    }
    if(theClass==null) {
      try {
        theClass = Class.forName(className);
      } catch(ClassNotFoundException ex) {}
    }
    return theClass;
  }

  /**
   * Gets the name of the object class for which this element stores data.
   *
   * @return the object class name
   */
  public String getObjectClassName() {
    return className;
  }

  /**
   * Saves an object's data in this element.
   *
   * @param obj the object to save.
   */
  public void saveObject(Object obj) {
    if(obj==null) {
      obj = object;
    }
    Class type = getObjectClass();
    if(type==null||type.equals(Object.class)) {
      if(obj==null) {
        return;
      }
      type = obj.getClass();
    }
    if(type.isInstance(obj)) {
      object = obj;
      className = obj.getClass().getName();
      clearValues();
      XML.ObjectLoader loader = XML.getLoader(type);
      loader.saveObject(this, obj);
    }
  }

  /**
   * Loads an object with data from this element. This asks the user for
   * approval and review before importing data from mismatched classes.
   *
   * @param obj the object to load
   * @return the loaded object
   */
  public Object loadObject(Object obj) {
    return loadObject(obj, false, false);
  }

  /**
   * Loads an object with data from this element. This asks the user to
   * review data from mismatched classes before importing it.
   *
   * @param obj the object to load
   * @param autoImport true to automatically import data from mismatched classes
   * @return the loaded object
   */
  public Object loadObject(Object obj, boolean autoImport) {
    return loadObject(obj, autoImport, false);
  }

  /**
   * Loads an object with data from this element.
   *
   * @param obj the object to load
   * @param autoImport true to automatically import data from mismatched classes
   * @param importAll true to import all importable data
   * @return the loaded object
   */
  public Object loadObject(Object obj, boolean autoImport, boolean importAll) {
    Class type = getObjectClass();
    if(type==null) {
      if(obj!=null) {
        if(!autoImport) {
          int result = JOptionPane.showConfirmDialog(null, "This XML data is for unknown class \""+className+"\"."+XML.NEW_LINE+"Do you wish to import it into "+obj.getClass()+"?", "Mismatched Classes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
          if(result!=JOptionPane.YES_OPTION) {
            return obj;
          }
        }
        if(!importInto(obj, importAll)) {
          return obj;
        }
        type = obj.getClass();
      } else {
        return null;
      }
    }
    if(obj!=null&&!type.isInstance(obj)) {
      if(!autoImport) {
        int result = JOptionPane.showConfirmDialog(null, "This XML data is for "+type+"."+XML.NEW_LINE+"Do you wish to import it into "+obj.getClass()+"?", "Mismatched Classes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(result!=JOptionPane.YES_OPTION) {
          return obj;
        }
      }
      if(!importInto(obj, importAll)) {
        return obj;
      }
      type = obj.getClass();
    }
    XML.ObjectLoader loader = XML.getLoader(type);
    if(obj==null) { // if obj is null, try to create a new one
      if(object==null) {
        object = loader.createObject(this);
      }
      obj = object;
    }
    if(obj==null) {
      return null; // unable to create new obj
    }
    if(type.isInstance(obj)) {
      obj = loader.loadObject(this, obj);
      object = obj;
    }
    return obj;
  }

  /**
   * Clears all properties.
   */
  public void clearValues() {
    props.clear();
    propNames.clear();
  }

  /**
   * Method required by the Control interface.
   *
   * @param s the string
   */
  public void println(String s) {
    System.out.println(s);
  }

  /**
   * Method required by the Control interface.
   */
  public void println() {
    System.out.println();
  }

  /**
   * Method required by the Control interface.
   *
   * @param s the string
   */
  public void print(String s) {
    System.out.print(s);
  }

  /**
   * Method required by the Control interface.
   */
  public void clearMessages() {}

  /**
   * Method required by the Control interface.
   *
   * @param s the string
   */
  public void calculationDone(String s) {}

  /**
   * Gets the property name.
   *
   * @return a name
   */
  public String getPropertyName() {
    XMLProperty parent = getParentProperty();
    // if no class name, return parent name
    if(className==null) {
      if(parent==null) {
        return "null";
      } else {
        return parent.getPropertyName();
      }
    }
    // else if array or collection item, return numbered class name
    else if(this.isArrayOrCollectionItem()) {
      if(this.name==null) {
        // add numbering
        XMLProperty root = this;
        while(root.getParentProperty()!=null) {
          root = root.getParentProperty();
        }
        if(root instanceof XMLControlElement) {
          XMLControlElement rootControl = (XMLControlElement) root;
          name = className.substring(className.lastIndexOf(".")+1);
          this.name = rootControl.addNumbering(name);
        }
      }
      return ""+this.name;
    }
    // else if this has a parent, return its name
    else if(parent!=null) {
      return parent.getPropertyName();
      // else return the short class name
    } else {
      return className.substring(className.lastIndexOf(".")+1);
    }
  }

  /**
   * Gets the property type.
   *
   * @return the type
   */
  public String getPropertyType() {
    return "object";
  }

  /**
   * Gets the property class.
   *
   * @return the class
   */
  public Class getPropertyClass() {
    return getObjectClass();
  }

  /**
   * Gets the immediate parent property, if any.
   *
   * @return the parent
   */
  public XMLProperty getParentProperty() {
    return parent;
  }

  /**
   * Gets the level of this property relative to the root.
   *
   * @return a non-negative integer
   */
  public int getLevel() {
    return level;
  }

  /**
   * Gets the property content of this control.
   *
   * @return a list of XMLProperties
   */
  public List getPropertyContent() {
    return props;
  }

  /**
   * Gets the named XMLControl child of this property. May return null.
   *
   * @param name the property name
   * @return the XMLControl
   */
  public XMLControl getChildControl(String name) {
    XMLControl[] children = getChildControls();
    for(int i = 0;i<children.length;i++) {
      if(children[i].getPropertyName().equals(name)) {
        return children[i];
      }
    }
    return null;
  }

  /**
   * Gets the XMLControl children of this property. The returned array has
   * length for type "object" = 1, "collection" and "array" = 0+, other
   * types = 0.
   *
   * @return an XMLControl array
   */
  public XMLControl[] getChildControls() {
    ArrayList list = new ArrayList();
    Iterator it = getPropertyContent().iterator();
    while(it.hasNext()) {
      XMLProperty prop = (XMLProperty) it.next();
      if(prop.getPropertyType().equals("object")) {
        list.add((XMLControl) prop.getPropertyContent().get(0));
      }
    }
    return(XMLControl[]) list.toArray(new XMLControl[0]);
  }

  /**
   * Gets the root control.
   *
   * @return the root control
   */
  public XMLControlElement getRootControl() {
    if(parent==null) {
      return this;
    }
    XMLProperty prop = parent;
    while(prop.getParentProperty()!=null) {
      prop = prop.getParentProperty();
    }
    if(prop instanceof XMLControlElement) {
      return(XMLControlElement) prop;
    }
    return null;
  }

  /**
   * Appends numbering to a specified name. Increments the number each time
   * this is called for the same name.
   *
   * @param name the name
   * @return the name with appended numbering
   */
  public String addNumbering(String name) {
    Integer count = (Integer) counts.get(name);
    if(count==null) {
      count = new Integer(0);
    }
    count = new Integer(count.intValue()+1);
    counts.put(name, count);
    return name+" "+count.toString();
  }

  /**
   * This does nothing since the property type is "object".
   *
   * @param stringValue the string value of a primitive or string property
   */
  public void setValue(String stringValue) {}

  /**
   * Returns the string xml representation.
   *
   * @return the string xml representation
   */
  public String toString() {
    StringBuffer xml = new StringBuffer("");
    // write the header if this is the top level
    if(getLevel()==0) {
      xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      if(isValid()) {
        xml.append(XML.NEW_LINE+"<!DOCTYPE object SYSTEM \""+doctype+"\">");
      }
    }
    // write the opening tag
    xml.append(XML.NEW_LINE+indent(getLevel())+"<object class=\""+className+"\"");
    // write the version if this is the top level
    if(version!=null&&getLevel()==0) {
      xml.append(" version=\""+version+"\"");
    }
    // write the content and closing tag
    List content = getPropertyContent();
    if(content.isEmpty()) {
      xml.append("/>");
    } else {
      xml.append(">");
      Iterator it = content.iterator();
      while(it.hasNext()) {
        xml.append(it.next().toString());
      }
      xml.append(XML.NEW_LINE+indent(getLevel())+"</object>");
    }
    return xml.toString();
  }

  // ____________________________ static methods _________________________________

  /**
   * Returns a list of objects of a specified class within this control.
   *
   * @param type the Class
   * @return the list of objects
   */
  public List getObjects(Class type) {
    return getObjects(type, false);
  }

  /**
   * Returns a list of objects of a specified class within this control.
   *
   * @param type the Class
   * @param useChooser true to allow user to choose
   * @return the list of objects
   */
  public List getObjects(Class type, boolean useChooser) {
    java.util.List props;
    if(useChooser) {
      String name = type.getName();
      name = name.substring(name.lastIndexOf(".")+1);
      // select objects using an xml tree chooser
      XMLTreeChooser chooser = new XMLTreeChooser("Select Objects", "Class "+name, null);
      props = chooser.choose(this, type);
    } else {
      // select all objects of desired type using an xml tree
      XMLTree tree = new XMLTree(this);
      tree.setHighlightedClass(type);
      tree.selectHighlightedProperties();
      props = tree.getSelectedProperties();
    }
    List objects = new ArrayList();
    Iterator it = props.iterator();
    while(it.hasNext()) {
      XMLControl prop = (XMLControl) it.next();
      objects.add(prop.loadObject(null));
    }
    return objects;
  }

  /**
   * Returns a copy of this control.
   *
   * @return a clone
   */
  public Object clone() {
    return new XMLControlElement(this);
  }

  // ____________________________ private methods _________________________________

  /**
   * Determines if this is (the child of) an array or collection item.
   *
   * @return true if this is an array or collection item
   */
  private boolean isArrayOrCollectionItem() {
    XMLProperty parent = getParentProperty();
    if(parent!=null) {
      parent = parent.getParentProperty();
      return(parent!=null&&"arraycollection".indexOf(parent.getPropertyType())>=0);
    }
    return false;
  }

  /**
   * Prepares this control for importing into the specified object.
   *
   * @param obj the importing object
   * @param importAll true to import all
   * @return <code>true</code> if the data is imported
   */
  private boolean importInto(Object obj, boolean importAll) {
    // get the list of importable properties
    XMLControl control = new XMLControlElement(obj);
    Collection list = control.getPropertyNames();
    list.retainAll(this.getPropertyNames());
    // choose the properties to import
    ListChooser chooser = new ListChooser("Import", "Select items to import");
    if(importAll||chooser.choose(list, list)) {
      // list now contains properties to keep
      Iterator it = getPropertyContent().iterator();
      while(it.hasNext()) {
        XMLProperty prop = (XMLProperty) it.next();
        if(!list.contains(prop.getPropertyName())) {
          it.remove();
          propNames.remove(prop.getPropertyName());
        }
      }
      // add object properties not in the list to this control
      it = control.getPropertyNames().iterator();
      while(it.hasNext()) {
        String name = (String) it.next();
        if(list.contains(name)) {
          continue;
        }
        String propType = control.getPropertyType(name);
        if(propType.equals("int")) {
          setValue(name, control.getInt(name));
        } else if(propType.equals("double")) {
          setValue(name, control.getDouble(name));
        } else if(propType.equals("boolean")) {
          setValue(name, control.getBoolean(name));
        } else if(propType.equals("string")) {
          setValue(name, control.getString(name));
        } else {
          setValue(name, control.getObject(name));
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Sets a property.
   *
   * @param name the name
   * @param type the type
   * @param value the value
   */
  private void setXMLProperty(String name, String type, Object value) {
    // remove any previous property with the same name
    int i = -1;
    if(propNames.contains(name)) {
      Iterator it = props.iterator();
      while(it.hasNext()) {
        i++;
        XMLProperty prop = (XMLProperty) it.next();
        if(prop.getPropertyName().equals(name)) {
          it.remove();
          break;
        }
      }
    } else {
      propNames.add(name);
    }
    if(i>-1) {
      props.add(i, new XMLPropertyElement(this, name, type, value));
    } else {
      props.add(new XMLPropertyElement(this, name, type, value));
    }
  }

  /**
   * Gets a named property. May return null.
   *
   * @param name the name
   * @return the XMLProperty
   */
  private XMLProperty getXMLProperty(String name) {
    if(name==null) {
      return null;
    }
    Iterator it = getPropertyContent().iterator();
    while(it.hasNext()) {
      XMLProperty prop = (XMLProperty) it.next();
      if(name.equals(prop.getPropertyName())) {
        return prop;
      }
    }
    return null;
  }

  /**
   * Reads this control from the current input.
   */
  private void readInput() {
    readFailed = false;
    try {
      // get document root opening tag line
      String openingTag = input.readLine();
      while(openingTag!=null&&openingTag.indexOf("<object")==-1) {
        openingTag = input.readLine();
      }
      // read this element from the root
      if(openingTag!=null) {
        // get version, if any
        String xml = openingTag;
        int i = xml.indexOf("version=");
        if(i!=-1) {
          xml = xml.substring(i+9);
          version = xml.substring(0, xml.indexOf("\""));
        }
        readObject(this, openingTag);
      } else {
        readFailed = true;
      }
    } catch(Exception ex) {
      readFailed = true;
      System.err.println("Failed to read xml: "+ex.getMessage());
    }
  }

  /**
   * Reads the current input into a control element.
   *
   * @param element the element to load
   * @param xml the xml opening tag line
   * @return the loaded element
   * @throws IOException
   */
  private XMLControlElement readObject(XMLControlElement element, String xml) throws IOException {
    element.clearValues();
    // set class name
    xml = xml.substring(xml.indexOf("class=")+7);
    String className = xml.substring(0, xml.indexOf("\""));
    // workaround for media package name change
    int i = className.lastIndexOf(".");
    if(i>-1) {
      String packageName = className.substring(0, i);
      if(packageName.endsWith("org.opensourcephysics.media")) {
        className = packageName+".core"+className.substring(i);
      }
    }
    element.className = className;
    // look for closing object tag on same line
    if(xml.indexOf("/>")!=-1) {
      input.readLine();
      return element;
    }
    // read and process input lines
    XMLProperty prop = (XMLProperty) element;
    xml = input.readLine();
    while(xml!=null) {
      // closing object tag
      if(xml.indexOf("</object>")!=-1) {
        input.readLine();
        return element;
      }
      // opening property tag
      else if(xml.indexOf("<property")!=-1) {
        XMLProperty child = readProperty(new XMLPropertyElement(prop), xml);
        element.props.add(child);
        element.propNames.add(child.getPropertyName());
      }
      xml = input.readLine();
    }
    return element;
  }

  /**
   * Reads the current input into a property element.
   *
   * @param prop the property element to load
   * @param xml the xml opening tag line
   * @return the loaded property element
   * @throws IOException
   */
  private XMLPropertyElement readProperty(XMLPropertyElement prop, String xml) throws IOException {
    // set property name
    prop.name = xml.substring(xml.indexOf("name=")+6, xml.indexOf("type=")-2);
    // set property type
    xml = xml.substring(xml.indexOf("type=")+6);
    prop.type = xml.substring(0, xml.indexOf("\""));
    // set property content and className
    if(prop.type.equals("array")||prop.type.equals("collection")) {
      xml = xml.substring(xml.indexOf("class=")+7);
      String className = xml.substring(0, xml.indexOf("\""));
      // workaround for media package name change
      int i = className.lastIndexOf(".");
      if(i>-1) {
        String packageName = className.substring(0, i);
        if(packageName.endsWith("org.opensourcephysics.media")) {
          className = packageName+".core"+className.substring(i);
        }
      }
      prop.className = className;
      if(xml.indexOf("/>")!=-1) { // property closing tag on same line
        return prop;
      }
      xml = input.readLine();
      while(xml.indexOf("<property")!=-1) {
        prop.content.add(readProperty(new XMLPropertyElement(prop), xml));
        xml = input.readLine();
      }
    } else if(prop.type.equals("object")) {
      XMLControlElement control = readObject(new XMLControlElement(prop), input.readLine());
      prop.content.add(control);
      prop.className = control.className;
    } else { // int, double, boolean or string types
      if(xml.indexOf(XML.CDATA_PRE)!=-1) {
        String s = xml.substring(xml.indexOf(XML.CDATA_PRE)+XML.CDATA_PRE.length());
        while(s.indexOf(XML.CDATA_POST)==-1) { // look for end tag
          s += XML.NEW_LINE+input.readLine();
        }
        xml = s.substring(0, s.indexOf(XML.CDATA_POST));
      } else {
        String s = xml.substring(xml.indexOf(">")+1);
        while(s.indexOf("</property>")==-1) {  // look for end tag
          s += XML.NEW_LINE+input.readLine();
        }
        xml = s.substring(0, s.indexOf("</property>"));
      }
      prop.content.add(xml);
    }
    return prop;
  }

  /**
   * Returns a space for indentation.
   *
   * @param level the indent level
   * @return the space
   */
  private String indent(int level) {
    String space = "";
    for(int i = 0;i<XML.INDENT*level;i++) {
      space += " ";
    }
    return space;
  }

  /**
   * Returns the object value of the specified property. May return null.
   *
   * @param prop the property
   * @return the array
   */
  private Object objectValue(XMLProperty prop) {
    if(!prop.getPropertyType().equals("object")) {
      return null;
    }
    XMLControl control = (XMLControl) prop.getPropertyContent().get(0);
    return control.loadObject(null);
  }

  /**
   * Returns the double value of the specified property.
   *
   * @param prop the property
   * @return the value
   */
  private double doubleValue(XMLProperty prop) {
    if(!prop.getPropertyType().equals("double")) {
      return Double.NaN;
    }
    return Double.parseDouble((String) prop.getPropertyContent().get(0));
  }

  /**
   * Returns the double value of the specified property.
   *
   * @param prop the property
   * @return the value
   */
  private int intValue(XMLProperty prop) {
    if(!prop.getPropertyType().equals("int")) {
      return Integer.MIN_VALUE;
    }
    return Integer.parseInt((String) prop.getPropertyContent().get(0));
  }

  /**
   * Returns the boolean value of the specified property.
   *
   * @param prop the property
   * @return the value
   */
  private boolean booleanValue(XMLProperty prop) {
    return prop.getPropertyContent().get(0).equals("true");
  }

  /**
   * Returns the string value of the specified property.
   *
   * @param prop the property
   * @return the value
   */
  private String stringValue(XMLProperty prop) {
    if(!prop.getPropertyType().equals("string")) {
      return null;
    }
    return(String) prop.getPropertyContent().get(0);
  }

  /**
   * Returns the array value of the specified property. May return null.
   *
   * @param prop the property
   * @return the array
   */
  private Object arrayValue(XMLProperty prop) {
    if(!prop.getPropertyType().equals("array")) {
      return null;
    }
    Class componentType = prop.getPropertyClass().getComponentType();
    List content = prop.getPropertyContent();
    // if no content, return a zero-length array
    if(content.isEmpty()) {
      return Array.newInstance(componentType, 0);
    }
    // determine the format from the first item
    XMLProperty first = (XMLProperty) content.get(0);
    if(first.getPropertyName().equals("array")) {
      // create the array from an array string
      Object obj = first.getPropertyContent().get(0);
      if(obj instanceof String) {
        return arrayValue((String) obj, componentType);
      }
      return null;
    }
    // create the array from a list of properties
    // determine the length of the array
    XMLProperty last = (XMLProperty) content.get(content.size()-1);
    String index = last.getPropertyName();
    int n = Integer.parseInt(index.substring(1, index.indexOf("]")));
    // create the array
    Object array = Array.newInstance(componentType, n+1);
    // populate the array
    Iterator it = content.iterator();
    while(it.hasNext()) {
      XMLProperty next = (XMLProperty) it.next();
      index = next.getPropertyName();
      n = Integer.parseInt(index.substring(1, index.indexOf("]")));
      String type = next.getPropertyType();
      if(type.equals("object")) {
        Array.set(array, n, objectValue(next));
      } else if(type.equals("int")) {
        Array.setInt(array, n, intValue(next));
      } else if(type.equals("double")) {
        Array.setDouble(array, n, doubleValue(next));
      } else if(type.equals("boolean")) {
        Array.setBoolean(array, n, booleanValue(next));
      } else if(type.equals("string")) {
        Array.set(array, n, stringValue(next));
      } else if(type.equals("array")) {
        Array.set(array, n, arrayValue(next));
      } else if(type.equals("collection")) {
        Array.set(array, n, collectionValue(next));
      }
    }
    return array;
  }

  /**
   * Returns the array value of the specified array string. May return null.
   * An array string must start and end with braces.
   *
   * @param arrayString the array string
   * @param componentType the component type of the array
   * @return the array
   */
  private Object arrayValue(String arrayString, Class componentType) {
    if(!(arrayString.startsWith("{")&&arrayString.endsWith("}"))) {
      return null;
    }
    // trim the outer braces
    String trimmed = arrayString.substring(1, arrayString.length()-1);
    if(componentType.isArray()) {
      // get opening and closing braces of enclosed array elements
      String opening = "";
      String closing = "";
      for(int n = 0;;n++) {
        if(trimmed.substring(n, n+1).equals("{")) {
          opening += "{";
          closing += "}";
        } else {
          break;
        }
      }
      // create and collect the array elements from substrings
      ArrayList list = new ArrayList();
      Class arrayType = componentType.getComponentType();
      int i = trimmed.indexOf(opening);
      int j = trimmed.indexOf(closing);
      while(j>0) {
        String nextArray = trimmed.substring(i, j+closing.length());
        Object obj = arrayValue(nextArray, arrayType);
        list.add(obj);
        trimmed = trimmed.substring(j+closing.length());
        i = trimmed.indexOf(opening);
        j = trimmed.indexOf(closing);
      }
      // create the array
      Object array = Array.newInstance(componentType, list.size());
      // populate the array
      Iterator it = list.iterator();
      int n = 0;
      while(it.hasNext()) {
        Object obj = it.next();
        Array.set(array, n++, obj);
      }
      return array;
    }
    // collect element substrings separated by commas
    ArrayList list = new ArrayList();
    while(!trimmed.equals("")) {
      int i = trimmed.indexOf(",");
      if(i>-1) {
        list.add(trimmed.substring(0, i));
        trimmed = trimmed.substring(i+1);
      } else {
        list.add(trimmed);
        break;
      }
    }
    // create the array
    Object array = Array.newInstance(componentType, list.size());
    // populate the array
    Iterator it = list.iterator();
    int n = 0;
    while(it.hasNext()) {
      if(componentType==Integer.TYPE) {
        int i = Integer.parseInt((String) it.next());
        Array.setInt(array, n++, i);
      } else if(componentType==Double.TYPE) {
        double x = Double.parseDouble((String) it.next());
        Array.setDouble(array, n++, x);
      } else if(componentType==Boolean.TYPE) {
        boolean bool = it.next().equals("true");
        Array.setBoolean(array, n++, bool);
      }
    }
    return array;
  }

  /**
   * Returns the collection value of the specified property. May return null.
   *
   * @param prop the property
   * @return the array
   */
  private Object collectionValue(XMLProperty prop) {
    if(!prop.getPropertyType().equals("collection")) {
      return null;
    }
    Class classType = prop.getPropertyClass();
    try {
      // create the collection
      Collection c = (Collection) classType.newInstance();
      List content = prop.getPropertyContent();
      // populate the array
      Iterator it = content.iterator();
      while(it.hasNext()) {
        XMLProperty next = (XMLProperty) it.next();
        String type = next.getPropertyType();
        if(type.equals("object")) {
          c.add(objectValue(next));
        } else if(type.equals("string")) {
          c.add(stringValue(next));
        } else if(type.equals("array")) {
          c.add(arrayValue(next));
        } else if(type.equals("collection")) {
          c.add(collectionValue(next));
        }
      }
      return c;
    } catch(IllegalAccessException ex) {}
    catch(InstantiationException ex) {}
    return null;
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
