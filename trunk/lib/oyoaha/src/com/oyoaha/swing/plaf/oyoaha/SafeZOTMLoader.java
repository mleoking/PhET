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
import java.lang.reflect.*;
import com.microstar.xml.*;

import com.oyoaha.jar2.*;

public class SafeZOTMLoader implements OyoahaThemeLoader
{
    protected String HEADER = "oyoaha theme 3.1";
    
  protected OyoahaJar2Reader reader;
  protected ClassLoader parent;

  protected Properties idProperties;
  protected Properties schemeProperties;

  public SafeZOTMLoader(ClassLoader parent, URL url)
  {
    this.parent = parent;
    reader = new OyoahaJar2Reader(new OyoahaJar2URLStreamWarper(url, true), HEADER);
  }

  public SafeZOTMLoader(ClassLoader parent, File file)
  {
    this.parent = parent;
    reader = new OyoahaJar2Reader(new OyoahaJar2FileStreamWarper(file, true), HEADER);
  }

  public SafeZOTMLoader(ClassLoader parent, InputStream inputStream)
  {
    this.parent = parent;
    reader = new OyoahaJar2Reader(new OyoahaJar2InputStreamWarper(inputStream), HEADER);
  }

//------------------------------------------------------------------------------
// OYOAHATHEMELOADER
//------------------------------------------------------------------------------

  public Hashtable loadTheme(OyoahaLookAndFeel _lnf, OyoahaTheme _theme)
  {
    Properties table = new Properties();

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
      }
    }
    catch(Exception ex)
    {

    }

    idProperties = new Properties();

    try
    {
      idProperties.load(getInputStream("id.properties"));
    }
    catch (Exception ex)
    {

    }

    schemeProperties = new Properties();

    try
    {
      idProperties.load(getInputStream("scheme.properties"));
    }
    catch (Exception ex)
    {

    }

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
    reader = null;
  }

//------------------------------------------------------------------------------
// CLASSLOADER
//------------------------------------------------------------------------------

  protected byte[] loadBytes(String _name)
  {
    return reader.getBytes(_name);
  }

  public InputStream getResourceAsStream(String _name)
  {
    try
    {
      return getInputStream(_name);
    }
    catch(Exception e1)
    {
      try
      {
        if(parent!=null)
        return parent.getResourceAsStream(_name);
        else
//        return getClass().getClassLoader().getResourceAsStream(_name);
        return OyoahaLookAndFeel.srrLoadResource( _name).openStream();
      }
      catch(Exception e2)
      {

      }
    }

    return null;
  }

  public URL getResource(String _name)
  {
    //TODO
    return null;
  }

  public InputStream getInputStream(String _name) throws IOException
  {
    InputStream inputStream = reader.getInputStream(_name);

    if(inputStream==null)
    throw new IOException();

    return inputStream;
  }

//------------------------------------------------------------------------------
// PROTECTED OYOAHA THEME LOADER FUNCTION
//------------------------------------------------------------------------------

  protected Hashtable readOyoahaTheme(Reader reader, OyoahaLookAndFeel lnf, OyoahaTheme theme) throws Exception
  {
    OyoahaThemeHandler handler = new OyoahaThemeHandler(this, lnf, theme);
    XmlParser parser = new XmlParser();
    parser.setHandler(handler);
//    parser.parse(getClass().getResource("OyoahaTheme.dtd").toString(), null, reader);
    parser.parse(OyoahaLookAndFeel.srrLoadResource("OyoahaTheme.dtd").toString(), null, reader);

    return handler.getHashtable();
  }

  protected Image readImage(String _string)
  {
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

    }

    return null;
  }

  public byte[] getByteArray(String _string)
  {
    return reader.getBytes(_string);
  }

  public Object createObject(String _name)
  {
    try
    {
      ClassLoader cl = getClass().getClassLoader();

      if(cl==null)
      {
        Class c = Class.forName(_name);
        return c.newInstance();
      }
      else
      {
        Class c = cl.loadClass(_name);

        if(c==null)
        c = Class.forName(_name);

        return c.newInstance();
      }
    }
    catch(Exception e)
    {

    }

    return null;
  }

  public Object createObject(String _name, Object[] object)
  {
    try
    {
      Class c = null;
      ClassLoader cl = getClass().getClassLoader();

      if(cl==null)
      {
        c = Class.forName(_name);
      }
      else
      {
        c = cl.loadClass(_name);

        if(c==null)
        c = Class.forName(_name);
      }

      if(c==null)
      {
        return null;
      }

      Constructor[] constructor = c.getConstructors();

      for(int i=0;i<constructor.length;i++)
      {
        try
        {
          return constructor[i].newInstance(object);
        }
        catch(Exception e)
        {

        }
      }
    }
    catch(Exception e)
    {

    }

    return null;
  }

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

    }

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

      return createObject(data, object);
    }
  }

  public class NullTag
  {

  }
}
