/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.clock;

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

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;

/**
 * StopwatchPanel is a panel that simulates a stopwatch on a specified IClock.
 * <p/>
 * Here is an example of adding it to a ModulePanel, to the left of the the simulation's
 * play/pause/step controls (do this inside your module's constructor):
 * <code>
 * StopwatchPanel stopwatchPanel = new StopwatchPanel( clock, "psec", 1E3, new DecimalFormat( "#0.00" ) );
 * getClockControlPanel().add( stopwatchPanel, BorderLayout.WEST );
 * </code>
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StopwatchPanel extends JPanel implements ClockListener {

    private static final Color DEFAULT_BACKGROUND = new Color( 237, 225, 113 ); // pale yellow
    private static final int DEFAULT_FONT_SIZE = 16;
    private static final int DEFAULT_FONT_STYLE = Font.BOLD;
        
    private JTextField clockTF = new JTextField();
    private NumberFormat clockFormat;
    private String[] startStopStr;
    private EventChannel stopwatchEventChannel = new EventChannel( StopwatchListener.class );
    private StopwatchListener stopwatchListenerProxy = (StopwatchListener)stopwatchEventChannel.getListenerProxy();
    // Time scale factor
    private double scaleFactor = 1;
    private double runningTime = 0;
    private boolean isRunning = false;
    private boolean isReset = true;
    private JLabel timeUnitsLabel;
    private JButton startStopBtn;
    private JButton resetBtn;
    private StartStopActionListener startStopActionListener;
    private boolean savedResetState;

    /**
     * @param clock
     */
    public StopwatchPanel( IClock clock ) {
        this( clock, "", 1, new DecimalFormat( "0.00" ) );
    }

    /**
     * @param clock
     * @param timeUnits   Gets printed on the panel
     * @param scaleFactor Time scale factor
     * @param timeFormat  The format that the panel is to show
     */
    public StopwatchPanel( IClock clock, String timeUnits, double scaleFactor, DecimalFormat timeFormat ) {

        clock.addClockListener( this );
        setBackground( DEFAULT_BACKGROUND );

        this.scaleFactor = scaleFactor;
        this.clockFormat = timeFormat;

        // Clock readout
        setBorder( BorderFactory.createRaisedBevelBorder() );
        clockTF = new JTextField( 5 );
        Font clockFont = clockTF.getFont();
        clockTF.setFont( new Font( clockFont.getName(), DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE ) );
        clockTF.setEditable( false );
        clockTF.setHorizontalAlignment( JTextField.RIGHT );

        // Initialize the contents of the clockTF
        resetClock();

        // Start/stop button
        startStopStr = new String[2];
        startStopStr[0] = SimStrings.get( "Common.StopwatchPanel.start" );
        startStopStr[1] = SimStrings.get( "Common.StopwatchPanel.stop" );
        startStopBtn = new JButton( startStopStr[0] );
        startStopActionListener = new StartStopActionListener();
        startStopBtn.addActionListener( startStopActionListener );

        // Reset button
        resetBtn = new JButton( SimStrings.get( "Common.StopwatchPanel.reset" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                resetClock();
            }
        } );
        
        SwingUtils.fixButtonOpacity( startStopBtn );
        SwingUtils.fixButtonOpacity( resetBtn );

        // Lay out the panel
        this.setLayout( new FlowLayout() );
        add( startStopBtn );
        add( resetBtn );
        add( clockTF );
        timeUnitsLabel = new JLabel( timeUnits );
        add( timeUnitsLabel );

        // Clear the clock
        resetClock();
    }

    private void resetClock() {
        runningTime = 0;
        displayRunningTime();
        StopwatchEvent event = new StopwatchEvent( this );
        isReset = true;
        isRunning = false;
        stopwatchListenerProxy.reset( event );
    }

    public void setClockPanelVisible( boolean isVisible ) {
        setVisible( isVisible );
    }

    public void setTimeUnits( String timeUnits ) {
        timeUnitsLabel.setText( timeUnits );
    }

    public boolean isClockPanelVisible() {
        return isVisible();
    }

    public boolean isReset() {
        return isReset;
    }

    public void reset() {
        resetClock();
        if( isRunning ) {
            startStopActionListener.actionPerformed( null );
        }
    }

    public void setTimeDisplayFont( Font font ) {
        clockTF.setFont( font );
    }
    
    public Font getTimeDisplayFont() {
        return clockTF.getFont();
    }
    
    /**
     * Private inner class that manages the state of the stopwatch
     * when buttons are clicked
     */
    private class StartStopActionListener implements ActionListener {
        int startStopState = 0;

        public StartStopActionListener() {
        }

        public void actionPerformed( ActionEvent e ) {
            if( startStopState == 0 ) {
                StopwatchEvent event = new StopwatchEvent( this );
                stopwatchListenerProxy.start( event );
                resetBtn.setEnabled( false );
                isRunning = true;
            }
            else {
                StopwatchEvent event = new StopwatchEvent( this );
                stopwatchListenerProxy.stop( event );
                resetBtn.setEnabled( true );
                isRunning = false;
            }

            // Set the proper text for the button, and do a bunch of messing arround to
            // set the size so it doesn't change when the text changes.
            startStopState = ( startStopState + 1 ) % 2;
            Dimension prevSize = startStopBtn.getSize();
            startStopBtn.setText( startStopStr[startStopState] );
            Dimension currSize = startStopBtn.getSize();
            Dimension newSize = new Dimension( Math.max( prevSize.width, currSize.width ), currSize.height );
            startStopBtn.setPreferredSize( newSize );
        }
    }

    private void displayRunningTime() {
        String s = clockFormat.format( runningTime * scaleFactor );
        clockTF.setText( s );
    }

    public boolean isRunning() {
        return isRunning;
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    /**
     * Responds to state changes in the simulation clock
     *
     * @param isPaused
     */
    private void stateChanged( boolean isPaused ) {
        if( isPaused ) {
            savedResetState = resetBtn.isEnabled();
            resetBtn.setEnabled( true );
        }
        else {
            resetBtn.setEnabled( savedResetState );
        }
    }

    public void clockStarted( ClockEvent clockEvent ) {
        stateChanged( false );
    }

    public void clockPaused( ClockEvent clockEvent ) {
        stateChanged( true );
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {
    }

    /**
     * Responds to ticks of the stopwatch clock
     *
     * @param event
     */
    public void clockTicked( ClockEvent event ) {
        if( isRunning ) {
            runningTime += event.getSimulationTimeChange();
            displayRunningTime();
        }
    }

    //-----------------------------------------------------------------
    // Event and Listener definitions
    //-----------------------------------------------------------------

    public interface StopwatchListener extends EventListener {
        void start( StopwatchEvent event );

        void stop( StopwatchEvent event );

        void reset( StopwatchEvent event );
    }

    public class StopwatchEvent extends EventObject {

        public StopwatchEvent( Object source ) {
            super( source );
        }

        public StopwatchPanel getStopwatch() {
            return (StopwatchPanel)getSource();
        }
    }

    public void addListener( StopwatchListener listener ) {
        stopwatchEventChannel.addListener( listener );
    }

    public void removeListener( StopwatchListener listener ) {
        stopwatchEventChannel.removeListener( listener );
    }
}
