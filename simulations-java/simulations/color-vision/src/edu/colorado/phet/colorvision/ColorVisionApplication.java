/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.colorvision;

import java.awt.Color;

import javax.swing.SwingUtilities;

import edu.colorado.phet.colorvision.view.BoundsOutliner;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * ColorVisionApplication is the main application for the Color Vision simulation.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ColorVisionApplication extends PiccoloPhetApplication {

    // The background color for the application's main Frame.
    private static final Color BACKGROUND = new Color( 148, 166, 158 );

    // Whether to turn on rendering of PhetGraphic bounds
    private static final boolean BOUNDS_OUTLINE_ENABLED = false; // DEBUG

    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public ColorVisionApplication( PhetApplicationConfig config ) {
        super( config );

        Module rgbBulbsModule = new RgbBulbsModule();
        addModule( rgbBulbsModule );
        Module singleBulbModule = new SingleBulbModule();
        addModule( singleBulbModule );
        
        getPhetFrame().setBackground( BACKGROUND );
    }

    /**
     * Main entry point for the PhET Color Vision application.
     * 
     * @param args command line arguments
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
                BoundsOutliner.setEnabled( BOUNDS_OUTLINE_ENABLED ); // DEBUG
                
                // Initialize look-and-feel
                PhetLookAndFeel laf = new PhetLookAndFeel();
                laf.initLookAndFeel();
                
                FrameSetup frameSetup = new FrameSetup.CenteredWithSize(  ColorVisionConstants.APP_FRAME_WIDTH,  ColorVisionConstants.APP_FRAME_HEIGHT );
                PhetApplicationConfig config = new PhetApplicationConfig( args, frameSetup, ColorVisionResources.getResourceLoader() );

                PhetApplication app = new ColorVisionApplication( config );
                app.startApplication();
            }
        } );
    }
}