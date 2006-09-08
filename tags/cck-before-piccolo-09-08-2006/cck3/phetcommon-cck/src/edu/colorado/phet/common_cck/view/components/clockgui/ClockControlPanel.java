// ClockDialog

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 9:12:44 PM
 */
package edu.colorado.phet.common_cck.view.components.clockgui;

import edu.colorado.phet.common_cck.model.clock.AbstractClock;
import edu.colorado.phet.common_cck.model.clock.ClockStateListener;
import edu.colorado.phet.common_cck.model.clock.ThreadedClock;
import edu.colorado.phet.common_cck.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClockControlPanel extends JPanel implements ClockStateListener {

    private AbstractClock clock;
    private SpinnerNumberModel spinnerModelDT;
    private JSpinner dtSpinner;
    private SpinnerNumberModel spinnerModelWT;
    JSpinner waitTimeSpinner = new JSpinner();
    private ThreadPriorityPanel tpp;

    private static void setFont( JSpinner j, Font f ) {
        Component[] c = j.getEditor().getComponents();
        for( int i = 0; i < c.length; i++ ) {
            c[i].setFont( f );
        }
    }

    Font bigfont = new Font( "dialog", 0, 20 );

    public ClockControlPanel( ThreadedClock clock ) {
        super( new FlowLayout() );
        spinnerModelWT = new SpinnerNumberModel( (int)clock.getDelay(), 0, 1000, 5 );
        dtSpinner = new JSpinner( spinnerModelDT );
        dtSpinner.setFont( new Font( "dialog", 0, 24 ) );
        waitTimeSpinner = new JSpinner( spinnerModelWT );
        setFont( dtSpinner, bigfont );
        setFont( waitTimeSpinner, bigfont );

        this.clock = clock;
        buildClockControlPanel();
    }

    private void syncPanelToClock() {
        double dt = clock.getDt();
        dtSpinner.setValue( new Double( dt ) );
        waitTimeSpinner.setValue( new Integer( (int)clock.getDelay() ) );
    }

    private static class ThreadPriorityPanel extends JPanel {
        private ThreadedClock c;
        private JRadioButton min;
        private JRadioButton norm;
        private JRadioButton max;

        public ThreadPriorityPanel( final ThreadedClock c ) {
            this.c = c;

            min = new JRadioButton( SimStrings.get( "Common.ClockControlPanel.Minumum" ) );
            min.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    c.setThreadPriority( Thread.MIN_PRIORITY );
                }
            } );

            norm = new JRadioButton( SimStrings.get( "Common.ClockControlPanel.Normal" ) );
            norm.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    c.setThreadPriority( Thread.NORM_PRIORITY );
                }
            } );
            max = new JRadioButton( SimStrings.get( "Common.ClockControlPanel.Maximum" ) );
            max.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    c.setThreadPriority( Thread.MAX_PRIORITY );
                }
            } );
            int priority = c.getThreadPriority();

            if( priority == Thread.MIN_PRIORITY ) {
                min.setSelected( true );
            }
            else if( priority == Thread.MAX_PRIORITY ) {
                max.setSelected( true );
            }
            else if( priority == Thread.NORM_PRIORITY ) {
                norm.setSelected( true );
            }
            ButtonGroup bg = new ButtonGroup();
            bg.add( min );
            bg.add( norm );
            bg.add( max );
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.blue, 2 ),
                                                         SimStrings.get( "Common.ClockControlPanel.BorderTitle" ) ) );
            add( max );
            add( norm );
            add( min );
        }
    }

    private void buildClockControlPanel() {
        JLabel dtlabel = new JLabel( SimStrings.get( "Common.ClockControlPanel.ChangeSeconds" ) + ":" );
        dtlabel.setFont( bigfont );
        this.add( dtlabel );
        dtSpinner.setValue( new Double( clock.getDt() ) );
        this.add( dtSpinner );
        dtSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateClockDT();
            }
        } );

        JLabel sleeplabel = new JLabel( SimStrings.get( "Common.ClockControlPanel.SleepInterval" ) + ":" );
        sleeplabel.setFont( bigfont );
        this.add( sleeplabel );
        waitTimeSpinner.setValue( new Integer( (int)clock.getDelay() ) );
        this.add( waitTimeSpinner );
        waitTimeSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateClockWaitTime();
            }
        } );
        if( clock instanceof ThreadedClock ) {
            ThreadedClock tc = (ThreadedClock)clock;
            tpp = new ThreadPriorityPanel( tc );
            add( tpp );
        }
    }

    public float getDt() {
        return ( (Double)this.dtSpinner.getValue() ).floatValue();
    }

    private void updateClockDT() {
        this.clock.setDt( getDt() );
    }

    private void updateClockWaitTime() {
        this.clock.setDelay( getSleepInterval() );
    }

    public int getSleepInterval() {
        return ( (Integer)waitTimeSpinner.getValue() ).intValue();
    }

    public void setClock( AbstractClock c ) {
        this.clock = c;
        syncPanelToClock();
    }

    public void delayChanged( int waitTime ) {
        this.waitTimeSpinner.setValue( new Integer( waitTime ) );
    }

    public void dtChanged( double dt ) {
        this.dtSpinner.setValue( new Double( dt ) );
    }

    public void threadPriorityChanged( int priority ) {
        if( priority == Thread.MIN_PRIORITY ) {
            tpp.min.setSelected( true );
        }
        else if( priority == Thread.MAX_PRIORITY ) {
            tpp.max.setSelected( true );
        }
        else if( priority == Thread.NORM_PRIORITY ) {
            tpp.norm.setSelected( true );
        }
    }

    public void pausedStateChanged( boolean b ) {
    }
}
