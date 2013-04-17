/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-12-18 14:37:20 -0800 (Tue, 18 Dec 2007) $
 * $Revision: 8811 $
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

import java.awt.*;

import org.jmol.api.JmolAppletInterface;

public interface WrappedApplet extends JmolAppletInterface {
  public String getAppletInfo();
  public void setAppletWrapper(AppletWrapper appletWrapper);
  public void init();
  public void update(Graphics g);
  public void paint(Graphics g);
  public boolean handleEvent(Event e);
  public void destroy();
}
