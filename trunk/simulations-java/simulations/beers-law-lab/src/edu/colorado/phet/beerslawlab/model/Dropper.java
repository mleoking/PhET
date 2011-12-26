// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Model of the dropper, contains solute in solution form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Dropper extends Movable {

    public final Property<Solute> solute;
    public final Property<Boolean> on; // true if the dropper is dispensing solution
    public final Property<Boolean> enabled;

    public Dropper( ImmutableVector2D location, PBounds bounds, Property<Solute> solute ) {
        super( location, bounds );
        assert ( bounds.contains( location.toPoint2D() ) );
        this.solute = solute;
        this.on = new Property<Boolean>( false );
        this.enabled = new Property<Boolean>( true );
    }
}
