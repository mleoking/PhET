/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics;



import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.QuickProfiler;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.nuclearphysics.module.chainreaction.ChainReactionModule;
import edu.colorado.phet.nuclearphysics.module.fissiononenucleus.FissionOneNucleusModule;
import edu.colorado.phet.nuclearphysics.module.nuclearreactor.NuclearReactorModule;

/**
 * NuclearFissionApplication is the main application class for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * 
 */
public class NuclearFissionApplication extends AbstractNuclearPhysicsApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    private FissionOneNucleusModule _fissionOneNucleusModule;
    private ChainReactionModule _chainReactionModule;
    private NuclearReactorModule _nuclearReactorModule;

    /**
     * Sole constructor.
     *
     * @param config - The configuration for the application.
     */
    public NuclearFissionApplication( PhetApplicationConfig config )
    {
        super( config );
        initTabbedPane();
        
        Frame parentFrame = getPhetFrame();

        _fissionOneNucleusModule = new FissionOneNucleusModule( parentFrame );
        addModule( _fissionOneNucleusModule );
    
        _chainReactionModule = new ChainReactionModule( parentFrame );
        addModule( _chainReactionModule );
    
        _nuclearReactorModule = new NuclearReactorModule( parentFrame );
        addModule( _nuclearReactorModule );

        initMenubar( config.getCommandLineArgs() );
    }

    /**
     * Main entry point.
     *
     * @param args command line arguments
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public static void main( final String[] args ) {
        final QuickProfiler profiler=new QuickProfiler( "Nuclear Fission 2 Startup Time");
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
    
                PhetApplicationConfig config = new PhetApplicationConfig( args, NuclearPhysicsConstants.FRAME_SETUP, 
                        NuclearPhysicsResources.getResourceLoader(), "nuclear-fission" );
                
                PhetLookAndFeel p = new PhetLookAndFeel();
                p.setBackgroundColor( NuclearPhysicsConstants.CONTROL_PANEL_COLOR );
                p.initLookAndFeel();
    
                // Create the application.
                NuclearFissionApplication app = new NuclearFissionApplication( config );
    
                // Start the application.
                app.startApplication();
                System.out.println( "profiler = " + profiler );
            }
        } );
    }
}
