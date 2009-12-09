/* Copyright 2009, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModule;
import edu.colorado.phet.reactantsproductsandleftovers.module.realreaction.RealReactionModule;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModule;

/**
 * The main application for the "Reactants, Products and Leftovers" simulation.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ReactantsProductsAndLeftoversApplication extends PiccoloPhetApplication {

    public ReactantsProductsAndLeftoversApplication( PhetApplicationConfig config ) {
        super( config );
        Frame parentFrame = getPhetFrame();
        addModule( new GameModule( parentFrame ) ); //XXX move this last after development
        addModule( new SandwichShopModule( parentFrame ) );
        addModule( new RealReactionModule( parentFrame ) );
    }

    public static void main( final String[] args ) throws ClassNotFoundException {
        new PhetApplicationLauncher().launchSim( args, RPALConstants.PROJECT_NAME, ReactantsProductsAndLeftoversApplication.class );
    }
}
