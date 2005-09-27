import javax.swing.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;

import com.graphbuilder.curve.*;

public class UnsortedValueVectorPanel extends JPanel {

	public static final int VALUES_ABOVE_BAR = 0;
	public static final int VALUES_BELOW_BAR = 1;

	private ValueVector valueVector = null;

	private Vector vvlVec = new Vector();

	private boolean showValues = true;
	private int numDigits = 3;

	private double min = 0;
	private double max = 1.0;

	private Shape knobShape = new Rectangle(0, 0, 7, 7);
	private int knobWidth = 7;
	private int knobHeight = 7;

	private Rectangle2D rect = new Rectangle2D.Double(0, 0, 0, 0);
	private boolean find = true;
	private int mousex = 0;
	private int mousey = 0;
	private int current = 0;
	private int offsety = 0;

	private boolean disabled = true;

	private Color barColor = Color.black;
	private Color knobDrawColor = Color.black;
	private Color knobFillColor = Color.blue;

	private Color barColorDisabled = Color.gray;
	private Color knobDrawColorDisabled = Color.gray;
	private Color knobFillColorDisabled = Color.gray;
	private Color valueTextColorDisabled = Color.gray;

	private int barValueGap = 5;

	private int valuePosition = VALUES_ABOVE_BAR;

	private Color valueTextColor = Color.black;

	private int top = 0;
	private int bottom = 5;
	private int left = 15;
	private int right = 15;
	private boolean doubleClick = false;
	private int currentValueIndex = -1;
	private boolean canInsertAndRemove = false;

	public UnsortedValueVectorPanel(boolean canInsertAndRemove) {
		Control ctrl = new Control();
		addMouseListener(ctrl);
		addMouseMotionListener(ctrl);
		this.canInsertAndRemove = canInsertAndRemove;
	}

	private class Control implements MouseListener, MouseMotionListener{

		public Control() {
		}

		public void mousePressed(MouseEvent evt) {
			if (disabled) return;

			mousex = evt.getX();
			mousey = evt.getY();
			doubleClick = false;
			if (canInsertAndRemove && evt.getClickCount() >= 2)
				doubleClick = true;
			repaint();
		}

		public void mouseReleased(MouseEvent evt) {
			if (disabled) return;

			mousex = Integer.MIN_VALUE;
			mousey = Integer.MIN_VALUE;
			find = true;
			repaint();
		}

		public void mouseDragged(MouseEvent evt) {
			if (disabled) return;

			mousex = evt.getX();
			mousey = evt.getY();
			repaint();
		}

		public void mouseClicked(MouseEvent evt) {}
		public void mouseEntered(MouseEvent evt) {}
		public void mouseExited(MouseEvent evt) {}
		public void mouseMoved(MouseEvent evt) {}
	}

	public Shape getKnobShape() {
		return knobShape;
	}

	public void setKnobShape(Shape s) {
		if (s == null) throw new IllegalArgumentException();
		Rectangle2D r = s.getBounds2D();
		knobWidth = (int) Math.round(r.getWidth());
		knobHeight = (int) Math.round(r.getHeight());
	}

