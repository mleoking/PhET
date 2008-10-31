/**
 * Class: MicrowaveApplication
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.microwaves;


import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

public class MicrowavesApplication extends PiccoloPhetApplication {

    public MicrowavesApplication( PhetApplicationConfig config ) {
        super( config );

        // modules
        Module oneMoleculesModule = new OneMoleculeModule();
        Module singleLineOfMoleculesModule2 = new SingleLineOfMoleculesModule2();
        Module manyMoleculesModule = new ManyMoleculesModule();
        Module coffeeModule = new CoffeeModule();
        Module[] modules = new Module[]{
                oneMoleculesModule,
                singleLineOfMoleculesModule2,
                manyMoleculesModule,
                coffeeModule
        };
        addModules( modules );
        setStartModule( singleLineOfMoleculesModule2 );
        
        // menus
        getPhetFrame().addMenu( new MicrowaveModule.ControlMenu() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, MicrowavesConfig.PROJECT_NAME, MicrowavesApplication.class );
    }
}
