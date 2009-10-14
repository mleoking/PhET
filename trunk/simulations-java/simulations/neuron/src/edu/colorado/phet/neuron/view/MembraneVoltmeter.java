/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
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
	
	private PPath meterBody;
	private VoltageDisplayNode readout;

	public MembraneVoltmeter() {
		meterBody =	new PPath(new RoundRectangle2D.Double(0, 0, METER_BODY_DIMENSIONS.getWidth(),
				METER_BODY_DIMENSIONS.getHeight(), METER_BODY_DIMENSIONS.getWidth()/5, METER_BODY_DIMENSIONS.getWidth()/5));
		meterBody.setPaint(METER_BODY_COLOR);
		addChild(meterBody);
		readout = new VoltageDisplayNode(READOUT_DIMENSIONS);
		readout.setOffset(METER_BODY_DIMENSIONS.getWidth() / 2 - READOUT_DIMENSIONS.getWidth() / 2, 15);
		addChild(readout);
	}
	
	/**
     * Class that represents the display readout.
     */
    private static class VoltageDisplayNode extends PNode{
    	
    	private final Color BACKGROUND_COLOR = new Color(255, 255, 255);
        private final Font  TIME_FONT = new PhetFont( Font.BOLD, 26 );
        
    	private PPath _background;
    	private RoundRectangle2D _backgroundShape;
    	private double _currentPercentage;
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
    	
    	public void setVoltageReading(double percentage){

    		_currentPercentage = percentage;
    		
    		// TODO: i18n
    		_voltageText.setText(_percentageFormatterOneDecimal.format(_currentPercentage) + " V");
    		
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
