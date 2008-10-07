// <pre>
// Copyright 2001 Ken Perlin

package render;

//----- SIMPLE CLASS TO HANDLE BASIC 3D MATRIX OPERATIONS -----

/**
   Provides functionality for 4x4 3D matrix manipulations.
   It's thread-safe.
   @author Ken Perlin 2001
*/

public class Matrix {

   private String notice = "Copyright 2001 Ken Perlin. All rights reserved.";

   private static double identity[] = new double[16];
   static {
      for (int i = 0; i < 4; i++)
         for (int j = 0; j < 4; j++) {
            identity[(i << 2) + j] = (i == j ? 1 : 0);
         }
   }
   private double mMatrix[] = new double[16];
   private double tmp[] = new double[16];
   private double tmp2[] = new double[16];

   /**
      Default constructor.
   */
   public Matrix() {
   }

   /**
      Constructor takes an array of 16 elements to populate the 4x4 matrix.
      @param a 4x4 quaternion values
   */
   public Matrix(double a[]) {
      if (a.length == 4) { // quaternion
         double Nq = a[0] * a[0] + a[1] * a[1] + a[2] * a[2] + a[3] * a[3];
         double s = (Nq > 0.0) ? (2.0 / Nq) : 0.0;
         double xs = a[0] * s, ys = a[1] * s, zs = a[2] * s;
         double wx = a[3] * xs, wy = a[3] * ys, wz = a[3] * zs;
         double xx = a[0] * xs, xy = a[0] * ys, xz = a[0] * zs;
         double yy = a[1] * ys, yz = a[1] * zs, zz = a[2] * zs;

         mMatrix[(0 << 2) + 0] = 1.0 - (yy + zz);
         mMatrix[(1 << 2) + 0] = xy + wz;
         mMatrix[(2 << 2) + 0] = xz - wy;
         mMatrix[(0 << 2) + 1] = xy - wz;
         mMatrix[(1 << 2) + 1] = 1.0 - (xx + zz);
         mMatrix[(2 << 2) + 1] = yz + wx;
         mMatrix[(0 << 2) + 2] = xz + wy;
         mMatrix[(1 << 2) + 2] = yz - wx;
         mMatrix[(2 << 2) + 2] = 1.0 - (xx + yy);
         mMatrix[(0 << 2) + 3] = mMatrix[(1 << 2) + 3] = mMatrix[(2 << 2) + 3] = mMatrix[(3 << 2) + 0] = mMatrix[(3 << 2) + 1] = mMatrix[(3 << 2) + 2] = 0.0;
         mMatrix[(3 << 2) + 3] = 1.0;
      } else {
         System.arraycopy(a, 0, mMatrix, 0, 16);
      }
   }

   /**
      Returns matrix value at m[i, j].
      @param i row index
      @param j column index
      @return value at specified location
   */
   public final double get(int i, int j) {
      return mMatrix[(i << 2) + j];
   }

   /**
      Sets matrix value at m[i,j] to d.
      @param i row index
      @param j column index
      @param d the new value
   */
   public final void set(int i, int j, double d) {
      mMatrix[(i << 2) + j] = d;
   }

   /** 
       Returns the actual array containing the matrix (not thread-safe).
       @return the actual matrix array of 16 elements
       @see #get
   */
   public final double[] getUnsafe() {
      return mMatrix;
   }

   /**
      Returns a copy of matrix (thread-safe)/
      @return a copy of the matrix array (16 elements).
      @see #getUnsafe
   */
   public final double[] get() {
      double m[] = new double[16];
      System.arraycopy(mMatrix, 0, m, 0, 16);
      return m;
   }

   /**
      Sets the desired matrix to the identity matrix.
      @param m the matrix to be modified
   */
   public static final void identity(Matrix m) {
      System.arraycopy(identity, 0, m.getUnsafe(), 0, 16);
   }

   /**
      Sets the object matrix to the identity matrix.
   */
   public final void identity() {
      System.arraycopy(identity, 0, mMatrix, 0, 16);
   }

   /**
      Sets the desired matrix array to the identity matrix.
      @param m matrix array
   */
   private static void identity(double[] m) {
      System.arraycopy(identity, 0, m, 0, 16);
   }

