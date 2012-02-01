// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkOptions;
import edu.colorado.phet.energyskatepark.serialization.EnergySkateParkIO;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkOptionsMenu;
import edu.colorado.phet.energyskatepark.view.swing.EnergySkateParkTestMenu;
import edu.colorado.phet.energyskatepark.view.swing.EnergySkateParkTrackMenu;

public class EnergySkateParkApplication extends PhetApplication {

    private final AbstractEnergySkateParkModule module;
    public static final double SIMULATION_TIME_DT = 0.03;
    public static final boolean IGNORE_THERMAL_DEFAULT = false;

    public EnergySkateParkApplication( PhetApplicationConfig config ) {
        super( config );

        module = new EnergySkateParkModule( null, "Module", getPhetFrame(), new EnergySkateParkOptions(), false );
        setModules( new Module[] { module } );

        if ( config.isDev() ) {
            getPhetFrame().addMenu( new EnergySkateParkOptionsMenu( module ) );
        }
        if ( config.isDev() ) {
            getPhetFrame().addMenu( new EnergySkateParkTestMenu( this ) );
        }
        getPhetFrame().addMenu( new EnergySkateParkTrackMenu( this ) );

        JMenuItem saveItem = new JMenuItem( EnergySkateParkResources.getString( "file-menu.save" ) + "..." );
        saveItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    EnergySkateParkIO.save( module );
                }
                catch ( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );

        JMenuItem openItem = new JMenuItem( EnergySkateParkResources.getString( "file-menu.open" ) + "..." );
        openItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    EnergySkateParkIO.open( module );
                }
                catch ( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        getPhetFrame().addFileMenuItem( openItem );
        getPhetFrame().addFileMenuItem( saveItem );

        getPhetFrame().addFileMenuSeparator();
    }

    public AbstractEnergySkateParkModule getModule() {
        return module;
    }

    public void startApplication() {
        super.startApplication();
        module.getPhetPCanvas().requestFocus();
    }

    public static void main( final String[] args ) {

        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new EnergySkateParkApplication( config );
            }
        };

        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, "energy-skate-park" );
        appConfig.setLookAndFeel( new EnergySkateParkLookAndFeel() );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }

}
