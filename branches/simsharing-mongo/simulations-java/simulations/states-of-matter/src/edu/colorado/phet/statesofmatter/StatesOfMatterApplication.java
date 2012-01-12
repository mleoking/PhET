// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.statesofmatter.module.atomicinteractions.AtomicInteractionsModule;
import edu.colorado.phet.statesofmatter.module.phasechanges.PhaseChangesModule;
import edu.colorado.phet.statesofmatter.module.solidliquidgas.SolidLiquidGasModule;
import edu.colorado.phet.statesofmatter.view.AbstractStatesOfMatterApplication;

/**
 * Main application class for the States of Matter simulation.
 *
 * @author John Blanco
 */
public class StatesOfMatterApplication extends AbstractStatesOfMatterApplication implements IProguardKeepClass {

    /**
     * Constructor.
     *
     * @param config
     */
    public StatesOfMatterApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
    }

    /**
     * Initializes the modules.
     */
    private void initModules() {

        Frame parentFrame = getPhetFrame();

        addModule( new SolidLiquidGasModule() );
        addModule( new PhaseChangesModule( true ) );
        addModule( new AtomicInteractionsModule( false ) );
    }

    /**
     * Main entry point.
     *
     * @param args command line arguments
     */
    public static void main( final String[] args ) {

        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new StatesOfMatterApplication( config );
            }
        };

        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, StatesOfMatterConstants.PROJECT_NAME,
                                                                     StatesOfMatterConstants.FLAVOR_STATES_OF_MATTER );

        PhetLookAndFeel p = new PhetLookAndFeel();
        p.setBackgroundColor( StatesOfMatterConstants.CONTROL_PANEL_COLOR );
        appConfig.setLookAndFeel( p );

        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
