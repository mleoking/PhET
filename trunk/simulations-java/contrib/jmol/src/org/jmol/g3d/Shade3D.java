/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-04-15 05:50:10 -0700 (Thu, 15 Apr 2010) $
 * $Revision: 12864 $
 *
 * Copyright (C) 2003-2005  The Jmol Development Team
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


/**
 *<p>
 * All static functions.
 * Implements the shading of RGB values to support shadow and lighting
 * highlights.
 *</p>
 *<p>
 * Each RGB value has 64 shades. shade[0] represents ambient lighting.
 * shade[63] is white ... a full specular highlight.
 *</p>
 *
 * @author Miguel, miguel@jmol.org
 */
final class Shade3D {

  // there are 64 shades of a given color
  // 0 = ambient
  // 52 = normal
  // 56 = max for 
  // 63 = specular
  static final int shadeIndexMax = 64;
  static final int shadeIndexLast = shadeIndexMax - 1;
  static final byte shadeIndexNormal = 52;
  final static byte shadeIndexNoisyLimit = 56;

  public static int zPower = 1; // the power for the zShading -- higher number --> more depth of fog

  // the viewer vector is always {0 0 1}

  // the light source vector -- up and to the left
  private static final float xLightsource = -1;
  private static final float yLightsource = -1;
  private static final float zLightsource = 2.5f;
  
  private static final float magnitudeLight =
    (float)Math.sqrt(xLightsource * xLightsource +
                     yLightsource * yLightsource +
                     zLightsource * zLightsource);
  // the light source vector normalized
  static final float xLight = xLightsource / magnitudeLight;
  static final float yLight = yLightsource / magnitudeLight;
  static final float zLight = zLightsource / magnitudeLight;
  
  static boolean specularOn = true; 
  static boolean usePhongExponent = false;
  
  //fractional distance from black for ambient color
  static int ambientPercent = 45;
  
  // df in I = df * (N dot L) + sf * (R dot V)^p
  static int diffusePercent = 84;

  // log_2(p) in I = df * (N dot L) + sf * (R dot V)^p
  // for faster calculation of shades
  static int specularExponent = 6;

  // sf in I = df * (N dot L) + sf * (R dot V)^p
  // not a percent of anything, really
  static int specularPercent = 22;
  
  // fractional distance to white for specular dot
  static int specularPower = 40;

  // p in I = df * (N dot L) + sf * (R dot V)^p
  static int phongExponent = 64;
  
  static float ambientFraction = ambientPercent / 100f;
  static float diffuseFactor = diffusePercent / 100f;
  static float intenseFraction = specularPower / 100f;
  static float specularFactor = specularPercent / 100f;
  
  /*
   * intensity calculation:
   * 
   * af ambientFraction (from ambient percent)
   * if intenseFraction (from specular power)
   * 
   * given a color rr gg bb, consider one of these components x:
   * 
   * int[0:63] shades   [0 .......... 52(normal) ........ 63]
   *                     af*x........ x ..............x+(255-x)*if
   *              black  <---ambient%--x---specular power---->  white
   */
  
