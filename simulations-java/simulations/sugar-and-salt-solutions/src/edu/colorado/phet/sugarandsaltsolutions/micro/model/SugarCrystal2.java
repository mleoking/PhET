package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.*;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.SugarLattice;

/**
 * TODO: implement, but share as much code as possible with SaltCrystal
 *
 * @author Sam Reid
 */
public class SugarCrystal2 extends Particle implements Iterable<LatticeConstituent> {
    public SugarCrystal2( ImmutableVector2D position, SugarLattice sugarLattice, double v ) {
        super( position );
    }

    public Iterator<LatticeConstituent> iterator() {
        return null;
    }

    @Override public Shape getShape() {
        return null;
    }
}
