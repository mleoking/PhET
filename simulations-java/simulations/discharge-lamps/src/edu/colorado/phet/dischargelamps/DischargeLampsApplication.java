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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.view.AtomGraphic;
import edu.colorado.phet.lasers.view.EnergyLevelGraphic;

/**
 * DischargeLampsApp
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampsApplication extends PiccoloPhetApplication {
    static private FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );

    /**
     * @param args
     */
    public DischargeLampsApplication( String[] args ) {
        super( args, SimStrings.getInstance().getString( "DischargeLampsApplication.title" ),
               SimStrings.getInstance().getString( "DischargeLampsApplication.title" ),
               new PhetApplicationConfig( new String[0], new FrameSetup.NoOp(), new PhetResources( "discharge-lamps" ) ).getVersion().formatForTitleBar(),
               frameSetup );

        // Determine the resolution of the screen
        DischargeLampModule singleAtomModule = new SingleAtomModule( SimStrings.getInstance().getString( "ModuleTitle.SingleAtomModule" ),
                                                                     new SwingClock( 1000 / DischargeLampsConfig.FPS, DischargeLampsConfig.DT ) );

        // Set the energy rep strategy for the AtomGraphics
        AtomGraphic.setEnergyRepColorStrategy( new AtomGraphic.GrayScaleStrategy() );

        double maxSpeed = 0.1;
        DischargeLampModule multipleAtomModule = new MultipleAtomModule( SimStrings.getInstance().getString( "ModuleTitle.MultipleAtomModule" ),
                                                                         new SwingClock( 1000 / DischargeLampsConfig.FPS, DischargeLampsConfig.DT ),
                                                                         30,
                                                                         maxSpeed );
        setModules( new Module[]{singleAtomModule,
                multipleAtomModule} );
        setActiveModule( singleAtomModule );

        // Add some options in a menu
        JMenu optionsMenu = new JMenu( DischargeLampsResources.getString( "menu.options" ));
        JMenuItem simulationSpeedMI = new JMenuItem( DischargeLampsResources.getString( "simulation.speed" ));
        simulationSpeedMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final IClock clock = PhetApplication.instance().getActiveModule().getClock();
                double dt = clock.getSimulationTimeChange();
                final JSlider clockTickSlider = new JSlider( 1, 15, (int) DischargeLampsConfig.DT );
                clockTickSlider.setMajorTickSpacing( 2 );
                clockTickSlider.setMinorTickSpacing( 1 );
                clockTickSlider.setPaintTicks( true );
                clockTickSlider.setPaintLabels( true );
                clockTickSlider.setSnapToTicks( true );
                clockTickSlider.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        DischargeLampsConfig.DT = clockTickSlider.getValue();
                        ( (Clock) clock ).setTimingStrategy( new TimingStrategy.Constant( clockTickSlider.getValue() ) );
                    }
                } );
                int confirm = JOptionPane.showConfirmDialog( getPhetFrame(), clockTickSlider, DischargeLampsResources.getString( "simulation.speed" ), 
                                                             JOptionPane.OK_CANCEL_OPTION );
                // If the user canceled, reset the clock to its original value
                if ( confirm == JOptionPane.CANCEL_OPTION ) {
                    ( (Clock) clock ).setTimingStrategy( new TimingStrategy.Constant( dt ) );
                }
            }
        } );
        optionsMenu.add( simulationSpeedMI );
        getPhetFrame().addMenu( optionsMenu );
    }

    /**
     * @param args
     */
    public static void main( final String[] args ) {
        EnergyLevelGraphic.showLifetimeLabelText = false;//see Unfuddle #431
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {

                PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
                phetLookAndFeel.initLookAndFeel();
                // Tell SimStrings where the simulations-specific strings are
                SimStrings.getInstance().init( args, DischargeLampsConfig.localizedStringsPath );
                SimStrings.getInstance().addStrings( LaserConfig.localizedStringsPath );

                DischargeLampsApplication application = new DischargeLampsApplication( args );

                application.getPhetFrame().setResizable( false );
                application.startApplication();
            }
        } );
    }

}