	public void setInsets(int top, int left, int bottom, int right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public void setRange(double min, double max) {
		if (min >= max) throw new IllegalArgumentException("Required: min < max");
		this.min = min;
		this.max = max;
	}

	public void setShowValues(boolean b) {
		showValues = b;
	}

	public boolean getShowValues() {
		return showValues;
	}

	public void setValuePosition(int p) {
		if (p != VALUES_ABOVE_BAR && p != VALUES_BELOW_BAR)
			throw new IllegalArgumentException("Unknown value position.");
		valuePosition = p;
	}

	private void setValue(int i, double val) {
		if (val < min) val = min;
		if (val > max) val = max;
		double oldValue = valueVector.get(i);

		if (oldValue != val) {
			valueVector.set(val, i);
			fireValueChanged(valueVector, i, oldValue);
		}
	}

	public int getValuePosition() {
		return valuePosition;
	}

	public void setNumDigits(int n) {
		numDigits = n;
	}

	public int getNumDigits() {
		return numDigits;
	}

	public ValueVector getValueVector() {
		return valueVector;
	}

	public void setValueVector(ValueVector kv) {
		valueVector = kv;
	}

	public Color getKnobFillColor() {
		return knobFillColor;
	}

	public void setKnobFillColor(Color c) {
		if (c == null) throw new IllegalArgumentException("Knob fill color cannot be null.");
		knobFillColor = c;
	}

	public void setDisabled(boolean b) {
		disabled = b;
	}

	public boolean getDisabled() {
		return disabled;
	}

	public void paint(Graphics _g) {
		super.paint(_g);

		Graphics2D g = (Graphics2D) _g;

		int w = getWidth();
		int h = getHeight();

		if (valueVector == null) {
			String msg = "<No Value-Vector Data Available>";
			TextLayout t = new TextLayout(msg, g.getFont(), g.getFontRenderContext());
			int th = (int) Math.round(t.getAscent() + t.getDescent());
			int tw = (int) Math.round(t.getAdvance());
			int x = (w - tw) / 2;
			int y = (h - th) / 2 + th;
			g.drawString(msg, x, y);
			return;
		}


		int nw = w - (left + right);
		int nh = h - (bottom + top);

		int y = top;

		int ty = 0;

		int n = valueVector.size();

		if (showValues) {
			String zero = "0";
			TextLayout t = new TextLayout(zero, g.getFont(), g.getFontRenderContext());
			int ascent = (int) (int) Math.round(t.getAscent());
			int th = ascent + barValueGap;

			if (valuePosition == VALUES_ABOVE_BAR) {
				ty = top + ascent;
				y += th;
				nh = nh - th;
			}
			else if (valuePosition == VALUES_BELOW_BAR) {
				ty = top + nh - 1;
				nh = nh - th;
			}
		}

		if (disabled)
			g.setColor(barColorDisabled);
		else
			g.setColor(barColor);

		g.drawRect(left, y, nw - 1, nh);

		double xgap = 0;

		if (n != 1) xgap = 1.0 * nw / (n - 1);

		for (int i = 0; i < n; i++) {
			int x = (int) Math.round(xgap * i + left);

			String s = String.valueOf(valueVector.get(i));
			int j = numDigits + 1; // assumes decimal place
			if (s.charAt(0) == '-') j++;
			if (j < s.length())
				s = s.substring(0, j);

			TextLayout t = new TextLayout(s, g.getFont(), g.getFontRenderContext());
			int tw = (int) Math.round(t.getAdvance());

			if (disabled)
				g.setColor(valueTextColorDisabled);
			else
				g.setColor(valueTextColor);

			g.drawString(s, x - tw / 2, ty);

			if (i != n - 1)
				g.drawLine(x,y,x,y+nh);

			int ky = y + (int) Math.round((1.0 - ((valueVector.get(i) - min) / (max - min))) * nh) - knobHeight / 2;
			int kx = x - knobWidth / 2;

			if (ky < y - knobHeight / 2) ky = y - knobHeight / 2;
			if (ky > y + nh - knobHeight / 2) ky = y + nh - knobHeight / 2;

			if (i == n - 1) kx--;

			g.translate(kx, ky);

			if (find) {
				rect.setRect(kx, ky, knobWidth, knobHeight);
				if (rect.contains(mousex, mousey)) {
					find = false;
					current = i;
					offsety = (ky + knobHeight / 2) - mousey;

					if (doubleClick) {
						doubleClick = false;
						double value = valueVector.get(i);
						valueVector.remove(i);
						fireValueRemoved(valueVector, i, value);
						current = -1;
						n--;
					}
				}

			}
			else if (current != -1) {
				setValue(current, (1.0 - 1.0 * (mousey - y + offsety) / nh) * (max - min) + min);
			}

			if (disabled)
				g.setColor(knobFillColorDisabled);
			else
				g.setColor(knobFillColor);

			g.fill(knobShape);

			if (disabled)
				g.setColor(knobDrawColorDisabled);
			else
				g.setColor(knobDrawColor);

			g.draw(knobShape);

			g.translate(-kx, -ky);
		}

		if (doubleClick) {
			doubleClick = false;
			int k = 0;
			double val = min;

			if (mousex < left) {}
			else if (mousex > left + nw) {
				k = n;
				val = max;
			}
			else {
				k = (int) ((1.0 * mousex - left) / xgap) + 1;

				if (k > 0 && k < valueVector.size())
					val = (valueVector.get(k) + valueVector.get(k-1)) / 2.0;
				else {
					if (k < 0) k = 0;
					else if (k > valueVector.size()) k = valueVector.size();
				}
			}

			valueVector.insert(val, k);
			fireValueInserted(valueVector, k, val);
		}
	}

	private void fireValueInserted(ValueVector v, int index, double value) {
		for (int i = 0; i < getNumValueVectorListeners(); i++)
			getValueVectorListener(i).valueInserted(v, index, value);
	}

	private void fireValueRemoved(ValueVector v, int index, double value) {
		for (int i = 0; i < getNumValueVectorListeners(); i++)
			getValueVectorListener(i).valueRemoved(v, index, value);
	}

	private void fireValueChanged(ValueVector v, int index, double oldValue) {
		for (int i = 0; i < getNumValueVectorListeners(); i++)
			getValueVectorListener(i).valueChanged(v, index, oldValue);
	}

	public void addValueVectorListener(ValueVectorListener vvl) {
		if (vvl == null)
			throw new IllegalArgumentException("cannot add null listener.");

		vvlVec.add(vvl);
	}

	public void removeValueVectorListener(ValueVectorListener vvl) {
		vvlVec.remove(vvl);
	}

	public ValueVectorListener getValueVectorListener(int i) {
		return (ValueVectorListener) vvlVec.get(i);
	}

	public int getNumValueVectorListeners() {
		return vvlVec.size();
	}

	public static void main(String[] args) {
		JPanel panel = new JPanel(new BorderLayout());
		UnsortedValueVectorPanel ukvp = new UnsortedValueVectorPanel(true);
		ukvp.setValueVector(new ValueVector());
		panel.add(ukvp, BorderLayout.CENTER);
		JFrame f = new JFrame();
		f.setSize(400, 400);
		f.setContentPane(panel);
		f.show();
	}
}
