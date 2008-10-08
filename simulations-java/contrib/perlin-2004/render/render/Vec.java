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
    * 
    * @return true if entries are the same
    */
   
   public static boolean equals(double[] a, double[] b){
    
     if(a.length != b.length){
      
       return false;
       
     }
     
     for(int i = 0; i < a.length; i++){
      
       if(a[i] != b[i]){
        
         return false;
         
       }
       
     }
     
     return true;
     
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
   
  /**
   *If vector's length is greater than maxLength, reduces it to that.
   *
   *@param vector vector to resize
   *@param maxLength if vector is longer than this, resizes it to this.
   */

  public static void capLength(double[] vector, double maxLength){
     
    if(length(vector) > maxLength){
        
      scale(vector,maxLength);
           
    }
       
  }
    
  /**
   * Resizes vector to given length 
   *
   * @param vector vector to resize
   * @param length desired length
   */
    
  public static void scale(double[] vector, double length){

    // if all entries are zero, make them 1
    
    boolean allEntriesZero = true;
    
    for(int i = 0; i < vector.length; i++){
     
      if(vector[i] != 0){
       
        allEntriesZero = false;
        break;
        
      }
      
    }
    
    if(allEntriesZero){
     
      // make all the entries the same size (but not zero) so that multipling
      // them by length will scale them to the correct size
      
      for(int i = 0; i < vector.length; i++){
       
        vector[i] = 1;
        
      }
      
    }
    
    normalize(vector);

    for(int i = 0; i < vector.length; i++){
            
      vector[i] *= length;
      
    }
      
  }

  public static double length(double[] vector){
     
    return Math.sqrt(dot(vector,vector));
        
  }
  
  /**
   * Set all the entries to 0
   *
   */
  
  public static void zero(double[] a){
     
    for(int i = 0; i < a.length; i++){
         
      a[i] = 0;
            
    }
        
  }
  
  /**
   *
   *@param temp a temp array of the same size as a and b to work with (its
   * contents will be modified).  This can be null, which will cause the 
   * code to dynamically allocate an array to be used.
   *
   *@return length of difference or Double.NaN if a and b do not have the same
   * # of dimensions
   */
  
  public static double distance(double[] a, double[] b, double[] temp){
      
    if(a.length != b.length){
     
      return Double.NaN;
      
    }
    
    double[] difference = temp;
    
    if(difference == null){
     
      difference = new double[a.length];
      
    }
    
    for(int i = 0; i < difference.length; i++){
         
      difference[i] = a[i] - b[i];
            
    }
        
    return length(difference);
        
  }
  
   
}

