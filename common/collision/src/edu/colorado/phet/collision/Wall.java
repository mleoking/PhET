/**
 * Class: Wall
 * Class: edu.colorado.phet.collision
 * User: Ron LeMaster
 * Date: Oct 22, 2004
 * Time: 9:39:44 PM
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Wall extends Body implements Collidable {
    private Vector2D velocity = new Vector2D.Double( );
    private Rectangle2D rep = new Rectangle2D.Double( );

    public Wall( Rectangle2D bounds ) {
        this.rep = bounds;
    }

    public Rectangle2D getBounds() {
        return rep;
    }

    public Vector2D getVelocityPrev() {
        return velocity;
    }

    public Point2D getPositionPrev() {
        return null;
    }

    public Point2D getCM() {
        return new Point2D.Double( rep.getMinX() + rep.getWidth() / 2,
                                   rep.getMinY() + rep.getHeight() / 2 );
    }

    public double getMomentOfInertia() {
        return Double.POSITIVE_INFINITY;
    }
}
