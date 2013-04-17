/* $RCSfile$
 * $Author: egonw $
 * $Date: 2005-11-10 07:52:44 -0800 (Thu, 10 Nov 2005) $
 * $Revision: 4255 $
 *
 * Copyright (C) 2005 Jmol Development
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
/*
 * listen to atom selections in a Jmol Viewer
 * @author DanaP
 */
package org.jmol.api;

import java.util.BitSet;

/**
 * listen to atom selections in a Jmol Viewer
 */
public interface JmolSelectionListener {
  
  /**
   * Called when the selected atoms change
   * @param selection bit set giving selection of atom indexes
   */
  public void selectionChanged(BitSet selection);
}
