package edu.colorado.phet.testproject.processorintensive;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.testproject.EmptyModule;
import edu.colorado.phet.testproject.TestProjectConstants;

/**
 * The application hammers the processor.
 * Used for testing workaround to "gray rectangle" problem (Unfuddle #89).
 */
public class ProcessorIntensiveApplication extends PiccoloPhetApplication {

    public ProcessorIntensiveApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new ProcessorIntensiveModule() );
        addModule( new EmptyModule( "Another Tab" ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( new PhetApplicationConfig( args, TestProjectConstants.PROJECT_NAME, "processor-intensive" ), ProcessorIntensiveApplication.class );
    }
}
