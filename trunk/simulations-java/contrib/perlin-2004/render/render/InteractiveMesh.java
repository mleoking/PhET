//<pre>

package render;

import java.awt.*;

/*
 * Created on Jan 13, 2004
 *  
 */

/**
 * @author Du Nguyen
 *  
 */

public class InteractiveMesh extends Geometry {

   int pixels[];
   Texture texture;
   int W, H;

   boolean dragging = true;
   boolean damage = true;
   
   public InteractiveMesh() {
   }

   /**
	  *  Constructor
	  * @param m number of rows in the mesh
	  * @param n number of columns in the mesh
	  * @param w width of the mesh in pixels
	  * @param h height of the mesh in pixels
	  */
   public InteractiveMesh(int m, int n, int w, int h, int[] pix) {
      super();
      pixels = pix;
      Geometry g = this.add();
      W = w;
      H = h;
      texture = new Texture(pixels, W, H, "grid", false);
      Material material = new Material();
      material.setTexture(texture);
      g.setMaterial(material);
      g.setDoubleSided(true);
      g.mesh(m, n);
   }

   /**
    * Converts world coordinates to relative coordinates
    * @param wp
    * @return
    */
   public double[] getXYZ(double wp[]) {
      double[] p = new double[4];
      Matrix transmat = new Matrix();
      transmat.invert(matrix);
      xf(transmat, wp[0], wp[1], wp[2], p);
      return p;
   }

   /**
    * Converts relative coordinates to world coordinates
    * @param xyz
    * @return
    */
   public int[] getXY(double[] xyz) {
      double p[] = getXYZ(xyz);
      int point[] = new int[2];
      point[0] = (int) ((p[0] + 1) * W / 2);
      point[1] = (int) ((p[1] + 1) * H / 2);
      return point;
   }

   /**
    * Returns true if Geometry g is selected
    * @param g
    * @return
    */
   public boolean isSelected(Geometry g) {
      Geometry mg = this;
      if (mg == g)
         return true;
      return find(mg, g);
   }

   public boolean find(Geometry level, Geometry g) {
      if (level.child == null)
         return false;
      if (level.child.length == 0)
         return false;
      for (int i = 0; i < level.child.length; i++) {
         if (level.child[i] == g)
            return true;
         return find(level.child[i], g);
      }
      return false;

   }

   private void xf(Matrix m, double x, double y, double z, double v[]) {
      for (int j = 0; j < 3; j++)
         v[j] = m.get(j, 0) * x + m.get(j, 1) * y + m.get(j, 2) * z + m.get(j, 3);
   }
      
   
   public void animate(double time) {
   }


   //Mouse handling
   public boolean mouseUp(Event evt, double[] xyz) {
      boolean ret = dragging;
      dragging = false;
      return ret;
   }

   public boolean mouseDown(Event evt, double[] xyz, Geometry g) {
      dragging = isSelected(g);
      return dragging;
   }

   public boolean mouseDrag(Event evt, double[] xyz) {
      return dragging;
   }

}
