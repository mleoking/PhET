/* $RCSfile$
 * $Author: egonw $
 * $Date: 2005-11-10 09:52:44 -0600 (Thu, 10 Nov 2005) $
 * $Revision: 4255 $
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

package org.jmol.util;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.viewer.JmolConstants;

/**
 * general-purpose simple unit cell for calculations 
 * and as a super-class of unitcell, which is only part of Symmetry
 * 
 * allows one-dimensional (polymer) and two-dimensional (slab) 
 * periodicity
 * 
 */

public class SimpleUnitCell {

  protected float[] notionalUnitcell; //6 parameters + optional 16 matrix items
  protected Matrix4f matrixCartesianToFractional;
  public Matrix4f matrixFractionalToCartesian;

  protected final static float toRadians = (float) Math.PI * 2 / 360;
  protected float a, b, c, alpha, beta, gamma;
  protected double cosAlpha, sinAlpha;
  protected double cosBeta, sinBeta;
  protected double cosGamma, sinGamma;
  protected double volume;
  protected double cA_, cB_;
  protected double a_;
  protected double b_, c_;
  protected int dimension;

  public static boolean isValid(float[] parameters) {
    return (parameters != null && (parameters[0] > 0 || parameters.length > 14
        && !Float.isNaN(parameters[14])));
  }

  public SimpleUnitCell(float[] parameters) {
    if (!isValid(parameters))
      return;
    notionalUnitcell = new float[parameters.length];
    System.arraycopy(parameters, 0, notionalUnitcell, 0, parameters.length);

    a = parameters[0];
    b = parameters[1];
    c = parameters[2];
    alpha = parameters[3];
    beta = parameters[4];
    gamma = parameters[5];

    if (a <= 0) {
      // must calculate a, b, c alpha beta gamma from vectors;
      Vector3f va = new Vector3f(parameters[6], parameters[7], parameters[8]);
      Vector3f vb = new Vector3f(parameters[9], parameters[10], parameters[11]);
      Vector3f vc = new Vector3f(parameters[12], parameters[13], parameters[14]);
      a = va.length();
      b = vb.length();
      c = vc.length();
      if (a == 0)
        return;
      if (b == 0)
        b = c = -1; //polymer
      else if (c == 0)
        c = -1; //slab
      alpha = (b < 0 || c < 0 ? 90 : vb.angle(vc) / toRadians);
      beta = (c < 0 ? 90 : va.angle(vc) / toRadians);
      gamma = (b < 0 ? 90 : va.angle(vb) / toRadians);
      if (c < 0) {
        float[] n = parameters.clone();
        if (b < 0) {
          vb.set(0, 0, 1);
          vb.cross(vb, va);
          if (vb.length() < 0.001f)
            vb.set(0, 1, 0);
          vb.normalize();
          n[9] = vb.x;
          n[10] = vb.y;
          n[11] = vb.z;
        }
        if (c < 0) {
          vc.cross(va, vb);
          vc.normalize();
          n[12] = vc.x;
          n[13] = vc.y;
          n[14] = vc.z;
        }
        parameters = n;
      }
    }
    if (b <= 0) {
      b = c = 1;
      dimension = 1;
    } else if (c <= 0) {
      c = 1;
      dimension = 2;
    } else {
      dimension = 3;
    }

    cosAlpha = Math.cos(toRadians * alpha);
    sinAlpha = Math.sin(toRadians * alpha);
    cosBeta = Math.cos(toRadians * beta);
    sinBeta = Math.sin(toRadians * beta);
    cosGamma = Math.cos(toRadians * gamma);
    sinGamma = Math.sin(toRadians * gamma);
    double unitVolume = Math.sqrt(sinAlpha * sinAlpha + sinBeta * sinBeta
        + sinGamma * sinGamma + 2.0 * cosAlpha * cosBeta * cosGamma - 2);
    volume = a * b * c * unitVolume;
    // these next few are for the B' calculation
    cA_ = (cosAlpha - cosBeta * cosGamma) / sinGamma;
    cB_ = unitVolume / sinGamma;
    a_ = b * c * sinAlpha / volume;
    b_ = a * c * sinBeta / volume;
    c_ = a * b * sinGamma / volume;

    if (parameters.length > 21 && !Float.isNaN(parameters[21])) {
      // parameters with a 3x4 matrix
      // [a b c alpha beta gamma m00 m01 m02 m03 m10 m11.... m20...]
      // this is for PDB and CIF reader
      float[] scaleMatrix = new float[16];
      for (int i = 0; i < 16; i++)
        scaleMatrix[i] = parameters[6 + i];
      matrixCartesianToFractional = new Matrix4f(scaleMatrix);
      matrixFractionalToCartesian = new Matrix4f();
      matrixFractionalToCartesian.invert(matrixCartesianToFractional);
    } else if (parameters.length > 14 && !Float.isNaN(parameters[14])) {
      // parameters with a 3 vectors
      // [a b c alpha beta gamma ax ay az bx by bz cx cy cz...]
      Matrix4f m = matrixFractionalToCartesian = new Matrix4f();
      m.setColumn(0, parameters[6], parameters[7], parameters[8], 0);
      m.setColumn(1, parameters[9], parameters[10], parameters[11], 0);
      m.setColumn(2, parameters[12], parameters[13], parameters[14], 0);
      m.setColumn(3, 0, 0, 0, 1);
      matrixCartesianToFractional = new Matrix4f();
      matrixCartesianToFractional.invert(matrixFractionalToCartesian);
    } else {
      Matrix4f m = matrixFractionalToCartesian = new Matrix4f();
      // 1. align the a axis with x axis
      m.setColumn(0, a, 0, 0, 0);
      // 2. place the b is in xy plane making a angle gamma with a
      m.setColumn(1, (float) (b * cosGamma), (float) (b * sinGamma), 0, 0);
      // 3. now the c axis,
      // http://server.ccl.net/cca/documents/molecular-modeling/node4.html
      m.setColumn(2, (float) (c * cosBeta), (float) (c
          * (cosAlpha - cosBeta * cosGamma) / sinGamma), (float) (volume / (a
          * b * sinGamma)), 0);
      m.setColumn(3, 0, 0, 0, 1);
      matrixCartesianToFractional = new Matrix4f();
      matrixCartesianToFractional.invert(matrixFractionalToCartesian);
    }
    matrixCtoFAbsolute = matrixCartesianToFractional;
    matrixFtoCAbsolute = matrixFractionalToCartesian;
  }

