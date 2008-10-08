//<pre>
//Copyright 2001 Ken Perlin

package render;

/**
 * Provides the computational functionality to render geometric objects in
 * realtime.
 * 
 * @author Ken Perlin 2001
 */

import java.util.*;

public class Renderer {

   private String notice = "Copyright 2001 Ken Perlin. All rights reserved.";

   /**
    * Flag controls table lookup mode for materials, true means on.
    */
   public static boolean tableMode = true;

   /**
    * Set the level of detail for meshes.
    */
   public int lod = 1;

   /**
    * Shows/overlays the geometry mesh in black when true.
    */
   public boolean showMesh = false;

   /**
    * Allocate space for transparent objects when true.
    */
   public boolean updateTransparency = true;

   /**
    * Flag that determines whether to keep a z-buffer of geometries, to to know
    * the frontmost object at any position (x, y) in the image.
    * 
    * @see #getGeometry(int x, int y)
    */
   public boolean bufferg = false;

   /**
    * Determines whether the camera tries to maintain a "heads up" orientation.
    */
   public boolean isHeadsUp = false;

   //--- PUBLIC METHODS

   public Renderer() {
      nLights = 0;
      world = new Geometry();
      Matrix.identity(camera);
      //		computeCamera();
   }

   /**
    * Initializes the renderer.
    * 
    * @param W
    *            framebuffer width
    * @param H
    *            framebuffer height.
    * @return framebuffer array.
    */
   public synchronized int[] init(int W, int H) {
      zbuffer = null;
      gzbuffer = null;

      int[] pix = new int[W * H];
      Arrays.fill(pix, 0);
      this.pix = pix;
      this.W = W;
      this.H = H;

      return pix;
   }

   /**
    * If the user is interactively dragging the mouse, we want the renderer to
    * know about it, so that any other background process (eg: a material which
    * is building a lookup table) can ask the renderer, and thereby avoid
    * consuming scarce CPU resources simultaneously.
    * 
    * @param tf
    *            dragging true or false
    */
   public static void setDragging(boolean tf) {
      dragging = tf;
   }

   /**
    * Returns whether dragging is active or not.
    * 
    * @return true when dragging is active, false otherwise
    */
   public static boolean isDragging() {
      return dragging;
   }

   /**
    * Forces an absolute value for the camera matrix.
    * 
    * @param theta
    *            horizontal angle (radians)
    * @param phi
    *            vertical angle (radians)
    */
   public synchronized void setCamera(double theta, double phi) {
      Matrix.identity(camera);
      changeCamera(theta, phi);
   }

   /**
    * Sets whether the camera tries to maintain a "heads up" orientation.
    * 
    * @param tf
    *            value true or false
    */
   public void headsUp(boolean tf) {
      isHeadsUp = tf;
   }

   /**
    * Sets the camera's focal length.
    * 
    * @param value
    *            focal lengh
    */
   public void setFL(double value) {
      FL = value;
   }

   /**
    * Returns the camera's focal length
    * 
    * @return camera's focal length
    */
   public double getFL() {
      return FL;
   }

   public void setW(int w) {
      W = w;
   }

   public void setH(int h) {
      H = h;
   }

   public int getW() {
      return W;
   }

   public int getH() {
      return H;
   }

   /**
    * Sets the camera field of view.
    * 
    * @param value
    *            field of view
    */
   public void setFOV(double value) {
      FOV = value;
   }

   /**
    * Returns the camera field of view.
    * 
    * @return value field of view
    */
   public double getFOV() {
      return FOV;
   }

   /**
    * Returns the root of the geometry tree.
    * 
    * @return the root of the geometry tree.
    */
   public Geometry getWorld() {
      return world;
   }

   /**
    * Set the background fill color.
    */
   public void setBgColor(double r, double g, double b) {
      bgColor = pack(f2i(r), f2i(g), f2i(b));
   }

   /**
    * Set the background fill color.
    */
   public void setBgColor(int color) {
      bgColor = color;
   }

   /**
    * Returns the background color.
    */
   public int getBgColor() {
      return bgColor;
   }

   /**
    * Add a light source where x,y,z are light source direction; r,g,b are
    * light source color.
    */
   public void addLight(double x, double y, double z, double r, double g, double b) {
      placeLight(nLights, x, y, z);
      colorLight(nLights, r, g, b);
      nLights++;
   }

   /**
    * Returns the number of lights in the scene.
    * 
    * @return the number of lights
    */
   public int getNumberOfLights() {
      return nLights;
   }

   /**
    * Moves an already defined light i, to point in the new direction of
    * normalized [x, y, z].
    * 
    * @param i
    *            the index of the light to be changed
    * @param x
    *            x direction of the light
    * @param y
    *            y direction of the light
    * @param z
    *            z direction of the light
    */
   public void placeLight(int i, double x, double y, double z) {
      double s = Math.sqrt(x * x + y * y + z * z);
      light[i][0] = x / s;
      light[i][1] = y / s;
      light[i][2] = z / s;
   }

   /**
    * Assigns new color values to the light i.
    * 
    * @param i
    *            index of the light to change
    * @param r
    *            the red color component value
    * @param g
    *            the green color component
    * @param b
    *            the blue color component
    */
   public void colorLight(int i, double r, double g, double b) {
      light[nLights][3] = r;
      light[nLights][4] = g;
      light[nLights][5] = b;
   }

   /**
    * Rotate angle of view.
    */
   public synchronized void rotateView(double t, double p) {
      theta += t;
      phi += p;
      computeCamera();
   }

   static double a1[] = { 0, 0, 0};

   /**
    * flag that enables full manual camera control for setting camera location,
    * aim target, and the up vector. (default = false). When true you can use :
    * {@link #lookAt},{@link #setCameraPos},{@link #setCameraAim},
    * {@link #setCameraUp}. When false you can use :
    * {@link #setCamera(double, double)}
    */
   public boolean manualCameraControl = false;

   /**
    * Sets the camera to move to the eye position, aim at the center, and
    * maintain the up direction ( requires the {@link #manualCameraControl}
    * flag to be turned on).
    * 
    * @param eye
    *            new position of the camera ( double[x, y, z] )
    * @param center
    *            aim point of the camera ( double[x, y, z] )
    * @param up
    *            unit vector specifying the up direction in the world (
    *            double[x, y, z] )
    */
   public void lookAt(double[] eye, double[] center, double[] up) {
      for (int i = 0; i < 3; i++) {
         cameraPos[i] = eye[i];
         cameraAim[i] = center[i];
         cameraUp[i] = up[i];
      }
      computeCamera();
   }

   /**
    * Returns the matrix that defines the camera transformation.
    * 
    * @return the matrix that defines the camera transformation
    */
   public Matrix getCamera() {
      return camera;
   }

   /**
    * Sets the camera matrix directly. Matrix needs to be 4x4.
    */
   public void setCamera(Matrix m) {
      camera.copy(m);
   }

