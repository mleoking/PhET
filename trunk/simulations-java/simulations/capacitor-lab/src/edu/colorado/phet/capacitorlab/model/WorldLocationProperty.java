// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * A location in 3D space that is constrained to the world bounds.
 * Attempting to set the value to a location outside the world bounds
 * will automatically (and silently) set the value to the closest location
 * inside the world bounds.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WorldLocationProperty extends Property<Point3D> {

    private final WorldBounds worldBounds;

    public WorldLocationProperty( WorldBounds worldBounds, Point3D location ) {
        super( location );
        this.worldBounds = worldBounds;

        // When the world bounds change, adjust the location so that it's inside world bounds.
        worldBounds.addObserver( new SimpleObserver() {
            public void update() {
                set( get() );
            }
        } );
    }

    /**
     * Sets the location. If the specific location is outside the world bounds,
     * the location is automatically and silently adjusted to the closest point
     * inside the world bounds.
     *
     * @param location
     */
    @Override
    public void set( Point3D location ) {
        super.set( worldBounds.getClosest( location ) );
    }
}