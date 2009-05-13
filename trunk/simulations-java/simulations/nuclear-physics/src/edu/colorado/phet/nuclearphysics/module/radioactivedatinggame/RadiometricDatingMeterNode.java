/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D.Double;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.ConstantPowerOfTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class represents a node that the user can interact with in order to
 * date various items that are available in the play area.
 * 
 * @author John Blanco
 */
public class RadiometricDatingMeterNode extends PNode {

	private static final Color BODY_COLOR = Color.DARK_GRAY;
	private static final double READOUT_WIDTH_PROPORTION = 0.75;
	private static final double READOUT_HEIGHT_PROPORTION = 0.2;
	
	public RadiometricDatingMeterNode(double width, double height) {
		
		// Create the main body of the meter.
		PPath mainBody = new PPath(new RoundRectangle2D.Double(0, 0, width, height, width/5, width/5));
		mainBody.setPaint(BODY_COLOR);
		addChild(mainBody);
		
		// Create the percentage readout.
		PercentageDisplayNode percentageDisplay = new PercentageDisplayNode(width * READOUT_WIDTH_PROPORTION,
				height * READOUT_HEIGHT_PROPORTION);
		percentageDisplay.setOffset(mainBody.getFullBounds().width / 2 - percentageDisplay.getFullBounds().width / 2,
				mainBody.getHeight() * 0.1 );
		addChild(percentageDisplay);
		percentageDisplay.setPercentage(100);
	}
	
    /**
     * Class that represents the percentage display readout.
     */
    private class PercentageDisplayNode extends PNode{
    	
    	private final Color BACKGROUND_COLOR = new Color(255, 255, 255);
        private final Font  TIME_FONT = new PhetFont( Font.BOLD, 26 );
        
    	private PPath _background;
    	private RoundRectangle2D _backgroundShape;
    	private double _currentPercentage;
    	private PText _percentageText;
        private DecimalFormat _percentageFormatterOneDecimal = new DecimalFormat( "##0.0" );
    	
        PercentageDisplayNode(double width, double height){
    		_backgroundShape = new RoundRectangle2D.Double(0, 0, width, height, 8, 8);
    		_background = new PPath(_backgroundShape);
    		_background.setPaint(BACKGROUND_COLOR);
    		addChild(_background);
    		_percentageText = new PText();
    		_percentageText.setFont(TIME_FONT);
    		addChild(_percentageText);
    	}
    	
    	public void setPercentage(double percentage){

    		_currentPercentage = percentage;
    		
    		_percentageText.setText(_percentageFormatterOneDecimal.format(_currentPercentage) + " %");
    		
    		_percentageText.setScale(1);
    		_percentageText.setScale(_backgroundShape.getWidth() * 0.9 / _percentageText.getFullBoundsReference().width);
    		_percentageText.setOffset(
    			_background.getFullBoundsReference().width / 2 - _percentageText.getFullBoundsReference().width / 2,
    			_background.getFullBoundsReference().height / 2 - _percentageText.getFullBoundsReference().height / 2);
    	}
    }
}
