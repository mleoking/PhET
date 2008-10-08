//<pre>
// Copyright 2001 Ken Perlin

package render;

import java.io.FileOutputStream; // for saving and loading lookup table
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.File;


// A VERY SIMPLE 3D RENDERER BUILT IN JAVA 1.0 - KEN PERLIN

/**
   Deals with aspects of color and material properties of objects.<p>
   Stores color properties of material: diffuse light, specular light,
   and ambient light.<br> 
   Holds information about transparency and whether the material is 
   double sided.
   @author Ken Perlin 2001
*/
public class Material implements Runnable {
   private String notice = "Copyright 2001 Ken Perlin. All rights reserved.";

   /**
      Flag determining wheter to precompute and store color tables for 
      direct look up intead of on the fly computation.
   */
   public boolean tableMode = true;

   /**
      Indicator whether background caching started.
   */
   private boolean startedBackgroundCaching = false;

   /**
      Bit depth of the resolution.
   */
   public int resP;

   /**
      Resolution of the material.
   */
   public int res;

   /**
      Stores the precomputed normal map for quick lookup later.
   */
   protected int table[] = new int[2 * res * res];

   /** 
   Indicates whether the material is double sided.
   */
   public boolean isDoubleSided = false;

   /**
      Indicates whether the material is anisotropic (light reflection  
      varies with respect to direction).
   */
   public boolean anisotropic = false;

   /**
      Noise frequency.
   */
   public double noiseF = 1;

   /**
      Noise amplitude.
   */
   public double noiseA = 0;

   /**
      Looks up the appropriate color value from the table at x, y, z.
      @param ix x
      @param iy y
      @param iz z
      @return the packed color value
   */
   public int getTable(int ix, int iy, int iz) {
      if (!tableMode || iz < 0 || iz > 1 || ix < 0 || ix >= res || iy < 0 || iy >= res)
         return 0;
      return table[tableIndex(ix, iy, iz)];
   }

   /**
      Sets the x, y, z, value in the lookup table to p.
      @param ix x
      @param iy y
      @param iz z
      @param p the packed color value
   */
   public void setTable(int ix, int iy, int iz, int p) {
      if (!tableMode)
         return;
      startedBackgroundCaching = true;
      if (iz < 0 || iz > 1 || ix < 0 || ix >= res || iy < 0 || iy >= res)
         return;
      table[tableIndex(ix, iy, iz)] = p;
   }

   /**
      Creates and initializes the lookup table to all black.
      @param p bit depth resolution
   */
   public void initTable(int p) {
      if (!tableMode)
         return;
      startedBackgroundCaching = false;
      resP = p;
      res = 1 << resP;
      table = new int[2 * res * res];
      for (int iz = 0; iz < 2; iz++)
         for (int ix = 0; ix < res; ix++)
            for (int iy = 0; iy < res; iy++)
               table[tableIndex(ix, iy, iz)] = 0;
      tableIndex=0;
   }

   /** 
   Counts the non-zero entries in the lookup table.
   @return the number of non-zero entries.
   */
   public int countTable() {
      int n = 0;
      for (int iz = 0; iz < 2; iz++)
         for (int ix = 0; ix < res; ix++)
            for (int iy = 0; iy < res; iy++)
               if (table[tableIndex(ix, iy, iz)] != 0)
                  n++;
      return n;
   }

   private int tableIndex(int ix, int iy, int iz) {
      return (((iz << resP) | ix) << resP) | iy;
   }

   /**
    * Holds diffuse color information ( R, G, B, exponent )	
    */
   protected double[] diffuse  = {1, 1, 1, 1};
   
   /**
    * Holds specular color information (R, G, B, exponent )
    */
   protected double[] specular = {0, 0, 0, 1};

   /**
      Transparency of the object (0-invisible, 1-opaque).
   */
   protected double transparency = 0;
   
   /**
      Ambient lighting color values in RGB (range [0,1]).
   */
   protected double ambient[] = { 0, 0, 0 };

   protected Texture texture;

   public boolean hasTexture() {
      return texture != null;
   }

   /**
    * Set a texture to the material.
    * Need to bind the material to the geometry before calling mesh 
    * @param texel
    * @return
    */
   public Material setTexture(Texture texel) {
      texture = texel;
      return this;
   }

   private double getw(int pz, int NB) {
      double p = 1. * pz / (1 << NB);
      double ret = 1. * p / (1 << 31 - NB);
      return ret;
   }

	/**
	 * Focal Length
	 */
   protected double FL = 10;
   
   
   private double getuv(int pz, int NB) {
      double ret = 1. * pz / (1 << 31 - NB);
      return ret;
   }

