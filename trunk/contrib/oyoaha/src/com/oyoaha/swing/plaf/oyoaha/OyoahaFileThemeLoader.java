/* ====================================================================
 * Copyright (c) 2001-2003 OYOAHA. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The names "OYOAHA" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. 
 *    For written permission, please contact email@oyoaha.com.
 *
 * 3. Products derived from this software may not be called "OYOAHA",
 *    nor may "OYOAHA" appear in their name, without prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OYOAHA OR ITS CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oyoaha.swing.plaf.oyoaha;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.util.zip.*;
import java.lang.reflect.*;
import com.microstar.xml.*;

public class OyoahaFileThemeLoader extends ClassLoader implements OyoahaThemeLoader
{
  protected DebugOyoahaConsole debug;

  protected File file;
  protected ZipFile zip;
  protected ClassLoader parent;

  protected Properties idProperties;
  protected Properties schemeProperties;

  public OyoahaFileThemeLoader(DebugOyoahaConsole _debug, ClassLoader _parent, File _file)
  {
    debug = _debug;
    parent = _parent;
    file = _file;
  }

//------------------------------------------------------------------------------
// OYOAHATHEMELOADER
//------------------------------------------------------------------------------

  public Hashtable loadTheme(OyoahaLookAndFeel _lnf, OyoahaTheme _theme)
  {
    Properties table = new Properties();

    open();

    try
    {
      InputStream in = getInputStream("OyoahaTheme.properties");
      table.load(in);

      Reader reader = new InputStreamReader(getInputStream("OyoahaTheme.xml"));
      Hashtable t = readOyoahaTheme(reader, _lnf, _theme);

      in.close();
      reader.close();

      Enumeration e = table.keys();

      while(e.hasMoreElements())
      {
        Object s = e.nextElement();
        Object o = table.get(s);

        if(t.containsKey(o))
        {
          Object tmp = t.get(o);

          if(tmp instanceof NullTag)
          table.remove(s);
          else
          table.put(s, tmp);
        }
        /*else
        {
          //try the old slaf theme way...
          Object tmp = readProperty((String)s, (String)o);

          if(tmp==null)
          table.remove(s);
          else
          table.put(s, tmp);
        }*/
      }
    }
    catch(Exception ex)
    {
      /*if(debug!=null)
      {
        debug.println("ERROR loadTheme: " + ex.toString());
      }*/
    }

    idProperties = new Properties();

    try
    {
      idProperties.load(getInputStream("id.properties"));
    }
    catch (Exception ex)
    {
      /*if(debug!=null)
      {
        debug.println("ERROR load id properties: " + ex.toString());
      }*/
    }

    schemeProperties = new Properties();

    try
    {
      idProperties.load(getInputStream("scheme.properties"));
    }
    catch (Exception ex)
    {
      /*if(debug!=null)
      {
        debug.println("ERROR load scheme properties: " + ex.toString());
      }*/
    }

    close();

    return table;
  }

  public Properties loadDefaultOyoahaThemeScheme()
  {
    return schemeProperties;
  }

  public String getProperty(String key)
  {
    if(idProperties!=null)
    return idProperties.getProperty(key, "???");

    return "???";
  }

  public void dispose()
  {
    close();
  }

