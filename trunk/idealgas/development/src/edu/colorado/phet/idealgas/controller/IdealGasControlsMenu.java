/*
 * Class: IdealGasControlsMenu
 * Package: edu.colorado.phet.idealgas.controller
 *
 * Created by: Ron LeMaster
 * Date: Nov 14, 2002
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.controller.PhetFrame;
import edu.colorado.phet.controller.ControlsMenu;
import edu.colorado.phet.graphics.ClockDialog;
import edu.colorado.phet.idealgas.graphics.IdealGasMonitorPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 */
public class IdealGasControlsMenu extends ControlsMenu {

    public IdealGasControlsMenu( final JFrame parent, final PhetApplication application ) {

        super( parent, application );

        // Enable/disable sounds
        JMenuItem enableSoundsMI = new JMenuItem( "Enable/disable Sounds");
        enableSoundsMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                toggleSounds();
            }
        } );
        this.add( enableSoundsMI );

        // Show/hide things
        JMenuItem showMonitorsMI = new JMenuItem( "Show/hide Readouts" );
        showMonitorsMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                toggleReadouts();
            }
        } );
        this.add( showMonitorsMI );

        JMenuItem showControlsMI = new JMenuItem( "Show/hide Controls" );
        showControlsMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                toggleControls();
            }
        } );
        this.add( showControlsMI );

        // Show/hide spinners for gauges
        JMenuItem showSpinnersMI = new JMenuItem( "Show Gauge Spinners");
        showSpinnersMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                toggleShowGaugeSpnners();
            }
        } );
        this.add( showSpinnersMI );
    }

    /**
     *
     */
    private IdealGasApplication getIdealGasApplication() {
        return (IdealGasApplication)PhetApplication.instance();
    }
    /**
     *
     */
    private void toggleReadouts() {
        getIdealGasApplication().getPhetMainPanel().toggleMonitorPanel();
    }

    /**
     *
     */
    private void toggleControls() {
        getIdealGasApplication().getPhetMainPanel().toggleControlPanel();
    }

    /**
     *
     */
    private void toggleSounds() {
        getIdealGasApplication().toggleSounds();
    }

    /**
     *
     */
    private void toggleShowGaugeSpnners() {
        boolean spinnersVisible = getMonitorPanel().isOomSpinnersVisible();
        getMonitorPanel().setOomSpinnersVisible( !spinnersVisible );
    }

    /**
     *
     */
    private IdealGasMonitorPanel getMonitorPanel() {
        return (IdealGasMonitorPanel)getIdealGasApplication().getPhetMainPanel().getMonitorPanel();
    }
}