  protected Matrix4f matrixCtoFAbsolute;
  protected Matrix4f matrixFtoCAbsolute;

  public final void toCartesian(Point3f pt, boolean isAbsolute) {
    if (matrixFractionalToCartesian != null)
      (isAbsolute ? matrixFtoCAbsolute : matrixFractionalToCartesian)
          .transform(pt);
  }

  public final void toFractional(Point3f pt, boolean isAbsolute) {
    if (matrixCartesianToFractional == null)
      return;
    (isAbsolute ? matrixCtoFAbsolute : matrixCartesianToFractional)
        .transform(pt);
  }

  public boolean isPolymer() {
    return (dimension == 1);
  }

  public boolean isSlab() {
    return (dimension == 2);
  }

  public final float[] getNotionalUnitCell() {
    return notionalUnitcell;
  }

  public final float[] getUnitCellAsArray() {
    Matrix4f m = matrixFractionalToCartesian;
    return new float[] { 
        a, b, c, alpha, beta, gamma, 
        m.m00, m.m10, m.m20, // Va
        m.m01, m.m11, m.m21, // Vb
        m.m02, m.m12, m.m22, // Vc
        dimension, (float) volume,
        };
  }

  public final float getInfo(int infoType) {
    switch (infoType) {
    case JmolConstants.INFO_A:
      return a;
    case JmolConstants.INFO_B:
      return b;
    case JmolConstants.INFO_C:
      return c;
    case JmolConstants.INFO_ALPHA:
      return alpha;
    case JmolConstants.INFO_BETA:
      return beta;
    case JmolConstants.INFO_GAMMA:
      return gamma;
    case JmolConstants.INFO_DIMENSIONS:
      return dimension;
    }
    return Float.NaN;
  }

}
