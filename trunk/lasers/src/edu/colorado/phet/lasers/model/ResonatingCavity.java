/**
 * Class: ResonatingCavity
 * Package: edu.colorado.phet.lasers.model
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.model;

import edu.colorado.phet.collision.Box2D;
import edu.colorado.phet.lasers.model.mirror.BandPass;
import edu.colorado.phet.lasers.model.mirror.LeftReflecting;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;
import edu.colorado.phet.lasers.model.mirror.RightReflecting;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ResonatingCavity extends Box2D {
//public class ResonatingCavity extends Body {


    private Point2D origin;
    private double width;
    private double height;
//    private Wall upperWall;
//    private Wall lowerWall;
//    private Wall leftWall;
//    private Wall rightWall;
    private PartialMirror rightMirror;
    private PartialMirror leftMirror;

    public ResonatingCavity( Point2D origin, double width, double height ) {
        super( origin, new Point2D.Double( origin.getX() + width, origin.getY() + height ) );
        this.origin = origin;
        this.width = width;
        this.height = height;

        // Set the position of the cavity
        setPosition( origin );

        // Create the walls
//        upperWall = new Wall( origin, new Point2D.Float( (float)origin.getX() + width,
//                                                         (float)origin.getY() ) );
//        lowerWall = new Wall( new Point2D.Float( (float)origin.getX(), (float)origin.getY() + height ),
//                              new Point2D.Float( (float)origin.getX() + width,
//                                                 (float)origin.getY() + height ) );
//        leftWall = new Wall( origin, new Point2D.Float( (float)origin.getX(),
//                                                                   (float)origin.getY() + height ) );
//        rightWall = new Wall( new Point2D.Float( (float)origin.getX() + width,
//                                                            (float)origin.getY() ),
//                                         new Point2D.Float( (float)origin.getX() + width,
//                                                            (float)origin.getY() + height ) );

        // Create the left mirror
        leftMirror = new PartialMirror( new Point2D.Double( origin.getX() - 40,
                                                            origin.getY() ),
                                        new Point2D.Double( origin.getX() - 40,
                                                            origin.getY() + height ) );
        leftMirror.addReflectionStrategy( new RightReflecting() );
        leftMirror.addReflectionStrategy( new BandPass( Photon.RED, Photon.RED ) );

        // Create the right mirror
        rightMirror = new PartialMirror( new Point2D.Double( origin.getX() + width + 40,
                                                             origin.getY() ),
                                         new Point2D.Double( origin.getX() + width + 40,
                                                             origin.getY() + height ) );
        rightMirror.addReflectionStrategy( new LeftReflecting() );
        rightMirror.addReflectionStrategy( new BandPass( Photon.RED, Photon.RED ) );
        rightMirror.setReflectivity( 0.2f );

//        new edu.colorado.phet.controller.command.AddParticleCmd( upperWall ).doIt();
//        new edu.colorado.phet.controller.command.AddParticleCmd( lowerWall ).doIt();
//        new AddPhysicalEntityCmd( leftWall ).doIt();
//        new AddPhysicalEntityCmd( rightWall ).doIt();
//        new edu.colorado.phet.controller.command.AddParticleCmd( leftMirror ).doIt();
//        new edu.colorado.phet.controller.command.AddParticleCmd( rightMirror ).doIt();
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double( getMinX(), getMinY(), getWidth(), getHeight() );
    }

    public float getReflectivity() {
        return rightMirror.getReflectivity();
    }

    public void setReflectivity( float reflectivity ) {
        rightMirror.setReflectivity( reflectivity );
    }

//    public Point2D.Float getOrigin() {
//        return origin;
//    }
//
//    public float getWidth() {
//        return width;
//    }
//
//    public void setWidth( float width ) {
//        this.width = width;
//        setChanged();
//        notifyObservers();
//    }
//
//    public float getHeight() {
//        return height;
//    }

    /**
     * @param height
     */
    public void setHeight( double height ) {

        // Reposition the walls of the cavity
        double yMiddle = origin.getY() + this.height / 2;
        origin.setLocation( origin.getX(), yMiddle - height / 2 );
        this.height = height;
        this.setBounds( getMinX(), origin.getY() - height / 2, getWidth(), height );
        notifyObservers();
//        upperWall.setLocation( origin.getX(), origin.getX() + this.width,
//                               origin.getY(), origin.getY() );
//        lowerWall.setLocation( origin.getX(), origin.getX() + this.width,
//                               origin.getY() + height, origin.getY() + height  );
//        leftWall.setLocation( origin.getX(), origin.getX(),
//                                origin.getY(), origin.getY() + height );
//        rightWall.setLocation( origin.getX() + this.width, origin.getX() + this.width,
//                                 origin.getY(), origin.getY() + height );
//
//        // Adjust the height of the mirrors
//        leftMirror.setLocation( origin.getX() - 40, origin.getX() - 40,
//                                origin.getY(), origin.getY() + height );
//        rightMirror.setLocation( origin.getX() + this.width + 40, origin.getX() + this.width + 40,
//                                 origin.getY(), origin.getY() + height );
//
//        setChanged();
//        notifyObservers();
//    }
//
//    public Wall getUpperWall() {
//        return upperWall;
//    }
//
//    public Wall getLowerWall() {
//        return lowerWall;
//    }
//
//    public boolean isInContactWithBody( Body body ) {
//        return false;
//    }
//
//    public float getContactOffset( Body body ) {
//        return 0;
    }
}
