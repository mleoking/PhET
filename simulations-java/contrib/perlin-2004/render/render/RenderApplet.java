// <pre>

package render;

import java.applet.Applet;
import java.awt.Event;
import java.awt.Graphics;

public abstract class RenderApplet extends Applet implements RenderablePanel {

	private RenderCore core;

	public RenderApplet() {
		core = new RenderCore(this);
	}
	

	////////////////////////////////////////////////////////////////////////////
	/// methods delegated to core

	public void addLight(double x, double y, double z, double r, double g,
			double b) {
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

	public int pull(Geometry s, double x0, double x1, double x2, double y0,
			double y1, double y2, double z0, double z1, double z2) {
		return core.pull(s, x0, x1, x2, y0, y1, y2, z0, z1, z2);
	}

	public void push() {
		core.push();
	}

	public boolean processCommand(int key) {
		return core.processCommand(key);
	}

	public void removeMenu(Widget menu) {
		core.removeMenu(menu);
	}

	public void resize(int width, int height) {
		core.recalculateSize(width, height);
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
}