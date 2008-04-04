/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * ClockTimePanel is a panel that displays a clock's time.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ClockTimePanel extends JPanel {

    public static final NumberFormat DEFAULT_TIME_FORMAT = new DecimalFormat( "0" );
    public static final int DEFAULT_TIME_DISPLAY_COLUMNS = 8;

    private IClock clock;
    private JTextField timeTextField;
    private JLabel unitsLabel;
    private NumberFormat timeFormat;
    private ClockListener clockListener;

    /**
     * Constructor
     *
     * @param clock
     * @param units
     * @param format
     * @param columns
     */
    public ClockTimePanel( IClock clock, String units, NumberFormat timeFormat, int timeColumns ) {
        super();

        this.clock = clock;

        clockListener = new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent event ) {
                super.simulationTimeChanged( event );
                update();
            }

            public void simulationTimeReset( ClockEvent event ) {
                super.simulationTimeReset( event );
                update();
            }
        };
        this.clock.addClockListener( clockListener );

        this.timeFormat = timeFormat;

//        Icon clockIcon = new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_CLOCK ) );
//        JLabel clockLabel = new JLabel( clockIcon );
        timeTextField = new JTextField();
        timeTextField.setColumns( timeColumns );
        timeTextField.setEditable( false );
        timeTextField.setHorizontalAlignment( JTextField.RIGHT );

        unitsLabel = new JLabel( units );

        // Layout
        setLayout( new FlowLayout( FlowLayout.CENTER ) );
//        add( clockLabel );
        add( timeTextField );
        add( unitsLabel );

        update();
    }

    /**
     * Updates the display when this component becomes visible.
     *
     * @param visible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            update();
        }
    }

    /**
     * Sets the clock whose time will be displayed.
     *
     * @param clock
     */
    public void setClock( IClock clock ) {
        if ( clock != this.clock ) {
            this.clock.removeClockListener( clockListener );
            this.clock = clock;
            this.clock.addClockListener( clockListener );
            update();
        }
    }

    /**
     * Sets the format of the time display.
     * See DecimalFormat for specification of pattern syntax.
     *
     * @param formatPattern
     */
    public void setTimeFormat( String formatPattern ) {
        setTimeFormat( new DecimalFormat( formatPattern ) );
    }

    /**
     * Sets the format of the time display.
     *
     * @param format
     */
    public void setTimeFormat( NumberFormat format ) {
        timeFormat = format;
        update();
    }

    /**
     * Sets the font used to display the time.
     *
     * @param font
     */
    public void setTimeFont( Font font ) {
        timeTextField.setFont( font );
    }

    /**
     * Sets the number of columns used to display the time.
     *
     * @param columns
     */
    public void setTimeColumns( int columns ) {
        timeTextField.setColumns( columns );
    }

    /**
     * Sets the time units.
     *
     * @param units
     */
    public void setUnits( String units ) {
        unitsLabel.setText( units );
    }

    /**
     * Sets the font for the time units.
     *
     * @param font
     */
    public void setUnitsFont( Font font ) {
        unitsLabel.setFont( font );
    }

    /*
    * Updates the time display if it's visible.
    */
    private void update() {
        if ( isVisible() ) {
            double time = clock.getSimulationTime();
            String sValue = timeFormat.format( time );
            timeTextField.setText( sValue );
        }
    }

}
