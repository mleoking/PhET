/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 5, 2003
 * Time: 4:16:28 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.sound.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.SoundResources;

public class ClockPanelLarge extends JPanel {

    private JTextField readoutTextField = new JTextField();
    private NumberFormat clockFormat = new DecimalFormat( "0.0000" );
    private String[] startStopStr = new String[]{PhetCommonResources.getString( "Common.StopwatchPanel.start" ),
            PhetCommonResources.getString( "Common.StopwatchPanel.stop" )};
    private EventChannel eventRegistry = new EventChannel( ClockPanelListener.class );
    private JButton resetBtn;

    private ModelElement modelTickCounter;
    private BaseModel model;
    private double runningTime = 0;

    private static final int STOPPED=0;
    private static final int RUNNING=1;
    private int state = STOPPED;
    private JButton startStopBtn;

    public ClockPanelLarge( BaseModel model ) {
        this.model = model;
        setBackground( new Color( 237, 225, 113 ) );

        // Clock readout
        setBorder( BorderFactory.createRaisedBevelBorder() );
        readoutTextField = new JTextField( 5 );
        Font clockFont = readoutTextField.getFont();
        readoutTextField.setFont( new Font( clockFont.getName(), Font.BOLD, 16 ) );
        readoutTextField.setEditable( false );
        readoutTextField.setHorizontalAlignment( JTextField.RIGHT );
        readoutTextField.setText( clockFormat.format( 0 ) );

        // Model element that keeps track of the time ticked off by the model
        runningTime = 0;
        modelTickCounter = new ModelElement() {
            public void stepInTime( double dt ) {
                runningTime = dt + runningTime;
                readoutTextField.setText( clockFormat.format( runningTime * SoundConfig.CLOCK_SCALE_FACTOR ) );
            }
        };

        // Start/stop button
        startStopBtn = new JButton( startStopStr[0] );
        startStopBtn.addActionListener( new StartStopActionListener() );

        // Reset button
        resetBtn = new JButton( PhetCommonResources.getString( "Common.StopwatchPanel.reset" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                resetClock();
            }
        } );

        // Lay out the panel
        this.setLayout( new GridBagLayout() );
        int rowIdx = 0;
        int padX = 0;
        int padY = 0;
        Insets insets = new Insets( 5, 5, 5, 5 );
        GridBagConstraints gbc = null;
        gbc = new GridBagConstraints( 0, rowIdx, 2, 1, 1, 1,
                                      GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                      insets, padX, padY );
        this.add( new JLabel( SoundResources.getString( "ClockPanelLarge.SimulationTime" ) ), gbc );
        rowIdx++;
        gbc = new GridBagConstraints( 0, rowIdx, 1, 1, 1, 1,
                                      GridBagConstraints.EAST, GridBagConstraints.NONE,
                                      insets, padX, padY );
        this.add( readoutTextField, gbc );
        gbc = new GridBagConstraints( 1, rowIdx, 1, 1, 1, 1,
                                      GridBagConstraints.WEST, GridBagConstraints.NONE,
                                      insets, padX, padY );
        this.add( new JLabel( SoundResources.getString( "ClockPanelLarge.Seconds" ) ), gbc );
        rowIdx++;
        gbc = new GridBagConstraints( 0, rowIdx, 2, 1, 1, 1,
                                      GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                      insets, padX, padY );
        gbc.gridy = ++rowIdx;
        this.add( startStopBtn, gbc );
        gbc.gridy = ++rowIdx;
        this.add( resetBtn, gbc );
    }

    private void resetClock() {

        model.removeModelElement( modelTickCounter );
        runningTime = 0;
        modelTickCounter.stepInTime( 0 );

        ClockPanelEvent event = new ClockPanelEvent( this );
        event.setReset( true );
        event.setRunning( false );
    }

    public void clockTicked( IClock c, double dt ) {
        String s = clockFormat.format( c.getSimulationTime() * SoundConfig.CLOCK_SCALE_FACTOR );
        readoutTextField.setText( s );
    }

    public void setClockPanelVisible( boolean isVisible ) {
        setVisible( isVisible );
    }

    public boolean isClockPanelVisible() {
        return isVisible();
    }

    private class StartStopActionListener implements ActionListener {

        public void actionPerformed( ActionEvent e ) {
            toggle();
        }

        private void toggle() {
            if( state == STOPPED ) {
                model.addModelElement( modelTickCounter );
                ClockPanelEvent event = new ClockPanelEvent( this );
                event.setRunning( true );
//                clockPanelListenerProxy.clockPaneEventOccurred( event );
                resetBtn.setEnabled( false );
                state=RUNNING;
            }
            else {
                model.removeModelElement( modelTickCounter );
                ClockPanelEvent event = new ClockPanelEvent( this );
                event.setRunning( false );
//                clockPanelListenerProxy.clockPaneEventOccurred( event );
                resetBtn.setEnabled( true );
                state=STOPPED;
            }
            startStopBtn.setText( startStopStr[state] );
        }
    }

    //-----------------------------------------------------------------
    // Event stuff
    //-----------------------------------------------------------------
    public interface ClockPanelListener extends EventListener {
        void clockPaneEventOccurred( ClockPanelEvent event );
    }

    public class ClockPanelEvent extends EventObject {
        boolean isRunning = true;
        boolean isReset;

        public ClockPanelEvent( Object source ) {
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

    public void addListener( ClockPanelListener listener ) {
        eventRegistry.addListener( listener );
    }

    public void removeListener( ClockPanelListener listener ) {
        eventRegistry.removeListener( listener );
    }
}
