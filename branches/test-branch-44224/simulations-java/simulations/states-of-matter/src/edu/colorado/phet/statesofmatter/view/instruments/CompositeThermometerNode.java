/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view.instruments;

import java.awt.Color;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.LiquidExpansionThermometerNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * This class defines a PNode that has a liquid thermometer, a numerical
 * readout for displaying degrees Kelvin, and a numerical readout for 
 * displaying degrees Fahrenheit.
 *
 * @author John Blanco
 */
public class CompositeThermometerNode extends PNode {

    private static final double THERMOMETER_WIDTH_PROPORTION = 0.38;
    private static final double LIQUID_THERMOMETER_SCALE_FACTOR = 20;
    
    private LiquidExpansionThermometerNode m_liquidThermometer;
    private double m_maxTemp;
    private DigitalReadoutNode m_kelvinReadout;
    
    /**
     * Constructor.
     * @param width - Width on the canvas of this node.
     * @param height - Height on the canvas of this node.
     * @param maxTemp - The maximum temperature in Kelvin that the thermometer can display. 
     */
    public CompositeThermometerNode(double width, double height, double maxTemp){

        m_maxTemp = maxTemp;
        
        // Add the digital readout.
        m_kelvinReadout = new DigitalReadoutNode( width, "\u212A" );
        addChild(m_kelvinReadout);
        
        // Add the thermometer.  !! NOTE !! - The thermometer is added initially as much smaller than
        // it needs to be and then is scaled up.  This is a workaround for an issue where the
        // thermometer was distorted on Mac OS 10.4.  This fixes the problem, though we're not entirely
        // sure why.  It has something to do with clipping regions.  See Unfuddle ticket #656.
        m_liquidThermometer = new LiquidExpansionThermometerNode( new PDimension(
        		width * THERMOMETER_WIDTH_PROPORTION / LIQUID_THERMOMETER_SCALE_FACTOR, 
        		height / LIQUID_THERMOMETER_SCALE_FACTOR) );
        m_liquidThermometer.setTicks(height / 10 / LIQUID_THERMOMETER_SCALE_FACTOR, Color.BLACK, (float)height / 3500);
        m_liquidThermometer.scale( LIQUID_THERMOMETER_SCALE_FACTOR );
        m_liquidThermometer.setOffset(0, m_kelvinReadout.getFullBoundsReference().height * 1.1);
        addChild(m_liquidThermometer);
    }
    
    public void setTemperatureInDegreesKelvin(double degrees){
        m_kelvinReadout.setValue( degrees );
        m_liquidThermometer.setLiquidHeight( Math.min( degrees / m_maxTemp, 1.0 ) );
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes
    //----------------------------------------------------------------------------

    /**
     * This class represents a node that will numerically display a value and
     * units.
     */
    private class DigitalReadoutNode extends PNode {
        
        private final Color BACKGROUND_COLOR = Color.YELLOW;
        private final Color FOREGROUND_COLOR = Color.WHITE;
        private static final double WIDTH_TO_HEIGHT_RATIO = 2.2;
        private static final double INSET_WIDTH_RATIO = 0.95;
        
        private final DecimalFormat highNumberFormatter = new DecimalFormat( "##0" );
        
        /*
         * The formats below were created when the model was designed to creep ever
         * closer to absolute zero without actually reaching it.  A decision was
         * made in October 2008 to allow the model to reach absolute zero, so this
         * is being commented out.  Delete this if, after several months, the decision
         * still stands.
        private final DecimalFormat lowNumberFormatter = new DecimalFormat( "#.0" );
        private final DecimalFormat lowerNumberFormatter = new DecimalFormat( "#.00" );
        private final DecimalFormat lowestNumberFormatter = new DecimalFormat( "0.#E0" );
         */
        
        private PText m_text;
        private String m_units;
        PPath m_foregroundNode;
        
        public DigitalReadoutNode(double width, String units) {

            if (units != null){
                m_units = new String(units);
            }
            
            double height = width / WIDTH_TO_HEIGHT_RATIO;
            PPath backgroundNode = new PPath(new RoundRectangle2D.Double(0, 0, width, height, height/5, height/5));
            backgroundNode.setPaint( BACKGROUND_COLOR );
            addChild(backgroundNode);
            
            double borderWidth = width * (1 - INSET_WIDTH_RATIO);
            m_foregroundNode = new PPath(new RoundRectangle2D.Double(0, 0, width - (borderWidth * 2),
                    height - (borderWidth * 2), height/5, height/5));
            m_foregroundNode.setPaint( FOREGROUND_COLOR );
            addChild(m_foregroundNode);
            m_foregroundNode.setOffset( borderWidth, borderWidth );
            
            m_text = new PText("0");
            m_text.setFont( new PhetFont(12, true) );
            m_text.scale( m_foregroundNode.getFullBoundsReference().height * 0.8 / m_text.getFullBoundsReference().height );
            addChild( m_text );
            
            setValue( 0 );
        }
        
        public void setUnitsString(String units){
            m_units = new String(units);
        }
        
        public void setValue(double value){
        	String valueString;
        	
        	/*
        	 * TODO JPB TBD - The following code adjusts the resolution as
        	 * the temperature descreases towards absolute zero.  A decision
        	 * was made on 10/8/2008 not to use this, and to just allow the
        	 * user to decrease all the way to absolute zero, so this was
        	 * commented out.  Leave this here for a couple of months and,
        	 * if the decision stands, remove it permanently.
        	if (value < 0.01){
                valueString = new String(lowestNumberFormatter.format( value ));
        	}
        	else if (value < 1){
                valueString = new String(lowerNumberFormatter.format( value ));
        	}
        	else if (value < 10){
                valueString = new String(lowNumberFormatter.format( value ));
        	}
        	else{
                valueString = new String(highNumberFormatter.format( value ));
        	}
        	 */
            valueString = new String(highNumberFormatter.format( Math.round( value ) ) );
        	
            if (m_units != null){
                valueString += " ";
                valueString += m_units;
            }
            m_text.setText( valueString );
            m_text.setOffset( 
                    getFullBoundsReference().width / 2 - m_text.getFullBoundsReference().width / 2, 
                    getFullBoundsReference().height * 0.2 );
        }
    }
}
