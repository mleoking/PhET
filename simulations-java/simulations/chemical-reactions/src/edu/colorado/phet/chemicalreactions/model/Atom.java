// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

public class Atom extends edu.colorado.phet.chemistry.model.Atom {

    public final Property<ImmutableVector2D> position;

    public Atom( Element element, Property<ImmutableVector2D> position ) {
        super( element );

        this.position = position;
    }
}