   /** Returns the packed integer of a particular pixel
    *  To do extra pixel computation, overload this method
    *  @param data array representing the pixel 
    *  indices of data are:
    *  0,1,2 are the x,y,z of the pixel
    *  3,4,5 are the r,g,b values
    *  6,7 are the u,v coordinates 
    *  @param dx size of the pixel in x
    *  @param dy size of the pixel in y
    *  @param NB precision value
    */
   public int computePixel(int[] data, int dx, int dy, int NB) {
      //0,1,2 are the x,y,z of the point
      //3,4,5 are the rbg of the point
      //6, 7 are the u, v
      double dw = getw(data[2], NB);

      double u = (double) (getuv(data[6], NB)) / dw;
      double v = (double) (getuv(data[7], NB)) / dw;

      int ret;
      int NBPower = 1 << NB;

      ret = texture.getTexel(u, v, dx, dy, NBPower);
      return ret;
   }

   /**
      Sets the diffuse components of light (range 0..1).
      @param r red
      @param g green
      @param b blue
   */
   public Material setDiffuse(double r, double g, double b) {
      return setDiffuse(r,g,b,1);
   }
   
	/**
		Sets the diffuse components of light (range 0..1).
		@param r red
		@param g green
		@param b blue
		@param p exponent
	*/
   public Material setDiffuse(double r, double g, double b, double p) {
      diffuse[0] = r;
      diffuse[1] = g;
      diffuse[2] = b;
      diffuse[3] = p;
      recache();
      return this;
   }

   /**
      Gets the diffuse color components in RGB (range 0 to 1).
      @param an array of 4 doubles corresponding to r g b color components + exponent
   */
   public void getDiffuse(double diff[]) {
		diff[0] = diffuse[0];
		diff[1] = diffuse[1];
		diff[2] = diffuse[2];
		diff[3] = diffuse[3];
   }

   /**
      Sets the specular color components (r, g, b, exp).
      @param r red
      @param g green
      @param b blue
      @return the material
   */
   public Material setSpecular(double r, double g, double b, double p) {
      specular[0] = r;
      specular[1] = g;
      specular[2] = b;
      specular[3] = p;
      recache();
      return this;
   }

   /**
      Gets the specular components of color (r, g, b, exponent).
      @param spec array of doubles containing 4 specular light components
   */
   public void getSpecular(double spec[]) {
   	//loop unrolled for efficiency
   	spec[0] = specular[0];
   	spec[1] = specular[1];
		spec[2] = specular[2];
		spec[3] = specular[3];
   }

   /**
      Sets the ambient lighting color values (range 0..1).
      @param r red
      @param g green
      @param b blue
      @return the material
   */
   public Material setAmbient(double r, double g, double b) {
      ambient[0] = r;
      ambient[1] = g;
      ambient[2] = b;
      recache();
      return this;
   }

   /**
      Gets the ambient light components (r, g, b).
      @param the array of the ambient lighting components (r, g, b)
   */
   public void getAmbient(double amb[]) {
		amb[0] = ambient[0];
		amb[1] = ambient[1];
		amb[2] = ambient[2];
   }

   /**
      Sets the diffuse color of an object.
      @param dr diffuse red
      @param dg diffuse green
      @param db diffuse blue
      @return the material
   */
   public Material setColor(double dr, double dg, double db) {
      return setDiffuse(dr, dg, db, 1);
   }
   
	/**
		Sets the diffuse color of an object.
		@param dr diffuse red
		@param dg diffuse green
		@param db diffuse blue
		@param dp diffuse exponent
		@return the material
	*/
	public Material setColor(double dr, double dg, double db, double dp) {
		return setDiffuse(dr, dg, db, dp);
	}

   /**
      Sets the diffuse and specular values of color.
      @param dr diffuse red
      @param dg diffuse green
      @param db diffuse blue
      @param sr specular red
      @param sg specular green
      @param sb specular blue
      @param sp specular exponent
      @return the material
   */
   public Material setColor(double dr, double dg, double db, double sr, double sg, double sb, double sp) {
      return (setDiffuse(dr, dg, db)).setSpecular(sr, sg, sb, sp);
   }
   
	/**
		Sets the diffuse and specular values of color.
		@param dr diffuse red
		@param dg diffuse green
		@param db diffuse blue
		@param dp diffuse exponent
		@param sr specular red
		@param sg specular green
		@param sb specular blue
		@param sp specular exponent
		@return the material
	*/
	public Material setColor(double dr, double dg, double db, double dp, double sr, double sg, double sb, double sp) {
			return (setDiffuse(dr, dg, db)).setSpecular(sr, sg, sb, sp);
		}

   /**
      Sets the diffuse, specular and ambient values of color.
      @param dr diffuse red
      @param dg diffuse green
      @param db diffuse blue
      @param sr specular red
      @param sg specular green
      @param sb specular blue
      @param se specular exponent
      @param ar ambient red
      @param ag ambient green
      @param ab ambient blue
      @return the material      
   */
   public Material setColor(double dr, double dg, double db, double sr, double sg, double sb, double sp, double ar, double ag, double ab) {
		return ((setDiffuse(dr, dg, db)).setSpecular(sr, sg, sb, sp)).setAmbient(ar, ag, ab);
   }
   
