/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-10-15 17:34:01 -0500 (Sun, 15 Oct 2006) $
 * $Revision: 5957 $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development, www.jmol.org
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jmol.adapter.readers.more;

import java.util.ArrayList;
import java.util.List;

import org.jmol.adapter.readers.cifpdb.PdbReader;
import org.jmol.adapter.smarter.Atom;

/**
 * P2N file reader.
 *
 * This format is the input for Resp ESP Charge Derive server
 * http://q4md-forcefieldtools.org/
 *  
 */

public class P2nReader extends PdbReader {

  private List<String> altNames = new ArrayList<String>();
  
  @Override
  protected void setAdditionalAtomParameters(Atom atom) {
    String altName = line.substring(69, 72).trim();
    if (altName.length() == 0)
      altName = atom.atomName;
    if (useAltNames)
      atom.atomName = altName;
    else
      altNames.add(altName);
  }
  
  @Override
  protected void finalizeReader() throws Exception {
    super.finalizeReader();
    if (useAltNames)
      return;
    String[] names = new String[altNames.size()];
    for (int i = 0; i < names.length; i++)
      names[i] = altNames.get(i);
    atomSetCollection.setAtomSetAuxiliaryInfo("altName", names);
  }

}