   /**
    * Sets the position of the camera. ( requires the
    * {@link manualCameraControl}flag to be turned on).
    */
   public void setCameraPos(double px, double py, double pz) {
      cameraPos[0] = px;
      cameraPos[1] = -py;
      cameraPos[2] = pz;
      computeCamera();
   }

   /**
    * returns the current position of the camera
    */
   public double[] getCameraPos() {
      double cp[] = new double[3];
      System.arraycopy(cameraPos, 0, cp, 0, 3);
      return cp;
   }

   /**
    * Sets the aiming point at which the camera should point. ( requires the
    * {@link manualCameraControl}flag to be turned on).
    */
   public void setCameraAim(double px, double py, double pz) {
      cameraAim[0] = px;
      cameraAim[1] = py;
      cameraAim[2] = pz;
      computeCamera();
   }

   /**
    * returns the current target look-at point of the camera
    */
   public double[] getCameraAim() {
      double cp[] = new double[3];
      System.arraycopy(cameraAim, 0, cp, 0, 3);
      return cp;
   }

   /**
    * Sets the cameraUp vector (must be a unit vector). ( requires the
    * {@link manualCameraControl}flag to be turned on).
    * 
    * @param px
    * @param py
    * @param pz
    */
   public void setCameraUp(double px, double py, double pz) {
      cameraUp[0] = px;
      cameraUp[1] = py;
      cameraUp[2] = pz;
      computeCamera();
   }

   /**
    * returns the current up vector of the camera
    */
   public double[] getCameraUp() {
      double cp[] = new double[3];
      System.arraycopy(cameraUp, 0, cp, 0, 3);
      return cp;
   }

   /**
    * Sets the distance of the clipping plane in front of the camera lens.
    * @param e   the actual disance
    */
   public void setClippingPlaneEpsilon(double e) {
      epsilonPlane = e;
   }

   /**
    * Returns the distance of the clipping plane from the camera lens.
    * @returns the distance of the clipping plane from the camera lens.
    */
   public double getClippingPlaneEpsilon() {
      return epsilonPlane;
   }

   /**
    * Render the entire world for this frame.
    */
   public synchronized void render() {

      computeCamera(); // UPDATE CAMERA MATRIX

      a1[0] = camera.get(0, 0);
      a1[1] = camera.get(1, 0);
      a1[2] = camera.get(2, 0);

      //inverseCamera.invert(camera);

      if (isAnaglyph) {
         anaglyphEye = 0;
         if (isOutline)
            isAnaglyph = false;
         refresh();
         clearScreen();
         isAnaglyph = true;
         cX = -.1 * FL;
         renderWorld();
         if (isOutline)
            convertToOutline();

         anaglyphEye = 1;
         cX = .1 * FL;
         refresh();
         clearScreen();
         renderWorld();
         if (isOutline)
            convertToOutline();
      } else {
         clearScreen(); // BLANK OUT RESULTS FROM PREVIOUS FRAME
         renderWorld(); // RENDER EVERYTHING IN SCENE
         if (isOutline)
            convertToOutline();
      }

   }

   void convertToOutline() {
      int zn, z0, zp, dz1, dz2;
      for (int y = Math.max(1, TOP); y < Math.min(BOTTOM, H - 1); y++)
         for (int x = Math.max(1, LEFT); x < Math.min(RIGHT, W - 1); x++) {
            int i = xy2i(x, y);

            zn = zbuffer[i - 1];
            z0 = zbuffer[i];
            zp = zbuffer[i + 1];
            dz1 = Math.abs(z0 - zn);
            dz2 = Math.abs(zp - z0);
            if (dz1 > 20 * dz2 || dz2 > 20 * dz1)
               if (isAnaglyph)
                  pack(pix, i, 0, 0, 0);
               else
                  pix[i] = black;
            else {
               boolean isEdge = edge(pix[i], pix[i + 1]) + edge(pix[i], pix[i + W]) > threshold;
               if (isAnaglyph) {
                  int c = isEdge ? 0 : 255 + (anaglyphEye == 0 ? getR(pix[i]) : getG(pix[i])) >> 1;
                  pack(pix, i, c, c, c);
               } else
                  pix[i] = isEdge ? black : whiten(pix[i]);
            }
         }
   }

   int whiten(int packed) {
      return packed == white ? white : 0xff000000 | ((0xfefefe & packed) >> 1) + 0x7f7f7f;
   }

   /**
    * Returns the outline threshold parameter for sketch-like (artistic)
    * rendition of the scene.
    */
   public double getOutline() {
      return outline_t;
   }

   /**
    * Thresholds t to produce a sketch-like (artistic) rendition of the scene.
    * 
    * @param t
    *            outline threshold
    */
   public void outline(double t) {
      outline_t = t;
      isOutline = (t > 0);
      if (isOutline)
         threshold = (int) (256 * t * t);
      refresh();
   }

   protected int edge(int p, int q) {
      int dr = getR(p) - getR(q), dg = getG(p) - getG(q), db = getB(p) - getB(q);
      if (isAnaglyph)
         switch (anaglyphEye) {
            case 0:
               return 3 * dr * dr;
            case 1:
               return 3 * dg * dg;
         }
      return dr * dr + dg * dg + db * db;
   }

   /**
    * Force a refresh of the entire window.
    */
   public synchronized void refresh() {
      LEFT = 0;
      TOP = 0;
      RIGHT = W;
      BOTTOM = H;
   }

   //------------------- PRIVATE METHODS ---------------------

   // CONVERT PIXEL (X,Y) TO INDEX INTO pix ARRAY

   protected int xy2i(int x, int y) {
      return y * W + x;
   }

   // CONVERT FLOATING POINT TO 0..255 INTEGER

   protected int f2i(double t) {
      return (int) (255 * t) & 255;
   }

   public boolean isAnaglyph = false;

   int anaglyphEye = 0;

   // PACK RGB INTO ONE WORD

   protected void pack(int px[], int i, int rgb) {
      pack(px, i, getR(rgb), getG(rgb), getB(rgb));
   }

   /*
    * protected void pack(int px[], int i, int r, int g, int b) { if
    * (isAnaglyph) { int c = r/4 + g/2 + b/4 & 255; switch (anaglyphEye) { case
    * 0: px[i] = 0xff000000 | c < < 16 | px[i] & 0x0000ffff ; break; case 1:
    * px[i] = 0xff000000 | px[i] & 0x00ff0000 | c < < 8 | c ; break; } } else
    * px[i] = r < < 16 | g < < 8 | b | 0xff000000; }
    */
   protected static int pack(int r, int g, int b) {
      return r << 16 | g << 8 | b | 0xff000000;
   }

   // UNPACK RGB OUT OF ONE WORD

   protected static void unpack(int rgb[], int packed) {
      rgb[0] = (packed >> 16) & 255;
      rgb[1] = (packed >> 8) & 255;
      rgb[2] = (packed) & 255;
   }

