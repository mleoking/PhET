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
import java.util.*;
import java.net.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;

public class OyoahaTheme
{
  protected OyoahaThemeLoader loader;

  public OyoahaTheme(InputStream _inputStream)
  {
    try
    {
      loader = new ZOTMLoader(getClass().getClassLoader(), _inputStream);
    }
    catch(java.security.AccessControlException ex)
    {
      loader = new SafeZOTMLoader(getClass().getClassLoader(), _inputStream);
    }
  }

  public OyoahaTheme(URL _url)
  {
    try
    {
      loader = new ZOTMLoader(getClass().getClassLoader(), _url);
    }
    catch(java.security.AccessControlException ex)
    {
      loader = new SafeZOTMLoader(getClass().getClassLoader(), _url);
    }
  }

  public OyoahaTheme(File _file)
  {
    try
    {
      loader = new ZOTMLoader(getClass().getClassLoader(), _file);
    }
    catch(java.security.AccessControlException ex)
    {
      loader = new SafeZOTMLoader(getClass().getClassLoader(), _file);
    }
  }

  public String getClassName()
  {
    return loader.getProperty(OyoahaThemeLoader.CLASS_NAME);
  }

  public String getName()
  {
    return loader.getProperty(OyoahaThemeLoader.NAME);
  }

  public String getVersion()
  {
    return loader.getProperty(OyoahaThemeLoader.VERSION);
  }

  public String getCopyright()
  {
    return loader.getProperty(OyoahaThemeLoader.COPYRIGHT);
  }

  public OyoahaThemeSchemeChanged loadDefaultOyoahaThemeScheme(OyoahaThemeScheme _scheme)
  {
    return _scheme.load(loader, null);
  }

  public OyoahaThemeSchemeChanged loadDefaultOyoahaThemeScheme(OyoahaThemeScheme _scheme, MetalTheme _theme)
  {
    return _scheme.load(loader, _theme);
  }

  public void dispose()
  {
    loader.dispose();
    loader = null;
  }

  public InputStream getInputStream(String name)
  {
    try
    {
      return loader.getInputStream(name);
    }
    catch(Exception ex)
    {

    }

    return null;
  }

  public void installUIDefaults(OyoahaLookAndFeel _lnf, UIDefaults _defaults)
  {
      System.out.println( "loader = " + loader );
    installUIDefaults(_defaults, loader.loadTheme(_lnf, this));
  }

  protected void installUIDefaults(UIDefaults _defaults, Hashtable table)
  {
    if(table==null)
    {
      return;
    }

    //object -> UIResourceObject
    checkForUIResource(table);

    //install
    Enumeration e = table.keys();

    while(e.hasMoreElements())
    {
      String s = (String)e.nextElement();
      int pos = s.indexOf('*');

      if(pos>=0)
      {
        Enumeration d = _defaults.keys();

        if(pos==0)
        {
          String end = s.substring(1,s.length());

          while(d.hasMoreElements())
          {
            String tmp = (String)d.nextElement();

            if(tmp.endsWith(end))
            {
            	_defaults.put(tmp, table.get(s));
            }
          }
        }
        else
        {
          String start = s.substring(0,pos);
          String end = s.substring(pos+1,s.length());

          while(d.hasMoreElements())
          {
            String tmp = (String)d.nextElement();

            if(tmp.startsWith(start) && tmp.endsWith(end))
            {
              _defaults.put(s, table.get(s));
            }
          }
        }
      }
      else
      {
        _defaults.put(s, table.get(s));
      }
    }
  }

  /**
  * get the corresponding UIResource from an Object
  */
  protected void checkForUIResource(Hashtable table)
  {
    Enumeration e = table.keys();

    while(e.hasMoreElements())
    {
      Object s = e.nextElement();
      Object o = table.get(s);

      if(!(o instanceof UIResource))
      {
        if(o instanceof Color)
        {
          o = new ColorUIResource((Color)o);
        }
        else
        if(o instanceof Border)
        {
          o = new BorderUIResource((Border)o);
        }
        else
        if(o instanceof Font)
        {
          o = new FontUIResource((Font)o);
        }
        else
        if(o instanceof Icon)
        {
          o = new IconUIResource((Icon)o);
        }
        else
        if(o instanceof Dimension)
        {
          Dimension d = (Dimension)o;
          o = new DimensionUIResource(d.width, d.height);
        }
        else
        if(o instanceof Insets)
        {
          Insets i = (Insets)o;
          o = new InsetsUIResource(i.top, i.left, i.bottom, i.right);
        }
      }

      table.put(s, o);
    }
  }
}