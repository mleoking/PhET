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

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

public class OyoahaMetalTheme extends DefaultMetalTheme
{
  protected String name = "Oyoaha Custom Theme";

  protected ColorUIResource primary1;
  protected ColorUIResource primary2;
  protected ColorUIResource primary3;

  protected ColorUIResource secondary1;
  protected ColorUIResource secondary2;
  protected ColorUIResource secondary3;

  protected ColorUIResource black;
  protected ColorUIResource white;

  protected FontUIResource controlFont;
  protected FontUIResource systemFont;
  protected FontUIResource userFont;
  protected FontUIResource smallFont;

  public OyoahaMetalTheme(File file)
  {
    initColors();

    try
    {
      loadProperties(new FileInputStream(file));
    }
    catch(Exception e)
    {

    }
  }

  public OyoahaMetalTheme(URL url)
  {
    initColors();

    try
    {
      loadProperties(url.openStream());
    }
    catch(Exception e)
    {

    }
  }

  public OyoahaMetalTheme(InputStream input)
  {
    initColors();

    try
    {
      loadProperties(input);
    }
    catch(Exception e)
    {

    }
  }

  protected void initColors()
  {
    primary1 = super.getPrimary1();
    primary2 = super.getPrimary2();
    primary3 = super.getPrimary3();

    secondary1 = super.getSecondary1();
    secondary2 = super.getSecondary2();
    secondary3 = super.getSecondary3();

    black = super.getBlack();
    white = super.getWhite();
  }

  protected void loadProperties(InputStream stream)
  {
    Properties prop = new Properties();

    try
    {
      prop.load(stream);
    }
    catch(IOException e)
    {

    }

    Object tempName = prop.get("name");
    if (tempName != null)
    {
      name = tempName.toString();
    }

    Object colorString = null;

    colorString = prop.get("primary1");
    if (colorString != null)
    {
      primary1 = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
    }

    colorString = prop.get("primary2");
    if (colorString != null)
    {
        primary2 = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
    }

    colorString = prop.get("primary3");
    if (colorString != null)
    {
        primary3 = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
    }

    colorString = prop.get("secondary1");
    if (colorString != null)
    {
        secondary1 = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
    }

    colorString = prop.get("secondary2");
    if (colorString != null)
    {
        secondary2 = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
    }

    colorString = prop.get("secondary3");
    if (colorString != null)
    {
        secondary3 = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
    }

    colorString = prop.get("black");
    if (colorString != null)
    {
        black = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
    }

    colorString = prop.get("white");
    if (colorString != null)
    {
      white = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
    }

    colorString = prop.get("controlFont");
    if (colorString!=null)
    {
      controlFont = OyoahaThemeLoaderUtilities.readFont(colorString.toString());
    }

    colorString = prop.get("systemFont");
    if (colorString!=null)
    {
      systemFont = OyoahaThemeLoaderUtilities.readFont(colorString.toString());
    }

    colorString = prop.get("userFont");
    if (colorString!=null)
    {
      userFont = OyoahaThemeLoaderUtilities.readFont(colorString.toString());
    }

    colorString = prop.get("smallFont");
    if (colorString!=null)
    {
      smallFont = OyoahaThemeLoaderUtilities.readFont(colorString.toString());
    }
  }

  public String getName()
  {
    return name;
  }

  public FontUIResource getControlTextFont()
  {
    return (controlFont!=null) ? controlFont : super.getControlTextFont();
  }

  public FontUIResource getSystemTextFont()
  {
    return (systemFont!=null) ? systemFont : super.getSystemTextFont();
  }

  public FontUIResource getUserTextFont()
  {
    return (userFont!=null) ? userFont : super.getUserTextFont();
  }

  public FontUIResource getMenuTextFont()
  {
    return (controlFont!=null) ? controlFont : super.getControlTextFont();
  }

  public FontUIResource getWindowTitleFont()
  {
    return (controlFont!=null) ? controlFont : super.getControlTextFont();
  }

  public FontUIResource getSubTextFont()
  {
    return (smallFont!=null) ? smallFont : super.getSubTextFont();
  }

  protected ColorUIResource getPrimary1()
  {
    return primary1;
  }

  protected ColorUIResource getPrimary2()
  {
    return primary2;
  }

  protected ColorUIResource getPrimary3()
  {
    return primary3;
  }

  protected ColorUIResource getSecondary1()
  {
    return secondary1;
  }

  protected ColorUIResource getSecondary2()
  {
    return secondary2;
  }

  protected ColorUIResource getSecondary3()
  {
    return secondary3;
  }

  protected ColorUIResource getBlack()
  {
    return black;
  }

  protected ColorUIResource getWhite()
  {
    return white;
  }
}