   protected static int getR(int packed) {
      return (packed >> 16) & 255;
   }

   protected static int getG(int packed) {
      return (packed >> 8) & 255;
   }

   protected static int getB(int packed) {
      return (packed) & 255;
   }

   // FILL A RECTANGLE WITH A COLOR

   protected void fill(int x, int y, int w, int h, int packed) {
      for (int Y = y; Y < y + h; Y++) {
         int i = xy2i(x, Y);
         if (isAnaglyph) {
            int r = getR(packed), g = getG(packed), b = getB(packed);
            for (int X = x; X < x + w; X++)
               pack(pix, i++, r, g, b);
         } else
            for (int X = x; X < x + w; X++)
               pix[i++] = packed;
      }
   }

   // CLEAR DAMAGED PART OF SCREEN

   protected void clearScreen() {
      if (TOP == -1) {
         LEFT = 0;
         RIGHT = W - 1;
         TOP = 0;
         BOTTOM = H - 1;
      }

      int L = LEFT = Math.max(LEFT, 0);
      int R = RIGHT = Math.min(RIGHT, W - 1);
      int T = TOP = Math.max(TOP, 0);
      int B = BOTTOM = Math.min(BOTTOM, H - 1);

      fill(L, T, 1 + R - L, 1 + B - T, isOutline ? white : bgColor);

      if (zbuffer == null || gzbuffer == null) {
         zbuffer = new int[W * H];
         gzbuffer = new Geometry[W * H];
      }
      for (int y = T; y <= B; y++) {
         int i = xy2i(L, y);
         for (int x = L; x <= R; x++) {
            gzbuffer[i] = null;
            zbuffer[i++] = -zHuge;
         }
      }

      LEFT = W + 1;
      RIGHT = -1;
      TOP = H + 1;
      BOTTOM = -1;
   }

   // CALLED IN RENDER THREAD TO RECOMPUTE CAMERA VALUE

   protected synchronized void computeCamera() {

      if (this.manualCameraControl) {
         double T[] = new double[3];

         T[0] = cameraAim[0] - cameraPos[0];
         T[1] = cameraAim[1] - cameraPos[1];
         T[2] = cameraAim[2] - cameraPos[2];

         Vec.normalize(T);

         double vz = Vec.dot(T, cameraUp);
         double vy = Math.sqrt(1 - vz * vz);

         Matrix.identity(camtmp);

         double crossUT[] = new double[3];
         double dotTU = Vec.dot(T, cameraUp);

         Vec.cross(cameraUp, T, crossUT);

         for (int i = 0; i < 3; i++) {
            camtmp.set(2, i, T[i]);
            camtmp.set(1, i, (cameraUp[i] - dotTU * T[i]) / vy);
            camtmp.set(0, i, crossUT[i] / vy);
         }

         camtmp.translate(cameraPos[0], cameraPos[1], cameraPos[2]);

         camera.copy(camtmp);
      } else {
         if (theta == 0 && phi == 0)
            return;

         changeCamera(theta, phi);
         theta = phi = 0; // WE'VE ACCOUNTED FOR ROTATION, SO RESET ANGLES.
      }
   }

   protected synchronized void changeCamera(double theta, double phi) {

      Matrix.identity(camtmp);
      camtmp.rotateY(theta);
      camera.postMultiply(camtmp);

      Matrix.identity(camtmp);
      camtmp.rotateX(phi);
      camera.postMultiply(camtmp);

      if (isHeadsUp) {
         Matrix.identity(camtmp);
         camtmp.rotateZ(.3 * Math.atan2(camera.get(0, 1), camera.get(1, 1)));
         camera.postMultiply(camtmp);
      }
   }

   // RENDER EVERYTHING IN SCENE

   protected void renderWorld() {

      // ALLOCATE SPACE FOR TRANSPARENT OBJECTS

      if (updateTransparency)
         tS = new Geometry[countT(world)];

      // RENDER OPAQUE OBJECTS

      nt = 0;
      renderT = false;
      world.globalMatrix.copy(world.matrix);

      render(world, camera);

      // RENDER TRANSPARENT OBJECTS

      renderT = true;
      for (int i = 0; i < nt; i++)
         renderGeometry(tS[i]);
   }

   // COUNT HOW MANY TRANSPARENT OBJECTS THERE ARE

   protected double transparencyOf(Geometry s) {
      return s.material == null ? 0 : s.material.transparency;
   }

   protected int countT(Geometry s) {
      int n = transparencyOf(s) == 0 ? 0 : 1;
      if (s.child != null)
         for (int i = 0; i < s.child.length && s.child[i] != null; i++)
            n += countT(s.child[i]);
      return n;
   }

   // RENDER ONE OBJECT FOR THIS FRAME

   protected void render(Geometry s, Matrix camera) {

      if (s.child != null) {

         Matrix cam = new Matrix();
         cam.copy(camera); // CAMERA MAY MOVE BEFORE CHILD RENDERS

         Matrix mat = new Matrix();
         mat.copy(s.globalMatrix);

         for (int i = 0; i < s.child.length && s.child[i] != null; i++) {
            s.child[i].globalMatrix.copy(mat);
            s.child[i].globalMatrix.preMultiply(s.child[i].matrix);
            render(s.child[i], cam);
         }
      }
      if (s.faces != null && s.vertices != null)
         renderGeometry(s);
   }

   // RENDER ONE OBJECT, TRANSFORMED BY THIS MATRIX

   protected int t[][]; // temporary array that holds transformed vertices and

   // normals

   protected double t1[][]; // temp array of vertices transformed but without

   // the perspective transform

   protected double ti[] = new double[6]; // temporary point for quick

   // processing.

   protected double epsilonPlane = 1.5;
//   protected double epsilonPlane = .0001; // KPKP

   //distance of the clipping plane from the camera lens

   //temporary transformed vertices in the device plane created by clipping
   protected int tv1[] = new int[6];

   protected int tv2[] = new int[6];

   //temporary computed vertex
   protected int pixel[] = new int[8];

   protected int pixelL[] = new int[8];

   protected int dpixelL[] = new int[8];

   protected int pixelR[] = new int[8];

   protected int dpixelR[] = new int[8];

   protected int[] dpixel = new int[8];

