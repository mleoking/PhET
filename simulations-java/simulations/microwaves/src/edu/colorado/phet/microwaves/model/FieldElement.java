/**
 * Class: FieldElement
 * Package: edu.colorado.phet.waves.model
 * Author: Another Guy
 * Date: May 29, 2003
 */
package edu.colorado.phet.microwaves.model;

import java.awt.geom.Point2D;
import java.util.Observable;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.microwaves.coreadditions.Vector2D;

public class FieldElement extends Observable implements ModelElement {

    private Point2D.Double location = new Point2D.Double();
    private Vector2D velocity;


    public FieldElement( Point2D location, Vector2D velocity ) {
        this.location.setLocation( location );
        this.velocity = velocity;
    }

    public void stepInTime( double dt ) {
        double x = location.x;
        double y = location.y;
        x += velocity.getX() * dt;
        y += velocity.getY() * dt;
        setLocation( x, y );
    }

    private void setLocation( double x, double y ) {
        location.setLocation( x, y );
        super.setChanged();
        super.notifyObservers();
    }
    
    public Point2D.Double getLocation() {
        return location;
    }

    //
    // Static fields and methods
    //
//    private static LinkedList s_freeList = new LinkedList();
//    static {
//        for( int i = 0; i < 2000; i++ ) {
//            s_freeList.add( new FieldElement( null, null ) );
//        }
//    }
//
//    public static FieldElement create( Point2D location, Vector2D velocity ) {
//        FieldElement fieldElement = (FieldElement)s_freeList.removeFirst();
//        if( fieldElement != null ) {
//            fieldElement.setLocation( location.getX(), location.getY() );
//            fieldElement.velocity = velocity;
//        }
//        else {
//            fieldElement = new FieldElement( location, velocity );
//        }
//        return fieldElement;
//    }
}
