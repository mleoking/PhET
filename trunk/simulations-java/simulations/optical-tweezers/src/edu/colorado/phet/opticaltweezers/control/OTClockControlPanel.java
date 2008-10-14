/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.FlowLayout;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock.ConstantDtClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock.ConstantDtClockEvent;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.opticaltweezers.OTResources;

/**
 * OTClockControlPanel is the clock control panel for Optical Tweezers.
 * It automatically formats its time display in appropriate scientific notation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OTClockControlPanel extends PiccoloClockControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private OTClockTimePanel _timePanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public OTClockControlPanel( ConstantDtClock clock ) {
        super( clock );

        setRestartButtonVisible( true );
        
        // Use our own time display
        setTimeDisplayVisible( false );
        String units = OTResources.getString( "units.time" );
        _timePanel = new OTClockTimePanel( clock, units );
        addBetweenTimeDisplayAndButtons( _timePanel );
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    /**
     * Sets the format of the time display.
     * Not supported, because time format is handled automatically.
     * 
     * @param formatPattern
     * @throws UnsupportedOperationException
     */
    public void setTimeFormat( String formatPattern ) {
        throw new UnsupportedOperationException( "this time display sets it's format automatically" );
    }
    
    /**
     * Sets the format of the time display.
     * Not supported, because time format is handled automatically.
     * 
     * @param format
     * @throws UnsupportedOperationException
     */
    public void setTimeFormat( NumberFormat format ) {
        throw new UnsupportedOperationException( "this time display sets it's format automatically" );
    }
    
    public void setTimeFont( Font font ) {
        _timePanel.setTimeFont( font );
    }
    
    public void setUnitsFont( Font font ) {
        _timePanel.setUnitsFont( font );
    }
    
    public void setTimeColumns( int columns ) {
        _timePanel.setTimeColumns( columns );
    }

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * OTClockTimePanel is a clock panel that sets automatically adjusts its
     * display format for scientific notation.  The format is updated each time
     * the clock's dt is changed.
     */
    private static class OTClockTimePanel extends JPanel {
        
        // format used for counting zeros to the right of the decimal place
        public static final DecimalFormat PLAIN_FORMAT = new DecimalFormat( "0.0000000000000000000000000000000000000000" );
        // format used to display the mantissa
        public static final DecimalFormat MANTISSA_FORMAT = new DecimalFormat( "0.0" );
        // default number of columns in the time display
        public static final int DEFAULT_TIME_COLUMNS = 8;

        private ConstantDtClock _clock;
        private JTextField _timeTextField;
        private JLabel _unitsLabel;
        private ClockListener _clockListener;
        private String _exponentString;
        private double _timeMultiplier;

        /**
         * Constructor
         * 
         * @param clock
         * @param units
         * @param format
         * @param columns
         */
        public OTClockTimePanel( ConstantDtClock clock, String units ) {
            super();

            this._clock = clock;

            _clockListener = new ClockAdapter() {

                public void simulationTimeChanged( ClockEvent event ) {
                    super.simulationTimeChanged( event );
                    update();
                }

                public void simulationTimeReset( ClockEvent event ) {
                    super.simulationTimeReset( event );
                    update();
                }
            };
            this._clock.addClockListener( _clockListener );

            clock.addConstantDtClockListener( new ConstantDtClockAdapter() {

                public void dtChanged( ConstantDtClockEvent event ) {
                    ConstantDtClock clock = event.getClock();
                    double dt = clock.getDt();
                    updateTimeFormat( dt );
                }
            } );

            _timeTextField = new JTextField();
            _timeTextField.setColumns( DEFAULT_TIME_COLUMNS );
            _timeTextField.setEditable( false );
            _timeTextField.setHorizontalAlignment( JTextField.RIGHT );

            _unitsLabel = new JLabel( units );

            // Layout
            setLayout( new FlowLayout( FlowLayout.CENTER ) );
            add( _timeTextField );
            add( _unitsLabel );

            updateTimeFormat( clock.getDt() );
        }
        
        /**
         * Sets the font used to display the time.
         * 
         * @param font
         */
        public void setTimeFont( Font font ) {
            _timeTextField.setFont( font );
        }

        /**
         * Sets the number of columns used to display the time.
         * 
         * @param columns
         */
        public void setTimeColumns( int columns ) {
            _timeTextField.setColumns( columns );
        }

        /**
         * Sets the time units.
         * 
         * @param units
         */
        public void setUnits( String units ) {
            _unitsLabel.setText( units );
        }
        
        /**
         * Sets the font used to display the units.
         * 
         * @param font
         */
        public void setUnitsFont( Font font ) {
            _unitsLabel.setFont( font );
        }

        /*
         * Updates the time display to match the clock.
         */
        private void update() {
            double mantissaTime = _clock.getSimulationTime() * _timeMultiplier;
            String s = MANTISSA_FORMAT.format( mantissaTime ) + _exponentString;
            _timeTextField.setText( s );
        }

        /*
         * Updates the time format to match the clock's dt.
         */
        private void updateTimeFormat( double dt ) {
            int exponent = getNumberOfZerosToRightOfDecimalPlace( dt ) + 1;
            _exponentString = "E-" + String.valueOf( exponent );
            _timeMultiplier = Math.pow( 10, exponent );
            update();
        }

        /*
         * Gets the number of leading zeros to the right of the the decimal place.
         * For example, if dt=0.0068294, this method returns 3.
         */
        private static int getNumberOfZerosToRightOfDecimalPlace( double dt ) {

            String sdt = PLAIN_FORMAT.format( dt );
            boolean foundDecimal = false;
            int countZeros = 0;
            boolean done = false;
            for ( int i = 0; !done && i < sdt.length(); i++ ) {
                char c = sdt.charAt( i );
                if ( c == '.' ) {
                    foundDecimal = true;
                }
                else if ( foundDecimal ) {
                    if ( c == '0' ) {
                        countZeros++;
                    }
                    else {
                        done = true;
                    }
                }
            }
            return countZeros;
        }
    }

}