   protected void renderGeometry(Geometry s) {

      matrix.copy(s.globalMatrix);
      matrix.postMultiply(camera);

      if (!renderT && transparencyOf(s) != 0) {
         tS[nt++] = s;
         return;
      }

      if (t == null || t.length < s.vertices.length || t[0].length < s.verticedepth)
         t = new int[s.vertices.length][s.verticedepth];

      if (t1 == null || t1.length < s.vertices.length || t1[0].length < s.verticedepth)
         t1 = new double[s.vertices.length][s.verticedepth];

      if (ti == null || ti.length < s.verticedepth)
         ti = new double[s.verticedepth];

      if (tv1 == null || tv1.length < s.verticedepth)
         tv1 = new int[s.verticedepth];

      if (tv2 == null || tv2.length < s.verticedepth)
         tv2 = new int[s.verticedepth];

      if (pixel == null || pixel.length < s.verticedepth)
         pixel = new int[s.verticedepth];

      if (pixelL == null || pixelL.length < s.verticedepth)
         pixelL = new int[s.verticedepth];

      if (pixelR == null || pixelR.length < s.verticedepth)
         pixelR = new int[s.verticedepth];

      if (pixelL == null || pixelL.length < s.verticedepth)
         dpixelL = new int[s.verticedepth];

      if (pixelR == null || pixelR.length < s.verticedepth)
         dpixelR = new int[s.verticedepth];

      if (c == null || c.length < s.verticedepth)
         c = new int[s.verticedepth];

      if (dpixel == null || dpixel.length < s.verticedepth)
         dpixel = new int[s.verticedepth];

      double r;
      normat.copy(matrix);
      double nort;
      for (int j = 0; j < 3; j++) {
         r = 0;
         for (int i = 0; i < 3; i++) {
            nort = normat.get(i, j);
            r += nort * nort;
         }
         for (int i = 0; i < 3; i++)
            normat.set(i, j, normat.get(i, j) / r);
      }

      transparency = transparencyOf(s);

      int m = s.getMeshRows();
      if (m >= 0)
         s.computeMeshNormals();
      s.modified = false;

      // RECTANGULAR MESH AT COARSE LEVEL OF DETAIL

      if (lod > 1 && m >= 40) {
         int M = m + 1, N = s.vertices.length / M;

         for (int J = 0; J <= N; J++)
            for (int I = 0; I <= M; I++) {
               int k = Math.min(J, N - 1) * M + Math.min(I, M - 1);
               transformVertex(matrix, s.vertices[k], k);
               t[k][3] = UNRENDERED;
            }

         for (int J = 0; J <= N - 1 - lod; J += lod)
            for (int I = 0; I <= M - 1 - lod; I += lod) {
               int a = J * M + I;
               int b = J * M + I + lod;
               int c = (J + lod) * M + I;
               int d = (J + lod) * M + I + lod;

               if (b % M >= M - lod)
                  b = (b / M + 1) * M - 1;
               if (d % M >= M - lod)
                  d = (d / M + 1) * M - 1;

               if (c >= M * (N - lod))
                  c = M * (N - 1) + (c % M);
               if (d >= M * (N - lod))
                  d = M * (N - 1) + (d % M);
               
               fillAndClipTriangle(s, a, b, c);
               fillAndClipTriangle(s, b, d, c);
            }
      }

      // ALL OTHER CASES

      else {
         for (int k = 0; k < s.vertices.length; k++) {
            transformVertex(matrix, s.vertices[k], k);
            t[k][3] = UNRENDERED;
         }
         for (int j = 0; j < s.faces.length; j++) {
            int f[] = s.faces[j];
            if (f != null)
               for (int k = 1; k < f.length - 1; k++) {
//               	System.out.println("CALLED FILL AND CLIP TRIANGLE *** ");
                  fillAndClipTriangle(s, f[0], f[k], f[k + 1]);
                  //FIXME
                  
               }
         }
      }
   }

   /**
    * When set true, only the wireframe structure of the objects is displayed
    * using appropriate colors.
    */
   public boolean seeMesh = false;

   // CENTER OF PROJECTION

   public void setCx(double x) {
      cX = x;
   }

   public void setCy(double y) {
      cY = y;
   }

   public double getCx() {
      return cX;
   }

   public double getCy() {
      return cY;
   }

   protected double cX = 0.0, cY = 0.0;

   public void projectPoint(double vp[]) {
      vp[2] = 1 / (FL - vp[2]);
      vp[0] = W / 2 + W * (cX / FL + vp[2] * (vp[0] - cX)) / FOV;
      vp[1] = H / 2 - W * (cY / FL + vp[2] * (vp[1] - cY)) / FOV;
    
   }

   protected void transformVertex(Matrix matrix, double vt[], int i) {

      xf(matrix, vt[0], vt[1], vt[2], 1, ti);
      xf(matrix, vt[0], vt[1], vt[2], 1, t1[i]);

      projectPoint(ti);
      
      
      double pz = ti[2];
      ti[2] = ti[2] * (1 << 31 - NB);
      for (int j = 0; j < 3; j++)
         t[i][j] = (int) ti[j] << NB;

      for (int j = 6; j < vt.length; j++) {
         t1[i][j] = vt[j];
         ti[j] = vt[j];
         t[i][j] = (int) (vt[j] * pz * (1 << 31 - NB));
      }
      
   }

   protected void fillAndClipTriangle(Geometry s, int iA, int iB, int iC) {
      int clipping = 0;

      if (t1[iA][2] + epsilonPlane > FL)
         clipping += 1;
      if (t1[iB][2] + epsilonPlane > FL)
         clipping += 2;
      if (t1[iC][2] + epsilonPlane > FL)
         clipping += 4;
      
//      if (clipping != 0 ) {
//      	System.out.println("clipping case : " + clipping);      	
//      }
      
      switch (clipping) {
         case (0):
            //render entire triangle
            fillTriangle(s, iA, iB, iC);         	
            break;
         case (7):
            //entire triangle clipped
            return;
         case (1):
            clip1Vert(s, iA, iB, iC);
            break;
         case (2):
            clip1Vert(s, iB, iC, iA);
            break;
         case (4):
            clip1Vert(s, iC, iA, iB);
            break;
         case (3):
            clip2Vert(s, iA, iB, iC);
            break;
         case (5):
            clip2Vert(s, iC, iA, iB);
            break;
         case (6):
            clip2Vert(s, iB, iC, iA);
            break;
      }

   }

   protected void clip1Vert(Geometry s, int clip, int b, int c) {

      double[] n1 = new double[s.verticedepth];
      double[] n2 = new double[s.verticedepth];

      clip(n1, t1[clip], t1[b]);
      clip(n2, t1[clip], t1[c]);

      perspective(n1, tv1);
      perspective(n2, tv2);

      n1 = s.vertices[clip];
      renderVertex(s, n1, tv1);

      n2 = s.vertices[clip];
      renderVertex(s, n2, tv2);

      renderVertex(s, b);
      renderVertex(s, c);

      
      fillTriangle(s, tv1, t[b], t[c]);
      fillTriangle(s, tv2, tv1, t[c]);  
   }

   protected void clip2Vert(Geometry s, int clip1, int clip2, int c) {
      // 1. compute vertices
      // 2. do perspective transform
      // 3. render with material
      // 4. fill triangle

      double[] n1 = new double[s.verticedepth];
      double[] n2 = new double[s.verticedepth];

      clip(n1, t1[clip1], t1[c]);
      clip(n2, t1[clip2], t1[c]);

      perspective(n1, tv1);
      perspective(n2, tv2);

      n1 = s.vertices[clip1];
      renderVertex(s, n1, tv1);

      n2 = s.vertices[clip2];
      renderVertex(s, n2, tv2);

      renderVertex(s, c);

      fillTriangle(s, tv1, tv2, t[c]);
      
   }

