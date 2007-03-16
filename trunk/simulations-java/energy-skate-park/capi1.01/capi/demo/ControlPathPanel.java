import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;

import com.graphbuilder.curve.*;
import com.graphbuilder.geom.*;

public class ControlPathPanel extends JPanel implements MouseListener, MouseMotionListener {

	private ControlPath cp = new ControlPath();
	private PointFactory pointFactory = new PointFactory();

	private int controlPointSize = 10;
	private Color controlPointColor = Color.red;
	private Color controlLineColor = Color.blue;
	private Color insertPointColor = Color.red;
	private Color closestPointColor = Color.black;
	private Color pointNumberColor = Color.black;

	private int grabDistanceThreshold = 10;
	private boolean grabbing = false;

	private int closestPointIndex1 = -1;
	private int closestPointIndex2 = -1;
	private int closestPointIndex = -1;

	private double mouseX = 0;
	private double mouseY = 0;
	private double deltaX = 0;
	private double deltaY = 0;

	private boolean showControlPoints = true;
	private boolean showControlLine = true;
	private boolean showPointNumbers = false;
	private boolean showPointLocations = false;
	private boolean antialiasCurves = false;
	private boolean showCurves = true;

	private ShapeMultiPath multiPath = new ShapeMultiPath();

	public ControlPathPanel() {
		super();
		addMouseListener(this);
		addMouseMotionListener(this);
		setBackground(Color.white);

		//cp.addCurve(new BezierCurve(cp, new GroupIterator("0:n-1", 0)));
		cp.addCurve(new BSpline(cp, new GroupIterator("0:n-1", 0)));
	}

	public void setShowControlLine(boolean b) {
		showControlLine = b;
	}

	public void setShowControlPoints(boolean b) {
		showControlPoints = b;
	}

	public boolean getShowControlLine() {
		return showControlLine;
	}

	public boolean getShowControlPoints() {
		return showControlPoints;
	}

	public boolean getAntialiasCurves() {
		return antialiasCurves;
	}

	public void setAntialiasCurves(boolean b) {
		antialiasCurves = b;
	}

	public boolean getShowPointNumbers() {
		return showPointNumbers;
	}

	public void setShowPointNumbers(boolean b) {
		showPointNumbers = b;
	}

	public ControlPath getControlPath() {
		return cp;
	}

	public void setControlPath(ControlPath cp) {
		this.cp = cp;
	}

	public double getFlatness() {
		return multiPath.getFlatness();
	}

	public void setFlatness(double f) {
		multiPath.setFlatness(f);
	}

	public void setShowPointLocations(boolean b) {
		showPointLocations = b;
	}

	public boolean getShowPointLocations() {
		return showPointLocations;
	}

	public void setShowCurves(boolean b) {
		showCurves = b;
	}

	public boolean getShowCurves() {
		return showCurves;
	}

	public void mousePressed(MouseEvent evt) {
		double x = evt.getX();
		double y = evt.getY();

		if (evt.getClickCount() == 2) {
			int numPoints = cp.numPoints();

			if (closestPointIndex >= 0) {
				cp.removePoint(closestPointIndex);
				closestPointIndex = -1;
				closestPointIndex1 = -1;
				closestPointIndex2 = -1;
			}
			else {
				Point p = pointFactory.createPoint(x, y);

				if (cp.numPoints() == 0) {
					closestPointIndex = cp.numPoints();
					cp.addPoint(p);
				}
				else {
					if (closestPointIndex2 < 0) {
						if (closestPointIndex1 == 0) {
							cp.insertPoint(p, 0);
							closestPointIndex = 0;
						}
						else if (closestPointIndex1 > 0) {
							closestPointIndex = cp.numPoints();
							cp.addPoint(p);
						}
					}
					else {
						cp.insertPoint(p, closestPointIndex1);
						closestPointIndex = closestPointIndex1;
					}
				}
			}

			if (cp.numPoints() != numPoints) {
				for (int i = 0; i < cp.numCurves(); i++) {
					Curve c = cp.getCurve(i);
					GroupIterator gi = c.getGroupIterator();
					c.setGroupIterator(new GroupIterator(gi.getControlString(), cp.numPoints()));
				}
			}
		}

		if (closestPointIndex >= 0) {
			double[] loc = cp.getPoint(closestPointIndex).getLocation();
			deltaX = loc[0] - x;
			deltaY = loc[1] - y;
			grabbing = true;
		}

		repaint();
	}

