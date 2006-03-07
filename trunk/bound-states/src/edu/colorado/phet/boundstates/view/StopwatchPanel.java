/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.*;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;

/**
 * StopwatchPanel simulates a stopwatch on a specified IClock.
 * <p/>
 * Here is an example of adding it to a ModulePanel, to the 
 * left of the the simulation's play/pause/step controls 
 * (do this inside your module's constructor):
 * <code>
 * IClock clock = getClock();
 * String timeUnits = "psec";
 * double scaleFactor = 1E3;
 * DecimalFormat timeFormat = new DecimalFormat( "#0.00" );
 * StopwatchPanel stopwatchPanel = new StopwatchPanel( clock, timeUnits, scaleFactor, timeFormat );
 * getClockControlPanel().addToLeft( stopwatchPanel );
 * </code>
 *
 * @author Ron LeMaster / Chris Malley
 * @version $Revision$
 */
public class StopwatchPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Default property values...
    private static final Color DEFAULT_BACKGROUND = new Color( 237, 225, 113 ); // pale yellow
    private static final int DEFAULT_COLUMNS = 5; // columns in the time display
    private static final int DEFAULT_FONT_SIZE = 16; // font size of the time display
    private static final int DEFAULT_FONT_STYLE = Font.BOLD; // font style of the time display
    private static final NumberFormat DEFAULT_FORMAT = new DecimalFormat( "0.00" ); // format of the time display
    private static final String DEFAULT_UNITS = "";
    private static final double DEFAULT_SCALE_FACTOR = 1.0;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private IClock clock;
    
    // User interface components...
    private String startString;
    private String stopString;
    private JButton startStopButton;
    private JButton resetButton;
    private JTextField timeDisplay;
    private NumberFormat timeFormat;
    private JLabel timeUnitsLabel;

    // Internal state...
    private double scaleFactor;
    private double runningTime;
    private boolean isRunning;

    // Event handling...
    ClockListener clockListener;
    
    // Notification...
    private EventChannel stopwatchEventChannel;
    private StopwatchListener stopwatchListenerProxy;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param clock
     */
    public StopwatchPanel( IClock clock ) {
        this( clock, DEFAULT_UNITS, DEFAULT_SCALE_FACTOR, DEFAULT_FORMAT );
    }

    /**
     * Constructor.
     * 
     * @param clock
     * @param timeUnits   Gets printed on the panel
     * @param scaleFactor Time scale factor
     * @param timeFormat  The format that the panel is to show
     */
    public StopwatchPanel( IClock clock, String timeUnits, double scaleFactor, NumberFormat timeFormat ) {

        setBackground( DEFAULT_BACKGROUND );
        setBorder( BorderFactory.createRaisedBevelBorder() );

        this.clock = clock;
        this.scaleFactor = scaleFactor;
        this.timeFormat = timeFormat;

        // Start/Stop button
        startString = SimStrings.get( "Common.StopwatchPanel.start" );
        stopString = SimStrings.get( "Common.StopwatchPanel.stop" );
        startStopButton = new JButton( startString );
        SwingUtils.fixButtonOpacity( startStopButton );

        // Reset button
        resetButton = new JButton( SimStrings.get( "Common.StopwatchPanel.reset" ) );
        SwingUtils.fixButtonOpacity( resetButton );

        // Time readout
        timeDisplay = new JTextField( DEFAULT_COLUMNS );
        Font timeDisplayFont = timeDisplay.getFont();
        timeDisplay.setFont( new Font( timeDisplayFont.getName(), DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE ) );
        timeDisplay.setEditable( false );
        timeDisplay.setHorizontalAlignment( JTextField.RIGHT );

        // Time units
        timeUnitsLabel = new JLabel( timeUnits );

        // Layout
        setLayout( new FlowLayout() );
        add( startStopButton );
        add( resetButton );
        add( timeDisplay );
        add( timeUnitsLabel );
        resizeButtons();

        // Event handling
        ActionListener buttonListener = new StopwatchButtonListener();
        startStopButton.addActionListener( buttonListener );
        resetButton.addActionListener( buttonListener );
        clockListener = new StopwatchClockListener();
        clock.addClockListener( clockListener );

        // State change notification
        stopwatchEventChannel = new EventChannel( StopwatchListener.class );
        stopwatchListenerProxy = (StopwatchListener) stopwatchEventChannel.getListenerProxy();

        // Initialize...
        runningTime = 0;
        isRunning = false;
        updateButtons();
        updateTimeDisplay();
    }

    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        clock.removeClockListener( clockListener );
    }
    
    //----------------------------------------------------------------------------
    // State changes
    //----------------------------------------------------------------------------

    /**
     * Starts the stopwatch and notifies listeners.
     * If it's already running, this does nothing.
     */
    public void start() {
        if ( !isRunning ) {
            isRunning = true;
            updateButtons();
            stopwatchListenerProxy.start( new StopwatchEvent( this ) );
        }
    }

    /**
     * Stops the stopwatch and notifies listeners.
     * If it's already stopped, this does nothing.
     */
    public void stop() {
        if ( isRunning ) {
            isRunning = false;
            updateButtons();
            stopwatchListenerProxy.stop( new StopwatchEvent( this ) );
        }
    }

    /**
     * Resets the stopwatch and notifies listeners.
     * If it's already reset, this does nothing.
     */
    public void reset() {
        if ( !isReset() ) {
            runningTime = 0;
            updateTimeDisplay();
            updateButtons();
            stopwatchListenerProxy.reset( new StopwatchEvent( this ) );
        }
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the font used in the time display.
     * 
     * @param font
     */
    public void setTimeDisplayFont( Font font ) {
        timeDisplay.setFont( font );
    }

    /**
     * Gets the font used in the time display.
     * 
     * @return
     */
    public Font getTimeDisplayFont() {
        return timeDisplay.getFont();
    }

    /**
     * Sets the number of columns in the time display.
     * 
     * @param columns
     */
    public void setTimeDisplayColumns( int columns ) {
        timeDisplay.setColumns( columns );
    }

    /**
     * Sets the time units that appear to the right of the time display.
     * 
     * @param timeUnits
     */
    public void setTimeUnits( String timeUnits ) {
        timeUnitsLabel.setText( timeUnits );
    }

    /**
     * Sets the factor that determines how time is scaled when it is displayed.
     * 
     * @param scaleFactor
     */
    public void setScaleFactor( double scaleFactor ) {
        this.scaleFactor = scaleFactor;
        updateTimeDisplay();
    }

    /**
     * Sets the time display format.
     * 
     * @param format
     */
    public void setTimeFormat( NumberFormat timeFormat ) {
        this.timeFormat = timeFormat;
        updateTimeDisplay();
    }
    
    /**
     * Is the stopwatch running?
     * 
     * @return true or false
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Is the time display reset (ie, does it show zero)?
     * 
     * @return true or false
     */
    public boolean isReset() {
        return ( runningTime == 0 );
    }

    /*
     * Resizes buttons to their largest size, so they don't jump around.
     */
    private void resizeButtons() {
        // Size the Start/Stop button to its largest preferred dimensions...
        String saveString = startStopButton.getText();
        startStopButton.setText( stopString );
        Dimension stopSize = startStopButton.getPreferredSize();
        startStopButton.setText( startString );
        Dimension startSize = startStopButton.getPreferredSize();
        Dimension preferredSize = new Dimension( Math.max( stopSize.width, startSize.width ), Math.max( stopSize.height, startSize.height ) );
        startStopButton.setPreferredSize( preferredSize );
        startStopButton.setText( saveString );
        // Nothing to do for Reset button...
    }

    //----------------------------------------------------------------------------
    // Updates
    //----------------------------------------------------------------------------

    /*
     * Updates the time display.
     */
    private void updateTimeDisplay() {
        String s = timeFormat.format( runningTime * scaleFactor );
        timeDisplay.setText( s );
    }

    /*
     * Updates the buttons to match the state of the stopwatch.
     */
    private void updateButtons() {
        // Start/Stop button...
        if ( isRunning ) {
            startStopButton.setText( stopString );
        }
        else {
            startStopButton.setText( startString );
        }
        // Nothing to do for the Reset button, it's always enabled.
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /*
     * Handles button clicks.
     */
    private class StopwatchButtonListener implements ActionListener {

        public StopwatchButtonListener() {}

        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == startStopButton ) {
                if ( isRunning() ) {
                    stop();
                }
                else {
                    start();
                }
            }
            else if ( event.getSource() == resetButton ) {
                reset();
            }
        }
    }

    /*
     * Handles clock events.
     */
    private class StopwatchClockListener extends ClockAdapter {

        public StopwatchClockListener() {
            super();
        }

        /* 
         * Updates the time display when the clock ticks.
         * The scaleFactor is applied when runningTime is displayed.
         */
        public void clockTicked( ClockEvent event ) {
            if ( isRunning ) {
                runningTime += event.getSimulationTimeChange();
                updateTimeDisplay();
            }
        }
    }

    //-----------------------------------------------------------------
    // Notification
    //-----------------------------------------------------------------
    
    /**
     * Adds a listener who will be notified when the stopwatch's state changes.
     * 
     * @param listener
     */
    public void addListener( StopwatchListener listener ) {
        stopwatchEventChannel.addListener( listener );
    }

    /**
     * Removes a listener.
     * 
     * @param listener
     */
    public void removeListener( StopwatchListener listener ) {
        stopwatchEventChannel.removeListener( listener );
    }
    
    /**
     * StopwatchListener is the interface implemented by anyone
     * who wants to be notified about stopwatch state changes.
     */
    public interface StopwatchListener extends EventListener {

        /** Indicates that the stopwatch has been started. */
        void start( StopwatchEvent event );

        /** Indicates that the stopwatch has been stopped. */
        void stop( StopwatchEvent event );

        /** Indicates that the stopwatch has been reset. */
        void reset( StopwatchEvent event );
    }

    /**
     * StopwatchEvent is the event that indicates a change in the stopwatch's state.
     */
    public class StopwatchEvent extends EventObject {

        public StopwatchEvent( Object source ) {
            super( source );
        }

        /** Convenience function */
        public StopwatchPanel getStopwatch() {
            return (StopwatchPanel) getSource();
        }
    }

}