   protected static double lerp(double t, double a, double b) {
      return a + t * (b - a);
   }

   protected void clip(double[] n, double[] a, double[] b) {
      n[2] = FL - epsilonPlane;

      for (int i = 0; i < 2; i++) {
         n[i] = ((b[i] - a[i]) / (b[2] - a[2])) * (FL - epsilonPlane - a[2]) + a[i];
      }
      
      if (a.length > 6) {
         double t = (b[2] - n[2]) / (b[2] - a[2]);
         for (int i = 6; i < a.length; i++) {
            n[i] = b[i] - t * (b[i] - a[i]);
            n[i] = n[i];
         }
      }
   }

   protected void perspective(double v[], int tv[]) {

      double pz = 1 / (FL - v[2]);
      ti[0] = W / 2 + W * (cX / FL + pz * (v[0] - cX)) / FOV;
      ti[1] = H / 2 - W * (cY / FL + pz * (v[1] - cY)) / FOV;
      ti[2] = pz * (1 << 31 - NB);

      for (int j = 0; j < 3; j++)
         tv[j] = (int) ti[j] << NB;

      for (int j = 6; j < v.length; j++) {
         tv[j] = (int) (v[j] * pz * (1 << 31 - NB));
      }

   }

   protected void renderVertex(Geometry s, int i) {

      if (t[i][3] != UNRENDERED)
         return;
      double v[] = s.vertices[i];
      double nn[] = normal;

      xf(normat, v[3], v[4], v[5], 0, nn);
      Vec.normalize(nn);
      for (int j = 0; j < 3; j++)
         ti[j + 3] = nn[j];

      renderVertex(ti, s.material);

      for (int j = 3; j < 6; j++)
         t[i][j] = (int) ti[j] << NB;

      //adding the u v
      /*
       * for (int j=6; j < s.verticedepth; j++) { ti[j] = s.vertices[i][j];
       * double mt = (ti[j] * (1 < < 31 - NB)); t[i][j] = (int) mt; }
       */
   }

   protected void renderVertex(Geometry s, double[] v, int[] tv) {

      double nn[] = normal;

      xf(normat, v[3], v[4], v[5], 0, nn);
      Vec.normalize(nn);
      for (int j = 0; j < 3; j++)
         ti[j + 3] = nn[j];

      renderVertex(ti, s.material);

      for (int j = 3; j < 6; j++)
         tv[j] = (int) ti[j] << NB;

      /*
       * for (int j=6; j < s.verticedepth; j++) { ti[j] = v[j]; double mt =
       * (ti[j] * (1 < < 31 -NB)); tv[j] = (int) mt; }
       */
   }

   protected void fillTriangle(Geometry s, int iA, int iB, int iC) {

      int A[] = t[iA], B[] = t[iB], C[] = t[iC];

      renderVertex(s, iA);
      renderVertex(s, iB);
      renderVertex(s, iC);

      fillTriangle(s, A, B, C);
   }
   
   private void printArray(int a[]) {
   		for (int i=0; i<a.length; i++)
   			System.out.print((a[i] >> NB) + " " );
   		System.out.println();
   }

   // ZBUFFER A TRIANGLE, INTERPOLATING RED,GREEN,BLUE
   protected void fillTriangle(Geometry s, int[] A, int[] B, int[] C) {

      if (same(A, B) || same(B, C)) {
         return;
      }

      if (!s.isDoubleSided() && backfacing(A, B, C)) {
      	return;      
      }

      int y0 = A[1] < B[1] ? (A[1] < C[1] ? 0 : 2) : (B[1] < C[1] ? 1 : 2);
      int y2 = A[1] > B[1] ? (A[1] > C[1] ? 0 : 2) : (B[1] > C[1] ? 1 : 2);
      int y1 = 3 - (y0 + y2);
      if (y0 == y2){
      	return;
      }
      
      a = y0 == 0 ? A : y0 == 1 ? B : C; // FIRST VERTEX IN Y SCAN
      b = y1 == 0 ? A : y1 == 1 ? B : C; // MIDDLE VERTEX IN Y SCAN
      d = y2 == 0 ? A : y2 == 1 ? B : C; // LAST VERTEX IN Y SCAN

      LEFT = Math.min(LEFT, Math.min(Math.min(A[0], B[0]), C[0]) >> NB);
      RIGHT = Math.max(RIGHT, Math.max(Math.max(A[0], B[0]), C[0]) >> NB);
      TOP = Math.min(TOP, Math.min(Math.min(A[1], B[1]), C[1]) >> NB);
      BOTTOM = Math.max(BOTTOM, Math.max(Math.max(A[1], B[1]), C[1]) >> NB);

      //doing the lerp here
      double t = (double) (b[1] - a[1]) / (d[1] - a[1]);
      for (int i = 0; i < s.verticedepth; i++)
         c[i] = (int) (a[i] + t * (d[i] - a[i]));

      // SPLIT TOP-TO-BOTTOM EDGE

      // ONLY RENDER FRONT FACING (COUNTER-CLOCKWISE) TRIANGLES

      //if ((y1 == (y0+1) % 3) == c[0] > b[0] ) {
      if (true) {
         if (b[0] < c[0]) {
            fillTrapezoid(a, a, b, c, s);
            fillTrapezoid(b, c, d, d, s);
         }
         if (b[0] > c[0]) {
            fillTrapezoid(a, a, c, b, s);
            fillTrapezoid(c, b, d, d, s);
         }
      }
   }

   protected boolean same(int A[], int B[]) {
      return same(A[0], B[0]) && same(A[1], B[1]) && same(A[2], B[2]);
   }

   protected boolean same(int A, int B) {
      return Math.abs(A - B) < (1 << NB - 8);
   }

   protected boolean backfacing(int A[], int B[], int C[]) {
//   		System.out.println("ab : " + areaUnder(A, B) + "  bc : " + areaUnder(B, C)+  "  ca : " +  areaUnder(C, A) );
      return areaUnder(A, B) + areaUnder(B, C) + areaUnder(C, A) < 0;
   }

   protected int areaUnder(int A[], int B[]) {
      return (B[0] - A[0] >> NB) * (B[1] + A[1] >> NB);
   }

   /*
    * THIS IS THE INNER-LOOP RENDERING ROUTINE. IT'S THE COMPUTATIONAL
    * BOTTLENECK, BECAUSE IT NEEDS TO PROCEED PIXEL BY PIXEL.
    * 
    * BECAUSE JAVA ENFORCES BOUNDS CHECKING ON EVERY ARRAY ACCESS (WHICH SLOWS
    * THINGS SIGNIFICANTLY), I'VE BROKEN THINGS OUT HERE INTO INDIVIDUAL
    * VARIABLES WHEREVER I COULD. -KEN
    */

