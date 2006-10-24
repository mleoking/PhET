/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This defines methods for storing data in an xml property element.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class XMLPropertyElement implements XMLProperty {
  // instance fields
  protected XMLProperty parent;
  protected String name;
  protected String type;
  protected String className;
  protected List content = new ArrayList();

  /**
   * Constructs an empty property element.
   *
   * @param mother the parent
   */
  public XMLPropertyElement(XMLProperty mother) {
    parent = mother;
  }

  /**
   * Constructs a property element with the specified value.
   *
   * @param mother the parent
   * @param propertyName the name
   * @param propertyType the type
   * @param value the value
   */
  public XMLPropertyElement(XMLProperty mother, String propertyName, String propertyType, Object value) {
    this(mother);
    name = propertyName;
    type = propertyType;
    if(type.equals("string")) {
      if(XML.requiresCDATA((String) value)) {
        content.add(XML.CDATA_PRE+value+XML.CDATA_POST);
      } else {
        content.add(value.toString());
      }
    } else if("intdoubleboolean".indexOf(type)!=-1) {
      content.add(value.toString());
    } else if(type.equals("object")) {
      className = value.getClass().getName();
      XMLControl control = new XMLControlElement(this);
      control.saveObject(value);
      content.add(control);
    } else if(type.equals("collection")) {
      className = value.getClass().getName();
      Iterator it = ((Collection) value).iterator();
      int i = 0;
      while(it.hasNext()) {
        Object next = it.next();
        String type = XML.getDataType(next);
        if(type==null) {
          continue;
        }
        content.add(new XMLPropertyElement(this, "item", type, next));
        i++;
      }
    } else if(type.equals("array")) {
      className = value.getClass().getName();
      // determine if base component type is primitive and count array elements
      Class baseType = value.getClass().getComponentType();
      Object array = value;
      int count = Array.getLength(array);
      while(count>0&&baseType.getComponentType()!=null) {
        baseType = baseType.getComponentType();
        array = Array.get(array, 0);
        if(array==null) {
          break;
        }
        count = count*Array.getLength(array);
      }
      boolean primitive = "intdoubleboolean".indexOf(baseType.getName())!=-1;
      if(primitive&&count>XMLControlElement.compactArraySize) {
        // write array as string if base type is primitive
        String s = getArrayString(value);
        content.add(new XMLPropertyElement(this, "array", "string", s));
      } else {
        int length = Array.getLength(value);
        for(int j = 0;j<length;j++) {
          Object next = Array.get(value, j);
          String type = XML.getDataType(next);
          if(type==null) {
            continue;
          }
          content.add(new XMLPropertyElement(this, "["+j+"]", type, next));
        }
      }
    }
  }

  /**
   * Gets the property name.
   *
   * @return a name
   */
  public String getPropertyName() {
    return name;
  }

  /**
   * Gets the property type.
   *
   * @return the type
   */
  public String getPropertyType() {
    return type;
  }

  /**
   * Gets the property class.
   *
   * @return the class
   */
  public Class getPropertyClass() {
    if(type.equals("int")) {
      return Integer.TYPE;
    } else if(type.equals("double")) {
      return Double.TYPE;
    } else if(type.equals("boolean")) {
      return Boolean.TYPE;
    } else if(type.equals("string")) {
      return String.class;
    }
    try {
      return Class.forName(className);
    } catch(ClassNotFoundException ex) {
      return null;
    }
  }

  /**
   * Gets the immediate parent property.
   *
   * @return the type
   */
  public XMLProperty getParentProperty() {
    return parent;
  }

  /**
   * Gets the level of this property relative to root.
   *
   * @return the non-negative integer level
   */
  public int getLevel() {
    return parent.getLevel()+1;
  }

  /**
   * Gets the xml content for this property. Content items may be strings,
   * XMLControls or XMLProperties.
   *
   * @return a list of content items
   */
  public List getPropertyContent() {
    return content;
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
    if(type.equals("object")) {
      XMLControl child = (XMLControl) getPropertyContent().get(0);
      return new XMLControl[] {child};
    } else if("arraycollection".indexOf(type)!=-1) {
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
    return new XMLControl[0];
  }

  /**
   * Sets the value of this property if property type is primitive or string.
   * This does nothing for other property types.
   *
   * @param stringValue the string value of a primitive or string property
   */
  public void setValue(String stringValue) {
    boolean valid = true;
    try {
      if(type.equals("int")) {
        Integer.parseInt(stringValue);
      } else if(type.equals("double")) {
        Double.parseDouble(stringValue);
      } else if(type.equals("boolean")) {
        stringValue = stringValue.equals("true") ? "true" : "false";
      } else if("objectarraycollection".indexOf(type)!=-1) {
        valid = false;
      }
    } catch(NumberFormatException ex) {
      valid = false;
    }
    if(valid) {
      content.clear();
      content.add(stringValue);
    }
  }

  /**
   * Returns the xml string representation of this property.
   *
   * @return the xml string
   */
  public String toString() {
    // write the opening tag with attributes
    StringBuffer xml = new StringBuffer(XML.NEW_LINE+indent(getLevel())+"<property name=\""+name+"\" type=\""+type+"\"");
    if("arraycollection".indexOf(type)!=-1) {
      xml.append(" class=\""+className+"\"");
    }
    // write the content
    List content = getPropertyContent();
    // if no content, write closing tag and return
    if(content.isEmpty()) {
      xml.append("/>");
      return xml.toString();
    }
    // else write content
    xml.append(">");
    boolean hasChildren = false;
    Iterator it = content.iterator();
    while(it.hasNext()) {
      Object next = it.next();
      hasChildren = hasChildren||(next instanceof XMLProperty);
      xml.append(next);
    }
    // write the closing tag
    if(hasChildren) {
      xml.append(XML.NEW_LINE+indent(getLevel()));
    }
    xml.append("</property>");
    return xml.toString();
  }

  // /**
  // * Returns the xml string representation of this property.
  // *
  // * @return the xml string
  // */
  // public String toString() {
  // // write the opening tag with attributes
  // String xml = XML.NEW_LINE + indent(getLevel())
  // + "<property name=\"" + name + "\" type=\"" + type + "\"";
  // if ("arraycollection".indexOf(type) != -1) {
  // xml += " class=\"" + className + "\"";
  // }
  // // write the content
  // List content = getPropertyContent();
  // // if no content, write closing tag and return
  // if (content.isEmpty()) {
  // xml += "/>";
  // return xml;
  // }
  // // else write content
  // xml += ">";
  // boolean hasChildren = false;
  // Iterator it = content.iterator();
  // while (it.hasNext()) {
  // Object next = it.next();
  // hasChildren = hasChildren || (next instanceof XMLProperty);
  // xml += next;
  // }
  // // write the closing tag
  // if (hasChildren) xml += XML.NEW_LINE + indent(getLevel());
  // xml += "</property>";
  // return xml;
  // }
  //

  /**
   * Returns a space for indentation.
   *
   * @param level the indent level
   * @return the space
   */
  protected String indent(int level) {
    String space = "";
    for(int i = 0;i<XML.INDENT*level;i++) {
      space += " ";
    }
    return space;
  }

  /**
   * Returns a string representation of a primitive array.
   *
   * @param array the array
   * @return the array string
   */
  protected String getArrayString(Object array) {
    StringBuffer sb = new StringBuffer("{");
    int length = Array.getLength(array);
    for(int j = 0;j<length;j++) {
      // add separator except for first element
      if(j>0) {
        sb.append(','); // s += ",";
      }
      Object element = Array.get(array, j);
      if(element!=null&&element.getClass().isArray()) {
        sb.append(getArrayString(element));
      } else {
        sb.append(element);
      }
    }
    sb.append('}');
    return sb.toString();
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
