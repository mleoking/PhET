/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-05-11 13:47:18 -0700 (Tue, 11 May 2010) $
 * $Revision: 13064 $
 *
 * Copyright (C) 2005  Miguel, Jmol Development, www.jmol.org
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

package org.jmol.g3d;

import org.jmol.geodesic.Geodesic;

import javax.vecmath.Vector3f;
import javax.vecmath.Matrix3f;
import java.util.Random;
import java.util.BitSet;

import org.jmol.util.Logger;

/**
 * Provides quantization of normalized vectors so that shading for
 * lighting calculations can be handled by a simple index lookup
 *<p>
 * A 'normix' is a normal index, represented as a short
 *
 * @author Miguel, miguel@jmol.org
 */
class Normix3D {

  
  // these inital 
  final static int NORMIX_GEODESIC_LEVEL = Geodesic.standardLevel;

  private final static int normixCount = Geodesic.getVertexCount(NORMIX_GEODESIC_LEVEL);
  private final static Vector3f[] vertexVectors = Geodesic.getVertexVectors(); 
  private final static short[] inverseNormixes = new short[normixCount];
  
  static short getInverseNormix(short normix) {
    return inverseNormixes[normix];
  }

  private final static short[][] neighborVertexesArrays = Geodesic.getNeighborVertexesArrays();
  private final static boolean TIMINGS = false;

  public static final short NORMIX_NULL = 9999;
  
  //private final static boolean DEBUG_WITH_SEQUENTIAL_SEARCH = false;

  
  static {
    // level 0 1 2 3
    // vertices 12, 42, 162, 642
    BitSet bsTemp = new BitSet();
    for (int n = normixCount; --n >= 0;) {
      Vector3f v = vertexVectors[n];
      inverseNormixes[n] = getNormix(-v.x, -v.y, -v.z, NORMIX_GEODESIC_LEVEL,
          bsTemp);
    }

    if (TIMINGS) {
      Logger.info("begin timings!");
      for (int i = 0; i < normixCount; ++i) {
        short normix = getNormix(vertexVectors[i], bsTemp);
        Logger.info("draw normix" + i + " {" + vertexVectors[i].x + " "
            + vertexVectors[i].y + " " + vertexVectors[i].z + "} {0 0 0} \""
            + i + "\"");
        if (normix != i)
          if (Logger.debugging) {
            Logger.debug("" + i + " -> " + normix);
          }
      }
      Random rand = new Random();
      Vector3f vFoo = new Vector3f();
      Vector3f vBar = new Vector3f();
      Vector3f vSum = new Vector3f();

      int runCount = 100000;
      short[] neighborVertexes = neighborVertexesArrays[NORMIX_GEODESIC_LEVEL];
      Logger.startTimer();
      for (int i = 0; i < runCount; ++i) {
        short foo = (short) (rand.nextDouble() * normixCount);
        int offsetNeighbor;
        short bar;
        do {
          offsetNeighbor = foo * 6 + (int) (rand.nextDouble() * 6);
          bar = neighborVertexes[offsetNeighbor];
        } while (bar == -1);
        vFoo.set(vertexVectors[foo]);
        vFoo.scale(rand.nextFloat());
        vBar.set(vertexVectors[bar]);
        vBar.scale(rand.nextFloat());
        vSum.add(vFoo, vBar);
        vSum.normalize();
      }
      Logger.checkTimer("base runtime for " + runCount);
      Logger.startTimer();
      for (int i = 0; i < runCount; ++i) {
        short foo = (short) (rand.nextDouble() * normixCount);
        int offsetNeighbor;
        short bar;
        do {
          offsetNeighbor = foo * 6 + (int) (rand.nextDouble() * 6);
          bar = neighborVertexes[offsetNeighbor];
        } while (bar == -1);
        vFoo.set(vertexVectors[foo]);
        vFoo.scale(rand.nextFloat());
        vBar.set(vertexVectors[bar]);
        vBar.scale(rand.nextFloat());
        vSum.add(vFoo, vBar);
        short sum = getNormix(vSum, bsTemp);
        if (sum != foo && sum != bar) {
          /*
           * if (Logger.debugging) { Logger.debug( "foo:" + foo + " -> " +
           * vertexVectors[foo] + "\n" + "bar:" + bar + " -> " +
           * vertexVectors[bar] + "\n" + "sum:" + sum + " -> " +
           * vertexVectors[sum] + "\n" + "foo.dist="+dist2(vSum,
           * vertexVectors[foo])+"\n"+ "bar.dist="+dist2(vSum,
           * vertexVectors[bar])+"\n"+ "sum.dist="+dist2(vSum,
           * vertexVectors[sum])+"\n"+ "\nvSum:" + vSum + "\n"); }
           */
          throw new NullPointerException();
        }
        short sum2 = getNormix(vSum, bsTemp);
        if (sum != sum2) {
          Logger.debug("normalized not the same answer?");
          throw new NullPointerException();
        }
      }
      Logger.checkTimer("normix2 runtime for " + runCount);
    }
  }
  