   protected void fillTrapezoid(int A[], int B[], int C[], int D[], Geometry s) {

      int zb[] = zbuffer, px[] = pix; // LOCAL ARRAYS CAN BE FASTER

      int yLo = A[1] >> NB;
      int yHi = C[1] >> NB;
      if (yHi < 0 || yLo >= H)
         return;

      int deltaY = yHi - yLo;
      if (deltaY <= 0)
         return;

      int xL = A[0];
      int zL = A[2];
      int rL = A[3];
      int gL = A[4];
      int bL = A[5];

      for (int i = 6; i < s.verticedepth; i++)
         pixelL[i] = A[i];

      int dxL = (C[0] - A[0]) / deltaY;
      int dzL = (C[2] - A[2]) / deltaY;
      int drL = (C[3] - A[3]) / deltaY;
      int dgL = (C[4] - A[4]) / deltaY;
      int dbL = (C[5] - A[5]) / deltaY;

      for (int i = 6; i < s.verticedepth; i++)
         dpixelL[i] = (C[i] - A[i]) / deltaY;

      int xR = B[0];
      int zR = B[2];
      int rR = B[3];
      int gR = B[4];
      int bR = B[5];

      for (int i = 6; i < s.verticedepth; i++)
         pixelR[i] = B[i];

      int dxR = (D[0] - B[0]) / deltaY;
      int dzR = (D[2] - B[2]) / deltaY;
      int drR = (D[3] - B[3]) / deltaY;
      int dgR = (D[4] - B[4]) / deltaY;
      int dbR = (D[5] - B[5]) / deltaY;

      for (int i = 6; i < s.verticedepth; i++)
         dpixelR[i] = (D[i] - B[i]) / deltaY;

      int ixL, ixR, deltaX, z, r, g, b, dz = 0, dr = 0, dg = 0, db = 0;

      if (s.verticedepth > 6)
         dpixel = new int[s.verticedepth];

      boolean isOpaque = (transparency == 0);
      int opacity = (int) ((1 - Math.abs(transparency)) * (1 << NB));
      int r0, g0, b0, packed;

      if (yLo < 0) {
         xL -= dxL * yLo;
         zL -= dzL * yLo;
         rL -= drL * yLo;
         gL -= dgL * yLo;
         bL -= dbL * yLo;

         if (s.verticedepth > 6) {
            for (int i = 6; i < s.verticedepth; i++)
               pixelL[i] -= dpixelL[i] * yLo;

         }

         xR -= dxR * yLo;
         zR -= dzR * yLo;
         rR -= drR * yLo;
         gR -= dgR * yLo;
         bR -= dbR * yLo;

         for (int i = 6; i < s.verticedepth; i++)
            pixelR[i] -= dpixelR[i] * yLo;

         yLo = 0;
      }
      yHi = Math.min(yHi, H);

      for (int y = yLo; y < yHi; y++) {

         ixL = xL >> NB;
         ixR = xR >> NB;

         deltaX = ixR - ixL;
         z = zL;
         r = rL;
         g = gL;
         b = bL;

         if (s.verticedepth > 6) {
            pixel[1] = y;
            pixel[2] = z;
            pixel[3] = r;
            pixel[4] = g;
            pixel[5] = b;
            for (int i = 6; i < s.verticedepth; i++)
               pixel[i] = pixelL[i];
         }

         if (deltaX > 0) {
            dz = (zR - zL) / deltaX;
            dr = (rR - rL) / deltaX;
            dg = (gR - gL) / deltaX;
            db = (bR - bL) / deltaX;
            for (int i = 6; i < s.verticedepth; i++)
               dpixel[i] = (pixelR[i] - pixelL[i]) / deltaX;
         }

         if (ixL < 0) {
            z -= dz * ixL;
            r -= dr * ixL;
            g -= dg * ixL;
            b -= db * ixL;

            if (s.verticedepth > 6) {
               pixel[1] = y;
               pixel[2] = z;
               pixel[3] = r;
               pixel[4] = g;
               pixel[5] = b;
               for (int i = 6; i < s.verticedepth; i++)
                  pixel[i] -= dpixel[i] * ixL;
            }
            ixL = 0;
         }

         ixR = Math.min(ixR, W);

         int i = xy2i(ixL, y);
         int op = opacity;
         for (int ix = ixL; ix < ixR; ix++) {
            if (z > zb[i]) {
               pixel[0] = ix;
               if (isOpaque) {
                  if (s.verticedepth > 6)
                     px[i] = s.material.computePixel(pixel, deltaX, deltaY, NB);
                  else
                     pack(px, i, r >> NB, g >> NB, b >> NB);

               } else {
                  packed = px[i];
                  r0 = (packed >> 16) & 255;
                  g0 = (packed >> 8) & 255;
                  b0 = (packed) & 255;
                  op = opacity;
                  if (s.verticedepth > 6) {
                     int rc, gc, bc;
                     int c = s.material.computePixel(pixel, deltaX, deltaY, NB);
                     rc = (c >> 16) & 255;
                     gc = (c >> 8) & 255;
                     bc = (c) & 255;
                     pack(px, i, r0 + (op * ((rc >> NB) - r0) >> NB), g0 + (op * ((gc >> NB) - g0) >> NB), b0
                           + (op * ((bc >> NB) - b0) >> NB));
                  } else
                     pack(px, i, r0 + (op * ((r >> NB) - r0) >> NB), g0 + (op * ((g >> NB) - g0) >> NB), b0
                           + (op * ((b >> NB) - b0) >> NB));
               }
               if (showMesh)
                  if (ix == ixL || ix == ixR - 1)
                     pack(px, i, 0, 0, 0);
               zb[i] = z;
               if (bufferg)
                  gzbuffer[i] = s;
               if (seeMesh)
                  break;
            }
            z += dz;
            r += dr;
            g += dg;
            b += db;

            if (s.verticedepth > 6) {
               pixel[0] = ix;
               pixel[2] = z;
               pixel[3] = r;
               pixel[4] = g;
               pixel[5] = b;
            }

            for (int k = 6; k < s.verticedepth; k++)
               pixel[k] += dpixel[k];
            i++;
         }

         xL += dxL;
         zL += dzL;
         rL += drL;
         gL += dgL;
         bL += dbL;

         for (int k = 6; k < s.verticedepth; k++)
            pixelL[k] += dpixelL[k];

         xR += dxR;
         zR += dzR;
         rR += drR;
         gR += dgR;
         bR += dbR;

         for (int k = 6; k < s.verticedepth; k++)
            pixelR[k] += dpixelR[k];
      }
   }

   // TRANSFORM ONE VERTEX OR NORMAL BY A MATRIX

