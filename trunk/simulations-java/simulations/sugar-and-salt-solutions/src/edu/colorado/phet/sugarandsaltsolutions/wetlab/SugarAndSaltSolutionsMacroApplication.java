// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.wetlab;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.menu.TeacherMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources;
import edu.colorado.phet.sugarandsaltsolutions.macro.MacroModule;

/**
 * This sim was written to accommodate a chemistry wet lab in Fall 2011, this sim just shows the macro tab.
 * Can probably be deleted after usage.  No need to internationalize this version--it is just used for one experiment.
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsMacroApplication extends PiccoloPhetApplication {

    private static final String NAME = "sugar-and-salt-solutions-macro";

    public SugarAndSaltSolutionsMacroApplication( PhetApplicationConfig config ) {
        super( config );

        //Create the modules
        final GlobalState globalState = new GlobalState( new SugarAndSaltSolutionsColorScheme(), config, getPhetFrame(), true );
        addModule( new MacroModule( globalState ) );

        //Add an options menu with the option to change the background to white for use on projectors in bright classrooms
        getPhetFrame().addMenu( new TeacherMenu() {{addWhiteBackgroundMenuItem( globalState.colorScheme.whiteBackground );}} );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, SugarAndSaltSolutionsResources.PROJECT_NAME, NAME, SugarAndSaltSolutionsMacroApplication.class );
    }
}