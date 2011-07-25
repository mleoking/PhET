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

    public final Property<Double> dipoleMagnitude;
    public final Property<ImmutableVector2D> endpoint1, endpoint2;

    public Bond( final Atom atom1, final Atom atom2 ) {

        this.dipoleMagnitude = new Property<Double>( 0d );
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
                dipoleMagnitude.set( atom1.electronegativity.get() - atom2.electronegativity.get() );
            }
        };
        atom1.electronegativity.addObserver( electronegativityObserver );
        atom2.electronegativity.addObserver( electronegativityObserver );
    }
}
