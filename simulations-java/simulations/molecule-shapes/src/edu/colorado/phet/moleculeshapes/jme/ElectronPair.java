// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;

public class ElectronPair {
    public Property<ImmutableVector3D> position;

    public ElectronPair( ImmutableVector3D position ) {
        this.position = new Property<ImmutableVector3D>( position );
    }
}
