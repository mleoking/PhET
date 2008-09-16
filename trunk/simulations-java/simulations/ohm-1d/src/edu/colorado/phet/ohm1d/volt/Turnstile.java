package edu.colorado.phet.ohm1d.volt;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.ohm1d.common.paint.Painter;
import edu.colorado.phet.ohm1d.common.phys2d.Law;
import edu.colorado.phet.ohm1d.common.phys2d.System2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 8, 2002
 * Time: 7:28:07 PM
 * To change this template use Options | File Templates.
 */
public class Turnstile implements Painter, Law, CurrentListener {
    BufferedImage image;
    double angle;
    double angularSpeed;
    int width;
    int height;
    AffineTransform centerTrf;
    double angVelScale;

    public Turnstile( Point center, BufferedImage image, double angVelScale ) {
        this.angVelScale = angVelScale;
        width = image.getWidth();
        height = image.getHeight();
        centerTrf = AffineTransform.getTranslateInstance( center.x, center.y );
        this.image = image;
        angularSpeed = .31;
    }

    //Want to model position as well as angular velocity easily.
    /*This class could observe electrons only,
    or electrons and current,
    or current only...*/
    public void paint( Graphics2D g ) {
        //System.err.println("Angle="+angle);
        AffineTransform rotation = AffineTransform.getRotateInstance( angle, width / 2, height / 2 );
        AffineTransform total = new AffineTransform();
        total.concatenate( centerTrf );
        total.concatenate( rotation );
        g.drawRenderedImage( image, total );
    }

    public void iterate( double dt, System2D system2D ) {
        angle = angularSpeed * dt + angle;
    }

    public void currentChanged( double a ) {
        this.angularSpeed = a * angVelScale;
    }
}
