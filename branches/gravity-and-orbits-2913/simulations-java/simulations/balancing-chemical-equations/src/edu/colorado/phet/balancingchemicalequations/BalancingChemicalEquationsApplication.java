// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations;

import javax.swing.*;

import edu.colorado.phet.balancingchemicalequations.control.BCEOptionsMenu;
import edu.colorado.phet.balancingchemicalequations.developer.ColorsMenuItem;
import edu.colorado.phet.balancingchemicalequations.developer.PreviewMoleculesMenuItem;
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
        {
            JMenu developerMenu = getPhetFrame().getDeveloperMenu();
            developerMenu.add( new JSeparator() );
            developerMenu.add( new PreviewMoleculesMenuItem( globalProperties.frame ) );
            developerMenu.add( new ColorsMenuItem( globalProperties ) );
            developerMenu.add( new PropertyCheckBoxMenuItem( "Show answers", globalProperties.answersVisible ) );
            developerMenu.add( new JSeparator() );
            developerMenu.add( new PropertyCheckBoxMenuItem( "Game: play all equations", globalProperties.playAllEquations ) );
            developerMenu.add( new JSeparator() );
            developerMenu.add( new PropertyCheckBoxMenuItem( "Game Popups: add \"Show Why\" button", globalProperties.popupsWhyButtonVisible ) );
            developerMenu.add( new PropertyCheckBoxMenuItem( "Game Popups: show close button", globalProperties.popupsCloseButtonVisible ) );
            developerMenu.add( new PropertyCheckBoxMenuItem( "Game Popups: show top bar", globalProperties.popupsTitleBarVisible ) );
        }
    }

    public static void main( final String[] args ) throws ClassNotFoundException {
        new PhetApplicationLauncher().launchSim( args, BCEConstants.PROJECT_NAME, BCEConstants.FLAVOR_BCE, BalancingChemicalEquationsApplication.class );
    }
}
