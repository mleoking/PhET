/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 26, 2003
 * Time: 3:05:16 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.graphics.util;

import edu.colorado.phet.graphics.MovableImageGraphic;
import edu.colorado.phet.physics.body.Particle;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Observable;

public class VerticalGuideline extends MovableImageGraphic {
//public class VerticalGuideline extends ShapeGraphic implements MouseListener, MouseMotionListener{

    public VerticalGuideline( float  position ) {
        super( null, position, 0, 0, 0, 800, 0 );
    }

    //
    // Abstract methods
    //
    protected void setPosition( Particle body ) {
    }

    public void paint( Graphics2D g ) {
        float  length = this.getApparatusPanel().getHeight();
        Line2D.Float line = new Line2D.Float(
                (float)getLocationPoint2D().getX(), 0,
                (float)getLocationPoint2D().getX(), length );
        Color oldColor = g.getColor();
        g.setColor( Color.blue );
        g.draw( line );
        g.setColor( oldColor );
    }

    public void update( Observable o, Object arg ) {
    }

    public boolean isInHotSpot( Point2D.Float p ) {
        return Math.abs( p.getX() - this.getLocationPoint2D().getX() ) <= 3;
    }
}
