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
	private JTextField voltageText = new JTextField("00.0 mv");
	
	public HodgkinHuxleyInternalDynamicsDlg(IClock clock, HodgkinHuxleyModel hhModel) {
		hodgkinHuxleyModel = hhModel;
		
		setTitle("Hodgkin-Huxley Model Internal Dynamics");
		setLayout(new GridLayout(1,2));
		
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
		voltageText.setEditable(false);
		add(voltageText);
		pack();
	}
	
	private void update(){
		voltageText.setText(String.format("%.2f mv", hodgkinHuxleyModel.getMembraneVoltage() * 1000));
	}
}
