// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.umd.cs.piccolo.PCanvas;

/**
 * Main application for the Magnifying Glass prototype.
 * This prototype was used to investigate the magnifying glass view in Acid-Base Solutions.
 * We wanted to know whether it was feasible to show molecule images, how many images were 
 * needed, what order they should be rendered in, how big the magnifying glass needed to be,
 * etc. (see control panel for other properties)
 * <p>
 * This prototype addressed weak acids only, no other solutions.
 * <p>
 * This work informed the implementation of the MagnifyingGlassNode and associated
 * classes in the final simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MagnifyingGlassPrototypeApplication extends PiccoloPhetApplication {

    public MagnifyingGlassPrototypeApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new MGPModule( getPhetFrame(), isDeveloperControlsEnabled() ) );
        addModule( new EmptyModule() );
    }
    
    public static class EmptyModule extends Module {
        public EmptyModule() {
            super( "Some Other Tab", new ConstantDtClock( 1000, 1 ) );
            setSimulationPanel( new PCanvas() );
            setClockControlPanel( null );
        }
    }

    public static void main( final String[] args ) throws ClassNotFoundException {
        new PhetApplicationLauncher().launchSim( args, ABSConstants.PROJECT, ABSConstants.FLAVOR_MAGNIFYING_GLASS_PROTOTYPE, MagnifyingGlassPrototypeApplication.class );
    }
    
}
