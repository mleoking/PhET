package org.jmol.api;

import java.awt.Image;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.util.MeshSurface;
import org.jmol.viewer.Viewer;

public interface JmolRendererInterface {

  // these methods are implemented in Export3D and Graphics3D
  
  public abstract int getExportType();
  
  public abstract String getExportName();

  public abstract boolean initializeExporter(String type, Viewer viewer,
                                             double privateKey, Graphics3D g3d, Object output);

  public abstract boolean isAntialiased();
  
  public abstract boolean checkTranslucent(boolean isAlphaTranslucent);

  public abstract boolean haveTranslucentObjects();

  /**
   * gets g3d width
   *
   * @return width pixel count;
   */
  public abstract int getRenderWidth();

  /**
   * gets g3d height
   *
   * @return height pixel count
   */
  public abstract int getRenderHeight();

  /**
   * gets g3d slab
   *
   * @return slab
   */
  public abstract int getSlab();

  public abstract void setSlab(int slabValue);

  /**
   * gets g3d depth
   *
   * @return depth
   */
  public abstract int getDepth();

  /**
   * sets current color from colix color index
   * @param colix the color index
   * @return true or false if this is the right pass
   */
  public abstract boolean setColix(short colix);

  public abstract void renderBackground();
  
  public abstract void drawAtom(Atom atom);

  /**
   * draws a ring and filled circle (halos, draw CIRCLE, draw handles)
   *
   * @param colixRing 
   * @param colixFill 
   * @param diameter 
   * @param x center x
   * @param y center y
   * @param z center z
   */
  public abstract void drawFilledCircle(short colixRing, short colixFill, int diameter, int x, int y,
                                          int z);

  /**
   * fills a solid sphere
   *
   * @param diameter pixel count
   * @param x center x
   * @param y center y
   * @param z center z
   */
  public abstract void fillSphere(int diameter, int x, int y, int z);

  /**
   * fills a solid sphere
   *
   * @param diameter pixel count
   * @param center javax.vecmath.Point3i defining the center
   */

  public abstract void fillSphere(int diameter, Point3i center);

  /**
   * fills a solid sphere
   *
   * @param diameter pixel count
   * @param center a javax.vecmath.Point3f ... floats are casted to ints
   */
  public abstract void fillSphere(int diameter, Point3f center);

  /**
   * draws a rectangle
   *
   * @param x upper left x
   * @param y upper left y
   * @param z upper left z
   * @param zSlab z for slab check (for set labelsFront)
   * @param rWidth pixel count
   * @param rHeight pixel count
   */
  public abstract void drawRect(int x, int y, int z, int zSlab, int rWidth,
                                int rHeight);

  /**
   * fills background rectangle for label
   *<p>
   *
   * @param x upper left x
   * @param y upper left y
   * @param z upper left z
   * @param zSlab  z value for slabbing
   * @param widthFill pixel count
   * @param heightFill pixel count
   */
  public abstract void fillRect(int x, int y, int z, int zSlab, int widthFill,
                                int heightFill);

  /**
   * draws the specified string in the current font.
   * no line wrapping -- axis, labels, measures
   *
   * @param str the String
   * @param font3d the Font3D
   * @param xBaseline baseline x
   * @param yBaseline baseline y
   * @param z baseline z
   * @param zSlab z for slab calculation
   */

  public abstract void drawString(String str, Font3D font3d, int xBaseline,
                                  int yBaseline, int z, int zSlab);

  public abstract void plotPixelClippedNoSlab(int argb, int x, int y, int z);
    
  /**
   * draws the specified string in the current font.
   * no line wrapping -- echo, frank, hover, molecularOrbital, uccage
   *
   * @param str the String
   * @param font3d the Font3D
   * @param xBaseline baseline x
   * @param yBaseline baseline y
   * @param z baseline z
   */

  public abstract void drawStringNoSlab(String str, Font3D font3d,
                                        int xBaseline, int yBaseline, int z);

  public abstract void setFont(byte fid);

  public abstract Font3D getFont3DCurrent();

  public abstract void drawPixel(int x, int y, int z);

  public abstract void plotPixelClipped(Point3i a);

  public abstract void drawPoints(int count, int[] coordinates, int scale);

  public abstract void drawDashedLine(int run, int rise, Point3i pointA,
                                      Point3i pointB);

  public abstract void drawDottedLine(Point3i pointA, Point3i pointB);

  public abstract void drawLine(int x1, int y1, int z1, int x2, int y2, int z2);

  public abstract void drawLine(Point3i pointA, Point3i pointB);

  public abstract void drawLine(short colixA, short colixB, int x1, int y1,
                                int z1, int x2, int y2, int z2);

  public abstract void drawBond(Atom atomA, Atom atomB, short colixA,
                                    short colixB, byte endcaps, short mad);

  public abstract void fillCylinder(short colixA, short colixB, byte endcaps,
                                    int diameter, int xA, int yA, int zA,
                                    int xB, int yB, int zB);

