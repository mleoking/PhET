// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers;

import java.awt.Frame;

import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.reactantsproductsandleftovers.dev.DevTestGameRewardMenuItem;
import edu.colorado.phet.reactantsproductsandleftovers.dev.DevTestReactionsMenuItem;
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
        
        // modules
        Frame parentFrame = getPhetFrame();
        addModule( new SandwichShopModule( parentFrame ) );
        addModule( new RealReactionModule( parentFrame ) );
        addModule( new GameModule( parentFrame ) );
        
        // Developer menu
        JMenu developerMenu = getPhetFrame().getDeveloperMenu();
        developerMenu.add( new DevTestReactionsMenuItem() );
        developerMenu.add( new DevTestGameRewardMenuItem() );

        // Teacher menu
        getPhetFrame().addMenu( new RPALTeacherMenu() );
    }

    public static void main( final String[] args ) throws ClassNotFoundException {
        new PhetApplicationLauncher().launchSim( args, RPALConstants.PROJECT_NAME, RPALConstants.FLAVOR_RPAL, ReactantsProductsAndLeftoversApplication.class );
    }
}
