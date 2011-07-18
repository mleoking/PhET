/* $RCSfile$
 * $Author: nicove $
 * $Date: 2010-07-31 05:38:10 -0700 (Sat, 31 Jul 2010) $
 * $Revision: 13793 $
 *
 * Copyright (C) 2004-2005  The Jmol Development Team
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

package org.jmol.appletwrapper;

import org.jmol.util.Logger;

class ClassPreloader extends Thread {
    
  AppletWrapper appletWrapper;

  ClassPreloader(AppletWrapper appletWrapper) {
    this.appletWrapper = appletWrapper;
  }
    
  @Override
  public void run() {
    String className;
    setPriority(getPriority() - 1);
    while ((className = appletWrapper.getNextPreloadClassName()) != null) {
      try {
        int lastCharIndex = className.length() - 1;
        boolean constructOne = className.charAt(lastCharIndex) == '+';
        //System.out.println("ClassPreloader - " + className);
        if (constructOne)
          className = className.substring(0, lastCharIndex);
        Class<?> preloadClass = Class.forName(className);
        if (constructOne)
          preloadClass.newInstance();
      } catch (Exception e) {
        Logger.fatal("error preloading " + className, e);
      }
    }
  }
}