	public void mouseReleased(MouseEvent evt) {
		grabbing = false;
	}

	public void mouseClicked(MouseEvent evt) {}
	public void mouseEntered(MouseEvent evt) {}
	public void mouseExited(MouseEvent evt) {}

	public void mouseDragged(MouseEvent evt) {
		closestPointIndex1 = -1;
		closestPointIndex2 = -1;

		if (grabbing) {
			double x = evt.getX();
			double y = evt.getY();

			double[] loc = cp.getPoint(closestPointIndex).getLocation();
			loc[0] = x + deltaX;
			loc[1] = y + deltaY;
		}

		repaint();
	}

	public void mouseMoved(MouseEvent evt) {
		mouseX = evt.getX();
		mouseY = evt.getY();

		findClosestLineSegment(mouseX, mouseY);
		findClosestPoint(mouseX, mouseY);
		repaint();
	}

	private void findClosestPoint(double x, double y) {
		double dist = Double.MAX_VALUE;

		for (int i = 0; i < cp.numPoints(); i++) {
			double[] loc = cp.getPoint(i).getLocation();
			double dx = x - loc[0];
			double dy = y - loc[1];
			double d = dx * dx + dy * dy;
			if (d < dist) {
				dist = d;
				closestPointIndex = i;
			}
		}

		if (Math.sqrt(dist) > controlPointSize / 2 + grabDistanceThreshold) {
			closestPointIndex = -1;
		}
	}

	private void findClosestLineSegment(double x, double y) {
		if (cp.numPoints() == 0) {
			closestPointIndex1 = -1;
			closestPointIndex2 = -1;
			return;
		}
		if (cp.numPoints() == 1) {
			closestPointIndex1 = 0;
			closestPointIndex2 = -1;
			return;
		}

		int index = 0;
		double dist = Double.MAX_VALUE;
		double angle = Math.PI;

		for (int i = 1; i < cp.numPoints(); i++) {
			Point p1 = cp.getPoint(i-1);
			Point p2 = cp.getPoint(i);
			double[] loc1 = p1.getLocation();
			double[] loc2 = p2.getLocation();
			double d = Geom.ptSegDistSq(loc1[0], loc1[1], loc2[0], loc2[1], x, y, null);

			if (d < dist) {
				dist = d;
				index = i;
			}
			else if (d == dist && index + 1 == i) {
				double[] loc3 = cp.getPoint(i-2).getLocation();

				double ox = loc1[0];
				double oy = loc1[1];

				double a = Geom.getAngle(ox, oy, x, y);
				double b = Geom.getAngle(ox, oy, loc3[0], loc3[1]);

				double dab = a - b;
				if (dab < 0) dab = -dab;
				if (dab > Math.PI) dab = 2 * Math.PI - dab;

				double c = Geom.getAngle(ox, oy, loc2[0], loc2[1]);
				double dac = a - c;
				if (dac < 0) dac = -dac;
				if (dac > Math.PI) dac = 2 * Math.PI - dac;

				if (dac < dab)
					index = i;
			}
		}

		boolean done = false;

		if (index == 1) {
			double[] loc1 = cp.getPoint(0).getLocation();
			double[] loc2 = cp.getPoint(1).getLocation();
			double a = Geom.getAngle(loc1[0], loc1[1], x, y);
			double b = Geom.getAngle(loc1[0], loc1[1], loc2[0], loc2[1]);

			double dab = a - b;
			if (dab < 0) dab = -dab;
			if (dab > Math.PI) dab = 2 * Math.PI - dab;

			if (dab > Math.PI / 2) {
				closestPointIndex1 = 0;
				closestPointIndex2 = -1;
				done = true;
			}
		}

		if (index == cp.numPoints() - 1) {
			double[] loc1 = cp.getPoint(index).getLocation();
			double[] loc2 = cp.getPoint(index-1).getLocation();
			double a = Geom.getAngle(loc1[0], loc1[1], x, y);
			double b = Geom.getAngle(loc1[0], loc1[1], loc2[0], loc2[1]);

			double dab = a - b;
			if (dab < 0) dab = -dab;
			if (dab > Math.PI) dab = 2 * Math.PI - dab;

			if (dab > Math.PI / 2) {
				closestPointIndex1 = index;
				closestPointIndex2 = -1;
				done = true;
			}
		}

		if (!done) {
			closestPointIndex1 = index;
			closestPointIndex2 = index - 1;
		}
	}

