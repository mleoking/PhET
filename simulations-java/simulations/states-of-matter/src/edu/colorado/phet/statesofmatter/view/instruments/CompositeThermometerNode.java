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

    private static final double THERMOMETER_WIDTH_PROPORTION = 0.3;
    
    private LiquidExpansionThermometerNode m_liquidThermometer;
    private double m_maxTemp;
    private DigitalReadoutNode m_kelvinReadout;
    
    /**
     * Constructor.
     * @param maxTemp TODO
     */
    public CompositeThermometerNode(double width, double height, double maxTemp){

        m_maxTemp = maxTemp;
        
        m_liquidThermometer = 
            new LiquidExpansionThermometerNode(new PDimension(width * THERMOMETER_WIDTH_PROPORTION, height));
        m_liquidThermometer.setTicks( m_liquidThermometer.getFullBoundsReference().height / 12, Color.BLACK, 4 );
        addChild(m_liquidThermometer);
        
        m_kelvinReadout = new DigitalReadoutNode( width * (1 - THERMOMETER_WIDTH_PROPORTION), "\u212A" );
        m_kelvinReadout.setOffset( -m_kelvinReadout.getFullBounds().width, 
                m_liquidThermometer.getFullBoundsReference().height * 0.2 );
        addChild(m_kelvinReadout);
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
    public class DigitalReadoutNode extends PNode {
        
        private final Color BACKGROUND_COLOR = Color.YELLOW;
        private final Color FOREGROUND_COLOR = Color.WHITE;
        private static final double WIDTH_TO_HEIGHT_RATIO = 2;
        private static final double INSET_WIDTH_RATIO = 0.95;
        private final DecimalFormat NUMBER_FORMATTER = new DecimalFormat( "##0" );
        
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
            m_text.scale( m_foregroundNode.getFullBoundsReference().height * 0.7 / m_text.getFullBoundsReference().height );
            addChild( m_text );
            
            setValue( 0 );
        }
        
        public void setUnitsString(String units){
            m_units = new String(units);
        }
        
        public void setValue(double value){
            String valueString = new String(NUMBER_FORMATTER.format( value ));
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
