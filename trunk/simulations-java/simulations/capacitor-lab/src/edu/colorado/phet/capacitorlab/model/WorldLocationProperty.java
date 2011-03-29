// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * A location in 3D space that is constrained to the bounds of the world.
 * Attempting to set the value to a location outside the world bounds
 * will automatically (and silently) set the value to the closest location
 * inside the world bounds.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WorldLocationProperty extends Property<Point3D> {

    private final World world;

    public WorldLocationProperty( World world, Point3D location ) {
        super( location );
        this.world = world;
        world.addBoundsObserver( new SimpleObserver() {
            public void update() {
                setValue( getValue() );
            }
        } );
    }

    public void setValue( Point3D location ) {
        super.setValue( world.getConstrainedLocation( location ) );
    }
}
