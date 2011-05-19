// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.wire;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorToCapacitorWireSegment;

/**
 * Wire that connects the bottom plate of one capacitor to the top plate of another capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireCapacitorBottomToCapacitorTop extends Wire {

    public WireCapacitorBottomToCapacitorTop( CLModelViewTransform3D mvt, final double thickness, final Capacitor topCapacitor, final Capacitor bottomCapacitor ) {
        super( mvt, thickness, new CapacitorToCapacitorWireSegment( topCapacitor, bottomCapacitor ) );
    }
}