  static short getNormix(Vector3f v, BitSet bsTemp) {
    return getNormix(v.x, v.y, v.z, NORMIX_GEODESIC_LEVEL, bsTemp);
  }

  static short get2SidedNormix(Vector3f v, BitSet bsTemp) {
    return (short)~getNormix(v.x, v.y, v.z, Normix3D.NORMIX_GEODESIC_LEVEL, bsTemp);
  }

  static Vector3f getVector(short normix) {
    return vertexVectors[normix];
  }
  
  static short getNormix(double x, double y, double z, int geodesicLevel, BitSet bsConsidered) {
    short champion;
    double t;
    if (z >= 0) {
      champion = 0;
      t = z - 1;
    } else {
      champion = 11;
      t = z - (-1);
    }
    bsConsidered.clear();
    bsConsidered.set(champion);
    double championDist2 = x*x + y*y + t*t;
    for (int lvl = 0; lvl <= geodesicLevel; ++lvl) {
      short[] neighborVertexes = neighborVertexesArrays[lvl];
      for (int offsetNeighbors = 6 * champion,
             i = offsetNeighbors + (champion < 12 ? 5 : 6);
           --i >= offsetNeighbors; ) {
        short challenger = neighborVertexes[i];
        if (bsConsidered.get(challenger))
            continue;
        bsConsidered.set(challenger);
        //Logger.debug("challenger=" + challenger);
        Vector3f v = vertexVectors[challenger];
        double d;
        // d = dist2(v, x, y, z);
        //Logger.debug("challenger d2=" + (d*d));
        d = v.x - x;
        double d2 = d * d;
        if (d2 >= championDist2)
          continue;
        d = v.y - y;
        d2 += d * d;
        if (d2 >= championDist2)
          continue;
        d = v.z - z;
        d2 += d * d;
        if (d2 >= championDist2)
          continue;
        champion = challenger;
        championDist2 = d2;
      }
    }

/*    
    if (DEBUG_WITH_SEQUENTIAL_SEARCH) {
      int champSeq = 0;
      double champSeqD2 = dist2(vertexVectors[champSeq], x, y, z);
      for (int k = Geodesic.getVertexCount(geodesicLevel); --k > 0; ) {
        double challengerD2 = dist2(vertexVectors[k], x, y, z);
        if (challengerD2 < champSeqD2) {
          champSeq = k;
          champSeqD2 = challengerD2;
        }
      }
      if (champion != champSeq) {
        if (champSeqD2 + .01 < championDist2) {
          Logger.debug("?que? getNormix is messed up?");
          boolean considered = bsConsidered.get(champSeq);
          if (Logger.debugging) {
            Logger.debug("Was the sequential winner considered? " + considered);
            Logger.debug(
                "champion " + champion + " @ " + championDist2 +
                " sequential champ " + champSeq + " @ " + champSeqD2 + "\n");
          }
          return (short)champSeq;
        }
      }
    }
*/
    return champion;
  }

  // only these three instance variables depend upon current orientation:
  
  private final Vector3f[] transformedVectors = new Vector3f[normixCount];
  {
    for (int i = normixCount; --i >= 0; )
      transformedVectors[i] = new Vector3f();
  }
  private final byte[] shadeIndexes = new byte[normixCount];
  private final byte[] shadeIndexes2Sided = new byte[normixCount];

  Vector3f[] getTransformedVectors() {
    return transformedVectors;
  }
  
  boolean isDirectedTowardsCamera(short normix) {
    // normix < 0 means a double sided normix, so always visible
    return (normix < 0) || (transformedVectors[normix].z > 0);
  }

  void setRotationMatrix(Matrix3f rotationMatrix) {
    for (int i = normixCount; --i >= 0; ) {
      Vector3f tv = transformedVectors[i];
      rotationMatrix.transform(vertexVectors[i], tv);
//        if (i == 0)
  //        System.out.println(i + " " + shadeIndexes[i]);

      shadeIndexes[i] = Shade3D.getShadeIndexNormalized(tv.x, -tv.y, tv.z);
    //  if (i == 0 || i == 219 || i == 162 || i == 193)
      //  System.out.println(i + " " + shadeIndexes[i]);
      shadeIndexes2Sided[i] = (tv.z >= 0 ? shadeIndexes[i] 
          : Shade3D.getShadeIndexNormalized(-tv.x, tv.y, -tv.z));
    }
  }

  private static byte nullShadeIndex = 50;
  
