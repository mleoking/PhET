/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.coreadditions;


/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 5, 2003
 * Time: 4:16:28 PM
 * To change this template use Options | File Templates.
 */

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.util.EventChannel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.EventListener;
import java.util.EventObject;

public class StopwatchPanel extends JPanel implements ClockTickListener {

    private int numSigDigits = 4;
    private JTextField clockTF = new JTextField();
    private NumberFormat clockFormat = new DecimalFormat( "0.0000" );
    private AbstractClock clock;
    private String[] startStopStr;
    private EventChannel stopwatchEventChannel = new EventChannel( StopwatchListener.class );
    private StopwatchListener stopwatchListenerProxy = (StopwatchListener)stopwatchEventChannel.getListenerProxy();
    private JButton resetBtn;

    public StopwatchPanel( AbstractClock clock ) {
        this.clock = new SwingTimerClock( clock.getDt(), 100 );
        this.clock.addClockTickListener( this );
        setBackground( new Color( 237, 225, 113 ) );

        // Clock readout
        setBorder( BorderFactory.createRaisedBevelBorder() );
        clockTF = new JTextField( 5 );
        Font clockFont = clockTF.getFont();
        clockTF.setFont( new Font( clockFont.getName(), Font.BOLD, 16 ) );
        clockTF.setEditable( false );
        clockTF.setHorizontalAlignment( JTextField.RIGHT );

        // Initialize the contents of the clockTF
        clockTicked( new ClockTickEvent( clock, 0 ));

        // Start/stop button
        startStopStr = new String[2];
        startStopStr[0] = "Start";
        startStopStr[1] = "Stop";
        JButton startStopBtn = new JButton( startStopStr[0] );
        startStopBtn.addActionListener( new StartStopActionListener( startStopBtn ) );

        // Reset button
        resetBtn = new JButton( "Reset" );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                resetClock();
            }
        } );

        // Lay out the panel
        this.setLayout( new FlowLayout( ));
        add( startStopBtn );
        add( resetBtn );
        add( clockTF );
        if( true ) return;

        this.setLayout( new GridBagLayout() );
        int rowIdx = 0;
        int padX = 0;
        int padY = 0;
        Insets insets = new Insets( 5, 5, 5, 5 );
        GridBagConstraints gbc = null;
        gbc = new GridBagConstraints( 0, rowIdx, 2, 1, 1, 1,
                                      GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                      insets, padX, padY );
        this.add( new JLabel( "Simulation Time" ), gbc );
        gbc.gridy = ++rowIdx;
        gbc.gridwidth = 1;
        this.add( clockTF, gbc );
        gbc = new GridBagConstraints( 1, rowIdx, 1, 1, 1, 1,
                                      GridBagConstraints.WEST, GridBagConstraints.NONE,
                                      insets, padX, padY );
        this.add( new JLabel( "sec" ), gbc );
        gbc = new GridBagConstraints( 0, rowIdx, 2, 1, 1, 1,
                                      GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                      insets, padX, padY );
        gbc.gridy = ++rowIdx;
        this.add( startStopBtn, gbc );
        gbc.gridy = ++rowIdx;
        this.add( resetBtn, gbc );
    }

    private void resetClock() {
        clock.resetRunningTime();
        clockTicked( new ClockTickEvent( clock, 0 ) );
        StopwatchEvent event = new StopwatchEvent( this );
        event.setReset( true );
        event.setRunning( false );
        stopwatchListenerProxy.reset( event );
    }

    public void clockTicked( ClockTickEvent event ) {
        AbstractClock c = (AbstractClock)event.getSource();
        // TODO: scale factor goes here
        String s = clockFormat.format( c.getRunningTime() );
        clockTF.setText( s );
    }

    public void setClockPanelVisible( boolean isVisible ) {
        setVisible( isVisible );
    }

    public boolean isClockPanelVisible() {
        return isVisible();
    }

    private class StartStopActionListener implements ActionListener {
        JButton startStopBtn;
        int startStopState = 0;

        public StartStopActionListener( JButton startStopBtn ) {
            this.startStopBtn = startStopBtn;
        }

        public void actionPerformed( ActionEvent e ) {
            if( startStopState == 0 ) {
                clock.start();
                StopwatchEvent event = new StopwatchEvent( this );
                event.setRunning( true );
                stopwatchListenerProxy.start( event );
                resetBtn.setEnabled( false );
            }
            else {
                clock.stop();
                StopwatchEvent event = new StopwatchEvent( this );
                event.setRunning( false );
                stopwatchListenerProxy.stop( event );
                resetBtn.setEnabled( true );
            }
            // Set the proper text for the button, and do a bunch of messing arround to
            // set the size so it doesn't change when the text changes.
            startStopState = ( startStopState + 1 ) % 2;
            Dimension prevSize = startStopBtn.getSize();
            startStopBtn.setText( startStopStr[startStopState] );
            Dimension currSize = startStopBtn.getSize();
            Dimension newSize = new Dimension( Math.max( prevSize.width, currSize.width), currSize.height );
            startStopBtn.setPreferredSize( newSize );
        }
    }

    //-----------------------------------------------------------------
    // Event stuff
    //-----------------------------------------------------------------
    public interface StopwatchListener extends EventListener {
        void start( StopwatchEvent event );
        void stop( StopwatchEvent event );
        void reset( StopwatchEvent event );
    }

    public class StopwatchEvent extends EventObject {
        boolean isRunning = true;
        boolean isReset;

        public StopwatchEvent( Object source ) {
            super( source );
        }

        public boolean isRunning() {
            return isRunning;
        }

        public void setRunning( boolean running ) {
            isRunning = running;
        }

        public boolean isReset() {
            return isReset;
        }

        public void setReset( boolean reset ) {
            isReset = reset;
        }
    }

    public void addListener( StopwatchListener listener ) {
        stopwatchEventChannel.addListener( listener );
    }

    public void removeListener( StopwatchListener listener ) {
        stopwatchEventChannel.removeListener( listener );
    }
}
