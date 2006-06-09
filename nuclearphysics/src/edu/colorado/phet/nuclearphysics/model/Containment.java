/**
 * Class: Containment
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Oct 6, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.math.MathUtil;

import java.util.List;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * A vessel that is supposed to emulate the containment vessel of a nuclear bomb
 */
public class Containment extends SimpleObservable {
    private Ellipse2D shape;
    private double wallThickness = 80;
    double opacity = 1;
    private ArrayList resizeListeners = new ArrayList();


    public Containment( Point2D center, double radius, NuclearPhysicsModel model ) {
        Ellipse2D shape = new Ellipse2D.Double( center.getX() - radius, center.getY() - radius,
                                                radius * 2, radius * 2 );
        this.shape = shape;

        model.addModelElement( new NeutronCollisionDetector( model ) );
    }

    public void adjustRadius( double dr ) {
        Ellipse2D containmentShape = (Ellipse2D)getShape();
        containmentShape.setFrame( containmentShape.getX() + dr, containmentShape.getY() + dr,
                                   containmentShape.getWidth() - dr * 2, containmentShape.getHeight() - dr * 2 );
        shape = containmentShape;
        notifyResizeListeners();
    }

    private void notifyResizeListeners() {
        for( int i = 0; i < resizeListeners.size(); i++ ) {
            ResizeListener resizeListener = (ResizeListener)resizeListeners.get( i );
            resizeListener.containementResized( this );
        }
    }

    public void addResizeListener( ResizeListener listener ) {
        resizeListeners.add( listener );
    }

    public void removeResizeListener( ResizeListener listener ) {
        resizeListeners.remove( listener );
    }

    public Shape getShape() {
        return shape;
    }

    public Rectangle2D getBounds2D() {
        return shape.getBounds2D();
    }

    public Point2D.Double getNeutronLaunchPoint() {
        return new Point2D.Double( shape.getBounds2D().getMinX(),
                                   shape.getBounds2D().getMinY() + shape.getBounds2D().getHeight() / 2 );
    }

    public void dissolve() {
        double decr = 0.05;
        opacity = Math.max( opacity - decr, 0 );
        notifyObservers();
    }

    public double getOpacity() {
        return opacity;
    }

    public double getWallThickness() {
        return wallThickness;
    }

    //--------------------------------------------------------------------------------------------------
    // Listeners
    //--------------------------------------------------------------------------------------------------

    public interface ResizeListener {
        void containementResized( Containment containment );
    }

    //--------------------------------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------------------------------

    /**
     * Detects neutrons hitting the vessel, and doing the correct thing when they do.
     */
    private class NeutronCollisionDetector implements ModelElement {
        private Random random = new Random();

        private NuclearPhysicsModel model;
        private double absorptionProbability = 0.5;
        private double reflectionSpreadAngle = Math.toRadians( 30 );

        public NeutronCollisionDetector( NuclearPhysicsModel model ) {
            this.model = model;
        }

        public void stepInTime( double dt ) {
            List elements = model.getNuclearModelElements();
            for( int i = 0; i < elements.size(); i++ ) {
                Object o = elements.get( i );
                if( o instanceof Neutron ) {
                    detectAndDoCollision( (Neutron)o );
                }
                if( o instanceof Nucleus ) {
                    detectAndDoCollision( (Nucleus)o );
                }
            }
        }

        private void detectAndDoCollision( Nucleus nucleus ) {
            double distSq = nucleus.getPosition().distanceSq( shape.getCenterX(), shape.getCenterY() );
            if( distSq > shape.getWidth()/ 2 * shape.getWidth() / 2 ) {
                nucleus.setVelocity( 0, 0 );
            }
        }

        private void detectAndDoCollision( Neutron neutron ) {
            double distSq = neutron.getPosition().distanceSq( shape.getCenterX(), shape.getCenterY() );
            if( distSq > shape.getWidth()/ 2 * shape.getWidth() / 2 ) {
                handleCollision( neutron );
            }
        }

        /**
         * The neutron is either absorbed or reflected in a somewhat random direction. The likelihood of
         * the neutron being absorbed is controlled by a field (absorptionProbability).
         * @param neutron
         */
        private void handleCollision( Neutron neutron ) {
            if( random.nextDouble() <= absorptionProbability ) {
                model.removeModelElement( neutron );
            }
            else {
                Vector2D v = neutron.getVelocity();
                double theta = Math.atan2( shape.getCenterY() - neutron.getPositionPrev().getY(),
                                           shape.getCenterX() - neutron.getPositionPrev().getX());
                double gamma = theta + random.nextDouble() * reflectionSpreadAngle * MathUtil.nextRandomSign();
                double delta = gamma - v.getAngle();
                v.rotate( delta );
                neutron.setVelocity( v );
            }
        }
    }
}
