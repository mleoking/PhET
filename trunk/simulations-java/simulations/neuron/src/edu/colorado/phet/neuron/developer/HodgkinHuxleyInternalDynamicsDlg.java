/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.developer;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.neuron.model.HodgkinHuxleyModel;

/**
 * Dialog the displays the internal dynamics of a Hodgkin-Huxley model, such
 * as the current flow in various channels, the membrane voltage, etc.
 * 
 * @author John Blanco
 */
public class HodgkinHuxleyInternalDynamicsDlg extends PaintImmediateDialog{

	private static final Font DISPLAY_FONT = new PhetFont(14);
	
	private HodgkinHuxleyModel hodgkinHuxleyModel;
	private JLabel voltageLabel = new JLabel("Membrane Voltage: ");
	private JTextField voltageText = new JTextField("00.0 mV");
	private JLabel soduimChannelCurrentLabel = new JLabel("Sodium Channel Current: ");
	private JTextField sodiumChannelCurrentValue = new JTextField("00.0 mA");
	private JLabel potassiumChannelCurrentLabel = new JLabel("Potassium Channel Current: ");
	private JTextField potassiumChannelCurrentValue = new JTextField("00.0 mA");
	private JLabel leakChannelCurrentLabel = new JLabel("Leak Channel: ");
	private JTextField leakChannelCurrentValue = new JTextField("00.0 mA");
	
	public HodgkinHuxleyInternalDynamicsDlg(IClock clock, HodgkinHuxleyModel hhModel) {
		hodgkinHuxleyModel = hhModel;
		
		setTitle("Hodgkin-Huxley Model Internal Dynamics");
		setLayout(new GridLayout(4,2));
		
		// Update ourself on every clock tick that we are visible.
		clock.addClockListener(new ClockAdapter(){
		    public void clockTicked( ClockEvent clockEvent ) {
		    	if (isVisible()){
		    		update();
		    	}
		    }
		});
		
		voltageLabel.setFont(DISPLAY_FONT);
		add(voltageLabel);
		voltageText.setFont(DISPLAY_FONT);
		voltageText.setEditable(false);
		add(voltageText);
		
		soduimChannelCurrentLabel.setFont(DISPLAY_FONT);
		add(soduimChannelCurrentLabel);
		sodiumChannelCurrentValue.setFont(DISPLAY_FONT);
		sodiumChannelCurrentValue.setEditable(false);
		add(sodiumChannelCurrentValue);
		
		potassiumChannelCurrentLabel.setFont(DISPLAY_FONT);
		add(potassiumChannelCurrentLabel);
		potassiumChannelCurrentValue.setFont(DISPLAY_FONT);
		potassiumChannelCurrentValue.setEditable(false);
		add(potassiumChannelCurrentValue);
		
		leakChannelCurrentLabel.setFont(DISPLAY_FONT);
		add(leakChannelCurrentLabel);
		leakChannelCurrentValue.setFont(DISPLAY_FONT);
		leakChannelCurrentValue.setEditable(false);
		add(leakChannelCurrentValue);

		pack();
	}
	
	private void update(){
		voltageText.setText(String.format("%.2f mV", hodgkinHuxleyModel.getMembraneVoltage() * 1000));
		sodiumChannelCurrentValue.setText(String.format("%.2f mA", hodgkinHuxleyModel.get_na_current()));
		potassiumChannelCurrentValue.setText(String.format("%.2f mA", hodgkinHuxleyModel.get_k_current()));
		leakChannelCurrentValue.setText(String.format("%.2f mA", hodgkinHuxleyModel.get_l_current()));
	}
}
