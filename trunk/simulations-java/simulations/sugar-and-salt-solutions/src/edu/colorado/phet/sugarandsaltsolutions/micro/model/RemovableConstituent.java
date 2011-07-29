// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * An item that can be removed from a crystal and corresponding lattice.
 *
 * @author Sam Reid
 */
public class RemovableConstituent<T extends Particle> {
    public final Constituent<T> constituent;//part to be removed from the crystal
    public final T latticeComponent;//part to be removed from the lattice
    public final T particle;

    public RemovableConstituent( Constituent<T> constituent, T latticeComponent ) {
        this.constituent = constituent;
        this.latticeComponent = latticeComponent;
        this.particle = constituent.particle;

        if ( latticeComponent != particle ) {
            throw new RuntimeException( "particle was different from lattice component" );
        }
    }
}
