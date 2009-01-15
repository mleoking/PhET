package edu.colorado.phet.simtemplate;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.simtemplate.module.example.ExampleModule;
import edu.colorado.phet.simtemplate.module.intensive.ProcessorIntensiveModule;

public class IntensiveSimApplication extends SimTemplateApplication {
    public IntensiveSimApplication( PhetApplicationConfig config ) {
        super( config );
    }

    protected ExampleModule getFirstModule( Frame parentFrame ) {
        return new ProcessorIntensiveModule( parentFrame );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, SimTemplateConstants.PROJECT_NAME, IntensiveSimApplication.class );
    }
}
