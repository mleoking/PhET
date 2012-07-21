// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * An atom that includes additional 3D position, and information about lone pair quantity.
 */
public class Atom3D extends Atom {
    public final Property<Vector3D> position; // model coordinates in angstroms
    public final int lonePairCount;

    public Atom3D( Element element, Vector3D position ) {
        this( element, position, 0 );
    }

    public Atom3D( Element element, Vector3D position, int lonePairCount ) {
        super( element );

        this.position = new Property<Vector3D>( position );
        this.lonePairCount = lonePairCount;
    }
}
