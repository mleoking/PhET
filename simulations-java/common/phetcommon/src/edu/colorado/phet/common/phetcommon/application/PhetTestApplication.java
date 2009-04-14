package edu.colorado.phet.common.phetcommon.application;

import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * Use this for test applications.
 * It will not send statistics messages or check for updates.
 */
public class PhetTestApplication {
    
    private String[] args;
    private FrameSetup frameSetup;
    private ArrayList modules = new ArrayList();

    public PhetTestApplication( String[] args ) {
        this( args, null );
    }
    
    public PhetTestApplication( String[] args, FrameSetup frameSetup ) {
        this.args = processArgs( args );
        this.frameSetup = frameSetup;
    }
    
    private String[] processArgs( String[] args ) {
        ArrayList list = new ArrayList( Arrays.asList( args ) );
        list.add( "-statistics-off" );
        list.add( "-updates-off" );
        return (String[]) list.toArray( new String[list.size()] );
    }
    
    public void addModule( Module module ) {
        modules.add( module );
    }

    public void startApplication() {
        ApplicationConstructor applicationConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                PhetApplication phetApplication = new PhetApplication( config ) {
                };
                phetApplication.setModules( (Module[]) modules.toArray( new Module[modules.size()] ) );
                return phetApplication;
            }
        };
        PhetApplicationConfig config = new PhetApplicationConfig( args, "phetcommon" );
        if ( frameSetup != null ) {
            config.setFrameSetup( frameSetup );
        }
        new PhetApplicationLauncher().launchSim( config, applicationConstructor );
    }

    public void setModules( Module[] m ) {
        modules.addAll( Arrays.asList( m ) );
    }

    public PhetFrame getPhetFrame() {
        return PhetApplication.getInstance().getPhetFrame();
    }

}
