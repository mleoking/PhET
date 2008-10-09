//
// Copyright (C) 1998-2000 Geometry Technologies, Inc. <info@geomtech.com>
// Copyright (C) 2002-2004 DaerMar Communications, LLC <jgv@daermar.com>
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package geom.util;

import java.awt.Color;
import java.util.Hashtable;

public class ColorTable {
  public static Hashtable table = new Hashtable();

  static {
    table.put("black", Color.black);
    table.put("blue", Color.blue);
    table.put("cyan", Color.cyan);
    table.put("darkGray", Color.darkGray);
    table.put("gray", Color.gray);
    table.put("green", Color.green);
    table.put("lightGray", Color.lightGray);
    table.put("magenta", Color.magenta);
    table.put("orange", Color.orange);
    table.put("pink", Color.pink);
    table.put("red", Color.red);
    table.put("white", Color.white);
    table.put("yellow", Color.yellow);
  }

  public static Color getColor(String c) {
    return (Color)table.get(c);
  }
}
