// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Model of a bond between 2 atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Bond {

    public final Property<ImmutableVector2D> dipole;
    public final Property<ImmutableVector2D> endpoint1, endpoint2; // ends of the bond at atom1 and atom2, respectively

    public Bond( final Atom atom1, final Atom atom2 ) {

        this.dipole = new Property<ImmutableVector2D>( new ImmutableVector2D() );
        this.endpoint1 = new Property<ImmutableVector2D>( atom1.location.get() );
        this.endpoint2 = new Property<ImmutableVector2D>( atom2.location.get() );

        // update bond endpoints when atoms move
        atom1.location.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D location ) {
                endpoint1.set( new ImmutableVector2D( atom1.location.get() ) );
            }
        } );
        atom2.location.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D location ) {
                endpoint2.set( new ImmutableVector2D( atom2.location.get() ) );
            }
        } );

        // update dipole magnitude when electronegativity of either atom changes
        VoidFunction1<Double> electronegativityObserver = new VoidFunction1<Double>() {
            public void apply( Double aDouble ) {
                double deltaEN = atom2.electronegativity.get() - atom1.electronegativity.get();
                dipole.set( getNormal().times( deltaEN ) );
            }
        };
        atom1.electronegativity.addObserver( electronegativityObserver );
        atom2.electronegativity.addObserver( electronegativityObserver );
    }

    // gets the center of the bond, using the midpoint formula
    public ImmutableVector2D getCenter() {
        return new ImmutableVector2D( ( endpoint1.get().getX() + endpoint2.get().getX() ) / 2, ( endpoint1.get().getY() + endpoint2.get().getY() ) / 2 );
    }

    // gets the angle of endpoint2 relative to the horizontal
    public double getAngle() {
        return getNormal().getAngle();
    }

    // dipole is in phase if it points from atom1 to atom2
    public boolean isDipoleInPhase() {
        return getAngle() == dipole.get().getAngle();
    }

    // gets the normal vector that points along the axis from atom1 to atom2
    private ImmutableVector2D getNormal() {
        return new ImmutableVector2D( endpoint1.get(), endpoint2.get() ).getNormalizedInstance();
    }
}
