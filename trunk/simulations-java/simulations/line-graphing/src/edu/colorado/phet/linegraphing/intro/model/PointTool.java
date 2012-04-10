// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model of the point tool.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointTool implements Resettable {

    public final Property<ImmutableVector2D> location;
    public final Property<Boolean> highlighted;

    public PointTool( ImmutableVector2D location ) {
        this.location = new Property<ImmutableVector2D>( location );
        this.highlighted = new Property<Boolean>( false );
    }

    public void reset() {
        location.reset();
        highlighted.reset();
    }
}
