// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import edu.colorado.phet.balancingchemicalequations.model.Atom;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A balance scale, depicts the relationship between the atom count
 * on the left and right side of an equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalanceScaleNode extends PComposite {

    private final static int DIFFERENCE_FOR_MAX_TILT = 4;

    private int leftNumberOfAtoms, rightNumberOfAtoms;

    public BalanceScaleNode( Atom atom ) {
        this.leftNumberOfAtoms = this.rightNumberOfAtoms = 0;
        update();
    }

    public void setNumberOfAtoms( int leftNumberOfAtoms, int rightNumberOfAtoms ) {
        if ( leftNumberOfAtoms != this.leftNumberOfAtoms || rightNumberOfAtoms != this.rightNumberOfAtoms ) {
            this.leftNumberOfAtoms = leftNumberOfAtoms;
            this.rightNumberOfAtoms = rightNumberOfAtoms;
            update();
        }
    }

    private void update() {
        // update to match leftNumberOfAtoms and rightNumberOfAtoms
    }
}
