/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-07-14 18:41:50 -0500 (Fri, 14 Jul 2006) $
 * $Revision: 5311 $
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

package org.jmol.adapter.readers.quantum;

/*
 * Wavefunction Odyssey reader -- old style -- just the INPUT section
 * 
 * - not an orbital reader, but extends SpartanInputReader, which is 
 *   the basis for other Spartan orbital readers, so we put it here. 
 * 
 */

public class OdysseyReader extends SpartanInputReader {
  
  @Override
  public void initializeReader() throws Exception {
    modelName = "Odyssey file";
    readInputRecords();
    continuing = false;
  }
  
}
