/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 5, 2003
 * Time: 4:16:28 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common_sound.model.BaseModel;
import edu.colorado.phet.common_sound.model.ModelElement;
import edu.colorado.phet.common_sound.model.clock.AbstractClock;
import edu.colorado.phet.common_sound.util.EventChannel;
import edu.colorado.phet.sound.SoundConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.EventListener;
import java.util.EventObject;

public class ClockPanelLarge extends JPanel {

    private JTextField clockTF = new JTextField();
    private NumberFormat clockFormat = new DecimalFormat( "0.0000" );
    private String[] startStopStr;
    private EventChannel eventRegistry = new EventChannel( ClockPanelListener.class );
    private ClockPanelListener clockPanelListenerProxy = (ClockPanelListener)eventRegistry.getListenerProxy();
    private JButton resetBtn;

    private ModelElement modelTickCounter;
    private BaseModel model;
    private double runningTime = 0;
    private int startStopState = 0;
    private JButton startStopBtn;

    public ClockPanelLarge( BaseModel model ) {
        this.model = model;
        setBackground( new Color( 237, 225, 113 ) );

        // Clock readout
        setBorder( BorderFactory.createRaisedBevelBorder() );
        clockTF = new JTextField( 5 );
        Font clockFont = clockTF.getFont();
        clockTF.setFont( new Font( clockFont.getName(), Font.BOLD, 16 ) );
        clockTF.setEditable( false );
        clockTF.setHorizontalAlignment( JTextField.RIGHT );
        clockTF.setText( clockFormat.format( 0 ) );

        // Model element that keeps track of the time ticked off by the model
        runningTime = 0;
        modelTickCounter = new ModelElement() {
            public void stepInTime( double dt ) {
                runningTime = dt + runningTime;
                clockTF.setText( clockFormat.format( runningTime * SoundConfig.CLOCK_SCALE_FACTOR ) );
            }
        };

        // Start/stop button
        startStopStr = new String[2];

        startStopStr[0] = PhetCommonResources.getString( "Common.StopwatchPanel.start" );
        startStopStr[1] = PhetCommonResources.getString( "Common.StopwatchPanel.stop" );
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
        this.add( new JLabel( SimStrings.get( "ClockPanelLarge.SimulationTime" ) ), gbc );
        rowIdx++;
        gbc = new GridBagConstraints( 0, rowIdx, 1, 1, 1, 1,
                                      GridBagConstraints.EAST, GridBagConstraints.NONE,
                                      insets, padX, padY );
        this.add( clockTF, gbc );
        gbc = new GridBagConstraints( 1, rowIdx, 1, 1, 1, 1,
                                      GridBagConstraints.WEST, GridBagConstraints.NONE,
                                      insets, padX, padY );
        this.add( new JLabel( SimStrings.get( "ClockPanelLarge.Seconds" ) ), gbc );
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
        clockPanelListenerProxy.clockPaneEventOccurred( event );
    }

    public void clockTicked( AbstractClock c, double dt ) {
        String s = clockFormat.format( c.getRunningTime() * SoundConfig.CLOCK_SCALE_FACTOR );
        clockTF.setText( s );
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
            if( startStopState == 0 ) {
                model.addModelElement( modelTickCounter );
                ClockPanelEvent event = new ClockPanelEvent( this );
                event.setRunning( true );
                clockPanelListenerProxy.clockPaneEventOccurred( event );
                resetBtn.setEnabled( false );
            }
            else {
                model.removeModelElement( modelTickCounter );
                ClockPanelEvent event = new ClockPanelEvent( this );
                event.setRunning( false );
                clockPanelListenerProxy.clockPaneEventOccurred( event );
                resetBtn.setEnabled( true );
            }
            startStopState = ( startStopState + 1 ) % 2;
            startStopBtn.setText( startStopStr[startStopState] );
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
