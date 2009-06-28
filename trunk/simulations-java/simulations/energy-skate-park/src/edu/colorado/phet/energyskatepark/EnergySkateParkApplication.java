
package edu.colorado.phet.energyskatepark;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.energyskatepark.serialization.EnergySkateParkIO;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkFrameSetup;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.colorado.phet.energyskatepark.view.swing.EnergySkateParkTestMenu;
import edu.colorado.phet.energyskatepark.view.swing.EnergySkateParkTrackMenu;

public class EnergySkateParkApplication extends PhetApplication {

    private EnergySkateParkModule module;
    public static double SIMULATION_TIME_DT = 0.03;
    public static final boolean IGNORE_THERMAL_DEFAULT = false;

    public EnergySkateParkApplication( PhetApplicationConfig config ) {
        super( config );

        EnergySkateParkOptions options = parseOptions( config.getCommandLineArgs() );

        module = new EnergySkateParkModule( "Module", new ConstantDtClock( 30, SIMULATION_TIME_DT ), getPhetFrame(), options );
        setModules( new Module[]{module} );
//        getPhetFrame().addMenu( new EnergySkateParkOptionsMenu( module ) );
        if ( config.isDev() ) {
            getPhetFrame().addMenu( new EnergySkateParkTestMenu( this ) );
        }
//        getPhetFrame().addMenu( new EnergySkateParkTrackMenu( this ) );

        JMenuItem saveItem = new JMenuItem( EnergySkateParkStrings.getString( "file-menu.save" ) + "..." );
        saveItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    EnergySkateParkIO.save( module );
                }
                catch( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );

        JMenuItem openItem = new JMenuItem( EnergySkateParkStrings.getString( "file-menu.open" ) + "..." );
        openItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    EnergySkateParkIO.open( module );
                }
                catch( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        getPhetFrame().addFileMenuItem( openItem );
        getPhetFrame().addFileMenuItem( saveItem );

        getPhetFrame().addFileMenuSeparator();
    }

    public EnergySkateParkModule getModule() {
        return module;
    }

    public void startApplication() {
        super.startApplication();
        module.getPhetPCanvas().requestFocus();
    }

    private static EnergySkateParkOptions parseOptions( String[] args ) {
        //todo: not yet implemented
        return new EnergySkateParkOptions();
    }

    public static void main( final String[] args ) {

        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new EnergySkateParkApplication( config );
            }
        };

        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, EnergySkateParkConstants.PROJECT_NAME );
        appConfig.setLookAndFeel( new EnergySkateParkLookAndFeel() );
        appConfig.setFrameSetup( new EnergySkateParkFrameSetup() );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }

}