	/**
		Sets the diffuse, specular and ambient values of color.
		@param dr diffuse red
		@param dg diffuse green
		@param db diffuse blue
		@param dp diffuse exponent
		@param sr specular red
		@param sg specular green
		@param sb specular blue
		@param se specular exponent
		@param ar ambient red
		@param ag ambient green
		@param ab ambient blue
		@return the material      
	*/
   public Material setColor(double dr, double dg, double db, double dp, double sr, double sg, double sb, double sp, double ar, double ag, double ab) {
		return ((setDiffuse(dr, dg, db, dp)).setSpecular(sr, sg, sb, sp)).setAmbient(ar, ag, ab);      
   }
   
   
   public Texture getTexture() {
   		return texture;
   }

   /**
      Sets the double sided flag true to indicate whether the object is
      double sided.
      @param t new value of isDoubleSided
      @return the material
   */
   public Material setDoubleSided(boolean t) {
      isDoubleSided = t;
      return this;
   }

   /**
      Sets the transparency of the material (0 transparent to 1 opaque).
      @param t new transparency value
      @return the material
   */
   public Material setTransparency(double t) {
      transparency = t;
      return this;
   }

   /**
      Returns the transparency of the material (0 transparent to 1 opaque).
      @return the actual transparency of the material
   */
   public double getTransparency() {
      return transparency;
   }


   // THE REST OF THIS CLASS IS DEDICATED TO BACKGROUND CACHING - FILLING
   // UP A NORMAL-MAP TABLE IN THE BACKGROUND DURING THE FIRST FEW SECONDS
   // THAT THE APPLET IS RUNNING.

   private Thread t = null;

   /**
      Start background caching thread.
   */
   private void start() { 
     if (t == null) {
         t = new Thread(this);
         t.start();
      }     
   }
   
   /**
      Stop background caching thread.
   */
   private void stop() {
      if (t != null)
         stopped = true;
   }

   public void recache() {
      initTable(7);
      if (tableMode)
         start();
   }

   protected double v[] = new double[6];
   private volatile boolean stopped = false;
   private volatile int tableIndex;

   /**
      Thread that runs in the background ( provided the resources are
      available - no mouse dragging for example) and computes the normal
      map table of values for quick look up later.
   */
   
   public void run() {
      stopped=false;
      int chunk = 0;
      while (!stopped) {

         if (tableMode && startedBackgroundCaching && !Renderer.isDragging() && tableIndex != table.length) {
             
            for (chunk = 0; chunk < 500; chunk++) {
               
               if (table[tableIndex] == 0){
                  Renderer.renderVertex(tableIndex, this);
               }
               
               tableIndex++;

               if ( tableIndex == table.length || stopped ) {
                  return;
               }
               
            }
            
         }

         try {
            Thread.sleep(20);
         } catch (InterruptedException e) {}

      }
   }
   

  /**
   * Returns true if save was successful
   *
   * What it writes to the file: 
   * 1) first: resP
   * 2) look up table's entries
   *
   * The output file of this is passed into loadlookupTable 
   * to recreate the material's lookup table without having to recalculate 
   * those values.
   *
   * Assumes lookup table is done being constructed when this is called.
   *
   */
   
  public boolean saveLookupTable(File file)
  {
  
    if(table == null){
      
      return false;
      
    }
 
    int[] storeThis = new int[table.length + 1];
      
    // store the table
      
    for(int i = 0; i < table.length; i++){
       
      storeThis[i] = table[i];
        
    }
      
    // store resP
    
    storeThis[table.length] = resP;
      
    try{
      
      ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
      output.writeObject(storeThis);
      output.close();
      
      return true;
      
    }catch(Exception e){
      
      System.out.println("Failed to save lookup table");
      return false;
        
    }
    
  }
  
  /**
   * Reconstructs lookup table from output of lookupTableToString
   *
   * resP and res gets loaded as well 
   *
   * returns whether or not succeeded
   *
   */
  
  public boolean loadLookupTable(File saved){
   
    try{
      
      return loadLookupTable(new ObjectInputStream(new FileInputStream(saved)));
      
      
    }catch(Exception e){
      
      System.out.println("Could not load lookup table");
      
      return false;
      
    }
    
    
  }  
  
  public boolean loadLookupTable(ObjectInputStream input){
    
    try{
    
      // packed data
      int[] packed = (int[])input.readObject();
      
      // make sure resP (resolution) matches
      if(resP != packed[packed.length - 1]){
      
        resP = packed[packed.length - 1];
        res = 1 << resP;
        table = new int[packed.length - 1];
        
      }
      
      // load table

      tableIndex = table.length; // so indicate that the table is fully loaded

      for(int i = 0; i < table.length; i++){

          table[i] = packed[i];

      }
    }catch(Exception e){
     
      System.out.println("Could not load lookup table");
      return false;
      
    }

    return true;
    
    
  }
   
}
