import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Vector;

import com.graphbuilder.curve.*;

public class EditCurvePanel extends JPanel implements ActionListener, ListSelectionListener, ValueVectorListener {

	private JList curveList = new JList();
	private Vector curveListVec = new Vector();
	private JButton insertButton = new JButton("Insert");
	private JButton removeButton = new JButton("Remove");

	private JLabel controlStringLabel = new JLabel("Control String:");
	private JTextField controlStringTextField = new JTextField(6);
	private JCheckBox connectCheckBox = new JCheckBox("Connect");

	private JLabel tminLabel = new JLabel("t_min:");
	private JLabel tmaxLabel = new JLabel("t_max:");
	private JTextField tminTextField = new JTextField(3);
	private JTextField tmaxTextField = new JTextField(3);
	private JLabel sampleLimitLabel = new JLabel("Sample Limit:");
	private JTextField sampleLimitTextField = new JTextField(2);

	private JLabel alphaLabel = new JLabel("Alpha:");
	private JTextField alphaTextField = new JTextField(4);

	private JLabel degreeLabel = new JLabel("Degree:");
	private JTextField degreeTextField = new JTextField(2);
	private JLabel knotVectorTypeLabel = new JLabel("Knot Vector Type:");
	private JComboBox knotVectorTypeComboBox = new JComboBox(new Object[] { "Uniform Clamped", "Uniform Unclamped", "Non-Uniform" });
	private JCheckBox useDefaultIntervalCheckBox = new JCheckBox("Use Default Interval");

	private JCheckBox interpolateEndpointsCheckBox = new JCheckBox("Interpolate Endpoints");
	private JCheckBox closedCheckBox = new JCheckBox("Closed");

	private UnsortedValueVectorPanel knotVectorPanel = new UnsortedValueVectorPanel(true);
	private UnsortedValueVectorPanel weightVectorPanel = new UnsortedValueVectorPanel(true);
	private JLabel baseIndexLabel = new JLabel("Base Index:");
	private JTextField baseIndexTextField = new JTextField(3);
	private JLabel baseLengthLabel = new JLabel("Base Length:");
	private JTextField baseLengthTextField = new JTextField(3);
	private JCheckBox interpolateFirstCheckBox = new JCheckBox("Interpolate First");
	private JCheckBox interpolateLastCheckBox = new JCheckBox("Interpolate Last");

	private JCheckBox useWeightVectorCheckBox = new JCheckBox("Use Weight Vector");

	private JDialog pickCurveDialog = null;
	private JList availableCurveList = null;
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");

	private JPanel listPanel = new JPanel(new BorderLayout());
	private JPanel editPanel = new JPanel(new BorderLayout());
	private ControlPath controlPath = null;
	private JPanel repaintPanel = null;

	private JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	private JButton applyButton = new JButton("Apply");
	private JButton closeButton = new JButton("Close");

	public EditCurvePanel() {
		super();
		curveList.setPrototypeCellValue("Natural Cubic Spline");
		curveList.setCellRenderer(new CurveListCellRenderer());
		setLayout(new BorderLayout());
		knotVectorPanel.addValueVectorListener(this);
		weightVectorPanel.addValueVectorListener(this);
		weightVectorPanel.setKnobFillColor(Color.red);
		weightVectorPanel.setRange(0, 3);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(insertButton);
		buttonPanel.add(removeButton);

		listPanel.add(new JScrollPane(curveList), BorderLayout.CENTER);
		listPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(listPanel, BorderLayout.WEST);

		insertButton.addActionListener(this);
		removeButton.addActionListener(this);
		cancelButton.addActionListener(this);
		okButton.addActionListener(this);
		applyButton.addActionListener(this);
		closeButton.addActionListener(this);

		curveList.addListSelectionListener(this);
		connectCheckBox.addActionListener(this);
		interpolateEndpointsCheckBox.addActionListener(this);
		closedCheckBox.addActionListener(this);
		interpolateFirstCheckBox.addActionListener(this);
		interpolateLastCheckBox.addActionListener(this);
		knotVectorTypeComboBox.addActionListener(this);
		useDefaultIntervalCheckBox.addActionListener(this);
		useWeightVectorCheckBox.addActionListener(this);

		closePanel.add(applyButton);
		closePanel.add(closeButton);
		editPanel.add(closePanel, BorderLayout.SOUTH);
		add(editPanel, BorderLayout.CENTER);
	}

