// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Bounds of the world coordinate system for the model.
 * This is a 2D bounds, and defines the bounds of the x and y axes; the z axis is infinite.
 * Primarily used to constrain object locations when dragging and resizing the canvas.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WorldBounds extends Property<Rectangle2D> {

    public WorldBounds() {
        super( new Rectangle2D.Double() ); //TODO questionable aspect of this approach is that we start with empty bounds until the canvas is realized
    }

    // Convenience method for setting bounds property
    public void setBounds( double x, double y, double width, double height ) {
        set( new Rectangle2D.Double( x, y, width, height ) );
    }

    public boolean isEmpty() {
        return get().getWidth() == 0 || get().getHeight() == 0;
    }

    public boolean contains( Point3D p ) {
        return get().contains( p.getX(), p.getY() );
    }

    /**
     * Returns the point that is closest to the specified point and still inside the bounds of the world.
     * In all cases, this returns a new Point3D.
     *
     * @param p
     * @param margin
     * @return
     */
    public Point3D getClosest( Point3D p, double margin ) {
        Point3D pConstrained = null;
        if ( isEmpty() ) {
            pConstrained = new Point3D.Double( p );
        }
        else {
            final Rectangle2D bounds = get();

            // adjust x coordinate
            double newX = p.getX();
            if ( p.getX() < bounds.getX() + margin ) {
                newX = bounds.getX() + margin;
            }
            else if ( p.getX() > bounds.getMaxX() - margin ) {
                newX = bounds.getMaxX() - margin;
            }

            // adjust y coordinate
            double newY = p.getY();
            if ( p.getY() < bounds.getY() + margin ) {
                newY = bounds.getY() + margin;
            }
            else if ( p.getY() > bounds.getMaxY() - margin ) {
                newY = bounds.getMaxY() - margin;
            }

            // z is fixed
            final double z = p.getZ();

            pConstrained = new Point3D.Double( newX, newY, z );
        }
        return pConstrained;
    }

    public Point3D getClosest( Point3D p ) {
        return getClosest( p, CLConstants.WORLD_DRAG_MARGIN );
    }
}
