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
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.oyoaha.swing.plaf.oyoaha.ui.*;

public class OyoahaDirectoryComboBoxModel extends AbstractListModel implements ComboBoxModel
{
  protected Vector directories = new Vector(30);
  protected int topIndex = -1;
  protected int pathCount = 0;

  protected JFileChooser filechooser;
  protected OyoahaDirectoryComboBoxNode selectedDirectory = null;

  public OyoahaDirectoryComboBoxModel(JFileChooser filechooser)
  {
    super();
    this.filechooser = filechooser;
    
    OyoahaFileView view = ((OyoahaFileChooserUI)getFileChooser().getUI()).getOyoahaFileView(getFileChooser());
    view.getFileSystem(this);
  }

  public void add(OyoahaDirectoryComboBoxNode node)
  {
    directories.addElement(node);
  }

  public JFileChooser getFileChooser()
  {
    return filechooser;
  }

  public void addItem(File directory)
  {
    if(directory==null)
    {
      return;
    }
    
    directories.removeAllElements();
    
    ComponentUI c = getFileChooser().getUI();
    
    OyoahaFileView view = ((OyoahaFileChooserUI)getFileChooser().getUI()).getOyoahaFileView(getFileChooser());
    view.getFileSystem(this);

    File canonical = null;

    try
    {
      canonical = getFileChooser().getFileSystemView().createFileObject(directory.getCanonicalPath());
    }
    catch (IOException e)
    {
      return;
    }

    File f = canonical;
    
    if(indexOf(f)>=0)
    {
      topIndex = indexOf(f);
      setSelectedItem(canonical);
      return;
    }

    Vector path = new Vector(10);

    while(f.getParent() != null)
    {
      int index = indexOf(f);

      if(index>0)
      {
        topIndex = index;
        break;
      }

      path.addElement(f);
      f = getFileChooser().getFileSystemView().createFileObject(f.getParent());
    }

    pathCount = path.size();

    if(topIndex<0)
    {
      int index = indexOf(f);

      if(index>=0)
      {
        topIndex = index;
      }
      else
      {
        topIndex = directories.size();
        directories.addElement(new OyoahaDirectoryComboBoxNode(f, 0));
      }
    }

    int depth = ((OyoahaDirectoryComboBoxNode)directories.elementAt(topIndex)).getDepth();

    for(int i=0;i<path.size();i++)
    {
      File tmp = (File)path.elementAt(i);
      directories.insertElementAt(new OyoahaDirectoryComboBoxNode(tmp, depth+path.size()-i), topIndex+1);
    }

    setSelectedItem(canonical);
  }

  public void setSelectedItem(Object selectedDirectory)
  {
    if(selectedDirectory instanceof File)
    this.selectedDirectory = new OyoahaDirectoryComboBoxNode((File)selectedDirectory, 0);
    else
    this.selectedDirectory = (OyoahaDirectoryComboBoxNode)selectedDirectory;

    fireContentsChanged(this, -1, -1);
  }

  public Object getSelectedItem()
  {
    return selectedDirectory;
  }

  public int getSize()
  {
    return directories.size();
  }

  public Object getElementAt(int index)
  {
    return directories.elementAt(index);
  }

  protected int indexOf(File file)
  {
    for(int i=0;i<directories.size();i++)
    {
      OyoahaDirectoryComboBoxNode node = (OyoahaDirectoryComboBoxNode)directories.elementAt(i);

      if(file.equals(node.getFile()))
      return i;
    }

    return -1;
  }
}