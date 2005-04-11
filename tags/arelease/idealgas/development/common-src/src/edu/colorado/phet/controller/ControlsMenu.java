/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 16, 2003
 * Time: 9:56:45 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.controller;

import edu.colorado.phet.graphics.ClockDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The base menu for application controls
 */
public class ControlsMenu extends JMenu {

    private PhetApplication application;
    private PhetFrame parent;
    private boolean showRunningTime = false;

    public ControlsMenu( final JFrame parent, final PhetApplication application ) {

        super( "Controls" );
        this.application = application;
        this.parent = (PhetFrame)parent;

        // Add the clock menu item
        JMenuItem clockControlMI = new JMenuItem( "Edit Clock Properties..." );
        clockControlMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showClockDialog();
            }
        } );
        this.add( clockControlMI );

        // Add the single-step selection
        JMenuItem singleStepMI = new JMenuItem( "Single Step" );
        singleStepMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                toggleSingleStep();
            }
        });
        this.add( singleStepMI );

        // Single-step backward
        JMenuItem stepBackMI = new JMenuItem( "Step Backward" );
        stepBackMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                stepBack();
            }
        });
        this.add( stepBackMI );

        // Show/hide the running time
        JMenuItem showClockMI = new JMenuItem( "Show Running Time");
        showClockMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                toggleShowRunningTime();
            }
        } );
        this.add( showClockMI );
    }

    /**
     *
     */
    protected void showClockDialog() {
        ClockDialog clockDialog = new ClockDialog( parent, application.getPhysicalSystem().getSystemClock() );
        clockDialog.show();
    }

    /**
     *
     */
    protected void toggleSingleStep() {
        application.toggleSingleStep();
    }

    /**
     *
     */
    protected void stepBack() {
        application.getPhysicalSystem().stepInTime( -application.getPhysicalSystem().getSystemClock().getDt() );
    }

    /**
     *
     */
    protected void toggleShowRunningTime() {
        showRunningTime = !showRunningTime;
//        application.getPhetMainPanel().getMonitorPanel().setClockPanelVisible( showRunningTime );
    }
}
