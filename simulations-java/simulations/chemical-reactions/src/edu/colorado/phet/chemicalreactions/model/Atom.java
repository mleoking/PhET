// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Atom + 2d position
 */
public class Atom extends edu.colorado.phet.chemistry.model.Atom {

    public final Property<Vector2D> position;

    public Atom( Element element, Property<Vector2D> position ) {
        super( element );

        this.position = position;
    }
}
