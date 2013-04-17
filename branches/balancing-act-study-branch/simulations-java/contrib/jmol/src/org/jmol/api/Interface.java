/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006  The Jmol Development Team
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
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *  02110-1301, USA.
 */

package org.jmol.api;

import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;

public class Interface {

  public static Object getOptionInterface(String name) {
    return getInterface(JmolConstants.CLASSBASE_OPTIONS + name);
  }

  public static Object getApplicationInterface(String name) {
    return getInterface("org.openscience.jmol.app." + name);
  }

  public static Object getInterface(String name) {
    try {
      return Class.forName(name).newInstance();
    } catch (Exception e) {
      Logger.error("Interface.java Error creating instance for " + name + ": \n" + e.getMessage());
      return null;
    }
  }

}
