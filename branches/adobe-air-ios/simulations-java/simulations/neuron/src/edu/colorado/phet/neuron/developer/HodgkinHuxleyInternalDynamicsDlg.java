// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.neuron.developer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.neuron.model.IHodgkinHuxleyModel;

/**
 * Dialog that displays the internal dynamics of a Hodgkin-Huxley model, such
 * as the current flow in various channels, the membrane voltage, etc.  This
 * is intended as a developer control.
 * 
 * @author John Blanco
 */
public class HodgkinHuxleyInternalDynamicsDlg extends PaintImmediateDialog{

	private static final Font DISPLAY_FONT = new PhetFont(14);
	
	private IHodgkinHuxleyModel hodgkinHuxleyModel;
	private JLabel timeLabel = new JLabel("Time: ");
	private JTextField timeText = new JTextField("000.0000 ms");
	private JLabel voltageLabel = new JLabel("Membrane Voltage: ");
	private JTextField voltageText = new JTextField("00.0 mV");
	private JLabel soduimChannelCurrentLabel = new JLabel("Sodium Channel Current: ");
	private JTextField sodiumChannelCurrentValue = new JTextField("00.0 mA");
	private JLabel potassiumChannelCurrentLabel = new JLabel("Potassium Channel Current: ");
	private JTextField potassiumChannelCurrentValue = new JTextField("00.0 mA");
	private JLabel leakChannelCurrentLabel = new JLabel("Leak Channel: ");
	private JTextField leakChannelCurrentValue = new JTextField("00.0 mA");
	private JLabel n4Label = new JLabel("n4: ");
	private JTextField n4Value = new JTextField("0.00");
	private JLabel m3hLabel = new JLabel("m3h: ");
	private JTextField m3hValue = new JTextField("0.00");
	private JButton captureButton = new JButton();
	private boolean capturing = false;
	
	// Used to verify that time has changed since last update.
	private double previousElapsedTime = 1000000;
	
	private ArrayList<HhDataCaptureEntry> captureData = new ArrayList<HhDataCaptureEntry>();
	
	public HodgkinHuxleyInternalDynamicsDlg(Frame frame, IClock clock, IHodgkinHuxleyModel hhModel) {
		super(frame);
		hodgkinHuxleyModel = hhModel;
		
		setTitle("Hodgkin-Huxley Model Internal Dynamics");
		setLayout(new GridLayout(8,2));
		
		// Update ourself on every clock tick that we are visible.
		clock.addClockListener(new ClockAdapter(){
		    public void clockTicked( ClockEvent clockEvent ) {
		    	if (isVisible()){
		    		update();
		    	}
		    }
		});
		
		timeLabel.setFont(DISPLAY_FONT);
		add(timeLabel);
		timeText.setFont(DISPLAY_FONT);
		timeText.setEditable(false);
		add(timeText);
		
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
		
		n4Label.setFont(DISPLAY_FONT);
		add(n4Label);
		n4Value.setFont(DISPLAY_FONT);
		n4Value.setEditable(false);
		add(n4Value);
		
		m3hLabel.setFont(DISPLAY_FONT);
		add(m3hLabel);
		m3hValue.setFont(DISPLAY_FONT);
		m3hValue.setEditable(false);
		add(m3hValue);
		
		// Add a button that allows the user to capture the values.
		add(captureButton);
		captureButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (capturing){
					displayCapturedData();
					captureData.clear();
				}
				capturing = !capturing;
				updateCaptureButtonState();
			}
		});
		updateCaptureButtonState();
		

		pack();
	}
	
	private void update(){
		double elapsedTime = hodgkinHuxleyModel.getElapsedTime();
		if (elapsedTime != previousElapsedTime){
			double membraneVoltage = hodgkinHuxleyModel.getMembraneVoltage() * 1000;
			double currentNa = hodgkinHuxleyModel.get_na_current();
			double currentK = hodgkinHuxleyModel.get_k_current();
			double currentLeak = hodgkinHuxleyModel.get_l_current();
			double n4 = hodgkinHuxleyModel.get_n4();
			double m3h = hodgkinHuxleyModel.get_m3h();
			timeText.setText(String.format("%.4f ms", elapsedTime));
			voltageText.setText(String.format("%.2f mV", membraneVoltage));
			sodiumChannelCurrentValue.setText(String.format("%.2f mA", -currentNa));
			potassiumChannelCurrentValue.setText(String.format("%.2f mA", -currentK));
			leakChannelCurrentValue.setText(String.format("%.2f mA", -currentLeak));
			n4Value.setText(String.format("%.5f", n4));
			m3hValue.setText(String.format("%.5f", m3h));
			
			if (capturing){
				captureData.add(new HhDataCaptureEntry(elapsedTime, membraneVoltage, -currentNa, -currentK, -currentLeak, n4, m3h));
			}
		}
		previousElapsedTime = elapsedTime;
	}
	
	private void updateCaptureButtonState(){
		if (!capturing){
			captureButton.setText("Start Capturing");
		}
		else{
			captureButton.setText("Stop Capturing");
		}
	}
	
	private void displayCapturedData(){
		
		JDialog dialog = new JDialog(this, true);
		JTextArea textDisplay = new JTextArea(captureData.size() + 1, 80);
		textDisplay.setEditable(false);
		textDisplay.append(HhDataCaptureEntry.getHeader());
		for (HhDataCaptureEntry dataEntry : captureData){
			textDisplay.append(dataEntry.toString());
		}
		textDisplay.setCaretPosition(0);
		JScrollPane scrollableTextDisplay = new JScrollPane(textDisplay);
		dialog.add(scrollableTextDisplay);
		dialog.setSize(new Dimension(500, 300));
		dialog.setLocationRelativeTo(this); 
		dialog.setVisible(true);
		
	}
	
	private static class HhDataCaptureEntry {
		
		private final double elapsedTime;
		private final double membraneVoltage;
		private final double currentNa;
		private final double currentK;
		private final double currentLeak;
		private final double n4;
		private final double m3h;
		
		public HhDataCaptureEntry(double elapsedTime, double membraneVoltage, double currentNa,
				double currentK, double currentLeak, double n4, double m3h) {
			this.elapsedTime = elapsedTime;
			this.membraneVoltage = membraneVoltage;
			this.currentNa = currentNa;
			this.currentK = currentK;
			this.currentLeak = currentLeak;
			this.n4 = n4;
			this.m3h = m3h;
		}
		
		protected static String getHeader(){
			return "elapsedTime, membraneVoltage, currentNa, currentK, currentLeak, n4, m3h\n";
		}
		
		@Override
		public String toString(){
			String retVal = new String();
			retVal += String.format("%.4f, ", elapsedTime);
			retVal += String.format("%.2f, ", membraneVoltage);
			retVal += String.format("%.2f, ", currentNa);
			retVal += String.format("%.2f, ", currentK);
			retVal += String.format("%.2f, ", currentLeak);
			retVal += String.format("%.5f, ", n4);
			retVal += String.format("%.5f, ", m3h);
			retVal += "\n";
			
			return retVal;
		}
	}
}