	public void setRepaintPanel(JPanel rp) {
		repaintPanel = rp;
	}

	public void setControlPath(ControlPath cp) {
		curveListVec = new Vector();
		for (int i = 0; i < cp.numCurves(); i++)
			curveListVec.add(cp.getCurve(i));
		controlPath = cp;
		curveList.setListData(curveListVec);
	}

	public void actionPerformed(ActionEvent evt) {
		Object src = evt.getSource();

		if (src == insertButton) {
			if (pickCurveDialog == null) {
				String[] curves = new String[] { "Bezier Curve", "B-Spline", "Cardinal Spline", "Catmull-Rom Spline", "Cubic B-Spline", "Lagrange Curve", "Natural Cubic Spline", "NURB-Spline", "Polyline" };
				availableCurveList = new JList(curves);

				Window w = SwingUtilities.windowForComponent(this);

				String title = "Insert Curve";

				if (w instanceof Frame)
					pickCurveDialog = new JDialog((Frame) w, title);
				else if (w instanceof Dialog)
					pickCurveDialog = new JDialog((Dialog) w, title);
				else
					pickCurveDialog = new JDialog((Frame) null, title);


				JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
				buttonPanel.add(okButton);
				buttonPanel.add(cancelButton);

				JPanel listPanel = new JPanel(new BorderLayout());
				availableCurveList.setBorder(BorderFactory.createLineBorder(Color.darkGray));
				listPanel.add(availableCurveList, BorderLayout.CENTER);
				listPanel.add(buttonPanel, BorderLayout.SOUTH);

				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				pickCurveDialog.setContentPane(listPanel);
				pickCurveDialog.setModal(true);
				pickCurveDialog.pack();
				pickCurveDialog.setLocation((d.width - pickCurveDialog.getWidth()) / 2, (d.height - pickCurveDialog.getHeight()) / 2);
			}

			pickCurveDialog.show();
		}
		else if (src == okButton || src == cancelButton) {
			String name = (String) availableCurveList.getSelectedValue();

			if (src == okButton && name != null) {
				Curve c = createCurve(name);
				int index = curveList.getSelectedIndex();
				if (index < 0) index = curveListVec.size();
				curveListVec.insertElementAt(c, index);
				curveList.setListData(curveListVec);
				controlPath.insertCurve(c, index);
				curveList.setSelectedValue(c, true);
			}
			pickCurveDialog.hide();
		}
		else if (src == removeButton) {
			int[] indices = curveList.getSelectedIndices();
			for (int i = indices.length - 1; i >= 0; i--) {
				curveListVec.removeElementAt(indices[i]);
				controlPath.removeCurve(indices[i]);
			}

			curveList.setListData(curveListVec);

			if (indices.length > 0) {
				int index = indices[0];
				if (index >= controlPath.numCurves())
					index = controlPath.numCurves() - 1;
				if (index >= 0) {
					curveList.setSelectedIndex(index);
				}
			}
		}
		else if (src == connectCheckBox) {
			Curve c = (Curve) curveList.getSelectedValue();
			c.setConnect(connectCheckBox.isSelected());
		}
		else if (src == interpolateEndpointsCheckBox) {
			CubicBSpline cbs = (CubicBSpline) curveList.getSelectedValue();
			cbs.setInterpolateEndpoints(interpolateEndpointsCheckBox.isSelected());
		}
		else if (src == interpolateFirstCheckBox) {
			LagrangeCurve lc = (LagrangeCurve) curveList.getSelectedValue();
			lc.setInterpolateFirst(interpolateFirstCheckBox.isSelected());
		}
		else if (src == interpolateLastCheckBox) {
			LagrangeCurve lc = (LagrangeCurve) curveList.getSelectedValue();
			lc.setInterpolateLast(interpolateLastCheckBox.isSelected());
		}
		else if (src == closedCheckBox) {
			NaturalCubicSpline ncs = (NaturalCubicSpline) curveList.getSelectedValue();
			ncs.setClosed(closedCheckBox.isSelected());
		}
		else if (src == useDefaultIntervalCheckBox) {
			BSpline bs = (BSpline) curveList.getSelectedValue();
			bs.setUseDefaultInterval(useDefaultIntervalCheckBox.isSelected());
		}
		else if (src == knotVectorTypeComboBox) {
			BSpline bs = (BSpline) curveList.getSelectedValue();
			int type = knotVectorTypeComboBox.getSelectedIndex();
			bs.setKnotVectorType(type);
			useDefaultIntervalCheckBox.setEnabled(type != BSpline.NON_UNIFORM);
			knotVectorPanel.setDisabled(type != BSpline.NON_UNIFORM);
			knotVectorPanel.repaint();
		}
		else if (src == useWeightVectorCheckBox) {
			boolean b = useWeightVectorCheckBox.isSelected();
			NURBSpline ns = (NURBSpline) curveList.getSelectedValue();
			ns.setUseWeightVector(b);
			weightVectorPanel.setDisabled(!b);
			weightVectorPanel.repaint();
		}
		else if (src == closeButton) {
			Window w = SwingUtilities.windowForComponent(this);
			w.hide();
		}
		else if (src == applyButton) {
			applySettings();
		}

		repaintPanel.repaint();
	}