	public void paint(Graphics _g) {
		super.paint(_g);

		if (cp.numPoints() == 0) return;

		Graphics2D g = (Graphics2D) _g;

		if (showCurves) {
			//long time = System.currentTimeMillis();
			//for (int x = 0; x < 100; x++) {
			multiPath.setNumPoints(0);
			for (int i = 0; i < cp.numCurves(); i++)
				cp.getCurve(i).appendTo(multiPath);
			//}
			//long dt = System.currentTimeMillis() - time;
			//System.out.println(dt);

			if (antialiasCurves)
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g.draw(multiPath);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		if (showControlLine) {
			Line2D.Double line = new Line2D.Double();
			g.setColor(controlLineColor);
			Point lastPoint = cp.getPoint(0);

			for (int i = 1; i < cp.numPoints(); i++) {
				Point p = cp.getPoint(i);
				double[] loc = p.getLocation();
				double[] old = lastPoint.getLocation();
				line.setLine(old[0], old[1], loc[0], loc[1]);
				g.draw(line);
				lastPoint = p;
			}

			g.setColor(insertPointColor);
			if (closestPointIndex1 >= 0) {
				double[] loc = cp.getPoint(closestPointIndex1).getLocation();
				line.setLine(mouseX, mouseY, loc[0], loc[1]);
				g.draw(line);
			}

			if (closestPointIndex2 >= 0) {
				double[] loc = cp.getPoint(closestPointIndex2).getLocation();
				line.setLine(mouseX, mouseY, loc[0], loc[1]);
				g.draw(line);
			}
		}


		if (showControlPoints) {
			Rectangle2D.Double rect = new Rectangle2D.Double();
			g.setColor(controlPointColor);

			for (int i = 0; i < cp.numPoints(); i++) {
				Point p = cp.getPoint(i);
				double[] loc = p.getLocation();
				rect.setRect(loc[0] - controlPointSize / 2, loc[1] - controlPointSize / 2, controlPointSize, controlPointSize);
				g.fill(rect);
			}

			if (closestPointIndex >= 0) {
				g.setColor(closestPointColor);
				double[] loc = cp.getPoint(closestPointIndex).getLocation();
				rect.setRect(loc[0] - controlPointSize / 2, loc[1] - controlPointSize / 2, controlPointSize, controlPointSize);
				g.fill(rect);
			}

			if (showPointNumbers || showPointLocations) {
				g.setColor(pointNumberColor);

				for (int i = 0; i < cp.numPoints(); i++) {
					double[] loc = cp.getPoint(i).getLocation();
					String s = "";
					if (showPointNumbers) {
						s = String.valueOf(i) + " ";
					}
					if (showPointLocations) {
						s += "(" + ((int) loc[0]) + ", " + ((int) loc[1]) + ")";
					}
					g.drawString(s, (int) loc[0], (int) loc[1] - controlPointSize);
				}
			}
		}
	}
}