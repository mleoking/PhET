// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lasers.view;

import java.awt.*;

import edu.colorado.phet.common.quantum.model.Atom;

/**
 * Author: Sam Reid
 * Aug 23, 2007, 7:37:54 PM
 */
public class LevelIconAnnotatedAtomGraphic extends AnnotatedAtomGraphic {
    public LevelIconAnnotatedAtomGraphic( Component component, Atom atom ) {
        super( component, atom );
        super.setNumberGraphicText();
    }

    protected void setNumberGraphicText() {
//        super.setNumberGraphicText();
    }
}