	private void applySettings() {
		Component comp = null;

		try {
			Curve c = (Curve) curveList.getSelectedValue();

			if (c != null) {
				comp = controlStringTextField;
				c.setGroupIterator(new GroupIterator(controlStringTextField.getText(), controlPath.numPoints()));
			}

			if (c instanceof BezierCurve) {
				BezierCurve bc = (BezierCurve) c;
				comp = tminTextField;
				double t_min = Double.parseDouble(tminTextField.getText());
				comp = tmaxTextField;
				double t_max = Double.parseDouble(tmaxTextField.getText());
				bc.setInterval(t_min, t_max);
				comp = sampleLimitTextField;
				bc.setSampleLimit(Integer.parseInt(sampleLimitTextField.getText()));
			}
			else if (c instanceof BSpline) {
				BSpline bs = (BSpline) c;
				comp = tminTextField;
				double t_min = Double.parseDouble(tminTextField.getText());
				comp = tmaxTextField;
				double t_max = Double.parseDouble(tmaxTextField.getText());
				bs.setInterval(t_min, t_max);
				comp = sampleLimitTextField;
				bs.setSampleLimit(Integer.parseInt(sampleLimitTextField.getText()));
				comp = degreeTextField;
				bs.setDegree(Integer.parseInt(degreeTextField.getText()));
			}
			else if (c instanceof CardinalSpline) {
				CardinalSpline cs = (CardinalSpline) c;
				comp = alphaTextField;
				cs.setAlpha(Double.parseDouble(alphaTextField.getText()));
			}
			else if (c instanceof LagrangeCurve) {
				LagrangeCurve lc = (LagrangeCurve) c;
				comp = baseIndexTextField;
				lc.setBaseIndex(Integer.parseInt(baseIndexTextField.getText()));
				comp = baseLengthTextField;
				lc.setBaseLength(Integer.parseInt(baseLengthTextField.getText()));
			}
		} catch (Throwable err) {
			JOptionPane.showMessageDialog(this, err, "ERROR", JOptionPane.ERROR_MESSAGE);
		}

		if (comp != null) comp.requestFocus();
	}

	private Curve createCurve(String name) {
		ControlPath cp = controlPath;
		GroupIterator gi = new GroupIterator("0:n-1", cp.numPoints());

		if (name.equals("Bezier Curve"))
			return new BezierCurve(cp, gi);
		if (name.equals("B-Spline"))
			return new BSpline(cp, gi);
		if (name.equals("Cardinal Spline"))
			return new CardinalSpline(cp, gi);
		if (name.equals("Catmull-Rom Spline"))
			return new CatmullRomSpline(cp, gi);
		if (name.equals("Cubic B-Spline"))
			return new CubicBSpline(cp, gi);
		if (name.equals("Lagrange Curve"))
			return new LagrangeCurve(cp, gi);
		if (name.equals("Natural Cubic Spline"))
			return new NaturalCubicSpline(cp, gi);
		if (name.equals("NURB-Spline"))
			return new NURBSpline(cp, gi);

		return new Polyline(cp, gi);
	}