  static int[] getShades(int rgb, boolean greyScale) {
    int[] shades = new int[shadeIndexMax];
    if (rgb == 0)
      return shades;
    

    float red0 = ((rgb >> 16) & 0xFF);
    float grn0 = ((rgb >>  8) & 0xFF);
    float blu0 = (rgb         & 0xFF);
    
    float red = 0;
    float grn = 0;
    float blu = 0;
    
    float f = ambientFraction;

    while (true) {
      red = red0 * f + 0.5f;
      grn = grn0 * f + 0.5f;
      blu = blu0 * f + 0.5f;
      if (f > 0 && red < 4 && grn < 4 && blu < 4) {
        // with antialiasing, black shades with all 
        // components less than 4 will be considered 0
        // so we must adjust things just a bit.
        red0++;
        grn0++;
        blu0++;
        if (f < 0.1f)
          f += 0.1f;
        rgb = rgb((int) red0, (int) grn0, (int) blu0);
        continue;
      }
      break;
    }
    f = (1 - f) / shadeIndexNormal;

    float redStep = red0 * f;
    float grnStep = grn0 * f;
    float bluStep = blu0 * f;

    int i;
    for (i = 0; i < shadeIndexNormal; ++i) {
      shades[i] = rgb((int) red, (int) grn, (int) blu);
      red += redStep;
      grn += grnStep;
      blu += bluStep;
    }

    shades[i++] = rgb;    

    f = intenseFraction / (shadeIndexMax - i);
    redStep = (255.5f - red) * f;
    grnStep = (255.5f - grn) * f;
    bluStep = (255.5f - blu) * f;

    for (; i < shadeIndexMax;i++) {
      red += redStep;
      grn += grnStep;
      blu += bluStep;
      shades[i] = rgb((int) red, (int) grn, (int) blu);
    }
    
    if (greyScale)
      for (; --i >= 0;)
        shades[i] = Graphics3D.calcGreyscaleRgbFromRgb(shades[i]);
    return shades;
  }

  final static int rgb(int red, int grn, int blu) {
    return 0xFF000000 | (red << 16) | (grn << 8) | blu;
  }

  static int getShadeIndex(float x, float y, float z) {
    // from Cylinder3D.calcArgbEndcap and renderCone
    // from Graphics3D.getShadeIndex and getShadeIndex
    double magnitude = Math.sqrt(x*x + y*y + z*z);
    return (int) (getFloatShadeIndexNormalized((float)(x/magnitude),
                                               (float)(y/magnitude),
                                               (float)(z/magnitude))
                  * shadeIndexLast + 0.5f);
  }

  static byte getShadeIndexNormalized(float x, float y, float z) {
    //from Normix3D.setRotationMatrix
    return (byte)(int) (getFloatShadeIndexNormalized(x, y, z)
                  * shadeIndexLast + 0.5f);
  }

  static int getFp8ShadeIndex(float x, float y, float z) {
    //from calcDitheredNoisyShadeIndex (not utilized)
    //and Cylinder.calcRotatedPoint
    double magnitude = Math.sqrt(x*x + y*y + z*z);
    return (int)(getFloatShadeIndexNormalized((float)(x/magnitude),
                                              (float)(y/magnitude),
                                              (float)(z/magnitude))
                 * shadeIndexLast * (1 << 8));
  }

  private static float getFloatShadeIndexNormalized(float x, float y, float z) {
    float NdotL = x * xLight + y * yLight + z * zLight;
    if (NdotL <= 0)
      return 0;
    // I = k_diffuse * f_diffuse + k_specular * f_specular
    // where
    // k_diffuse = (N dot L)
    // k_specular = {[(2(N dot L)N - L] dot V}^p
    //
    // and in our case V = {0 0 1} so the z component of that is:
    // 
    // k_specular = ( 2 * NdotL * z - zLight )^p
    // 
    // HOWEVER -- Jmol's "specularExponent is log_2(phongExponent)
    //
    // "specularExponent" phong_exponent
    // 0 1
    // 1 2
    // 2 4
    // 3 8
    // 4 16
    // 5 32
    // 5.322 40
    // 6 64
    // 7 128
    // 8 256
    // 9 512
    // 10 1024
    float intensity = NdotL * diffuseFactor;
    if (specularOn) {
      float k_specular = 2 * NdotL * z - zLight;
      if (k_specular > 0) {
        if (usePhongExponent) {
          k_specular = (float) Math.pow(k_specular, phongExponent);
        } else {
          for (int n = specularExponent; --n >= 0
              && k_specular > .0001f;)
            k_specular *= k_specular;
        }
        intensity += k_specular * specularFactor;
      }
    }
    if (intensity > 1)
      return 1;
    return intensity;
  }

