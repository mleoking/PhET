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
TODO:

I18n
Improve kit controls and add kit number?
Add in the BCE's "ding" when a collection box is filled
Add in "Reset Kit" button
Make kits sufficient to fill the entire collection box

NOTES (questions):

---mostly completed (2): Currently the highlight showing a completed collection box is (to my eyes) somewhat faint. Should the colors be changed, and/or should we add in
a sound when a collection box is filled?
    I like the colors used in this sim http://www.colorado.edu/physics/phet/dev/bending-light/0.00.41/ with the grey background on the toolbars and
    the light blue color (it’s water in the bending light sim, but I don’t think that will be confusing). What do you think of these colors? Then
    the box outline will stand out more.
    I like the idea of adding a sound, perhaps the single ding sound that’s in BCE game after balancing one of the equations would be good.

(3): Should we add in an indication that a just-created molecule could be put in an associated collection box?
    I interviewed a student on this yesterday, he was I 4th grade. He noticed right away that the two tabs had different ‘collection areas’, but it
    took him almost 10 minutes to realize he could put molecules in those boxes. What if the outline of the collection box blinks when the student
    makes an appropriate molecule?

(4): How should resetting work in general? Should we have a "Reset All" button, and should this reset back to the very initial configuration of the
tab like normal? Should we have a "Reset Kit" button that pulls all atoms back to the buckets (and pulls molecules out of collection boxes if
necessary)?
    Middle schoolers are quite timid about using reset. I like the idea of having a "reset kit" button, that way minimal work is undone if students
    select this. It will need to appear only after a student has gotten a molecule made from that kit into a collection box.

(6): Current behavior of the molecule readout is in conflict with the design doc. Doc shows "H20" above the molecule in ONE part, and in another
shows the formula above and the molecule name BELOW. Sim shows the molecule name (e.g. "water") above currently, but could easily switch to showing
the formula in addition (additional padding between molecules may be necessary)
    We’ll need to discuss this at the CHEM meeting. I think if possible it would be good to allow the greatest flexibility, with an option for name
    and formula, though which one should be the default I’m not sure. Problem the formula should be the default, because it’s simple to understand as
    the symbols match the atoms.

(7): For kits and collection boxes (particularly randomly generated ones), is it ok for 1 kit to not completely fill one multiple-molecule collection
box? For instance, if we want 2 C2H2, a kit with four carbons would require MANY more molecules in our database, so we may want to have a larger
quantity of kits than collection boxes.
    I think it will work to have multiple kits necessary to fill the multiple molecule collection boxes. The student yesterday had no trouble (once
    he figured out he needed to fill the collection boxes) understanding that he may need to flip between kits to find what he needs. He actually was
    really bummed out when he realized that in the current version, there is not enough of the right atoms to fill the collection boxes! He told me I
    should let him know what that gets fixed so he can keep playing.

(8): Is the scale of the pseudo-3d molecules in the collection boxes OK? (We can only fit 2 C02s with the current size, but we could increase the
width of the actual collection boxes a bit safely.
    I think it’s important to have the molecules show as large as possible. What do you think about having the molecules be larger in the first tab,
    and in the second tab start out the same size as in the first tab, but as you add molecules they get smaller (to fit)? Would that look weird? I’m
    also ok with having the boxes be different sizes (for example chloromethane box larger than hydrogen box). We should discuss this further at the
    CHEM meeting, I’m not the best at visualizing these types of changes.

(9): I am not terribly pleased with the spacing on the 2nd tab collection box labels. The subscripts are farther down and larger than shown in the
design doc, so the layout gives it more space. Should we either (a) tweak the subscripts to be smaller, (b) try to manually reduce the padding, or
(c) combine the 1st and 2nd lines somehow, or put the 2nd line to the left of the collection box? We could even put the quantity (e.g. 1NH3) inside
the collection box on the left, and shift the molecules to the right.
    I have one suggestion for how the formulas look on the second tab, can the subscript be moved slightly to the right from the letter on the left.
    It looks to me like the subscript touches the letter to it’s left, and I would rather have a slight amount of space there. Maybe half the distance
    as is between the coefficient and the formula.
    On this tab, since the focus is on coefficients versus subscripts, the collection box will always be labeled with the formula, not the full name,
    so it won’t get much longer. I’m ok with the idea of having the quantity on the same line, as long as it doesn’t distract from, or clutter, the
    goal formula and coefficient (I guess this would mean that a decent amount of space needs to separate the goal:formula from the quantity amount).

(10): For molecular formulas, I was under the impression that the Hill System was the standard, however this gives us "H3N" for "NH3". Is there a
procedure or set of steps that can be used to get an accurate naming convention? (One way that may work would be to break the Hill convention and if
there is no carbon, STILL put the hydrogen first).
    There are a number of categories of naming systems, and unfortunately the molecules included in the sim span two cases, regular covalent compounds
    and organic compounds. For all molecules that are not organic (these contain C and H but do not include HCN) the names follow the rule that the
    symbols are listed in increasing order of electronegativity. Here’s a table of electronegativey ( http://en.wikipedia.org/wiki/Electronegativity
    scroll down to periodic table view). For example, in HF, hydrogen has an electronegativity of 2.2 and fluorine has an electronegativity of 3.98,
    so the order is HF.
    For organic compounds, you start with C, then H, then whatever else...unless it’s an an alcohol in the case of CH3OH (this way of naming indicates
    something about the structure, that you have an OH group hanging off the side). So in short, would be naming rules that work for the molecules
    included in the sim:
        * If don’t include both C and H, name in increasing order of electronegativity
        * If includes C and H name with C first, then H, then whatever else.
    Exceptions:
        * NH3, name like organic where the H is second
        * HCN not considered organic, so name as regular covalent
        * CH3OH, name shows structure, doesn’t follow either naming convention.

(11): We currently have no design for the "You have filled all of your collection boxes. Would you like to fill a different set of collection boxes?"
dialog. Should I add in a prototype, or should we sketch a quick design first?
    If it’s easy, why don’t you do something like...have a popup box that appears and maybe have the outline for the whole toolbar light up. If you
    can do that by Thursday, we can all discuss further. Or, if you have a better idea, go for that.

(13): Currently atoms all move with the same speed in different situations (falling back to buckets, moving back to the play area, breaking apart).
I'd like to speed up movement when the atom is far from its destination, but keep it about the same when it is close (like breaking up). Is that OK?
    Sounds good to me. As a note, I noticed that when the student yesterday would break apart molecules, sometimes that atoms would bump into another
    atom and form a molecule spontaneously. The student didn’t seem confused by this, but I’ll bring it up at the meeting.

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
