/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics;

import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplication.TabbedPaneType;
import edu.colorado.phet.common.phetcommon.util.QuickProfiler;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.nuclearphysics.module.alpharadiation.AlphaRadiationModule;


public class AlphaRadiationApplication extends AbstractNuclearPhysicsApplication {

    private AlphaRadiationModule _alphaRadiationModule;

    public AlphaRadiationApplication( PhetApplicationConfig config ) {
        super( config );
        
        Frame parentFrame = getPhetFrame();
    
        _alphaRadiationModule = new AlphaRadiationModule( parentFrame );
        addModule( _alphaRadiationModule );
    }
    
    /**
     * Main entry point.
     *
     * @param args command line arguments
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public static void main( final String[] args ) {
        final QuickProfiler profiler=new QuickProfiler( "Nuclear Physics 2 Startup Time");
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
    
                PhetApplicationConfig config = new PhetApplicationConfig( args, NuclearPhysicsConstants.FRAME_SETUP, NuclearPhysicsResources.getResourceLoader(), "alpha-radiation" );
                
                PhetLookAndFeel p = new PhetLookAndFeel();
                p.setBackgroundColor( NuclearPhysicsConstants.CONTROL_PANEL_COLOR );
                p.initLookAndFeel();
    
                // Create the application.
                AlphaRadiationApplication app = new AlphaRadiationApplication( config );
    
                // Start the application.
                app.startApplication();
                System.out.println( "profiler = " + profiler );
            }
        } );
    }
}
