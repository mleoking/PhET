package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * This lattice for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SaltCrystalLattice extends Particle implements Iterable<LatticeConstituent> {
    private ArrayList<LatticeConstituent> latticeConstituents = new ArrayList<LatticeConstituent>();

    public SaltCrystalLattice( ImmutableVector2D position, LatticeConstituent... constituents ) {
        super( position );
        for ( LatticeConstituent constituent : constituents ) {
            latticeConstituents.add( constituent );
        }
        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( new ImmutableVector2D(), 0.0 );
    }

    public boolean contains( SphericalParticle particle ) {
        for ( LatticeConstituent latticeConstituent : latticeConstituents ) {
            if ( latticeConstituent.particle == particle ) {
                return true;
            }
        }
        return false;
    }

    @Override public void stepInTime( ImmutableVector2D acceleration, double dt ) {
        super.stepInTime( acceleration, dt );
        for ( LatticeConstituent latticeConstituent : latticeConstituents ) {
            latticeConstituent.particle.position.set( position.get().plus( latticeConstituent.location ) );
        }
    }

    //The shape of a lattice is the combined area of its constituents
    @Override public Shape getShape() {
        return new Area() {{
            for ( LatticeConstituent constituent : latticeConstituents ) {
                add( new Area( constituent.particle.getShape() ) );
            }
        }};
    }

    public Iterator<LatticeConstituent> iterator() {
        return latticeConstituents.iterator();
    }
}
