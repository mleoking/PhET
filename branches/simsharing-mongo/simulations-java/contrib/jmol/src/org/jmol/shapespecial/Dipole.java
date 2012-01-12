/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-03-05 12:22:08 -0600 (Sun, 05 Mar 2006) $
 * $Revision: 4545 $
 *
 * Copyright (C) 2002-2005  The Jmol Development Team
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

package org.jmol.shapespecial;

import javax.vecmath.Vector3f;
import javax.vecmath.Point3f;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.modelset.Bond;
import org.jmol.util.Escape;

class Dipole {
  
  String thisID = "";
  short mad;
  short colix = 0;
  short type;

  Point3f origin;
  Point3f center;
  Vector3f vector;

  String dipoleInfo = "";
  float dipoleValue;
  
  boolean isUserValue;
  float offsetSide;
  float offsetAngstroms;
  int offsetPercent;
  int visibilityFlags;
  int modelIndex;

  boolean visible;
  boolean noCross;
  boolean haveAtoms;
  boolean isValid;

  Atom[] atoms = new Atom[2]; //for reference only
  Point3f[] coords = new Point3f[2]; //for reference only
  Bond bond;

  final static short DIPOLE_TYPE_UNKNOWN = 0;
  final static short DIPOLE_TYPE_POINTS = 1;
  final static short DIPOLE_TYPE_ATOMS = 2;
  final static short DIPOLE_TYPE_BOND = 3;
  final static short DIPOLE_TYPE_MOLECULAR = 4;
  final static short DIPOLE_TYPE_POINTVECTOR = 5;

  Dipole() {
  }

  Dipole(int modelIndex, String thisID, String dipoleInfo,
      short colix, short mad, boolean visible) {
    this.modelIndex = modelIndex;
    this.thisID = thisID;
    this.dipoleInfo = dipoleInfo;
    this.colix = colix;
    this.mad = mad;
    this.visible = visible;
    this.type = DIPOLE_TYPE_UNKNOWN;
  }

  void setTranslucent(boolean isTranslucent, float translucentLevel) {
    colix = Graphics3D.getColixTranslucent(colix, isTranslucent, translucentLevel);
  }

  void set(String thisID, String dipoleInfo, Atom[] atoms, float dipoleValue,
           short mad, float offsetAngstroms, int offsetPercent, float offsetSide, Point3f origin, Vector3f vector) {
    this.thisID = thisID;
    this.dipoleInfo = dipoleInfo;
    this.dipoleValue = dipoleValue;
    this.mad = mad;
    this.offsetAngstroms = offsetAngstroms;
    this.offsetPercent = offsetPercent;
    this.offsetSide = offsetSide;
    this.vector = new Vector3f(vector);
    this.origin = new Point3f(origin);
    this.haveAtoms = (atoms[0] != null);
    if (haveAtoms) {
      this.atoms[0] = atoms[0];
      this.atoms[1] = atoms[1];
      centerDipole();
    } else {
      center = null;
    }
  }

  private void set(Point3f pt1, Point3f pt2) {
    coords[0] = new Point3f(pt1);
    coords[1] = new Point3f(pt2);
    isValid = (coords[0].distance(coords[1]) > 0.1f);
    
    if (dipoleValue < 0) { 
      origin = new Point3f(pt2);
      vector = new Vector3f(pt1);
      dipoleValue = -dipoleValue;
    } else {
      origin = new Point3f(pt1);
      vector = new Vector3f(pt2);
    }
    dipoleInfo = "" + origin + vector;
    vector.sub(origin);
    if (dipoleValue == 0)
      dipoleValue = vector.length();
    else
      vector.scale(dipoleValue / vector.length());
    this.type = DIPOLE_TYPE_POINTS;
  }

  void set(float value) {
    float d = dipoleValue;
    dipoleValue = value;
    if (value == 0)
      isValid = false;
    if (vector == null)
      return;
    vector.scale(dipoleValue / vector.length());
    if (d * dipoleValue < 0)
      origin.sub(vector);
  }

  void set(Point3f pt1, Point3f pt2, float value) {
    dipoleValue = value;
    atoms[0] = null;
    set(pt1, pt2);
  }

  void set(Point3f pt1, Vector3f dipole) {
    set(dipole.length());
    Point3f pt2 = new Point3f(pt1);
    pt2.add(dipole);
    set(pt1, pt2);
    type = DIPOLE_TYPE_POINTVECTOR;
  }

  void set(Atom atom1, Atom atom2, float value) {
    //also from frame
    set(value);
    set(atom1, atom2);
    offsetSide = Dipoles.DEFAULT_OFFSETSIDE;
    mad = Dipoles.DEFAULT_MAD;
    atoms[0] = atom1;
    atoms[1] = atom2;
    haveAtoms = true;
    centerDipole();
  }

  void centerDipole() {
    isValid = (atoms[0] != atoms[1] && dipoleValue != 0);
    if (!isValid)
      return;
    float f = atoms[0].distance(atoms[1]) / (2 * dipoleValue)
        - 0.5f;
    origin.scaleAdd(f, vector, atoms[0]);
    center = new Point3f();
    center.scaleAdd(0.5f, vector, origin);
    bond = atoms[0].getBond(atoms[1]);
    type = (bond == null ? Dipole.DIPOLE_TYPE_ATOMS : Dipole.DIPOLE_TYPE_BOND);
  }
  
  boolean isBondType() {
    return (type == Dipole.DIPOLE_TYPE_ATOMS || type == Dipole.DIPOLE_TYPE_BOND);
  }
  
 public String getShapeState() {
    if (!isValid)
      return "";
    StringBuffer s = new StringBuffer();
    s.append("dipole ID ").append(thisID);
    if (haveAtoms)
      s.append(" ({").append(atoms[0].getIndex()).append(" ").
                      append(atoms[1].getIndex()).append("})");
    else if (coords[0] == null)
      return "";
    else
      s.append(" ").append(Escape.escape(coords[0])).
        append(" ").append(Escape.escape(coords[1]));
    if (isUserValue)
      s.append(" value ").append(dipoleValue);
    if (mad != Dipoles.DEFAULT_MAD)
      s.append(" width ").append(mad / 1000f);
    if (offsetAngstroms != 0)
      s.append(" offset ").append(offsetAngstroms);
    else if (offsetPercent != 0)
      s.append(" offset ").append(offsetPercent);
    if (offsetSide != Dipoles.DEFAULT_OFFSETSIDE)
      s.append(" offsetSide ").append(offsetSide);
    if (noCross)
      s.append(" nocross");
    if (!visible)
      s.append(" off");
    s.append(";\n");
    return s.toString();
  }
}
