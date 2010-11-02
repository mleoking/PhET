package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;


public class World {
    
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
    
    public void addBoundsObserver( SimpleObserver o ) {
        bounds.addObserver( o );
    }
    
    public boolean contains( Point3D p ) {
        return bounds.getValue().contains( p.getX(), p.getY() );
    }
}
