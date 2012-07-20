// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;

/**
 * Model of a bond between 2 atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Bond {

    public final Property<Vector2D> endpoint1, endpoint2; // ends of the bond at atom1 and atom2, respectively, in world coordinates
    public final Property<Vector2D> dipole; // the bond dipole

    public Bond( final Atom atom1, final Atom atom2 ) {

        this.endpoint1 = new Property<Vector2D>( atom1.location.get() );
        this.endpoint2 = new Property<Vector2D>( atom2.location.get() );
        this.dipole = new Property<Vector2D>( new Vector2D() ); // proper initialization occurs when observer registers below

        RichSimpleObserver observer = new RichSimpleObserver() {
            public void update() {

                // adjust endpoints to match atom locations
                endpoint1.set( new Vector2D( atom1.location.get() ) );
                endpoint2.set( new Vector2D( atom2.location.get() ) );

                // adjust dipole
                final double deltaEN = atom2.electronegativity.get() - atom1.electronegativity.get();
                double magnitude = Math.abs( deltaEN ); // this is a simplification. in reality, magnitude is a function of deltaEN and many other things.
                double angle = getAngle();
                if ( deltaEN < 0 ) {
                    angle += Math.PI;
                }
                dipole.set( Vector2D.createPolar( magnitude, angle ) );
            }
        };
        observer.observe( atom1.location, atom2.location, atom1.electronegativity, atom2.electronegativity );
    }

    // gets the center of the bond, using the midpoint formula
    public Vector2D getCenter() {
        return new Vector2D( ( endpoint1.get().getX() + endpoint2.get().getX() ) / 2, ( endpoint1.get().getY() + endpoint2.get().getY() ) / 2 );
    }

    // gets the angle of endpoint2 relative to the horizontal axis
    public double getAngle() {
        Vector2D center = getCenter();
        return PolarCartesianConverter.getAngle( endpoint2.get().getX() - center.getX(), endpoint2.get().getY() - center.getY() );
    }

    // Gets the bond length, the distance between the 2 endpoints.
    public double getLength() {
        return endpoint1.get().distance( endpoint2.get() );
    }
}
