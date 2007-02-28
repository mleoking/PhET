/*
 * Class: ImageGraphic
 * Package: edu.colorado.phet.graphicaldomain
 *
 * Created by: Ron LeMaster
 * Date: Oct 28, 2002
 */
package edu.colorado.phet.graphics;

import edu.colorado.phet.physics.body.Particle;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * A subclass of PhetGraphic that draws an awt.Image.
 */
public class ImageGraphic extends PhetGraphic {

    private Point2D.Float location = new Point2D.Float();

    /**
     *
     */
    protected ImageGraphic() {
    }

    /**
     *
     * @param image The Image to be associated with the ImageGraphic
     * @param x The initial x coordinate of the ImageGraphic
     * @param y The initial y coordinate of the ImageGraphic
     */
    public ImageGraphic( Image image, float  x, float  y ) {
        super();
        this.setRep( image );
        location = new Point2D.Float( x, y );
    }

    /**
     *
     * @param g
     */
    public void paint( Graphics2D g ) {
        Image image = (Image)getRep();
        g.drawImage( image, (int)location.getX(), (int)location.getY(), null );
    }

    /**
     * Returns the Image associated with the receiver
     * @return The Image associated with the receiver
     */
    protected Image getImage() {
        return (Image)this.getRep();
    }

    protected void setImage( Image image ) {
        this.setRep( image );
    }

    /**
     * Sets the position at which the Image should be drawn
     * @param location
     */
    public void setPosition( Point2D.Float location ) {
        this.location = location;
        this.location.setLocation( location.getX(), location.getY() );
    }

    /**
     *
     * @param x
     * @param y
     */
    public void setPosition( float  x, float  y ) {
        location.setLocation( x, y );
    }

    /**
     *
     * @return
     */
    public Point2D.Float getLocationPoint2D() {
        return location;
    }

    /**
     *
     * @param body
     */
    protected void setPosition( Particle body ) {
        setPosition( body.getPosition().getX(), body.getPosition().getY() );
    }
}
