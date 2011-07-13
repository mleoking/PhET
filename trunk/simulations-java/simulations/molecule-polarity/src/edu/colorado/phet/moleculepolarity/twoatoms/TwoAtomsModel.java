// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import java.awt.*;

import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.MPModel;

/**
 * Model for the "One Atom" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsModel extends MPModel {

    private final Atom atomA, atomB;

    public TwoAtomsModel() {
        atomA = new Atom( MPStrings.A, Color.YELLOW, MPConstants.ELECTRONEGATIVITY_RANGE.getDefault() );
        atomB = new Atom( MPStrings.B, Color.ORANGE, MPConstants.ELECTRONEGATIVITY_RANGE.getDefault() );
    }

    @Override public void reset() {
        super.reset();
        atomA.reset();
        atomB.reset();
    }

    public Atom getAtomA() {
        return atomA;
    }

    public Atom getAtomB() {
        return atomB;
    }
}
