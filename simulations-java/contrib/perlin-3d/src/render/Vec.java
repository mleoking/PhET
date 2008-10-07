//<pre>
// Copyright 2001 Ken Perlin

package render;

/**
   Provides functionality to manipulate vectors.
*/

public class Vec {
   //----- SIMPLE CLASS TO HANDLE BASIC VECTOR OPERATIONS -----

   /**
      Normalizes vector v to unit-length.
      @param v a vector
   */
   public static void normalize(double[] v) {
      double s = norm(v);
      if ( s==0 ) 
         return;
      for (int i = 0; i < v.length; i++)
         v[i] /= s;
   }

   /**
      Computes the magnitude of the vector.
      @param v a vector
      @return the magnitude of vector v
   */
   public static double norm(double[] v) {
      return Math.sqrt(dot(v, v));
   }

   /** 
   Computes the dot product of vectors a and b. Vectors a and b must be of the same length.
   @param a source vector
   @param b source vector
   @return the result of a dot b
   */
   public static double dot(double[] a, double[] b) {
      double sum = 0;
      for (int i = 0; i < b.length; i++)
         sum += a[i] * b[i];
      return sum;
   }

   /** 
   Computes the cross-product of two vectors a and b and stores the result in dst.	a, b, and dst must be 3 dimensional vectors.
   @param a source vector 1
   @param b source vector 2
   @param dst resulting vector from a cross b
   */
   public static void cross(double[] a, double[] b, double[] dst) {
      dst[0] = b[1] * a[2] - b[2] * a[1];
      dst[1] = b[2] * a[0] - b[0] * a[2];
      dst[2] = b[0] * a[1] - b[1] * a[0];
   }

   /** 
   Copies contents of the src vector to the dst vector. Both vectors must be of the same length.
   @param src original vector
   @param dst copy of original vector 
   */
   public static void copy(double[] src, double[] dst) {
      for (int i = 0; i < src.length; i++)
         dst[i] = src[i];
   }

   /** 
   Populates the dst vector with values x, y, z.
   @param dst vector to be populated
   @param x component 0
   @param y component 1
   @param z component 2
   */
   public static void set(double[] dst, double x, double y, double z) {
      dst[0] = x;
      dst[1] = y;
      dst[2] = z;
   }

   /**
   Rotates a vector about x or y or z axis
   @param dst vector to be rotated
   @param axis of rotation: 0=x, 1=y, 2=z
   @param angle in radians
   */
   public static void rotate(double dst[], int axis, double angle) {
      int i = (axis+1) % 3, j = (axis+2) % 3;
      double c = Math.cos(angle), s = Math.sin(angle);
      double u = dst[i], v = dst[j];
      dst[i] = c * u - s * v;
      dst[j] = s * u + c * v;
   }
}

