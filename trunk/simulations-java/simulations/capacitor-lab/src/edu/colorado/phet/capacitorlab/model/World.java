// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * World coordinate system for the model.
 * The primary purpose of this class is to define the visible bounds of the world,
 * so that we can constrain object locations and dragging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class World {
    
    // observable properties
    private final Property<Rectangle2D> bounds; // meters
    
    public World() {
        this.bounds = new Property<Rectangle2D>( new Rectangle2D.Double() );
    }
    
    public void setBounds( double x, double y, double width, double height ) {
        setBounds( new Rectangle2D.Double( x, y, width, height ) );
    }
    
    public void setBounds( Rectangle2D bounds ) {
        if ( !this.bounds.equals( bounds ) ) {
            this.bounds.setValue( bounds );
        }
    }
    
    public Rectangle2D getBoundsReference() {
        return bounds.getValue();
    }
    
    public boolean isBoundsEmpty() { 
        return bounds.getValue().getWidth() == 0 || bounds.getValue().getHeight() == 0;
    }
    
    public void addBoundsObserver( SimpleObserver o ) {
        bounds.addObserver( o );
    }
    
    public boolean contains( Point3D p ) {
        return bounds.getValue().contains( p.getX(), p.getY() );
    }
    
    /**
     * Returns the point that is closest to the specified point and still inside the bounds of the world.
     * In all cases, this returns a new Point3D.
     * @param p
     * @return
     */
    public Point3D getConstrainedLocation( Point3D p, double margin ) {
        Point3D pConstrained = null;
        if ( isBoundsEmpty() ) {
            pConstrained = new Point3D.Double( p );
        }
        else {
            // adjust x coordinate
            double newX = p.getX();
            if ( p.getX() < getBoundsReference().getX() + margin ) {
                newX = getBoundsReference().getX() + margin;
            }
            else if ( p.getX() > getBoundsReference().getMaxX() - margin ) {
                newX = getBoundsReference().getMaxX() - margin;
            }

            // adjust y coordinate
            double newY = p.getY();
            if ( p.getY() < getBoundsReference().getY() + margin ) {
                newY = getBoundsReference().getY() + margin;
            }
            else if ( p.getY() > getBoundsReference().getMaxY() - margin ) {
                newY = getBoundsReference().getMaxY() - margin;
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
