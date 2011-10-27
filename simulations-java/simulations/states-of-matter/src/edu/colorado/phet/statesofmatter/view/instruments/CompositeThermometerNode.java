// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.view.instruments;

import java.awt.Color;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.LiquidExpansionThermometerNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.view.TemperatureUnits;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * This class defines a PNode that has a liquid thermometer and a numerical
 * readout that can display the temperature in degrees Kelvin or degrees
 * Celsius.
 *
 * @author John Blanco
 */
public class CompositeThermometerNode extends PNode {

    private static final double THERMOMETER_WIDTH_PROPORTION = 0.38;
    private static final double LIQUID_THERMOMETER_SCALE_FACTOR = 20;

    private final LiquidExpansionThermometerNode m_liquidThermometer;
    private final double m_maxTemp;
    private final DigitalReadoutNode m_digitalReadout;

    private enum Units {KELVIN, CELSIUS}

    /**
     * Constructor.
     *
     * @param width           - Width on the canvas of this node.
     * @param height          - Height on the canvas of this node.
     * @param maxTempInKelvin - The maximum temperature in Kelvin that the thermometer can display.
     */
    public CompositeThermometerNode( double width, double height, double maxTempInKelvin, Property<TemperatureUnits> temperatureUnitsProperty ) {

        m_maxTemp = maxTempInKelvin;

        // Add the digital readout.
        m_digitalReadout = new DigitalReadoutNode( width, temperatureUnitsProperty.get() );
        addChild( m_digitalReadout );

        // Add the thermometer.  !! NOTE !! - The thermometer is added initially as much smaller than
        // it needs to be and then is scaled up.  This is a workaround for an issue where the
        // thermometer was distorted on Mac OS 10.4.  This fixes the problem, though we're not entirely
        // sure why.  It has something to do with clipping regions.  See Unfuddle ticket #656.
        m_liquidThermometer = new LiquidExpansionThermometerNode( new PDimension(
                width * THERMOMETER_WIDTH_PROPORTION / LIQUID_THERMOMETER_SCALE_FACTOR,
                height / LIQUID_THERMOMETER_SCALE_FACTOR ) );
        m_liquidThermometer.setTicks( height / 10 / LIQUID_THERMOMETER_SCALE_FACTOR, Color.BLACK, (float) height / 3500 );
        m_liquidThermometer.scale( LIQUID_THERMOMETER_SCALE_FACTOR );
        m_liquidThermometer.setOffset( 0, m_digitalReadout.getFullBoundsReference().height * 1.1 );
        addChild( m_liquidThermometer );

        // Monitor a property that determines whether the temperature should
        // be displayed in Kelvin or Celsius.
        temperatureUnitsProperty.addObserver( new VoidFunction1<TemperatureUnits>() {
            public void apply( TemperatureUnits temperatureUnits ) {
                m_digitalReadout.setTemperatureUnits( temperatureUnits );
            }
        } );
    }

    public void setTemperatureInDegreesKelvin( double degrees ) {
        m_digitalReadout.setValueKelvin( degrees );
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

        private final DecimalFormat numberFormatter = new DecimalFormat( "##0" );

        private TemperatureUnits m_temperatureUnits = TemperatureUnits.KELVIN;
        private double m_valueInKelvin = 0;
        private final PText m_text;
        private String m_units;
        private final PPath m_foregroundNode;
        private double m_defaultTextScale = 1;

        /**
         * Constructor.
         *
         * @param width
         * @param temperatureUnits
         */
        public DigitalReadoutNode( double width, TemperatureUnits temperatureUnits ) {
            double height = width / WIDTH_TO_HEIGHT_RATIO;
            PPath backgroundNode = new PPath( new RoundRectangle2D.Double( 0, 0, width, height, height / 5, height / 5 ) );
            backgroundNode.setPaint( BACKGROUND_COLOR );
            addChild( backgroundNode );

            double borderWidth = width * ( 1 - INSET_WIDTH_RATIO );
            m_foregroundNode = new PPath( new RoundRectangle2D.Double( 0, 0, width - ( borderWidth * 2 ),
                                                                       height - ( borderWidth * 2 ), height / 5, height / 5 ) );
            m_foregroundNode.setPaint( FOREGROUND_COLOR );
            addChild( m_foregroundNode );
            m_foregroundNode.setOffset( borderWidth, borderWidth );

            m_text = new PText( "0" );
            m_text.setFont( new PhetFont( 12, true ) );
            m_defaultTextScale = m_foregroundNode.getFullBoundsReference().height * 0.8 / m_text.getFullBoundsReference().height;
            addChild( m_text );

            setTemperatureUnits( temperatureUnits );
            update();
        }

        /**
         * Set the units in which the temperature should be displayed.
         *
         * @param temperatureUnits
         */
        public void setTemperatureUnits( TemperatureUnits temperatureUnits ) {
            // Doesn't handle all units.  Modify if needed.
            assert temperatureUnits == TemperatureUnits.KELVIN || temperatureUnits == TemperatureUnits.CELSIUS;

            m_temperatureUnits = temperatureUnits;

            // Update the string used for the units.
            switch( m_temperatureUnits ) {
                case KELVIN:
                    m_units = StatesOfMatterStrings.UNITS_K;
                    break;
                case CELSIUS:
                    m_units = StatesOfMatterStrings.UNITS_C;
                    break;
                default:
                    assert false; // Don't have support for specified units.
                    m_units = "X";
                    break;
            }

            update();
        }

        public void setValueKelvin( double value ) {
            m_valueInKelvin = value;
            update();
        }

        private void update() {
            String valueString;
            if ( m_temperatureUnits == TemperatureUnits.CELSIUS ) {
                valueString = numberFormatter.format( Math.round( m_valueInKelvin - 273.15 ) );
            }
            else {
                valueString = numberFormatter.format( Math.round( m_valueInKelvin ) );
            }

            if ( m_units != null ) {
                valueString += " ";
                valueString += m_units;
            }
            m_text.setScale( m_defaultTextScale );
            m_text.setText( valueString );
            if ( m_text.getFullBoundsReference().width > m_foregroundNode.getFullBoundsReference().width * 0.95 ) {
                // Scale the text to fit in the readout.
                m_text.setScale( 1 );
                m_text.setScale( m_foregroundNode.getFullBoundsReference().width * 0.95 / m_text.getFullBoundsReference().width );
            }
            m_text.setOffset(
                    m_foregroundNode.getFullBoundsReference().getCenterX() - m_text.getFullBoundsReference().width / 2,
                    m_foregroundNode.getFullBoundsReference().getCenterY() - m_text.getFullBoundsReference().height / 2 );
        }
    }
}
