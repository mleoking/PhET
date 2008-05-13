/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample;

import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * MVCApplication is the main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MVCExampleApplication extends PiccoloPhetApplication {

    public static final PhetResources RESOURCE_LOADER = new PhetResources( "mvc-example" );
    
    /**
     * Sole constructor.
     *
     * @param args command line arguments
     */
    public MVCExampleApplication( PhetApplicationConfig config )
    {
        super( config );
        Frame parentFrame = getPhetFrame();
        MVCModule module = new MVCModule( parentFrame );
        addModule( module );
    }

    /**
     * Main entry point.
     *
     * @param args command line arguments
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public static void main( final String[] args ) {
        
        /*
         * Wrap the body of main in invokeLater, so that all initialization occurs
         * in the event dispatch thread. Sun now recommends doing all Swing init in
         * the event dispatch thread. And the Piccolo-based tabs in TabbedModulePanePiccolo
         * seem to cause startup deadlock problems if they aren't initialized in the
         * event dispatch thread. Since we don't have an easy way to separate Swing and
         * non-Swing init, we're stuck doing everything in invokeLater.
         */
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {
                
                FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );

                PhetApplicationConfig config = new PhetApplicationConfig( args, FRAME_SETUP, RESOURCE_LOADER );

                // Create the application.
                MVCExampleApplication app = new MVCExampleApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}
