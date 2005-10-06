/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.LaserConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * DischargeLampsApp
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampsApp extends PhetApplication {
    static private FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );
    static private String version = "1.00";

    /**
     * @param args
     */
    public DischargeLampsApp( String[] args ) {
        super( args, SimStrings.get( "DischargeLampsApplication.title" ),
               SimStrings.get( "DischargeLampsApplication.title" ),
               version,
               new SwingTimerClock( DischargeLampsConfig.DT, DischargeLampsConfig.FPS, AbstractClock.FRAMES_PER_SECOND ),
               true,
               frameSetup );

        // Determine the resolution of the screen
        DischargeLampModule singleAtomModule = new SingleAtomModule( SimStrings.get( "ModuleTitle.SingleAtomModule" ),
                                                                     getClock() );

        double maxSpeed = 0.1;
        DischargeLampModule multipleAtomModule = new MultipleAtomModule( SimStrings.get( "ModuleTitle.MultipleAtomModule" ),
                                                                         getClock(), 30,
                                                                         DischargeLampsConfig.NUM_ENERGY_LEVELS,
                                                                         maxSpeed );
        setModules( new Module[]{singleAtomModule,
                                 multipleAtomModule} );
        setInitialModule( singleAtomModule );

        // Add some options in a menu
        JMenu optionsMenu = new JMenu( "Options" );
        JMenuItem simulationSpeedMI = new JMenuItem( "Simulation speed..." );
        simulationSpeedMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JSlider clockTickSlider = new JSlider( 1, 15, (int)DischargeLampsConfig.DT );
                clockTickSlider.setMajorTickSpacing( 2 );
                clockTickSlider.setMinorTickSpacing( 1 );
                clockTickSlider.setPaintTicks( true );
                clockTickSlider.setPaintLabels( true );
                clockTickSlider.setSnapToTicks( true );
                JOptionPane.showMessageDialog( getPhetFrame(), clockTickSlider, "Simulation speed",
                                               JOptionPane.OK_OPTION );
                DischargeLampsConfig.DT = clockTickSlider.getValue();
                getClock().setDt( clockTickSlider.getValue() );
            }
        } );
        optionsMenu.add( simulationSpeedMI );
        getPhetFrame().addMenu( optionsMenu );
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {

        // Tell SimStrings where the simulations-specific strings are
        SimStrings.setStrings( DischargeLampsConfig.localizedStringsPath );
        SimStrings.setStrings( LaserConfig.localizedStringsPath );

        DischargeLampsApp app = new DischargeLampsApp( args );
        app.startApplication();
    }

}
