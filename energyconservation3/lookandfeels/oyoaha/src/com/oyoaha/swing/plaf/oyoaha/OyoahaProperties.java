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
import java.io.*;

public class OyoahaProperties extends Hashtable
{
  protected static final String keyValueSeparators = "=: \t\r\n\f";
  protected static final String strictKeyValueSeparators = "=:";
  protected static final String whiteSpaceChars = " \t\r\n\f";

  public OyoahaProperties()
  {

  }

  public void load(InputStream inStream, boolean inverseAndConvert)
  {
    try
    {
      BufferedReader in = new BufferedReader(new InputStreamReader(inStream));

      while (true)
      {
        String line = in.readLine();

        if (line == null)
        {
          return;
        }

        if (line.length() > 0)
        {
          char firstChar = line.charAt(0);

          if((firstChar != '#') && (firstChar != '!'))
          {
            StringTokenizer tok = new StringTokenizer(line, "=");

            String key = tok.nextToken();
            String value = tok.nextToken();

            if(inverseAndConvert)
            {
              put(new File(value), key);
            }
            else
            {
              put(key, value);
            }
          }
        }
      }
    }
    catch(Exception ex)
    {

    }
  }

  public void save(OutputStream out, String header, boolean inverseAndConvert)
  {
    try
    {
      BufferedWriter awriter = new BufferedWriter(new OutputStreamWriter(out));

      if (header != null)
      {
        awriter.write("#");
        awriter.write(header);
        awriter.newLine();
      }

      awriter.write("#");
      awriter.write(new Date().toString());
      awriter.newLine();

      for (Enumeration e = keys(); e.hasMoreElements();)
      {
        Object key = e.nextElement();
        Object val = get(key);

        String skey;
        String sval;

        if(key instanceof File)
        {
          skey = ((File)key).getPath();
        }
        else
        if(key instanceof String)
        {
          skey = (String)key;
        }
        else
        {
          continue;
        }

        if(val instanceof File)
        {
          sval = ((File)val).getPath();
        }
        else
        if(val instanceof OyoahaImageIcon)
        {
          sval = ((OyoahaImageIcon)val).getImageName();
        }
        else
        if(val instanceof Color)
        {
          Color c = (Color)val;
          sval = c.getRed() + "," + c.getBlue() + "," + c.getGreen();
        }
        else
        if(val instanceof Dimension)
        {
          Dimension d = (Dimension)val;
          sval = d.width + "," + d.height;
        }
        else
        if(val instanceof String)
        {
          sval = (String)val;
        }
        else
        {
          continue;
        }

        if(inverseAndConvert)
        {
          awriter.write(sval);
          awriter.write("=");
          awriter.write(skey);
          awriter.newLine();
        }
        else
        {
          awriter.write(skey);
          awriter.write("=");
          awriter.write(sval);
          awriter.newLine();
        }
      }

      awriter.flush();
    }
    catch (Exception ex)
    {

    }
  }
}