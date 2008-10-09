/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics;

import java.awt.Color;
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig.ApplicationConstructor;
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
     */
    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new AlphaRadiationApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, 
        		NuclearPhysicsConstants.PROJECT_NAME, NuclearPhysicsConstants.FLAVOR_NAME_ALPHA_DECAY );
        
        PhetLookAndFeel p = new PhetLookAndFeel();
        p.setBackgroundColor( NuclearPhysicsConstants.ALPHA_DECAY_CONTROL_PANEL_COLOR );
        appConfig.setLookAndFeel( p );

        appConfig.launchSim();
    }
}
