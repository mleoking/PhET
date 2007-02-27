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

package com.oyoaha.swing.plaf.oyoaha.filechooser;

import java.io.*;
import java.awt.*;
import javax.swing.*;

import com.oyoaha.swing.plaf.oyoaha.ui.*;
import com.oyoaha.swing.plaf.oyoaha.icon.*;

public class OyoahaDirectoryComboBoxRenderer extends OyoahaListCellRenderer
{
  protected JFileChooser filechooser;
  protected OyoahaIndentIcon indentIcon = new OyoahaIndentIcon();
  protected FileRendererIcon fileRendererIcon = new FileRendererIcon(20);

  public OyoahaDirectoryComboBoxRenderer(JFileChooser filechooser)
  {
    this.filechooser = filechooser;
    setIcon(fileRendererIcon);
  }

  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
  {
    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

    File directory = (value instanceof OyoahaDirectoryComboBoxNode)? ((OyoahaDirectoryComboBoxNode)value).getFile() : (File)value;

    if(directory == null)
    {
      setText("");
      return this;
    }

    setText(filechooser.getName(directory));
    Icon icon = filechooser.getIcon(directory);

    indentIcon.setIcon(icon);
    
    if (value instanceof OyoahaDirectoryComboBoxNode)
    {
        indentIcon.setDepth(((OyoahaDirectoryComboBoxNode)value).getDepth());
    }
    
    fileRendererIcon.icon = indentIcon;
    setIcon(fileRendererIcon);

    return this;
  }
}