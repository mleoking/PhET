/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;

/**
 * User: Sam Reid
 * Date: Aug 23, 2005
 * Time: 3:23:29 AM
 * Copyright (c) Aug 23, 2005 by Sam Reid
 */

public class ExplainCollisionsSection40 extends Page {
    public ExplainCollisionsSection40( BasicTutorialCanvas basicPage ) {
        super( basicPage );
        setText( "So each particle chooses its direction as the average of " +
                 "the directions of all particles within its visual range (including itself).\n\n" +
                 "Notice that as soon as the particles saw each other, they set their angle to the average angle, " +
                 "and subsequently travel as a pair in a straight line." );
        showNextButton();
        artificialAdvance();
    }


}