   /**
      Copies contents from matrix src to the object matrix.
      @param src original matrix to be copied
   */
   public final void copy(Matrix src) {
      System.arraycopy(src.getUnsafe(), 0, mMatrix, 0, 16);
   }

   private void preMultiply(double b[]) {
      double dst[] = getUnsafe();
      System.arraycopy(mMatrix, 0, tmp, 0, 16);
      for (int i = 0; i < 4; i++)
         for (int j = 0; j < 4; j++) {
            dst[(i << 2) + j] = 0.0;
            dst[(i << 2) + j] += tmp[(i << 2) + 0] * b[(0 << 2) + j];
            dst[(i << 2) + j] += tmp[(i << 2) + 1] * b[(1 << 2) + j];
            dst[(i << 2) + j] += tmp[(i << 2) + 2] * b[(2 << 2) + j];
            dst[(i << 2) + j] += tmp[(i << 2) + 3] * b[(3 << 2) + j];
         }
   }

   /**
      Premultiplies the object matrix by mb and stores the result in the object;
      As a result, the translation, scaling and rotation operations 
      contained in mb are effectively performed before those in the object . 
      @param mb the multiplier matrix
   */

   public final void preMultiply(Matrix mb) {
      preMultiply(mb.getUnsafe());
   }

   private void postMultiply(double b[]) {
      double dst[] = getUnsafe();
      System.arraycopy(mMatrix, 0, tmp, 0, 16);
      for (int i = 0; i < 4; i++)
         for (int j = 0; j < 4; j++) {
            dst[(i << 2) + j] = 0;
            dst[(i << 2) + j] += b[(i << 2) + 0] * tmp[(0 << 2) + j];
            dst[(i << 2) + j] += b[(i << 2) + 1] * tmp[(1 << 2) + j];
            dst[(i << 2) + j] += b[(i << 2) + 2] * tmp[(2 << 2) + j];
            dst[(i << 2) + j] += b[(i << 2) + 3] * tmp[(3 << 2) + j];
         }
   }

   /** 
        Postmultiplies the object matrix by mb and stores the result in the 
        object matrix;
        As a result, the translation, scaling and rotation operations 
        contained in mb are effectively performed after those in the object
        matrix . 
        @param mb the multiplier matrix
    */
   public final void postMultiply(Matrix mb) {
      postMultiply(mb.getUnsafe());
   }

   //----- ROUTINES TO ROTATE AND TRANSLATE MATRICES -----

   /**
        Applies a translation by x, y, z to the obeject matrix. The shape or 
        orientation of the object are not affected. 
        @param x amount of translation along the x axis
        @param y amount of translation along the y axis
        @param z amount of translation along the z axis
     */
   public final void translate(double x, double y, double z) {
      makeTranslationMatrix(tmp2, x, y, z);
      preMultiply(tmp2);
   }

   /** 
   	Modifies the object matrix to rotate about the X axis by angle theta.
         @param theta angle of rotation in radians
     */
   public final void rotateX(double theta) {
      makeRotationMatrix(tmp2, 1, 2, theta);
      preMultiply(tmp2);
   }

   /** 
   	Modifies the object matrix to rotate about the Y axis by angle theta.
         @param theta angle of rotation in radians
     */
   public final void rotateY(double theta) {
      makeRotationMatrix(tmp2, 2, 0, theta);
      preMultiply(tmp2);
   }

   /** 
   	Modifies the object matrix to rotate about the Z axis by angle theta.
         @param theta angle of rotation in radians
     */
   public final void rotateZ(double theta) {
      makeRotationMatrix(tmp2, 0, 1, theta);
      preMultiply(tmp2);
   }

   /**
   	  Modifies the object matrix to rotate by angle theta about axis x,y,z.
   		@param theta angle of rotation in radians
   		@param x 1st coord of rotation axis
   		@param y 2nd coord of rotation axis
   		@param z 3rd coord of rotation axis
     */
   public final void rotate(double theta, double x, double y, double z) {
      double unY = Math.atan2(y, x);
      double unX = Math.atan2(Math.sqrt(x * x + y * y), z);
      rotateZ(unY);
      rotateY(unX);
      rotateZ(theta);
      rotateY(-unX);
      rotateZ(-unY);
   }

