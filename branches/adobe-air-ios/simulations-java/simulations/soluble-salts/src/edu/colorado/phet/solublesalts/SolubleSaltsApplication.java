// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.solublesalts.control.DeveloperMenuSetup;
import edu.colorado.phet.solublesalts.module.ConfigurableSaltModule;
import edu.colorado.phet.solublesalts.module.RealSaltsModule;
import edu.colorado.phet.solublesalts.module.SodiumChlorideModule;
import edu.colorado.phet.solublesalts.view.IonGraphic;

/**
 * SolubleSaltsApplication
 *
 * @author Ron LeMaster
 */
public class SolubleSaltsApplication extends PiccoloPhetApplication {

    public SolubleSaltsApplication( PhetApplicationConfig config ) {
        super( config );

        Module moduleA = new RealSaltsModule( new SolubleSaltsClock() );
        Module moduleB = new ConfigurableSaltModule( new SolubleSaltsClock() );
        Module moduleC = new SodiumChlorideModule( new SolubleSaltsClock(), SolubleSaltResources.getString( "Module.sodiumChloride" ) );

        setModules( new Module[] { moduleC, moduleA, moduleB } );

        // developer options, a bit non-standard
        if ( isDeveloperControlsEnabled() ) {
            DeveloperMenuSetup.setup( getPhetFrame().getDeveloperMenu(), getPhetFrame() );
        }
    }

    public static class SolubleSaltsClock extends SwingClock {
        public SolubleSaltsClock() {
            super( 1000 / SolubleSaltsConfig.FPS, SolubleSaltsConfig.DT );
        }
    }

    public static void main( final String[] args ) {
        PhetApplicationConfig p = new PhetApplicationConfig( args, "soluble-salts" );
        new PhetApplicationLauncher().launchSim( p, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {

                for ( int i = 0; i < args.length; i++ ) {
                    String arg = args[i];
                    if ( arg.equals( "-b" ) ) {
                        IonGraphic.showBondIndicators( true );
                    }
                    if ( arg.startsWith( "-w" ) ) {
                        SolubleSaltsConfig.DEFAULT_WATER_LEVEL = Integer.parseInt( arg.substring( 3 ) );
                    }
                    if ( arg.equals( "-o" ) ) {
                        SolubleSaltsConfig.ONE_CRYSTAL_ONLY = true;
                    }
                    if ( arg.startsWith( "-s=" ) ) {
                        SolubleSaltsConfig.DEFAULT_LATTICE_STICK_LIKELIHOOD = Double.parseDouble( arg.substring( 3 ) );
                    }
                    if ( arg.startsWith( "-d=" ) ) {
                        SolubleSaltsConfig.DEFAULT_LATTICE_DISSOCIATION_LIKELIHOOD = Double.parseDouble( arg.substring( 3 ) );
                    }
                    if ( arg.startsWith( "-c=" ) ) {
                        SolubleSaltsConfig.CONCENTRATION_CALIBRATION_FACTOR = Double.parseDouble( arg.substring( 3 ) );
                    }
                    if ( arg.startsWith( "debug=" ) ) {
                        SolubleSaltsConfig.DEBUG = true;
                    }
                }

                Color blueBackground = new Color( 230, 250, 255 );
                Color grayBackground = new Color( 220, 220, 220 );
                UIManager.put( "Panel.background", blueBackground );
                UIManager.put( "MenuBar.background", grayBackground );
                UIManager.put( "Menu.background", grayBackground );
                UIManager.put( "TabbedPane.background", blueBackground );
                UIManager.put( "TabbedPane.selected", blueBackground );

                PiccoloPhetApplication app = new SolubleSaltsApplication( config );

                return app;
            }
        } );
    }
}