  /*
   static byte getDitheredShadeIndex(float x, float y, float z) {
   //not utilized
   // add some randomness to prevent banding
   int fp8ShadeIndex = getFp8ShadeIndex(x, y, z);
   int shadeIndex = fp8ShadeIndex >> 8;
   // this cannot overflow because the if the float shadeIndex is 1.0
   // then shadeIndex will be == shadeLast
   // but there will be no fractional component, so the next test will fail
   if ((fp8ShadeIndex & 0xFF) > nextRandom8Bit())
   ++shadeIndex;
   int random16bit = seed & 0xFFFF;
   if (random16bit < 65536 / 3 && shadeIndex > 0)
   --shadeIndex;
   else if (random16bit > 65536 * 2 / 3 && shadeIndex < shadeLast)
   ++shadeIndex;
   return (byte)shadeIndex;
   }
   */

  static byte getDitheredNoisyShadeIndex(float x, float y, float z, float r) {
    // from Sphere3D only
    // add some randomness to prevent banding
    int fp8ShadeIndex = (int) (getFloatShadeIndexNormalized(x / r, y / r, z / r)
        * shadeIndexLast * (1 << 8));
    int shadeIndex = fp8ShadeIndex >> 8;
    // this cannot overflow because the if the float shadeIndex is 1.0
    // then shadeIndex will be == shadeLast
    // but there will be no fractional component, so the next test will fail
    if ((fp8ShadeIndex & 0xFF) > nextRandom8Bit())
      ++shadeIndex;
    int random16bit = seed & 0xFFFF;
    if (random16bit < 65536 / 3 && shadeIndex > 0)
      --shadeIndex;
    else if (random16bit > 65536 * 2 / 3 && shadeIndex < shadeIndexLast)
      ++shadeIndex;
    return (byte) shadeIndex;
  }

  /*
    This is a linear congruential pseudorandom number generator,
    as defined by D. H. Lehmer and described by Donald E. Knuth in
    The Art of Computer Programming,
    Volume 2: Seminumerical Algorithms, section 3.2.1.

  static long seed = 1;
  static int nextRandom8Bit() {
    seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
    //    return (int)(seed >>> (48 - bits));
    return (int)(seed >>> 40);
  }
  */

  
  ////////////////////////////////////////////////////////////////
  // Sphere shading cache for Large spheres
  ////////////////////////////////////////////////////////////////

  static boolean sphereShadingCalculated = false;
  final static byte[] sphereShadeIndexes = new byte[256 * 256];

  synchronized static void calcSphereShading() {
    //if (!sphereShadingCalculated) { //unnecessary -- but be careful!
    float xF = -127.5f;
    for (int i = 0; i < 256; ++xF, ++i) {
      float yF = -127.5f;
      for (int j = 0; j < 256; ++yF, ++j) {
        byte shadeIndex = 0;
        float z2 = 130 * 130 - xF * xF - yF * yF;
        if (z2 > 0) {
          float z = (float) Math.sqrt(z2);
          shadeIndex = getDitheredNoisyShadeIndex(xF, yF, z, 130);
        }
        sphereShadeIndexes[(j << 8) + i] = shadeIndex;
      }
    }
    sphereShadingCalculated = true;
  }
  
  /*
  static byte getSphereshadeIndex(int x, int y, int r) {
    int d = 2*r + 1;
    x += r;
    if (x < 0)
      x = 0;
    int x8 = (x << 8) / d;
    if (x8 > 0xFF)
      x8 = 0xFF;
    y += r;
    if (y < 0)
      y = 0;
    int y8 = (y << 8) / d;
    if (y8 > 0xFF)
      y8 = 0xFF;
    return sphereShadeIndexes[(y8 << 8) + x8];
  }
  */
    
  // this doesn't really need to be synchronized
  // no serious harm done if two threads write seed at the same time
  private static int seed = 0x12345679; // turn lo bit on
  /**
   *<p>
   * Implements RANDU algorithm for random noise in lighting/shading.
   *</p>
   *<p>
   * RANDU is the classic example of a poor random number generator.
   * But it is very cheap to calculate and is good enough for our purposes.
   *</p>
   *
   * @return Next random
   */
  static int nextRandom8Bit() {
    int t = seed;
    seed = t = ((t << 16) + (t << 1) + t) & 0x7FFFFFFF;
    return t >> 23;
  }

}
