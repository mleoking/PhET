// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations;

import javax.swing.JMenu;
import javax.swing.JSeparator;

import edu.colorado.phet.balancingchemicalequations.control.BCEOptionsMenu;
import edu.colorado.phet.balancingchemicalequations.control.DeveloperColorsMenuItem;
import edu.colorado.phet.balancingchemicalequations.module.game.GameModule;
import edu.colorado.phet.balancingchemicalequations.module.introduction.IntroductionModule;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * The main application for the "Balancing Chemical Equations" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalancingChemicalEquationsApplication extends PiccoloPhetApplication {

    public BalancingChemicalEquationsApplication( PhetApplicationConfig config ) {
        super( config );

        // Global settings
        BCEGlobalProperties globalProperties = new BCEGlobalProperties( getPhetFrame() );

        // modules
        addModule( new IntroductionModule( globalProperties ) );
        addModule( new GameModule( globalProperties ) );

        // Options menu
        getPhetFrame().addMenu( new BCEOptionsMenu( globalProperties ) );

        // Developer menu
        JMenu developerMenu = getPhetFrame().getDeveloperMenu();
        developerMenu.add( new JSeparator() );
        developerMenu.add( new DeveloperColorsMenuItem( globalProperties ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Show answers", globalProperties.showAnswers ) );
        developerMenu.add( new JSeparator() );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Game: play all equations", globalProperties.playAllEquations ) );
        developerMenu.add( new JSeparator() );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Game Popups: add Show Why button", globalProperties.showWhyButton ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Game Popups: show close button", globalProperties.showPopupCloseButton ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Game Popups: show top bar", globalProperties.showPopupTitleBar ) );
    }

    public static void main( final String[] args ) throws ClassNotFoundException {
        new PhetApplicationLauncher().launchSim( args, BCEConstants.PROJECT_NAME, BCEConstants.FLAVOR_BCE, BalancingChemicalEquationsApplication.class );
    }
}