//------------------------------------------------------------------------------
// CLASSLOADER
//------------------------------------------------------------------------------

  /**
  *
  */
  protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException
  {
    Class c = findLoadedClass(name);

    if (c==null)
    {
      try
      {
        c = findSystemClass(name);
      }
      catch (ClassNotFoundException e1)
      {
        try
        {
          c = findClass(name);
        }
        catch (Exception e2)
        {
          if(parent!=null)
          {
            c = parent.loadClass(name);
          }
          else
          {
            c = super.loadClass(name);
          }
        }

        if (c==null)
        {
          throw e1;
        }
      }
    }

    if (resolve)
    {
      resolveClass(c);
    }

    return c;
  }

  /**
  * find class
  */
  protected Class findClass(String name) throws ClassNotFoundException
  {
    byte data[] = loadClassData(name);

    if(data == null)
    {
      throw new ClassNotFoundException();
    }

    Class result = defineClass(name, data, 0, data.length);

    if(result == null)
    {
      throw new ClassFormatError();
    }

    return result;
  }

  protected byte[] loadClassData(String _name)
  {
    String name = _name.replace('.', '/') + ".class";
    return loadBytes(name);
  }

  protected byte[] loadBytes(String _name)
  {
    try
    {
      InputStream in;

      if(file.isDirectory())
      {
        in = new FileInputStream(new File(file, _name));
      }
      else
      {
        ZipEntry entry = zip.getEntry(_name);

        if(entry!=null)
        {
          in = zip.getInputStream(entry);
        }
        else
        {
          return null;
        }
      }

      byte[] b = new byte[1024];
      byte[] bytes = new byte[0];

      while(true)
      {
        int i = in.read(b);

        if(i>0)
        {
          if(bytes.length==0)
          {
            bytes = new byte[i];
            System.arraycopy(b, 0, bytes, 0, i);
          }
          else
          {
            byte[] newbytes = new byte[bytes.length + i];
            System.arraycopy(bytes, 0, newbytes, 0, bytes.length);
            System.arraycopy(b, 0, newbytes, bytes.length, i);
            bytes = newbytes;
          }
        }
        else
        {
          break;
        }
      }

      return bytes;
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  /**
  * Get an InputStream on a given resource.  return null if no
  * resource with this name is found.
  */
  public InputStream getResourceAsStream(String _name)
  {
    try
    {
      return getInputStream(_name);
    }
    catch(Exception e)
    {
      if(parent!=null)
      return parent.getResourceAsStream(_name);
      else
      return super.getResourceAsStream(_name);
    }
  }

  /**
  * Find a resource with a given name.  The return is a URL to the resource.
  * Doing a getContent() on the URL may return an Image, an AudioClip,
  * or an InputStream.
  */
  public URL getResource(String _name)
  {
    if(_name!=null)
    {
      try
      {
        if(file.isDirectory())
        {
          StringBuffer buf = new StringBuffer();
          buf.append("file:");
          buf.append(file.getPath());
          buf.append(File.separatorChar);
          buf.append(_name);

          /*if(debug!=null)
          {
            debug.println("> getResource from file: " + buf.toString());
          }*/

          return new URL(buf.toString());
        }
        else
        {
          String path = file.getPath();

          StringBuffer buf = new StringBuffer();
          buf.append("jar:file:");
          buf.append(path);
          buf.append("!/");
          buf.append(_name);

          /*if(debug!=null)
          {
            debug.println("> getResource from jar: " + buf.toString());
          }*/

          return new URL(buf.toString());
        }
      }
      catch(Exception e)
      {

      }
    }

    return super.getResource(_name);
  }

  public InputStream getInputStream(String _name) throws IOException
  {
    if(file.isDirectory())
    {
      return new BufferedInputStream(new FileInputStream(new File(file, _name)));
    }
    else
    {
      try
      {
        return new BufferedInputStream(zip.getInputStream(zip.getEntry(_name)));
      }
      catch(Exception e)
      {
        return new BufferedInputStream(new FileInputStream(new File(file.getParent(), _name)));
      }
    }
  }

  protected void open()
  {
    if(file.isDirectory() || zip!=null)
    {
      return;
    }

    try
    {
      zip = new ZipFile(file);
    }
    catch(Exception e)
    {

    }
  }

  protected void close()
  {
    if(zip==null)
    {
      return;
    }

    try
    {
      zip.close();
      zip = null;
    }
    catch(Exception e)
    {

    }
  }

//------------------------------------------------------------------------------
// PROTECTED OYOAHA THEME LOADER FUNCTION
//------------------------------------------------------------------------------

  protected Hashtable readOyoahaTheme(Reader reader, OyoahaLookAndFeel lnf, OyoahaTheme theme) throws Exception
  {
    OyoahaThemeHandler handler = new OyoahaThemeHandler(this, lnf, theme);
    XmlParser parser = new XmlParser();
    parser.setHandler(handler);
    parser.parse(getClass().getResource("OyoahaTheme.dtd").toString(), null, reader);

    return handler.getHashtable();
  }

  protected Image readImage(String _string)
  {
System.out.println("readImage: " + _string);

    if(_string.endsWith(".png"))
    {
      //load and return a png image
      try
      {
        PNGImageProducer p = new PNGImageProducer(getInputStream(_string));
        Image image = Toolkit.getDefaultToolkit().createImage(p);
        OyoahaUtilities.loadImage(image);
        p.dispose();
        return image;       
      }
      catch(Exception e)
      {

      }
    }

    Icon icon = readIcon(_string);

    if(icon!=null)
    return ((ImageIcon)icon).getImage();

    return null;
  }

  protected Icon readIcon(String _string)
  {
    try
    {
      return new ImageIcon(getByteArray(_string));
    }
    catch(Exception e)
    {
      /*if(debug!=null)
      {
        debug.println("ERROR readIcon: " + e.toString());
      }*/
    }

    return null;
  }

  public byte[] getByteArray(String _string)
  {
    try
    {
      InputStream in = getInputStream(_string);
      ByteArrayOutputStream out = new ByteArrayOutputStream(4096);

      int c = in.read();

      while (c != -1)
      {
        out.write(c);
        c = in.read();
      }

      in.close();
      out.close();

      return out.toByteArray();
    }
    catch(Exception e)
    {
      /*if(debug!=null)
      {
        debug.println("ERROR return a byte array: " + e.toString());
      }*/
    }

    return null;
  }

  /**
  * create a object from a class name
  */
  public Object createObject(String _name)
  {
    try
    {
      Class c = loadClass(_name);
      return c.newInstance();
    }
    catch(Exception e)
    {
      /*if(debug!=null)
      {
        debug.println("ERROR create a object from a class name: " + e.toString());
      }*/
    }

    //print info about the correct way to built this object
    debug.println("<br><hr><font color=red>Maybe wrong constructor for: </font>" + _name);

    try
    {
      Class c = loadClass(_name);
      Constructor[] constructor = c.getConstructors();

      for(int i=0;i<constructor.length;i++)
      {
        debug.println("");
        debug.println("&lt;object type=\"Object\"&gt;");
        debug.println("&lt;name&gt;THE_OBJECT_NAME_HERE&lt;/name&gt;");
        debug.println("&lt;data&gt;" + _name + "</data&gt;");

        Class[] classes = constructor[i].getParameterTypes();

        for(int u=0;u<classes.length;u++)
        {
          if(classes[u].getName().equals("java.lang.Integer"))
          debug.println("&lt;para type=\"Integer\"&gt;");
          else
          if(classes[u].getName().equals("java.lang.Long"))
          debug.println("&lt;para type=\"Long\"&gt;");
          else
          if(classes[u].getName().equals("java.lang.Short"))
          debug.println("&lt;para type=\"Short\"&gt;");
          else
          if(classes[u].getName().equals("java.lang.Double"))
          debug.println("&lt;para type=\"Double\"&gt;");
          else
          if(classes[u].getName().equals("java.lang.Float"))
          debug.println("&lt;para type=\"Float\"&gt;");
          else
          if(classes[u].getName().equals("java.lang.Boolean"))
          debug.println("&lt;para type=\"Boolean\"&gt;");
          else
          if(classes[u].getName().equals("java.lang.String"))
          debug.println("&lt;para type=\"String\"&gt;");
          else
          if(classes[u].getName().equals("java.awt.Image"))
          debug.println("&lt;para type=\"Image\"&gt;");
          else
          if(classes[u].getName().equals("javax.swing.Icon"))
          debug.println("&lt;para type=\"Icon\"&gt;");
          else
          if(classes[u].getName().equals("java.awt.Font"))
          debug.println("&lt;para type=\"Font\"&gt;");
          else
          if(classes[u].getName().equals("java.awt.Color"))
          debug.println("&lt;para type=\"Color\"&gt;");
          else
          if(classes[u].getName().equals("java.awt.Insets"))
          debug.println("&lt;para type=\"Insets\"&gt;");
          else
          if(classes[u].getName().equals("java.awt.Dimension"))
          debug.println("&lt;para type=\"Dimension\"&gt;");
          else
          if(classes[u].getName().equals("java.awt.Rectangle"))
          debug.println("&lt;para type=\"Rectangle\"&gt;");
          else
          debug.println("&lt;para type=\"" + classes[u] + "\"&gt;");

          debug.println("&lt;data&gt;TODO&lt;/data&gt;");
          debug.println("&lt;/para&gt;");
        }

        debug.println("&lt;/object&gt;");
        debug.println("");
      }
    }
    catch(Exception e)
    {
      debug.println("<font color=red>ERROR unabled to print info about this class: </font>" + _name + " - " + e.toString());
      debug.println("");
    }

    return null;
  }

  /**
  * create a object from a class name
  */
  public Object createObject(String _name, Object[] object)
  {
    try
    {
      Class c = loadClass(_name);
      Constructor[] constructor = c.getConstructors();

      for(int i=0;i<constructor.length;i++)
      {
        try
        {
          return constructor[i].newInstance(object);
        }
        catch(Exception e)
        {
          /*if(debug!=null)
          {
            debug.println("ERROR create a object from a class name: " + e.toString());
          }*/
        }
      }
    }
    catch(Exception e)
    {
      /*if(debug!=null)
      {
        debug.println("ERROR create a object from a class name: " + e.toString());
      }*/
    }

    //print info about the correct way to built this object
    debug.println("<br><hr><font color=red>Maybe wrong constructor for: </font>" + _name);

    try
    {
      Class c = loadClass(_name);
      Constructor[] constructor = c.getConstructors();

      for(int i=0;i<constructor.length;i++)
      {
        debug.println("");
        debug.println("&lt;object type=\"Object\"&gt;");
        debug.println("&lt;name&gt;THE_OBJECT_NAME_HERE&lt;/name&gt;");
        debug.println("&lt;data&gt;" + _name + "</data&gt;");

        Class[] classes = constructor[i].getParameterTypes();

        for(int u=0;u<classes.length;u++)
        {
          if(classes[u].getName().equals("java.lang.Integer"))
          debug.println("&lt;para type=\"Integer\"&gt;");
          else
          if(classes[u].getName().equals("java.lang.Long"))
          debug.println("&lt;para type=\"Long\"&gt;");
          else
          if(classes[u].getName().equals("java.lang.Short"))
          debug.println("&lt;para type=\"Short\"&gt;");
          else
          if(classes[u].getName().equals("java.lang.Double"))
          debug.println("&lt;para type=\"Double\"&gt;");
          else
          if(classes[u].getName().equals("java.lang.Float"))
          debug.println("&lt;para type=\"Float\"&gt;");
          else
          if(classes[u].getName().equals("java.lang.Boolean"))
          debug.println("&lt;para type=\"Boolean\"&gt;");
          else
          if(classes[u].getName().equals("java.lang.String"))
          debug.println("&lt;para type=\"String\"&gt;");
          else
          if(classes[u].getName().equals("java.awt.Image"))
          debug.println("&lt;para type=\"Image\"&gt;");
          else
          if(classes[u].getName().equals("javax.swing.Icon"))
          debug.println("&lt;para type=\"Icon\"&gt;");
          else
          if(classes[u].getName().equals("java.awt.Font"))
          debug.println("&lt;para type=\"Font\"&gt;");
          else
          if(classes[u].getName().equals("java.awt.Color"))
          debug.println("&lt;para type=\"Color\"&gt;");
          else
          if(classes[u].getName().equals("java.awt.Insets"))
          debug.println("&lt;para type=\"Insets\"&gt;");
          else
          if(classes[u].getName().equals("java.awt.Dimension"))
          debug.println("&lt;para type=\"Dimension\"&gt;");
          else
          if(classes[u].getName().equals("java.awt.Rectangle"))
          debug.println("&lt;para type=\"Rectangle\"&gt;");
          else
          debug.println("&lt;para type=\"" + classes[u] + "\"&gt;");

          debug.println("&lt;data&gt;TODO&lt;/data&gt;");
          debug.println("&lt;/para&gt;");
        }

        debug.println("&lt;/object&gt;");
        debug.println("");
      }
    }
    catch(Exception e)
    {
      debug.println("<font color=red>ERROR unabled to print info about this class: </font>" + _name + " - " + e.toString());
      debug.println("");
    }

    return null;
  }

  /**
  *
  */
  /*public Object readProperty(String key, String data)
  {
    try
    {
      key = key.toLowerCase();

      if(key.startsWith("oyoaha") || key.startsWith("Oyoaha"))
      {
        return data;
      }
      else
      if(key.startsWith("sound") || key.startsWith("Sound"))
      {
        return data;
      }
      else
      if(key.endsWith("foreground") || key.endsWith("background") || key.endsWith("color") || key.endsWith("shadow") || key.endsWith("highlight") || key.endsWith("focus"))
      {
        return OyoahaThemeLoaderUtilities.readColor(data);
      }
      else
      if(key.endsWith("font"))
      {
        return OyoahaThemeLoaderUtilities.readFont(data);
      }
      else
      if(key.endsWith("icon"))
      {
        return readIcon(data);
      }
      else
      if(data!=null)
      {
        if(data.equalsIgnoreCase("true") || data.equalsIgnoreCase("false"))
        return new Boolean(data);

        return new Integer(data);
      }
    }
    catch(Exception e)
    {
      /*if(debug!=null)
      {
        debug.println("ERROR readProperty: " + e.toString());
      }*/
    /*}

    return null;
  }*/

//------------------------------------------------------------------------------
// INNER CLASS
//------------------------------------------------------------------------------

  public class OyoahaThemeHandler extends HandlerBase
  {
    protected String value;
    protected String type;
    protected Stack stack;
    protected Hashtable table;
    protected OyoahaThemeLoader loader;

    public OyoahaThemeHandler(OyoahaThemeLoader _loader, OyoahaLookAndFeel _lnf, OyoahaTheme _theme)
    {
      loader = _loader;

      table = new Hashtable();
      table.put("LOOKANDFEEL", _lnf);
      table.put("CLASSLOADER", _loader);
      table.put("THEME", _theme);
      table.put("SCHEME", _lnf.getOyoahaThemeScheme());

      /*if(debug!=null)
      {
        debug.println("create a OyoahaThemeHandler");
      }*/
    }

    public void attribute(String name, String value, boolean isSpecified)
    {
      name = name.toLowerCase();

      if(name.equals("type"))
      {
        type = value;
      }
    }

    public void doctypeDecl(String name, String publicId, String systemId) throws Exception
    {
      if (!name.equalsIgnoreCase("OYOAHATHEME"))
      {
        throw new Exception("Not a valid OyoahaTheme file !");
      }
    }

    public void charData(char[] c, int off, int len)
    {
      value = new String(c, off, len);
    }

    public void startElement(String name)
    {
      if (name.equalsIgnoreCase("object"))
      {
        ObjectLoader l = new ObjectLoader(this);
        l.setType(type);
        stack.push(l);
      }
      else
      if (name.equalsIgnoreCase("para"))
      {
        ObjectLoader l = new ObjectLoader(this);
        l.setType(type);
        stack.push(l);
      }
    }

    public void endElement(String name)
    {
      if (name.equalsIgnoreCase("object"))
      {
        ObjectLoader l = (ObjectLoader)stack.pop();
        Object o = l.getObject();

        debug.println("<BIG> RESULT: </BIG> <font color=blue>" + l.getName() + "</font> <font color=purple>" + o + "</font><hr>");
        debug.println("");

        if(o!=null)
        table.put(l.getName(), o);
        else
        table.put(l.getName(), new NullTag());
      }
      else
      if (name.equalsIgnoreCase("para"))
      {
        ObjectLoader l = (ObjectLoader)stack.pop();

        ObjectLoader l2 = (ObjectLoader)stack.peek();
        l2.addObject(l.getObject());
      }
      else
      if (name.equalsIgnoreCase("name"))
      {
        ObjectLoader l = (ObjectLoader)stack.peek();
        l.setName(value);
      }
      else
      if (name.equalsIgnoreCase("data"))
      {
        ObjectLoader l = (ObjectLoader)stack.peek();
        l.setData(value);
      }
    }

    public void startDocument()
    {
      stack = new Stack();
    }

    public Object getObject(String _key)
    {
      return table.get(_key);
    }

    public Hashtable getHashtable()
    {
      return table;
    }
  }

  public class ObjectLoader
  {
    protected String name;
    protected String data;
    protected String type;
    protected Vector objects;
    protected OyoahaThemeHandler gThemeHandler;

    public ObjectLoader(OyoahaThemeHandler _gThemeHandler)
    {
      gThemeHandler = _gThemeHandler;
      objects = new Vector();

      /*if(debug!=null)
      {
        debug.println("create a ObjectLoader");
      }*/
    }

    public void setName(String _name)
    {
      name = _name;
    }

    public String getName()
    {
      return name;
    }

    public void setData(String _data)
    {
      data = _data;
    }

    public void setType(String _type)
    {
      type = _type;
    }

    public void addObject(Object _object)
    {
      objects.addElement(_object);
    }

    public Object getObject()
    {
      Object[] o = new Object[objects.size()];
      objects.copyInto(o);

      return getObject(o);
    }

    protected final Object getObject(Object[] object)
    {
      if(type.equals("null"))
      debug.println("getObject type: <font color=blue>" + type + "</font>");
      else
      debug.println("getObject type: <font color=blue>" + type + "</font> data: <font color=purple>" + data + "</font>");

      if(type.equals("instance"))
      {
        return gThemeHandler.getObject(data);
      }
      else
      if(type.equals("null"))
      {
        return null;
      }
      else
      if(type.equals("Integer"))
      {
        return new Integer(data);
      }
      else
      if(type.equals("Byte"))
      {
        return new Byte(data);
      }
      else
      if(type.equals("Long"))
      {
        return new Long(data);
      }
      else
      if(type.equals("Short"))
      {
        return new Short(data);
      }
      else
      if(type.equals("Double"))
      {
        return new Double(data);
      }
      else
      if(type.equals("Float"))
      {
        return new Float(data);
      }
      else
      if(type.equals("Boolean"))
      {
        return new Boolean(data);
      }
      else
      if(type.equals("String"))
      {
        return new String(data);
      }
      else
      if(type.equals("Image"))
      {
        return readImage(data);
      }
      else
      if(type.equals("Icon"))
      {
        return readIcon(data);
      }
      else
      if(type.equals("Font"))
      {
        return OyoahaThemeLoaderUtilities.readFont(data);
      }
      else
      if(type.equals("Color"))
      {
        return OyoahaThemeLoaderUtilities.readColor(data);
      }
      else
      if(type.equals("Insets"))
      {
        return OyoahaThemeLoaderUtilities.readInsets(data);
      }
      else
      if(type.equals("Dimension"))
      {
        return OyoahaThemeLoaderUtilities.readDimension(data);
      }
      else
      if(type.equals("Point"))
      {
        return OyoahaThemeLoaderUtilities.readPoint(data);
      }
      else
      if(type.equals("Rectangle"))
      {
        return OyoahaThemeLoaderUtilities.readRectangle(data);
      }

      debug.println("createObject name: <font color=blue>" + name + "</font> data: <font color=purple>" + data + "</font>");

      return createObject(data, object);
    }
  }

  public class NullTag
  {

  }
}