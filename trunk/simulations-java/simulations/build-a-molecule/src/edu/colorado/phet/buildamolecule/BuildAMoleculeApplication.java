// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.module.CollectMultipleModule;
import edu.colorado.phet.buildamolecule.module.LargerMoleculesModule;
import edu.colorado.phet.buildamolecule.module.MakeMoleculeModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/*
NOTES (questions):

(1): Should we support showing a "schematic" view in the collection boxes? (possibly hard to see).
If so, should we only show a "canonical" version? Molecules with a vertical layout may be very difficult to see.

(2): Currently the highlight showing a completed collection box is (to my eyes) somewhat faint. Should the colors be changed, and/or should we add in
a sound when a collection box is filled?

(3): Should we add in an indication that a just-created molecule could be put in an associated collection box?

(4): How should resetting work in general? Should we have a "Reset All" button, and should this reset back to the very initial configuration of the
tab like normal? Should we have a "Reset Kit" button that pulls all atoms back to the buckets (and pulls molecules out of collection boxes if
necessary)?

(5): Current behavior when trying to add a molecule to a "full" collection box just pulls it back out to the play area. Is this acceptable?

(6): Current behavior of the molecule readout is in conflict with the design doc. Doc shows "H20" above the molecule in ONE part, and in another
shows the formula above and the molecule name BELOW. Sim shows the molecule name (e.g. "water") above currently, but could easily switch to showing
the formula in addition (additional padding between molecules may be necessary)

(7): For kits and collection boxes (particularly randomly generated ones), is it ok for 1 kit to not completely fill one multiple-molecule collection
box? For instance, if we want 2 C2H2, a kit with four carbons would require MANY more molecules in our database, so we may want to have a larger
quantity of kits than collection boxes.

(8): Is the scale of the pseudo-3d molecules in the collection boxes OK? (We can only fit 2 C02s with the current size, but we could increase the
width of the actual collection boxes a bit safely.

(9): I am not terribly pleased with the spacing on the 2nd tab collection box labels. The subscripts are farther down and larger than shown in the
design doc, so the layout gives it more space. Should we either (a) tweak the subscripts to be smaller, (b) try to manually reduce the padding, or
(c) combine the 1st and 2nd lines somehow, or put the 2nd line to the left of the collection box? We could even put the quantity (e.g. 1NH3) inside
the collection box on the left, and shift the molecules to the right.

(10): For molecular formulas, I was under the impression that the Hill System was the standard, however this gives us "H3N" for "NH3". Is there a
procedure or set of steps that can be used to get an accurate naming convention? (One way that may work would be to break the Hill convention and if
there is no carbon, STILL put the hydrogen first).

(11): We currently have no design for the "You have filled all of your collection boxes. Would you like to fill a different set of collection boxes?"
dialog. Should I add in a prototype, or should we sketch a quick design first?

(12): For the 3rd tab (3D view), it should be possible to change the view style. We could offer the user "Filled spheres", "Ball-and-stick", and
possibly more. Do we want to integrate those style changes into the design, or should I work them into a prototype?

 */

/**
 * The main application for this simulation.
 */
public class BuildAMoleculeApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public BuildAMoleculeApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     */
    private void initModules() {

        Frame parentFrame = getPhetFrame();

        Module makeMoleculeModule = new MakeMoleculeModule( parentFrame );
        addModule( makeMoleculeModule );

        Module collectMultipleModule = new CollectMultipleModule( parentFrame );
        addModule( collectMultipleModule );

        Module largerMolecules = new LargerMoleculesModule( parentFrame );
        addModule( largerMolecules );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar() {

        // Create main frame.
        final PhetFrame frame = getPhetFrame();

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        // add menu items here, or in a subclass on OptionsMenu
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        // add items to the Developer menu here...
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        /*
         * If you want to customize your application (look-&-feel, window size, etc)
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */
        new PhetApplicationLauncher().launchSim( args, BuildAMoleculeConstants.PROJECT_NAME, BuildAMoleculeApplication.class );
    }
}
