/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a voltmeter in the view that is capable of measuring
 * the potential between various points in and out of the cell membrane.
 * 
 * @author John Blanco
 */
public class MembraneVoltmeter extends PNode {

	private static final Dimension2D METER_BODY_DIMENSIONS = new PDimension(150, 230);  // In screen coords, which are essentially pixels.
	private static final Dimension2D READOUT_DIMENSIONS = new PDimension(METER_BODY_DIMENSIONS.getWidth() * 0.8, 40);
	private static final Color METER_BODY_COLOR = Color.GRAY;
	
	private AxonModel axonModel;
	private PPath meterBody;
	private VoltageDisplayNode readout;
	private Timer updateTimer;

	public MembraneVoltmeter(AxonModel axonModel) {
		this.axonModel = axonModel;
		meterBody =	new PPath(new RoundRectangle2D.Double(0, 0, METER_BODY_DIMENSIONS.getWidth(),
				METER_BODY_DIMENSIONS.getHeight(), METER_BODY_DIMENSIONS.getWidth()/5, METER_BODY_DIMENSIONS.getWidth()/5));
		meterBody.setPaint(METER_BODY_COLOR);
		addChild(meterBody);
		readout = new VoltageDisplayNode(READOUT_DIMENSIONS);
		readout.setOffset(METER_BODY_DIMENSIONS.getWidth() / 2 - READOUT_DIMENSIONS.getWidth() / 2, 15);
		addChild(readout);
		updateVoltageDisplay();
		
		// Set up the update timer that will periodically update the reading.
		updateTimer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateVoltageDisplay();
			}
		});
		updateTimer.start();
	}
	
	private void updateVoltageDisplay(){
		int quantizedMembranePotential = axonModel.getQuantizedMembranePotential();
		double membranePotentialMillivolts = quantizedMembranePotential * 2;
		readout.setMillivoltsReading(membranePotentialMillivolts);
	}
	
	/**
     * Class that represents the display readout.
     */
    private static class VoltageDisplayNode extends PNode{
    	
    	private final Color BACKGROUND_COLOR = new Color(255, 255, 255);
        private final Font  TIME_FONT = new PhetFont( Font.BOLD, 26 );
        
    	private PPath _background;
    	private RoundRectangle2D _backgroundShape;
    	private double _currentMillivolts;
    	private PText _voltageText;
        private DecimalFormat _percentageFormatterOneDecimal = new DecimalFormat( "##0.0" );
    	
        VoltageDisplayNode(Dimension2D dimensions){
    		_backgroundShape = new RoundRectangle2D.Double(0, 0, dimensions.getWidth(), dimensions.getHeight(), 8, 8);
    		_background = new PPath(_backgroundShape);
    		_background.setPaint(BACKGROUND_COLOR);
    		addChild(_background);
    		_voltageText = new PText();
    		_voltageText.setFont(TIME_FONT);
    		addChild(_voltageText);
    	}
    	
    	public void setMillivoltsReading(double millivolts){

    		_currentMillivolts = millivolts;
    		
    		// TODO: i18n
    		_voltageText.setText(_percentageFormatterOneDecimal.format(_currentMillivolts) + " mV");
    		
    		scaleAndCenterText();
    	}
    	
    	/**
    	 * Set the display to indicate that the meter is not probing anything.
    	 */
    	public void setBlank(){
    		_voltageText.setText("-----");
    		scaleAndCenterText();
    	}
    	
    	private void scaleAndCenterText(){
    		if (_voltageText.getFullBoundsReference().width == 0 || 
    			_voltageText.getFullBoundsReference().height == 0) {
    			
    			// Avoid divide by 0 errors.
    			return;
    		}
    		
    		_voltageText.setScale(1);
    		double newScale = Math.min(
    				_backgroundShape.getWidth() * 0.9 / _voltageText.getFullBoundsReference().width,
    				_backgroundShape.getHeight() * 0.9 / _voltageText.getFullBoundsReference().height );
    		_voltageText.setScale(newScale);
    		_voltageText.setOffset(
    			_background.getFullBoundsReference().width / 2 - _voltageText.getFullBoundsReference().width / 2,
    			_background.getFullBoundsReference().height / 2 - _voltageText.getFullBoundsReference().height / 2);
    	}
    }
}