	public void valueChanged(ListSelectionEvent evt) {
		Curve c = (Curve) curveList.getSelectedValue();

		if (c != null) {
			controlStringTextField.setText(c.getGroupIterator().getControlString());
			connectCheckBox.setSelected(c.getConnect());
		}

		editPanel.removeAll();
		editPanel.add(closePanel, BorderLayout.SOUTH);

		if (c instanceof BezierCurve) {
			editPanel.add(getBezierCurvePanel(), BorderLayout.CENTER);

			BezierCurve bc = (BezierCurve) c;
			tminTextField.setText(String.valueOf(bc.t_min()));
			tmaxTextField.setText(String.valueOf(bc.t_max()));
			sampleLimitTextField.setText(String.valueOf(bc.getSampleLimit()));
		}
		else if (c instanceof BSpline) {
			boolean nurbs = (c instanceof NURBSpline);

			editPanel.add(getBSplinePanel(nurbs), BorderLayout.CENTER);
			BSpline bs = (BSpline) c;
			tminTextField.setText(String.valueOf(bs.t_min()));
			tmaxTextField.setText(String.valueOf(bs.t_max()));
			sampleLimitTextField.setText(String.valueOf(bs.getSampleLimit()));
			degreeTextField.setText(String.valueOf(bs.getDegree()));
			int type = bs.getKnotVectorType();
			knotVectorTypeComboBox.setSelectedIndex(type);
			useDefaultIntervalCheckBox.setSelected(bs.getUseDefaultInterval());
			useDefaultIntervalCheckBox.setEnabled(type != BSpline.NON_UNIFORM);
			knotVectorPanel.setValueVector(bs.getKnotVector());

			if (nurbs) {
				NURBSpline ns = (NURBSpline) bs;
				boolean b = ns.getUseWeightVector();
				weightVectorPanel.setValueVector(ns.getWeightVector());
				useWeightVectorCheckBox.setSelected(b);
				weightVectorPanel.setDisabled(!b);
			}
		}
		else if (c instanceof CardinalSpline) {
			editPanel.add(getCardinalSplinePanel(), BorderLayout.CENTER);
			CardinalSpline cs = (CardinalSpline) c;
			alphaTextField.setText(String.valueOf(cs.getAlpha()));
		}
		else if (c instanceof CubicBSpline) {
			editPanel.add(getCubicBSplinePanel(), BorderLayout.CENTER);
			CubicBSpline cbs = (CubicBSpline) c;
			interpolateEndpointsCheckBox.setSelected(cbs.getInterpolateEndpoints());
		}
		else if (c instanceof LagrangeCurve) {
			editPanel.add(getLagrangeCurvePanel(), BorderLayout.CENTER);
			LagrangeCurve lc = (LagrangeCurve) c;
			baseIndexTextField.setText(String.valueOf(lc.getBaseIndex()));
			baseLengthTextField.setText(String.valueOf(lc.getBaseLength()));
			interpolateFirstCheckBox.setSelected(interpolateFirstCheckBox.isSelected());
			interpolateLastCheckBox.setSelected(interpolateLastCheckBox.isSelected());
			knotVectorPanel.setValueVector(lc.getKnotVector());
			knotVectorPanel.setDisabled(false);
			knotVectorPanel.repaint();
		}
		else if (c instanceof NaturalCubicSpline) {
			editPanel.add(getNaturalCubicSplinePanel(), BorderLayout.CENTER);
			NaturalCubicSpline ncs = (NaturalCubicSpline) c;
			closedCheckBox.setSelected(ncs.getClosed());
		}
		else if (c != null) {
			editPanel.add(getCurvePanel(), BorderLayout.CENTER);
		}

		revalidate();
		repaint();
	}

	public void valueInserted(ValueVector v, int index, double value) {
		repaintPanel.repaint();
	}