   public void xf(Matrix m, double x, double y, double z, double w, double v[]) {
      if (w == 0)
         for (int j = 0; j < 3; j++)
            v[j] = m.get(j, 0) * x + m.get(j, 1) * y + m.get(j, 2) * z;
      else
         for (int j = 0; j < 3; j++)
            v[j] = m.get(j, 0) * x + m.get(j, 1) * y + m.get(j, 2) * z + m.get(j, 3);
   }

   // DO LIGHTING AND SHADING FOR ONE VERTEX

   // I WILL BE ABLE TO GET SUBSTANTIAL SPEED-UP IF I CONVERT THIS ENTIRE
   // ROUTINE TO FIXED POINT, AND USE TABLE LOOKUP FOR SPECULAR POWER.

   // I ALSO NEED TO ADD IN EXTENDED LIGHT SOURCES (JUST LOWER THE POWER
   // WHEN DOING SHINY CALCULATION). -KEN

   protected static double v[] = { 0, 0, 0, 0, 0, 0};

   /**
    * Renders vertex i ( packed x,y,z) with material m.
    * 
    * @param i
    *            vertex to be rendered
    * @param m
    *            material properties to be applied to the vertex
    */
   public static void renderVertex(int i, Material m) {

      if (!tableMode || !m.tableMode)
         return;

      int izx = i >> m.resP;
      int iz = izx >> m.resP;
      int ix = izx & m.res - 1;
      int iy = i & m.res - 1;

      int n = 1 << m.resP - 1;

      double x = (double) (ix - n) / (n - 1);
      double y = -(double) (iy - n) / (n - 1);
      double z = (iz == 0 ? 1 : -1) * Math.sqrt(1 - x * x - y * y);
      m.v[3] = x;
      m.v[4] = y;
      m.v[5] = z;
      renderVertex(ix, iy, iz, x, y, z, m.v, m);
   }

   /**
    * Renders vertex v with material m, if table mode is enabled the data is
    * just looked up in the material's table, otherwise it is computed.
    * 
    * @param v
    *            vertex x,y,z and the r,g,b values for it.
    * @param m
    *            material with which to render the vertex.
    */
   public void renderVertex(double v[], Material m) {
      if (m == null) {
         v[3] = v[4] = v[5] = 0;
         return;
      }
      double x = v[3], y = v[4], z = v[5];
      int ix = 0, iy = 0, iz = 0;

      if (m.anisotropic) {
         double a2[] = { x, y, z};
         double a3[] = { 0, 0, 0};
         Vec.cross(a1, a2, a3);
         Vec.normalize(a3);
         x = a3[0];
         y = a3[1];
         z = a3[2];
      }

      // NORMAL LOOKUP MAP

      if (tableMode && m.tableMode) {
         int n = 1 << m.resP - 1;
         ix = n + (int) (x * (n - 1));
         iy = n - (int) (y * (n - 1));
         iz = (z > 0 ? 0 : 1);
         int packed = m.getTable(ix, iy, iz);
         if (packed != 0) {
            v[3] = (packed >> 16) & 255;
            ;
            v[4] = (packed >> 8) & 255;
            ;
            v[5] = (packed) & 255;
            ;
            return;
         }
         x = (double) (ix - n) / (n - 1);
         y = -(double) (iy - n) / (n - 1);
         z = (iz == 0 ? 1 : -1) * Math.sqrt(1 - x * x - y * y);
      }
      renderVertex(ix, iy, iz, x, y, z, v, m);
   }

   protected synchronized static void renderVertex(int ix, int iy, int iz, double x, double y, double z, double v[],
         Material m) {

      if (m == null)
         return;

      double rD = 0, gD = 0, bD = 0, t, L[];
      double rS = 0, gS = 0, bS = 0;
      double D[] = m.diffuse, S[] = m.specular, A[] = m.ambient;
      double red, grn, blu;

      boolean isSpecular = S[0] != 0 || S[1] != 0 || S[2] != 0;

      for (int i = 0; i < nLights; i++) {

         L = light[i];
         t = L[0] * x + L[1] * y + L[2] * z;
         if (D[3] != 1)
            t = Math.pow(.5 + .5 * t, D[3]) * 2 - 1;
         if (m.anisotropic)
            t = Math.sqrt(1 - t * t);
         t = Math.max(0, t);
         if (m.noiseA != 0)
            t *= noiseTexture(m, x, y, z);
         rD += L[3] * t;
         gD += L[4] * t;
         bD += L[5] * t;

         if (isSpecular) {
            if (m.anisotropic) {
               double a1[] = { L[0], L[1], L[2] + 2};
               Vec.normalize(a1);
               double a2[] = { x, y, z};
               t = Vec.dot(a1, a2);
               t = Math.sqrt(1 - t * t);
               double dp = L[0] * v[3] + L[1] * v[4] + L[2] * v[5];
               t = Math.max(0, dp * t);
            } else
               t = computeFastHilite(x, y, z, L);
            if (t > 0) {
               t = Math.pow(t, S[3]);
               //t = computeHilite(0,0,1, x,y,z, L);
               if (m.noiseA != 0) {
                  double d = 2 * (x * v[3] + y * v[4] + z * v[5]);
                  double rx = d * v[3] - x;
                  double ry = d * v[4] - y;
                  double rz = d * v[5] - z;
                  t *= noiseTexture(m, rx, ry, rz);
               }
               rS += L[3] * t;
               gS += L[4] * t;
               bS += L[5] * t;
            }
         }
      }

      red = rD * D[0] + rS * S[0] + A[0];
      grn = gD * D[1] + gS * S[1] + A[1];
      blu = bD * D[2] + bS * S[2] + A[2];

      v[3] = Math.max(0, Math.min(255, 255 * red));
      v[4] = Math.max(0, Math.min(255, 255 * grn));
      v[5] = Math.max(0, Math.min(255, 255 * blu));

      if (tableMode && m.tableMode)
         m.setTable(ix, iy, iz, pack((int) v[3], (int) v[4], (int) v[5]));
   }

   /**
    * Returns the Geometry of the frontmost object at the point (x, y) in the
    * image (like a z-buffer value of geometries).
    * 
    * @param x
    *            x coordinate in the image
    * @param y
    *            y coordinate in the image
    * @return the geometry of the foremost object at that location
    */
   public synchronized Geometry getGeometry(int x, int y) {
      if (x < 0 || x >= W || y < 0 || y >= H)
         return null;

      if (gzbuffer == null) // buffer not initialized yet. Yes, this happens.
         return null;

      if (y * W + x < gzbuffer.length)
         return gzbuffer[y * W + x];
      return null;
   }

