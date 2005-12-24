import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CurveDemo implements ActionListener {

	private EditCurvePanel editCurvePanel = new EditCurvePanel();
	private ControlPathPanel controlPathPanel = new ControlPathPanel();
	private JFrame frame = new JFrame("Curve Demo");
	private JDialog editDialog = new JDialog(frame, "Curve Options");

	private JMenuItem editCurveMenuItem = new JMenuItem("Edit Curves");
	private JMenuItem setFlatnessMenuItem = new JMenuItem("Set Flatness");
	private JCheckBoxMenuItem showControlPointsCheckBox = new JCheckBoxMenuItem("Show Control Points", controlPathPanel.getShowControlPoints());
	private JCheckBoxMenuItem showControlLineCheckBox = new JCheckBoxMenuItem("Show Control Line", controlPathPanel.getShowControlLine());
	private JCheckBoxMenuItem showPointNumbersCheckBox = new JCheckBoxMenuItem("Show Point Numbers", controlPathPanel.getShowPointNumbers());
	private JCheckBoxMenuItem showPointLocationsCheckBox = new JCheckBoxMenuItem("Show Point Locations", controlPathPanel.getShowPointLocations());
	private JCheckBoxMenuItem antialiasCurvesCheckBox = new JCheckBoxMenuItem("Anti-alias Curves", controlPathPanel.getAntialiasCurves());
	private JCheckBoxMenuItem showCurvesCheckBox = new JCheckBoxMenuItem("Show Curves", controlPathPanel.getShowCurves());

	public CurveDemo() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(500, 500);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});

		frame.setContentPane(controlPathPanel);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu controlPathMenu = new JMenu("Control-Path");

		controlPathMenu.add(showControlPointsCheckBox);
		controlPathMenu.add(showControlLineCheckBox);
		controlPathMenu.add(showPointNumbersCheckBox);
		controlPathMenu.add(showPointLocationsCheckBox);
		controlPathMenu.add(antialiasCurvesCheckBox);
		controlPathMenu.add(showCurvesCheckBox);
		controlPathMenu.add(setFlatnessMenuItem);
		controlPathMenu.add(editCurveMenuItem);

		menuBar.add(controlPathMenu);

		editDialog.setContentPane(editCurvePanel);
		editDialog.setSize(500, 300);
		editDialog.setLocation((d.width - editDialog.getWidth()) / 2, (d.height - editDialog.getHeight()) / 2);

		editCurveMenuItem.addActionListener(this);
		setFlatnessMenuItem.addActionListener(this);
		showControlPointsCheckBox.addActionListener(this);
		showControlLineCheckBox.addActionListener(this);
		showPointNumbersCheckBox.addActionListener(this);
		showPointLocationsCheckBox.addActionListener(this);
		antialiasCurvesCheckBox.addActionListener(this);
		showCurvesCheckBox.addActionListener(this);

		frame.show();
	}

	public void actionPerformed(ActionEvent evt) {
		Object src = evt.getSource();

		if (src == editCurveMenuItem) {
			editCurvePanel.setControlPath(controlPathPanel.getControlPath());
			editCurvePanel.setRepaintPanel(controlPathPanel);
			editDialog.show();
		}
		else if (src == showControlPointsCheckBox) {
			controlPathPanel.setShowControlPoints(showControlPointsCheckBox.isSelected());
		}
		else if (src == showControlLineCheckBox) {
			controlPathPanel.setShowControlLine(showControlLineCheckBox.isSelected());
		}
		else if (src == showPointNumbersCheckBox) {
			controlPathPanel.setShowPointNumbers(showPointNumbersCheckBox.isSelected());
		}
		else if (src == showPointLocationsCheckBox) {
			controlPathPanel.setShowPointLocations(showPointLocationsCheckBox.isSelected());
		}
		else if (src == antialiasCurvesCheckBox) {
			controlPathPanel.setAntialiasCurves(antialiasCurvesCheckBox.isSelected());
		}
		else if (src == showCurvesCheckBox) {
			controlPathPanel.setShowCurves(showCurvesCheckBox.isSelected());
		}
		else if (src == setFlatnessMenuItem) {
			double f = controlPathPanel.getFlatness();

			Object o = JOptionPane.showInputDialog(frame, "Enter flatness value:", "Flatness", JOptionPane.QUESTION_MESSAGE, null, null, String.valueOf(f));

			if (o != null) {
				try {
					f = Double.parseDouble(o.toString());
					controlPathPanel.setFlatness(f);
				} catch (Throwable err) {
					JOptionPane.showMessageDialog(frame, err, "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		controlPathPanel.repaint();
	}

	public static void main(String[] args) {
		new CurveDemo();
	}
}