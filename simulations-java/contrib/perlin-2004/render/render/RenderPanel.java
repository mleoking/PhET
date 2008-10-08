// <pre>

package render;

import java.awt.*;
import java.awt.event.*;

import javax.swing.event.MouseInputAdapter;
import javax.swing.*;

public class RenderPanel extends Panel implements RenderablePanel {

  
   RenderCore core;

   public RenderPanel() {
      core = new RenderCore(this);
      
      MouseHandler mh = new MouseHandler(this);
      addMouseListener(mh);
      addMouseMotionListener(mh);
      KeyHandler kh = new KeyHandler(this);
      addKeyListener(kh);
   }

   private static final long serialVersionUID = 4683367684024675926L;
   //***************************************************************************
   // mouse and keyboard handlers
   //

   protected class MouseHandler extends MouseInputAdapter {

      RenderPanel rp;

      public MouseHandler(RenderPanel r) {
         rp = r;
      }

      public void mouseMoved(MouseEvent me) {
         rp.mouseMove(null, me.getX(), me.getY());
      }

      public void mousePressed(MouseEvent me) {
         rp.mouseDown(null, me.getX(), me.getY());
      }

      public void mouseDragged(MouseEvent me) {
         rp.mouseDrag(null, me.getX(), me.getY());
      }

      public void mouseReleased(MouseEvent me) {
         rp.mouseUp(null, me.getX(), me.getY());
      }
   }

   protected class KeyHandler extends KeyAdapter {

      RenderPanel rp;

      public KeyHandler(RenderPanel r) {
         rp = r;
      }

      public void keyReleased(KeyEvent ke) {         
         rp.processCommand(ke.getKeyChar());
      }
      
     
   }

   /////////////////////////////////////////////////////////////////////////////
   //  METHODS DELEGATED TO CORE

   public Renderable getRenderable() {
      return core.getRenderable();
   }

   public void setRenderable(Renderable renderable) {
      core.setRenderable(renderable);
   }

   public void addLight(double x, double y, double z, double r, double g, double b) {
      core.addLight(x, y, z, r, g, b);
   }

   public Widget addMenu(String label, int x, int y) {
      return core.addMenu(label, x, y);
   }

   public void addMenu(Widget menu) {
      core.addMenu(menu);
   }

   public void damage() {
      core.damage();
   }

   public boolean equals(Object obj) {
      return core.equals(obj);
   }

   public double getCurrentTime() {
      return core.getCurrentTime();
   }

   public double getFOV() {
      return core.getFOV();
   }

   public double getFL() {
      return core.getFL();
   }

   public Geometry getGeometry(int x, int y) {
      return core.getGeometry(x, y);
   }

   public Matrix[] getMatrix() {
      return core.getMatrix();
   }

   public int[] getPix() {
      return core.getPix();
   }

   public boolean getPoint(int x, int y, double[] xyz) {
      return core.getPoint(x, y, xyz);
   }

   public Geometry getWorld() {
      return core.getWorld();
   }

   public int hashCode() {
      return core.hashCode();
   }

   public void identity() {
      core.identity();
   }

   public void init() {
      core.init();
   }

   public boolean keyUp(Event evt, int key) {
      return core.keyUp(evt, key);
   }

   public Matrix m() {
      return core.m();
   }

   public Widget menu(int i) {
      return core.menu(i);
   }

   public boolean mouseDown(Event evt, int x, int y) {
      return core.mouseDown(evt, x, y);
   }

   public boolean mouseDrag(Event evt, int x, int y) {
      return core.mouseDrag(evt, x, y);
   }

   public boolean mouseMove(Event evt, int x, int y) {
      return core.mouseMove(evt, x, y);
   }

   public boolean mouseUp(Event evt, int x, int y) {
      return core.mouseUp(evt, x, y);
   }

   public void pause() {
      core.pause();
   }

   public void pop() {
      core.pop();
   }

   public int pull(Geometry s, double x0, double x1, double x2, double y0, double y1, double y2, double z0, double z1,
         double z2) {
      return core.pull(s, x0, x1, x2, y0, y1, y2, z0, z1, z2);
   }

   public void push() {
      core.push();
   }

   public boolean processCommand(int key) {
      return core.processCommand(key);
   }

   public void recalculateSize(int currentWidth, int currentHeight) {
      core.recalculateSize(currentWidth, currentHeight);
   }

   public void removeMenu(Widget menu) {
      core.removeMenu(menu);
   }

   public void rotateX(double t) {
      core.rotateX(t);
   }

   public void rotateY(double t) {
      core.rotateY(t);
   }

