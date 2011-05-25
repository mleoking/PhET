// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.IntroModel;
import edu.colorado.phet.sugarandsaltsolutions.intro.view.RemoveSaltSugarButton;
import edu.umd.cs.piccolo.PNode;

/**
 * Main application for PhET's "Sugar and Salt Solutions" simulation but using a combined remove salt sugar button instead of separate, for testing purposes.
 * Kelly decided on May 24 to delete this variant since the separate buttons are better, but I'm keeping this around in case she is overriden or we change our minds
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsApplicationCombinedButton extends SugarAndSaltSolutionsApplication {
    public SugarAndSaltSolutionsApplicationCombinedButton( PhetApplicationConfig config ) {
        super( config );
    }

    //Use the consolidated RemoveSaltSugarButton instead of separate buttons for each solute
    //This feature is implemented as an override so that we can still use reflection in PhetApplicationLauncher.launchSim
    protected Function1<IntroModel, PNode> getRemoveSolutesControl() {
        return new Function1<IntroModel, PNode>() {
            public PNode apply( IntroModel model ) {
                return new RemoveSaltSugarButton( model );
            }
        };
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, SugarAndSaltSolutionsApplication.NAME, SugarAndSaltSolutionsApplicationCombinedButton.class );
    }
}