  int getShadeIndex(short normix) {
    return (normix == ~NORMIX_NULL
        || normix == NORMIX_NULL 
        ? nullShadeIndex
        : normix < 0 ? shadeIndexes2Sided[~normix] : shadeIndexes[normix]);
  }

  /*
   * unused in Jmol

  private static double dist2(Vector3f v1, Vector3f v2) {
    double dx = v1.x - v2.x;
    double dy = v1.y - v2.y;
    double dz = v1.z - v2.z;
    return dx*dx + dy*dy + dz*dz;
  }
    
  private static double dist2(Vector3f v1, double x, double y, double z) {
    double dx = v1.x - x;
    double dy = v1.y - y;
    double dz = v1.z - z;
    return dx*dx + dy*dy + dz*dz;
  }
    

  short[] getFaceNormixes(int level) {
    if (faceNormixesArrays == null)
      faceNormixesArrays = new short[NORMIX_GEODESIC_LEVEL + 1][];
    short[] faceNormixes = faceNormixesArrays[level];
    if (faceNormixes != null)
      return faceNormixes;
    return calcFaceNormixes(level);
  }

  private final static short[][] faceVertexesArrays = Geodesic.getFaceVertexesArrays();
  private static short[][] faceNormixesArrays;
  private final static boolean DEBUG_FACE_VECTORS = false;

  private synchronized short[] calcFaceNormixes(int level) {
    //Logger.debug("calcFaceNormixes(" + level + ")");
    short[] faceNormixes = faceNormixesArrays[level];
    if (faceNormixes != null)
      return faceNormixes;
    Vector3f t = new Vector3f();
    short[] faceVertexes = faceVertexesArrays[level];
    int j = faceVertexes.length;
    int faceCount = j / 3;
    faceNormixes = new short[faceCount];
    for (int i = faceCount; --i >= 0; ) {
      Vector3f vA = vertexVectors[faceVertexes[--j]];
      Vector3f vB = vertexVectors[faceVertexes[--j]];
      Vector3f vC = vertexVectors[faceVertexes[--j]];
      t.add(vA, vB);
      t.add(vC);
      short normix = getNormix(t);
      faceNormixes[i] = normix;
      if (DEBUG_FACE_VECTORS) {
        Vector3f vN = vertexVectors[normix];
        
        double d2At = dist2(t, vA);
        double d2Bt = dist2(t, vB);
        double d2Ct = dist2(t, vC);
        double d2Nt = dist2(t, vN);
        if (d2At < d2Nt ||
            d2Bt < d2Nt ||
            d2Ct < d2Nt) {
          if (Logger.debugging) {
            Logger.debug(
                " d2At =" + d2At +
                " d2Bt =" + d2Bt +
                " d2Ct =" + d2Ct +
                " d2Nt =" + d2Nt);
          }
        }
      }
 
      / *
      double champD = dist2(vertexVectors[normix], t);
      int champ = normix;
      for (int k = normixCount; --k >= 0; ) {
        double d = dist2(vertexVectors[k], t);
        if (d < champD) {
          champ = k;
          champD = d;
        }
      }
      if (champ != normix) {
        Logger.debug("normix " + normix + " @ " +
                           dist2(vertexVectors[normix], t) +
                           "\n" +
                           "champ " + champ + " @ " +
                           dist2(vertexVectors[champ], t) +
                           "\n");
      }
      * /
    }
    faceNormixesArrays[level] = faceNormixes;
    return faceNormixes;
  }
  
  short getVisibleNormix(double x, double y, double z,
                         int[] visibilityBitmap, int level) {
    int minMapped = Bmp.getMinMappedBit(visibilityBitmap);
    //Logger.debug("minMapped =" + minMapped);
    if (minMapped < 0)
      return -1;
    int maxMapped = Bmp.getMaxMappedBit(visibilityBitmap);
    int maxVisible = Geodesic.getVertexCount(level);
    int max = maxMapped < maxVisible ? maxMapped : maxVisible;
    Vector3f v;
    double d;
    double championDist2;
    int champion = minMapped;
    v = vertexVectors[champion];
    d = x - v.x;
    championDist2 = d * d;
    d = y - v.y;
    championDist2 += d * d;
    d = z - v.z;
    championDist2 += d * d;

    for (int challenger = minMapped + 1; challenger < max; ++challenger) {
      if (! Bmp.getBit(visibilityBitmap, challenger))
        continue;
      double challengerDist2 = dist2(vertexVectors[challenger],
                                     x, y, z);
      if (challengerDist2 < championDist2) {
        champion = challenger;
        championDist2 = challengerDist2;
      }
    }
    //Logger.debug("visible champion=" + champion);
    if (! Bmp.getBit(visibilityBitmap, champion))
      throw new IndexOutOfBoundsException();
    return (short)champion;
  }
  */
}
