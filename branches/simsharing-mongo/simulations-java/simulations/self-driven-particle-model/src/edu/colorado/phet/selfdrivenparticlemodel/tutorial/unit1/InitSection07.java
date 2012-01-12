// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;

public class InitSection07 extends Page {

    public InitSection07( final BasicTutorialCanvas basicPage ) {
        super( basicPage );
        String initText = "You should try to perform the activity on each page, " +
                          "however, if you get stuck, " +
                          "you can navigate forward and backward by pressing the arrow keys " +
                          "(after clicking somewhere in the background).  " +
                          "To skip units, press 1 [introduction], 2 [emergent properties] or 3 [explorer].\n\nPress the Next button to continue.";
        super.setText( initText );
        super.artificialAdvance();
        super.showNextButton();
    }

}