  public abstract void fillCylinder(byte endcaps, int diameter,
                                    Point3i screenA, Point3i screenB);

  public abstract void fillCylinderBits(byte endcaps, int diameter,
                                        Point3f screenA, Point3f screenB);

  public abstract void fillCylinderScreen(byte endcaps, int diameter, int xA, int yA,
                                          int zA, int xB, int yB, int zB);


  public abstract void fillCylinderScreen(byte endcapsOpenend, int diameter,
                                                Point3i pt0i, Point3i pt1i);

  public abstract void fillConeScreen(byte endcap, int screenDiameter, Point3i screenBase,
                                Point3i screenTip, boolean isBarb);

  public abstract void fillConeSceen(byte endcap, int screenDiameter, Point3f screenBase,
                                Point3f screenTip);

  public abstract void drawHermite(int tension, Point3i s0, Point3i s1,
                                   Point3i s2, Point3i s3);

  public abstract void drawHermite(boolean fill, boolean border, int tension,
                                   Point3i s0, Point3i s1, Point3i s2,
                                   Point3i s3, Point3i s4, Point3i s5,
                                   Point3i s6, Point3i s7, int aspectRatio);

  public abstract void fillHermite(int tension, int diameterBeg,
                                   int diameterMid, int diameterEnd,
                                   Point3i s0, Point3i s1, Point3i s2,
                                   Point3i s3);

  // isosurface "mesh" option -- color mapped vertices
  public abstract void drawTriangle(Point3i screenA, short colixA,
                                    Point3i screenB, short colixB,
                                    Point3i screenC, short colixC, int check);

  // isosurface and other meshes -- preset colix
  public abstract void drawTriangle(Point3i screenA, Point3i screenB,
                                    Point3i screenC, int check);

  /* was for stereo -- not implemented
  public abstract void drawfillTriangle(int xA, int yA, int zA, int xB, int yB,
                                        int zB, int xC, int yC, int zC);
  */
  
  // isosurface colored triangles
  public abstract void fillTriangle(Point3i screenA, short colixA,
                                    short normixA, Point3i screenB,
                                    short colixB, short normixB,
                                    Point3i screenC, short colixC, short normixC);

  // polyhedra
  public abstract void fillTriangleTwoSided(short normix, int xScreenA, int yScreenA,
                                    int zScreenA, int xScreenB, int yScreenB,
                                    int zScreenB, int xScreenC, int yScreenC,
                                    int zScreenC);

  public abstract void fillTriangle(Point3f screenA, Point3f screenB,
                                    Point3f screenC);

  public abstract void fillTriangle(Point3i screenA, Point3i screenB,
                                    Point3i screenC);

  public abstract void fillTriangle(Point3i screenA, short colixA,
                                    short normixA, Point3i screenB,
                                    short colixB, short normixB,
                                    Point3i screenC, short colixC,
                                    short normixC, float factor);

  public abstract void drawQuadrilateral(short colix, Point3i screenA,
                                         Point3i screenB, Point3i screenC,
                                         Point3i screenD);

  public abstract void fillQuadrilateral(Point3f screenA, Point3f screenB,
                                         Point3f screenC, Point3f screenD);

  public abstract void fillQuadrilateral(Point3i screenA, short colixA,
                                         short normixA, Point3i screenB,
                                         short colixB, short normixB,
                                         Point3i screenC, short colixC,
                                         short normixC, Point3i screenD,
                                         short colixD, short normixD);

  public abstract void drawSurface(MeshSurface meshSurface);

  public abstract boolean isInDisplayRange(int x, int y);

  public abstract boolean isClippedZ(int z);

  public abstract boolean isClippedXY(int i, int screenX, int screenY);

  public abstract int getColorArgbOrGray(short colix);

  public abstract void setNoisySurfaceShade(Point3i screenA, Point3i screenB,
                                        Point3i screenC);

  public abstract byte getFontFid(String fontFace, float fontSize);

  public abstract boolean isDirectedTowardsCamera(short normix);

  public abstract Vector3f[] getTransformedVertexVectors();

  public abstract Font3D getFont3DScaled(Font3D font3d, float imageFontScaling);

  public abstract byte getFontFid(float fontSize);

  public abstract void fillEllipsoid(Point3f center, Point3f[] points, int x, int y, int z, 
      int diameter, Matrix3f mToEllipsoidal, double[] coef, Matrix4f mDeriv, int selectedOctant, Point3i[] octantPoints);

  public abstract void drawImage(Image image, int x, int y, int z, int zslab, short bgcolix, int width, int height);

  public abstract String finalizeOutput();

  public abstract short[] getBgColixes(short[] bgcolixes);

  public abstract void setTranslucentCoverOnly(boolean TF);

  public abstract boolean drawEllipse(Point3f ptAtom, Point3f ptX, Point3f ptY,
                                    boolean fillArc, boolean wireframeOnly);

}
