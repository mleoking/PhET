// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * World coordinate system for the model.
 * The primary purpose of this class is to define the visible bounds of the world,
 * so that we can constrain object locations and dragging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class World {

    // publicly observable properties
    public final Property<Rectangle2D> boundsProperty; // meters

    public World() {
        this.boundsProperty = new Property<Rectangle2D>( new Rectangle2D.Double() );
    }

    // Convenience method for setting bounds property
    public void setBounds( double x, double y, double width, double height ) {
        boundsProperty.setValue( new Rectangle2D.Double( x, y, width, height ) );
    }

    public boolean isBoundsEmpty() {
        return boundsProperty.getValue().getWidth() == 0 || boundsProperty.getValue().getHeight() == 0;
    }

    public boolean contains( Point3D p ) {
        return boundsProperty.getValue().contains( p.getX(), p.getY() );
    }

    /**
     * Returns the point that is closest to the specified point and still inside the bounds of the world.
     * In all cases, this returns a new Point3D.
     *
     * @param p
     * @return
     */
    public Point3D getConstrainedLocation( Point3D p, double margin ) {
        Point3D pConstrained = null;
        if ( isBoundsEmpty() ) {
            pConstrained = new Point3D.Double( p );
        }
        else {
            final Rectangle2D bounds = boundsProperty.getValue();

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

    public Point3D getConstrainedLocation( Point3D p ) {
        return getConstrainedLocation( p, CLConstants.WORLD_DRAG_MARGIN );
    }
}