	public void valueRemoved(ValueVector v, int index, double oldValue) {
		repaintPanel.repaint();
	}

	public void valueChanged(ValueVector v, int index, double oldValue) {
		repaintPanel.repaint();
	}

	private JPanel getCurvePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p.add(controlStringLabel);
		p.add(controlStringTextField);
		p.add(connectCheckBox);
		panel.add(p, BorderLayout.NORTH);
		return panel;
	}

	private JPanel getBezierCurvePanel() {
		JPanel panel = getCurvePanel();
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.add(tminLabel);
		p1.add(tminTextField);
		p1.add(tmaxLabel);
		p1.add(tmaxTextField);
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p2.add(sampleLimitLabel);
		p2.add(sampleLimitTextField);
		JPanel p3 = new JPanel(new BorderLayout());
		p3.add(p1, BorderLayout.NORTH);
		p3.add(p2, BorderLayout.CENTER);
		panel.add(p3, BorderLayout.CENTER);
		return panel;
	}

	private JPanel getBSplinePanel(boolean nurbs) {
		JPanel panel = getCurvePanel();
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.add(tminLabel);
		p1.add(tminTextField);
		p1.add(tmaxLabel);
		p1.add(tmaxTextField);
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p2.add(sampleLimitLabel);
		p2.add(sampleLimitTextField);
		p2.add(degreeLabel);
		p2.add(degreeTextField);

		JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p3.add(knotVectorTypeLabel);
		p3.add(knotVectorTypeComboBox);
		JPanel p4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p4.add(useDefaultIntervalCheckBox);
		if (nurbs) p4.add(useWeightVectorCheckBox);

		JPanel p5 = new JPanel(new BorderLayout());
		p5.add(p3, BorderLayout.NORTH);
		p5.add(p4, BorderLayout.CENTER);

		JPanel p6 = new JPanel(new BorderLayout());
		p6.add(p2, BorderLayout.NORTH);
		p6.add(p5, BorderLayout.CENTER);

		JPanel p7 = new JPanel(new BorderLayout());
		p7.add(p1, BorderLayout.NORTH);
		p7.add(p6, BorderLayout.CENTER);

		JPanel p8 = new JPanel(new BorderLayout());
		p8.add(p7, BorderLayout.NORTH);

		JPanel vvp = null;
		if (nurbs) {
			vvp = new JPanel(new GridLayout(1,2));
			vvp.add(knotVectorPanel);
			vvp.add(weightVectorPanel);
		}
		else
			vvp = knotVectorPanel;

		p8.add(vvp, BorderLayout.CENTER);
		panel.add(p8, BorderLayout.CENTER);
		return panel;
	}

	private JPanel getCardinalSplinePanel() {
		JPanel panel = getCurvePanel();
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.add(alphaLabel);
		p1.add(alphaTextField);
		panel.add(p1, BorderLayout.CENTER);
		return panel;
	}

	private JPanel getCubicBSplinePanel() {
		JPanel panel = getCurvePanel();
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.add(interpolateEndpointsCheckBox);
		panel.add(p1, BorderLayout.CENTER);
		return panel;
	}

	private JPanel getLagrangeCurvePanel() {
		JPanel panel = getCurvePanel();
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.add(baseIndexLabel);
		p1.add(baseIndexTextField);
		p1.add(baseLengthLabel);
		p1.add(baseLengthTextField);
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p2.add(interpolateFirstCheckBox);
		p2.add(interpolateLastCheckBox);
		JPanel p3 = new JPanel(new BorderLayout());
		p3.add(p1, BorderLayout.NORTH);
		p3.add(p2, BorderLayout.CENTER);
		JPanel p4 = new JPanel(new BorderLayout());
		p4.add(p3, BorderLayout.NORTH);
		p4.add(knotVectorPanel, BorderLayout.CENTER);
		panel.add(p4, BorderLayout.CENTER);
		return panel;
	}

	private JPanel getNaturalCubicSplinePanel() {
		JPanel panel = getCurvePanel();
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.add(closedCheckBox);
		panel.add(p1, BorderLayout.CENTER);
		return panel;
	}
}