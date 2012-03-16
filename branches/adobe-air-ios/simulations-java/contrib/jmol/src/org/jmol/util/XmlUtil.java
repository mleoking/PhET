/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006  The Jmol Development Team
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

package org.jmol.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jmol.script.Token;

public class XmlUtil {

  // / simple Xml parser/generator ///

  public static void openDocument(StringBuffer data) {
    data.append("<?xml version=\"1.0\"?>\n");
  }

  public static void openTag(StringBuffer sb, String name) {
    sb.append("<").append(name).append(">\n");
  }

  public static void openTag(StringBuffer sb, String name, Object[] attributes) {
    appendTag(sb, name, attributes, null, false, false);
    sb.append("\n");
  }

  public static void closeTag(StringBuffer sb, String name) {
    sb.append("</").append(name).append(">\n");
  }

  public static void appendTag(StringBuffer sb, String name,
                               Object[] attributes, Object data,
                               boolean isCdata, boolean doClose) {
    String closer = ">";
    if (name.endsWith("/")){
      name = name.substring(0, name.length() - 1);
      if (data == null) {
        closer = "/>\n";
        doClose = false;
      }
    }
    sb.append("<").append(name);
    if (attributes != null)
      for (int i = 0; i < attributes.length; i++) {
        Object o = attributes[i];
        if (o instanceof Object[])
          appendAttrib(sb, ((Object[]) o)[0], ((Object[]) o)[1]);
        else
          appendAttrib(sb, o, attributes[++i]);
      }
    sb.append(closer);
    if (data != null) {
      if (isCdata)
        data = wrapCdata(data);
      sb.append(data);
    }
    if (doClose)
      closeTag(sb, name);
  }

  /**
   * wrap the string as character data, with replacements for [ noted 
   * as a list starting with * after the CDATA termination
   * 
   * @param data
   * @return      wrapped text
   */
  public static String wrapCdata(Object data) {
    String s = "" + data;
    return (s.indexOf("&") < 0 && s.indexOf("<") < 0 ? (s.startsWith("\n") ? "" : "\n") + s 
        : "<![CDATA[" + TextFormat.simpleReplace(s, "]]>", "]]]]><![CDATA[>") + "]]>");
  }
  
  /**
   * @param s
   * @return   unwrapped text
   */
  public static String unwrapCdata(String s) {
    return (s.startsWith("<![CDATA[") && s.endsWith("]]>") ?
        s.substring(9, s.length()-3).replace("]]]]><![CDATA[>", "]]>") : s);
  }
  
  /**
   * standard <name attr="..." attr="...">data</name>"
   * 
   * @param sb
   * @param name
   * @param attributes
   * @param data
   */
  public static void appendTag(StringBuffer sb, String name,
                               Object[] attributes, Object data) {
    appendTag(sb, name, attributes, data, false, true);
  }

  /**
   * standard <name>data</name>"
   * standard <name attr="..." attr="..."></name>"
   * 
   * @param sb
   * @param name
   * @param data
   */
  public static void appendTag(StringBuffer sb, String name, Object data) {
    if (data instanceof Object[])
      appendTag(sb, name, (Object[]) data, null, false, true);
    else
      appendTag(sb, name, null, data, false, true);
  }

  /**
   * <name><![CDATA[data]]></name>"
   * 
   * will convert ]]> to ]] >
   * 
   * @param sb
   * @param name
   * @param attributes 
   * @param data
   */
  public static void appendCdata(StringBuffer sb, String name, 
                                 Object[] attributes, String data) {
    appendTag(sb, name, attributes, data, true, true);
  }

  /**
   * 
   * @param sb
   * @param name
   * @param value
   */
  public static void appendAttrib(StringBuffer sb, Object name, Object value) {
    if (value == null)
      return;
    
    // note: <&" are disallowed but not checked for here
    
    sb.append(" ").append(name).append("=\"").append(value).append("\"");
  }

  public static void toXml(StringBuffer sb, String name, List<Object[]> properties) {
    for (int i = 0; i < properties.size(); i++) {
      Object[] o = properties.get(i);
      appendTag(sb, name, (Object[]) o[0], o[1]);
    }
  }

  @SuppressWarnings("unchecked")
  public static Object escape(String name, List<Object[]> atts, Object value,
                              boolean asString, String indent) {

    StringBuffer sb;
    String type = (value == null ? null : value.getClass().getName());
    if (name == "token") {
      type = null;
      value = Token.nameOf(((Integer) value).intValue());
    } else if (type != null) {
      type = type.substring(0, type.lastIndexOf("[") + 1)
          + type.substring(type.lastIndexOf(".") + 1);
      if (value instanceof String) {
        value = wrapCdata(value);
      } else if (value instanceof BitSet) {
        value = Escape.escape(value);
      } else if (value instanceof List) {
        List<Object> v = (List<Object>) value;
        sb = new StringBuffer("\n");
        if (atts == null)
          atts = new ArrayList<Object[]>();
        atts.add(new Object[] { "count", Integer.valueOf(v.size()) });
        for (int i = 0; i < v.size(); i++)
          sb.append(
              escape(null, null, v.get(i), true, indent + "  "));
        value = sb.toString();
      } else if (value instanceof Map) {
        Map<String, Object> ht = (Map<String, Object>) value;
        sb = new StringBuffer("\n");
        Iterator<String> e = ht.keySet().iterator();
        int n = 0;
        while (e.hasNext()) {
          n++;
          String name2 = e.next();
          sb.append(
              escape(name2, null, ht.get(name2), true, indent + "  "));
        }
        if (atts == null)
          atts = new ArrayList<Object[]>();
        atts.add(new Object[] { "count", new Integer(n) });
        value = sb.toString();
      } else if (type.startsWith("[")) {
        if (value instanceof float[]) {
          float[] f = (float[]) value;
          sb = new StringBuffer("\n");
          if (atts == null)
            atts = new ArrayList<Object[]>();
          atts.add(new Object[] { "count", new Integer(f.length) });
          for (int i = 0; i < f.length; i++)
            sb.append(escape(null, null, new Float(f[i]), true, indent + "  "));
          value = sb.toString();
        } else if (value instanceof int[]) {
          int[] iv = (int[]) value;
          sb = new StringBuffer("\n");
          if (atts == null)
            atts = new ArrayList<Object[]>();
          atts.add(new Object[] { "count", new Integer(iv.length) });
          for (int i = 0; i < iv.length; i++)
            sb.append(escape(null, null, new Integer(iv[i]), true, indent + "  "));
          value = sb.toString();
          
        } else if (value instanceof Object[]) {
          Object[] o = (Object[]) value;
          sb = new StringBuffer("\n");
          if (atts == null)
            atts = new ArrayList<Object[]>();
          atts.add(new Object[] { "count", new Integer(o.length) });
          for (int i = 0; i < o.length; i++)
            sb.append(escape(null, null, o[i], true, indent + "  "));
          value = sb.toString();
          
        } else {
          // out of luck for now
        }
          
      }
    }
    List<Object[]> attributes = new ArrayList<Object[]>();
    attributes.add(new Object[] { "name", name });
    attributes.add(new Object[] { "type", type });
    if (atts != null)
      for (int i = 0; i < atts.size(); i++)
        attributes.add(atts.get(i));
    if (!asString)
      return new Object[] { attributes.toArray(), value };
    sb = new StringBuffer();
    sb.append(indent);
    appendTag(sb, "val", attributes.toArray(), null, false, false);
    sb.append(value);
    if (value instanceof String && ((String)value).indexOf("\n") >= 0)
      sb.append(indent);      
    closeTag(sb, "val");
    return sb.toString();
  }

}
