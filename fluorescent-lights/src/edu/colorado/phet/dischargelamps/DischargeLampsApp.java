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
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.view.AtomGraphic;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
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
               frameSetup );

        // Determine the resolution of the screen
        DischargeLampModule singleAtomModule = new SingleAtomModule( SimStrings.get( "ModuleTitle.SingleAtomModule" ),
                                                                     new SwingClock( 1000 / DischargeLampsConfig.FPS, DischargeLampsConfig.DT ) );

        // Set the energy rep strategy for the AtomGraphics
        AtomGraphic.setEnergyRepColorStrategy( new AtomGraphic.GrayScaleStrategy() );

        double maxSpeed = 0.1;
        DischargeLampModule multipleAtomModule = new MultipleAtomModule( SimStrings.get( "ModuleTitle.MultipleAtomModule" ),
                                                                         new SwingClock( 1000 / DischargeLampsConfig.FPS, DischargeLampsConfig.DT ),
                                                                         30,
                                                                         DischargeLampsConfig.NUM_ENERGY_LEVELS,
                                                                         maxSpeed );
        setModules( new Module[]{singleAtomModule,
                                 multipleAtomModule} );
        setActiveModule( singleAtomModule );

        // Add some options in a menu
        JMenu optionsMenu = new JMenu( "Options" );
        JMenuItem simulationSpeedMI = new JMenuItem( "Simulation speed..." );
        simulationSpeedMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final IClock clock = PhetApplication.instance().getActiveModule().getClock();
                double dt = clock.getSimulationTimeChange();
                final JSlider clockTickSlider = new JSlider( 1, 15, (int)DischargeLampsConfig.DT );
                clockTickSlider.setMajorTickSpacing( 2 );
                clockTickSlider.setMinorTickSpacing( 1 );
                clockTickSlider.setPaintTicks( true );
                clockTickSlider.setPaintLabels( true );
                clockTickSlider.setSnapToTicks( true );
                clockTickSlider.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        DischargeLampsConfig.DT = clockTickSlider.getValue();
                        clock.setSimulationDt( clockTickSlider.getValue() );
                    }
                } );
                int confirm = JOptionPane.showConfirmDialog( getPhetFrame(), clockTickSlider, "Simulation speed",
                                                             JOptionPane.OK_CANCEL_OPTION );
                // If the user canceled, reset the clock to its original value
                if( confirm == JOptionPane.CANCEL_OPTION ) {
                    clock.setSimulationDt( dt );
                }
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
