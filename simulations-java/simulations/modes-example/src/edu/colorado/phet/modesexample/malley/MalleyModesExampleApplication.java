// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.modesexample.malley;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Top-level class to demonstrate my mode architecture.
 * <p/>
 * In this architecture, a mode is a lightweight container for the things that change when the mode changes.
 * A mode has no responsibility for creating or interacting with these things, it is solely a container.
 * As such, it introduces no new coupling, and allows us to use the MVC paradigm typical of mode-less PhET sims.
 * <p/>
 * As in mode-less sims, the Module has responsibility for instantiating the model, view and control components.
 * The Module also has responsibility for creating the modes, and watching for mode changes.
 * Watching for mode changes is done via a Property<T> called currentMode.
 * When currentMode changes, the Module is responsible for swapping the things that need to change
 * (eg, model, canvas, control panel, part of a control panel,... whatever) and it gets these things
 * from the mode container.
 * <p/>
 * In the user interface, mode is changed via a control, in this case a set of radio buttons.
 * Those radio buttons control the value of the Module's currentMode property, which the Module is observing.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MalleyModesExampleApplication extends PiccoloPhetApplication {

    public MalleyModesExampleApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new RectanglesModule( getPhetFrame() ) );
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "modes-example", MalleyModesExampleApplication.class );
    }
}