   /** 
   	Scales the transformation matrix by x,y,z in the respective 
   	directions. 
          @param x scale factor along the x axis
   	@param y scale factor along the y axis
   	@param z scale factor along the z axis
      */
   public final void scale(double x, double y, double z) {
      makeScaleMatrix(tmp2, x, y, z);
      preMultiply(tmp2);
   }

   //----- INVERTING A 4x4 THAT WAS CREATED BY TRANSLATIONS+ROTATIONS+SCALES

   /**
      Inverts the 4x4 matrix and stores the result in the object
      matrix.  
      @param msrc original matrix to be inverted
   */
   public final void invert(Matrix msrc) {
      double src[] = msrc.getUnsafe();
      double dst[] = mMatrix;

      // COMPUTE ADJOINT COFACTOR MATRIX FOR THE ROTATION+SCALE 3x3

      for (int i = 0; i < 3; i++)
         for (int j = 0; j < 3; j++) {
            int i0 = (i + 1) % 3;
            int i1 = (i + 2) % 3;
            int j0 = (j + 1) % 3;
            int j1 = (j + 2) % 3;
            dst[(j << 2) + i] = src[(i0 << 2) + j0] * src[(i1 << 2) + j1] - src[(i0 << 2) + j1] * src[(i1 << 2) + j0];
         }

      // RENORMALIZE BY DETERMINANT TO GET ROTATION+SCALE 3x3 INVERSE

      double determinant = src[(0 << 2) + 0] * dst[(0 << 2) + 0] + src[(1 << 2) + 0] * dst[(0 << 2) + 1] + src[(2 << 2) + 0] * dst[(0 << 2) + 2];
      double invd = 1.0 / determinant;
      for (int i = 0; i < 3; i++)
         for (int j = 0; j < 3; j++)
            dst[(i << 2) + j] *= invd;

      // COMPUTE INVERSE TRANSLATION

      for (int i = 0; i < 3; i++)
         dst[(i << 2) + 3] = -dst[(i << 2) + 0] * src[(0 << 2) + 3] - dst[(i << 2) + 1] * src[(1 << 2) + 3] - dst[(i << 2) + 2] * src[(2 << 2) + 3];
   }

   //----- FOR DEBUGGING -----
   /**
         Converts the transformation matrix to a String.
         @param m matrix to be translated to text
         @return a textual representation of the matrix
      */
   public final String toString(Matrix mm) {
      double m[] = mm.getUnsafe();
      String s = "{";
      for (int i = 0; i < 4; i++) {
         s += "{";
         for (int j = 0; j < 4; j++) {
            int n = (int) (100 * m[(i << 2) + j]);
            s += (n / 100.) + (j == 3 ? "" : ",");
         }
         s += "}" + (i == 3 ? "" : ",");
      }
      return s + "}";
   }

   //----- ROUTINES TO GENERATE TRANSFORMATION MATRICES -----

   private static void makeTranslationMatrix(double m[], double x, double y, double z) {
      identity(m);
      m[(0 << 2) + 3] = x;
      m[(1 << 2) + 3] = y;
      m[(2 << 2) + 3] = z;
   }
   private static void makeRotationMatrix(double m[], int i, int j, double theta) {
      identity(m);
      m[(i << 2) + i] = m[(j << 2) + j] = Math.cos(theta);
      m[(i << 2) + j] = -Math.sin(theta);
      m[(j << 2) + i] = -m[(i << 2) + j];
   }
   private static void makeScaleMatrix(double m[], double x, double y, double z) {
      identity(m);
      m[(0 << 2) + 0] *= x;
      m[(1 << 2) + 1] *= y;
      m[(2 << 2) + 2] *= z;
   }

   public String toString() {
      int k = 0;
      String s = new String("[ ");
      for (int i = 0; i < 4; i++) {
         s += "\t[ ";
         for (int j = 0; j < 4; j++)
            s += String.valueOf(mMatrix[k++]) + " ";
         s += "]\n";
      }
      s += "]";
      return s;
   }
}