   public void rotateZ(double t) {
      core.rotateZ(t);
   }

   public void run() {
      core.run();
   }

   public void scale(double x, double y, double z) {
      core.scale(x, y, z);
   }

   public void setFL(double value) {
      core.setFL(value);
   }

   public void setFOV(double value) {
      core.setFOV(value);
   }

   public void start() {
      core.start();
   }

   public void stop() {
      core.stop();
   }

   public String toString() {
      return core.toString();
   }

   public void transform(Geometry s) {
      core.transform(s);
   }

   public void translate(double x, double y, double z) {
      core.translate(x, y, z);
   }

   public void translate(double[] v) {
      core.translate(v);
   }

   public void update(Graphics g) {
      core.update(g);
   }

   public Widget widgetAt(int x, int y) {
      return core.widgetAt(x, y);
   }

   /**
    * @see render.RenderablePanel#getRenderer()
    */
   public Renderer getRenderer() {
      return core.getRenderer();
   }

   /**
    * @see render.RenderablePanel#refresh()
    */
   public void refresh() {
      core.damage();
   }

   /**
    * @see render.RenderablePanel#setBgColor(double, double, double)
    */
   public void setBgColor(double r, double g, double b) {
      core.renderer.setBgColor(r, g, b);
   }

   /**
    * @see render.RenderablePanel#init(int, int)
    */
   public void init(int width, int height) {
      core.init();
   }

   /**
    * @see render.RenderablePanel#rotateView(double, double)
    */
   public void rotateView(double theta, double phi) {
      core.renderer.rotateView(theta, phi);
   }

   /**
    * @see render.RenderablePanel#getLod()
    */
   public int getLod() {
      return core.getLod();
   }

   /**
    * @see render.RenderablePanel#setLod(int)
    */
   public void setLod(int value) {
      core.setLod(value);
   }

   /**
    * @see render.RenderablePanel#getGeometryBuffer()
    */
   public boolean getGeometryBuffer() {
      return core.getGeometryBuffer();
   }

   /**
    * @see render.RenderablePanel#setGeometryBuffer(boolean)
    */
   public void setGeometryBuffer(boolean value) {
      core.setGeometryBuffer(value);
   }

   /**
    * @see render.RenderablePanel#setDragging(boolean)
    */
   public void setDragging(boolean value) {
      Renderer.setDragging(value);
   }

   /**
    * @see render.RenderablePanel#isDragging()
    */
   public boolean isDragging() {
      return Renderer.isDragging();
   }

   /**
    * @see render.RenderablePanel#setTableMode(boolean)
    */
   public void setTableMode(boolean value) {
      Renderer.tableMode = value;
   }

   /**
    * @see render.RenderablePanel#getTableMode()
    */
   public boolean getTableMode() {
      return Renderer.tableMode;
   }

   /**
    * @see render.RenderablePanel#showMesh(boolean)
    */
   public void showMesh(boolean value) {
      core.renderer.showMesh = value;
   }

   /**
    * @see render.RenderablePanel#setOutline(boolean)
    */
   public void setOutline(boolean value) {
      core.renderer.isOutline = value;
   }

   /**
    * @see render.RenderablePanel#getOutline()
    */
   public boolean getOutline() {
      return core.renderer.isOutline;
   }

   /**
    * @see render.RenderablePanel#mouseEntered(java.awt.Event, int, int)
    */
   public void mouseEntered(Event e, int x, int y) {}

   /**
    * @see render.RenderablePanel#mouseExited(java.awt.Event, int, int)
    */
   public void mouseExited(Event e, int x, int y) { }

   /**
    * @see render.RenderablePanel#mousePressed(java.awt.Event, int, int)
    */
   public void mousePressed(Event e, int x, int y) {}

   /**
    * @see render.RenderablePanel#mouseReleased(java.awt.Event, int, int)
    */
   public void mouseReleased(Event e, int x, int y) { }

   /**
    * @see render.RenderablePanel#mouseClicked(java.awt.Event, int, int)
    */
   public void mouseClicked(Event e, int x, int y) {}

   /**
    * @see render.RenderablePanel#mouseDragged(java.awt.Event, int, int)
    */
   public void mouseDragged(Event e, int x, int y) { }

   /**
    * @see render.RenderablePanel#mouseMoved(java.awt.Event, int, int)
    */
   public void mouseMoved(Event e, int x, int y) { }

  /**
   * @see render.RenderablePanel#mouseWheelMoved(java.awt.event.MouseEvent, int)
   */
  public void mouseWheelMoved(MouseWheelEvent e, int rotation) {}
}