   public synchronized boolean getPoint(int ix, int iy, double xyz[]) {
      if (ix < 0 || ix >= W || iy < 0 || iy >= H)
         return false;

      if (zbuffer == null) { // this happens because getPoint might get called
         // before initialization is done (this happened when I had a thread
         // checking mouse location and calling getPoint with that location
         // every time the mouse moved)
         return false;
      }

      int zb = zbuffer[iy * W + ix];
      if (zb < 0)
         return false;
      Matrix cameraInv = new Matrix();
      double pz = (double) (zb >> NB) / (1 << 31 - NB);
      double x = (FOV * (ix - W / 2) / W - cX / FL) / pz + cX;
      double y = -(FOV * (iy - H / 2) / W + cY / FL) / pz + cY;
      double z = FL - 1 / pz;
      cameraInv.invert(camera);
      xf(cameraInv, x, y, z, 1, xyz);
      return true;
   }

   /**
    * Given z value of point to determine where 2d points ix,iy convert to. (so
    * this is not Geometry based)
    * 
    * [aw]
    */

   public synchronized void getPoint(int ix, int iy, double z, double xyz[]) {

      Matrix cameraInv = new Matrix();

      double pz = 1 / (FL - z);
      double x = FOV / pz * (ix - W / 2) / W;
      double y = FOV / pz * (H / 2 - iy) / W;
      cameraInv.invert(camera);
      xf(cameraInv, x, y, z, 1, xyz);

   }

   /**
    * Puts in p_xy the pixel point that x, y, z corresponds to. Also transforms
    * xyz by camera (so becomes xyz that is actually seen)
    * 
    * [aw]
    */

   // synchronized just in case it should be.. I don't see why it might
   // need to be, but I don't see why getPoint would need to be either
   // and this does something similar to that...
   public synchronized void getPoint(double[] xyz, int[] p_xy) {

      // transform xyz based on camera

      xf(camera, xyz[0], xyz[1], xyz[2], 1, xyz);

      double pz = 1 / (FL - xyz[2]);

      p_xy[0] = (int) ((xyz[0] * W * pz) / FOV + W / 2);
      p_xy[1] = (int) (H / 2 - xyz[1] * pz * W / FOV);

   }

   protected static double noiseTexture(Material m, double x, double y, double z) {
      if (m.noiseA != 0)
         return 1 + m.noiseA * Noise.noise(m.noiseF * x, m.noiseF * y, m.noiseF * z);
      else
         return 1;
   }

   // COMPUTE DIRECTION OF SPECULAR REFLECTION, THEN DOT PRODUCT WITH LIGHT

   protected static double computeHilite(double x, double y, double z, double nx, double ny, double nz, double L[]) {
      double d = 2 * (x * nx + y * ny + z * nz);
      double rx = d * nx - x;
      double ry = d * ny - y;
      double rz = d * nz - z;
      return L[0] * rx + L[1] * ry + L[2] * rz;
   }

   // FASTER VERSION OF HILITE, WHICH ASSUMES CAMERA IS IN Z DIRECTION

   protected static double computeFastHilite(double nx, double ny, double nz, double L[]) {
      return 2 * nz * (L[0] * nx + L[1] * ny + L[2] * nz) - L[2];
   }

   //--- protected DATA FIELDS FOR RENDERER

   protected double FL = 10; // FOCAL LENGTH OF VIEW

   protected double FOV = 1; // FIELD OF VIEW

   protected Geometry world; // THE ROOT OF THE GEOMETRY TREE

   protected Widget rootMenu; // THE ROOT OF THE INTERACTION MENU

   protected int W, H; // THE RESOLUTION OF THE IMAGE

   protected double theta = 0, phi = 0; // VIEW ROTATION ANGLES

   protected int pix[]; // THE FRAME BUFFER

   protected int bgColor = pack(0, 0, 0); // BACKGROUND FILL COLOR

   protected final int zHuge = 1 << 31; // BIGGEST POSSIBLE ZBUFFER VALUE

   protected int TOP = -1, BOTTOM, LEFT, RIGHT;

   // PIXEL BOUNDS FOR DAMAGED IMAGE
   protected int zbuffer[] = null; // THE ZBUFFER

   protected Geometry gzbuffer[] = null; //THE GEOMETRY ZBUFFER

   protected Matrix camera = new Matrix(); // THE CAMERA MATRIX

   protected Matrix camtmp = new Matrix(); // CAMERA TEMP MATRIX

   protected Matrix matrix = new Matrix(); // TEMP MATRIX

   protected int nt = 0; // NUM. OF TRANSPARENT OBJECTS

   protected Geometry tS[] = null; // LIST OF TRANSPARENT OBJECTS

   protected boolean renderT = false; // IS THIS TRANSP RENDER PASS?

   protected double normal[] = new double[3]; // VERTEX NORMAL

   protected Matrix normat = new Matrix(); // NORMAL MATRIX XFORM

   protected double transparency; // TRANSPARENCY FOR CURRENT OBJECT

   protected final int NB = 14; // PRECISION FOR FIXED PT PIXEL OPS

   protected int NBPower = (int) Math.pow(2, this.NB);

   protected int a[], b[], c[] = new int[6], d[];

   // TEMPS FOR FILLING TRIANGLE
   protected double refl[] = new double[3]; // TEMP TO COMPUTE REFL VECTOR

   protected final int UNRENDERED = 1234567; // RENDERING PHASE

   protected static int nLights = 0; // NUM. OF LIGHT SOURCES DEFINED

   protected static double light[][] = new double[20][6]; // DATA FOR LIGHTS

   protected static boolean dragging = false;

   protected boolean isOutline = false;

   protected int threshold = 256;

   protected double outline_t = -1;

   protected int black = pack(0, 0, 0), white = pack(255, 255, 255);

   protected double cameraPos[] = { 0., 0., FL}; // camera position

   protected double cameraAim[] = { 0., 0., 0.}; // look at point

   protected double cameraUp[] = { 0., 1., 0.}; // camera up vector

   protected void pack(int px[], int i, int r, int g, int b) {
      if (isAnaglyph) {
         int lum = Math.max(r, Math.max(g, b));
         //int min = Math.min(r, Math.min(g, b));
         //int lum = max + min >> 1;
         int cr = r + lum >> 1;
         int cg = g + lum >> 1;
         int cb = b + lum >> 1;
         switch (anaglyphEye) {
            case 0:
               px[i] = 0xff000000 | cr << 16 | px[i] & 0x0000ffff;
               break;
            case 1:
               px[i] = 0xff000000 | px[i] & 0x00ff0000 | cg << 8 | cb;
               break;
         }
      } else
         px[i] = r << 16 | g << 8 | b | 0xff000000;
   }

   /**
    * @return Returns the level of detail (lod).
    */
   public int getLod() {
      return lod;
   }

   /**
    * @param lod
    *            The level of detail to set.
    */
   public void setLod(int lod) {
      this.lod = lod;
   }

   /**
    * @return Returns the status of the geometry z-buffer.
    */
   public boolean getGeometryBuffer() {
      return bufferg;
   }

   /**
    * @param value
    *            enables or disables the geometry z-buffer.
    */
   public void setGeometryBuffer(boolean value) {
      this.bufferg = value;
   }
}