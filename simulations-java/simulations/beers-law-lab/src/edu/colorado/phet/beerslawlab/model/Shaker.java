// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model of the dropper, contains solute in solid form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Shaker extends Movable {

    public final Property<Solute> solute;
    public final Rectangle2D dragBounds;

    public Shaker( ImmutableVector2D location, Rectangle2D dragBounds, Property<Solute> solute ) {
        super( location );
        assert ( dragBounds.contains( location.toPoint2D() ) );
        this.solute = solute;
        this.dragBounds = dragBounds;
    }
}
