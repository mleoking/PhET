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
import java.util.*;
import javax.swing.plaf.*;

public class OyoahaThemeLoaderUtilities
{
  /**
  * parse this String return a Insets
  */
  public static Insets readInsets(String _string)
  {
    try
    {
      StringTokenizer st = new StringTokenizer(_string, ",");
      String[] string = new String[4];

      string[0] = st.nextToken();
      string[1] = st.nextToken();
      string[2] = st.nextToken();
      string[3] = st.nextToken();

      return new Insets(Integer.valueOf(string[0]).intValue(), Integer.valueOf(string[1]).intValue(), Integer.valueOf(string[2]).intValue(), Integer.valueOf(string[3]).intValue());
    }
    catch(Exception e)
    {

    }

    return null;
  }

  /**
  * parse this String return a Rectangle
  */
  public static Rectangle readRectangle(String _string)
  {
    try
    {
      StringTokenizer st = new StringTokenizer(_string, ",");
      String[] string = new String[4];

      string[0] = st.nextToken();
      string[1] = st.nextToken();
      string[2] = st.nextToken();
      string[3] = st.nextToken();

      return new Rectangle(Integer.valueOf(string[0]).intValue(), Integer.valueOf(string[1]).intValue(), Integer.valueOf(string[2]).intValue(), Integer.valueOf(string[3]).intValue());
    }
    catch(Exception e)
    {

    }

    return null;
  }

  /**
  * parse this String return a Dimension
  */
  public static Dimension readDimension(String _string)
  {
    try
    {
      StringTokenizer st = new StringTokenizer(_string, ",");
      String[] string = new String[2];

      string[0] = st.nextToken();
      string[1] = st.nextToken();

      return new Dimension(Integer.valueOf(string[0]).intValue(), Integer.valueOf(string[1]).intValue());
    }
    catch(Exception e)
    {

    }

    return null;
  }

  /**
  * parse this String return a Dimension
  */
  public static Point readPoint(String _string)
  {
    try
    {
      StringTokenizer st = new StringTokenizer(_string, ",");
      String[] string = new String[2];

      string[0] = st.nextToken();
      string[1] = st.nextToken();

      return new Point(Integer.valueOf(string[0]).intValue(), Integer.valueOf(string[1]).intValue());
    }
    catch(Exception e)
    {

    }

    return null;
  }

    /**
  * parse the string give a Color
  */
  public static ColorUIResource readColor(String s)
  {
    if(s.startsWith("#"))
    {
      s=s.substring(1);

      try
      {
        long i=Long.parseLong(s,16);
        return new ColorUIResource((int)i);
      }
      catch(Exception ex)
      {

      }
    }
    else
    {
      try
      {
        StringTokenizer st = new StringTokenizer(s, ",");

        int r = Integer.parseInt(st.nextToken());
        int g = Integer.parseInt(st.nextToken());
        int b = Integer.parseInt(st.nextToken());

        return new ColorUIResource(r, g, b);
      }
      catch (Exception e)
      {
        try
        {
          int i=Integer.parseInt(s,10);
          return new ColorUIResource(i);
        }
        catch(Exception ex)
        {

        }
      }
    }

    return null;
  }

  /**
  * parse this String give a Font
  */
  public static FontUIResource readFont(String s)
  {
    try
    {
      StringTokenizer st = new StringTokenizer(s, ",");

      String name = st.nextToken();
      String tmp = st.nextToken().toLowerCase();
      int size = Integer.parseInt(st.nextToken());

      int style;
      if(tmp.equals("plain"))
      {
      	style = Font.PLAIN;
      }
      else
      if(tmp.equals("bold"))
      {
      	style = Font.BOLD;
      }
      else
      if(tmp.equals("italic"))
      {
      	style = Font.ITALIC;
      }
      else
      if(tmp.equals("bolditalic"))
      {
      	style = Font.BOLD|Font.ITALIC;
      }
      else
      {
      	style = Integer.parseInt(tmp);
      }

      return new FontUIResource(name, style, size);
    }
    catch (Exception e)
    {
      return null;
    }
  }
}