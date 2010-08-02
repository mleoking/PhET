/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model of a beaker.
 * Location is at the bottom center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Beaker extends SolutionRepresentation {
    
    private PDimension size;
    private Rectangle2D bounds;

    public Beaker( AqueousSolution solution, Point2D location, boolean visible, PDimension size ) {
        super( solution, location, visible );
        this.size = new PDimension( size );
        bounds = new Rectangle2D.Double();
        updateBounds();
    }
    
    @Override
    public void setLocation( Point2D location ) {
        super.setLocation( location );
        updateBounds();
    }
    
    public double getWidth() {
        return size.getWidth();
    }
    
    public double getHeight() {
        return size.getHeight();
    }
    
    public boolean inSolution( Point2D p ) {
        return bounds.contains( p );
    }
    
    private void updateBounds() {
        double x = getX() - ( size.width / 2 );
        double y = getY() - size.height;
        bounds.setRect( x, y, size.getWidth(), size.getHeight() );
    }
}
