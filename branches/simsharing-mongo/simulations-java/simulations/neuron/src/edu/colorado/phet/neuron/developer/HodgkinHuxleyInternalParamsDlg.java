// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.neuron.developer;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.neuron.model.IHodgkinHuxleyModel;

/**
 * Dialog that allows the user to set some of the internal parameters of a
 * Hodgkin-Huxley model of neuron action potential.  This is intended as a
 * developer control.
 * 
 * @author John Blanco
 */
public class HodgkinHuxleyInternalParamsDlg extends PaintImmediateDialog{

	private IHodgkinHuxleyModel hodgkinHuxleyModel;
	
	private ParameterEntry membraneCapacitanceParmEntry = new ParameterEntry("Membrane Capacitance:", "mF");
	private ParameterEntry gNaParmEntry = new ParameterEntry("Sodium Chan Conductance:", "S");
	private ParameterEntry gKParmEntry = new ParameterEntry("Potassium Chan Conductance:", "S");
	private ParameterEntry gLParmEntry = new ParameterEntry("Leak Chan Conductance:", "S");
	private JButton readValuesButton = new JButton("Read Values");
	private JButton writeValuesButton = new JButton("Write Values");
	
	public HodgkinHuxleyInternalParamsDlg(Frame frame, IHodgkinHuxleyModel hhModel) {
		super(frame);
		hodgkinHuxleyModel = hhModel;
		
		setTitle("Hodgkin-Huxley Model Parameters");
		setLayout(new GridLayout(5,3));
		
		add(membraneCapacitanceParmEntry.getLabel());
		add(membraneCapacitanceParmEntry.getField());
		add(membraneCapacitanceParmEntry.getUnits());
		
		add(gNaParmEntry.getLabel());
		add(gNaParmEntry.getField());
		add(gNaParmEntry.getUnits());
		
		add(gKParmEntry.getLabel());
		add(gKParmEntry.getField());
		add(gKParmEntry.getUnits());
		
		add(gLParmEntry.getLabel());
		add(gLParmEntry.getField());
		add(gLParmEntry.getUnits());
		
		// Add a button that allows the user to update the displayed values.
		add(readValuesButton);
		readValuesButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				readValuesFromModel();
			}
		});
		
		// Add a button that allows the user to write new values.
		add(writeValuesButton);
		writeValuesButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				writeValuesToModel();
				readValuesFromModel();
			}
		});
		
		// Initialize all values.
		readValuesFromModel();

		pack();
	}

	private void readValuesFromModel(){
		membraneCapacitanceParmEntry.setValueText(String.format("%.6f", hodgkinHuxleyModel.getCm()));
		gNaParmEntry.setValueText(String.format("%.6f", hodgkinHuxleyModel.get_gna()));
		gKParmEntry.setValueText(String.format("%.6f", hodgkinHuxleyModel.get_gk()));
		gLParmEntry.setValueText(String.format("%.6f", hodgkinHuxleyModel.get_gl()));
	}
	
	private void writeValuesToModel(){
		hodgkinHuxleyModel.setCm(membraneCapacitanceParmEntry.getValue());
		hodgkinHuxleyModel.set_gna(gNaParmEntry.getValue());
		hodgkinHuxleyModel.set_gk(gKParmEntry.getValue());
		hodgkinHuxleyModel.set_gl(gLParmEntry.getValue());
	}
	
	/**
	 * Make sure values are accurate whenever the window is brought up.
	 */
	@Override
	public void setVisible(boolean isVisible){
		if (isVisible){
			readValuesFromModel();
		}
		super.setVisible(isVisible);
	}
	
	/**
	 * A class used to simplify the creation of and interaction with the
	 * parameter data.
	 *
	 */
	private static class ParameterEntry {
		
		private static final Font LABEL_FONT = new PhetFont(14);

		private final JLabel label;
		private final JTextField field;
		private final JLabel units;
		
		public ParameterEntry(String label, String units){
			this.label = new JLabel(label);
			this.label.setFont(LABEL_FONT);
			this.field = new JTextField();
			this.field.setFont(LABEL_FONT);
			this.units = new JLabel(units);
			this.units.setFont(LABEL_FONT);
		}

		protected JLabel getLabel() {
			return label;
		}

		protected JTextField getField() {
			return field;
		}

		protected JLabel getUnits() {
			return units;
		}
		
		protected void setValueText(String valueText){
			field.setText(valueText);
		}
		
		protected double getValue(){
			return Double.parseDouble(field.getText());
		}
	}
}
