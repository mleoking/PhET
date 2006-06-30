/**
 * Class: Containment
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Oct 6, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.nuclearphysics.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Area;
import java.util.*;
import java.util.List;

/**
 * A vessel that is supposed to emulate the containment vessel of a nuclear bomb.
 * <p/>
 * This class implements Modelelement, so it can detect when nuclei and neutrons hit it.
 */
public class Containment extends SimpleObservable implements ModelElement {
//    private Ellipse2D shape;
    private double wallThickness = 80;
    double opacity = 1;
    private ArrayList resizeListeners = new ArrayList();
    private Containment.MyCollisionDetector collisionDetector;
    private NuclearPhysicsModel model;

    // Fields having to do with the containment blowing up
    private double totalImpact;
    private double neutronImpact = 1;
    private double nucleusImpact = 10;
    private double explosionThreshold = Config.CONTAINMENT_EXPLOSION_THRESHOLD;
    private List embeddedNuclearModelElements = new ArrayList();
    private Area area;
    private Point2D center;
    private double radius;
    private Rectangle2D aperture;
    private Area interiorArea;


    public Containment( Point2D center, double radius, NuclearPhysicsModel model ) {
        this.center = center;
        this.radius = radius;
        adjustRadius( 0 );
        this.model = model;
        collisionDetector = new MyCollisionDetector( model );
    }

    /**
     * Handles neutrons and nuclei striking the vessel
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        List elements = model.getNuclearModelElements();
        for( int i = 0; i < elements.size(); i++ ) {
            Object o = elements.get( i );
            if( o instanceof Neutron ) {
                collisionDetector.detectAndDoCollision( (Neutron)o );
            }
            if( o instanceof Nucleus ) {
                collisionDetector.detectAndDoCollision( (Nucleus)o );
            }
        }
    }

    public void adjustRadius( double dr ) {
        Ellipse2D containmentShape = new Ellipse2D.Double();
        radius -= dr;
        containmentShape.setFrame( center.getX() - radius, center.getY() - radius,
                                   radius * 2, radius * 2 );

        Ellipse2D outer = new Ellipse2D.Double( containmentShape.getMinX() - getWallThickness(),
                        containmentShape.getMinY() - getWallThickness(),
                        containmentShape.getWidth() + getWallThickness() * 2,
                        containmentShape.getHeight() + getWallThickness() * 2 );
        Ellipse2D inner = new Ellipse2D.Double( containmentShape.getMinX(),
                        containmentShape.getMinY(),
                        containmentShape.getWidth(),
                        containmentShape.getHeight() );

        interiorArea = new Area( inner );
        double apertureHeight = 40;
        aperture = new Rectangle2D.Double( center.getX() - radius - getWallThickness(),
                                                       center.getY() - apertureHeight / 2,
                                                       getWallThickness(),
                                                       apertureHeight );
        area = new Area( outer );
        area.subtract( new Area( inner ));
        area.subtract( new Area( aperture ));
        notifyResizeListeners();
    }


    public Shape getInteriorArea() {
        return interiorArea;
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

//    public Shape getShape() {
//        return shape;
//    }

    public Rectangle2D getBounds2D() {
        return area.getBounds2D();
//        return shape.getBounds2D();
    }

    public Area getArea() {
        return area;
    }

    public Rectangle2D getAperture() {
        return aperture;
    }

//    public Point2D.Double getNeutronLaunchPoint() {
//        return new Point2D.Double( shape.getBounds2D().getMinX(),
//                                   shape.getBounds2D().getMinY() + shape.getBounds2D().getHeight() / 2 );
//    }

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

    /**
     * Records the impact of a neutron or nucleus with the containment vessel. When enough impacts
     * have occured, the vessel blows up.
     *
     * @param impact
     */
    private void recordImpact( double impact ) {
        totalImpact += impact;
        if( totalImpact > explosionThreshold ) {

            // Defer blowing up the containment until the end of the time step, so that all nuclei that hit
            // the vessel during this time step get removed from the model
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    model.removeModelElement( Containment.this );
                    while( embeddedNuclearModelElements.size() > 0 ) {
                        NuclearModelElement nuclearModelElement = (NuclearModelElement)embeddedNuclearModelElements.get( 0 );
                        model.removeModelElement( nuclearModelElement );
                        embeddedNuclearModelElements.remove( 0 );
                    }
                    changeListenerProxy.containmentExploded( new ChangeEvent( this ) );
                }
            } );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Listeners
    //--------------------------------------------------------------------------------------------------

    public interface ResizeListener {
        void containementResized( Containment containment );
    }


    //--------------------------------------------------------------------------------------------------
    // ChangeListener definition
    //--------------------------------------------------------------------------------------------------
    EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public Containment get() {
            return (Containment)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void containmentExploded( ChangeEvent event );
    }

    //--------------------------------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------------------------------

    /**
     * Detects neutrons hitting the vessel, and doing the correct thing when they do.
     */
    private class MyCollisionDetector {
        private Random random = new Random();

        private NuclearPhysicsModel model;
        private double absorptionProbability = 0.5;
        private double reflectionSpreadAngle = Math.toRadians( 30 );

        public MyCollisionDetector( NuclearPhysicsModel model ) {
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
            double distSq = nucleus.getPosition().distanceSq( area.getBounds2D().getCenterX(),
                                                              area.getBounds2D().getCenterY() );
            double embeddedDist = 30;
            if( area.contains( nucleus.getPosition()) && nucleus.getVelocity().getMagnitudeSq() > 0 ) {
                nucleus.setVelocity( 0, 0 );
                double dx = ( nucleus.getPosition().getX() - area.getBounds2D().getCenterX() ) / Math.sqrt( distSq )
                            * ( area.getBounds2D().getWidth() / 2 - embeddedDist );
                double dy = ( nucleus.getPosition().getY() - area.getBounds2D().getCenterY() ) / Math.sqrt( distSq )
                            * ( area.getBounds2D().getHeight() / 2 - embeddedDist );
                nucleus.setPosition( area.getBounds2D().getCenterX() + dx, area.getBounds2D().getCenterY() + dy );
                recordImpact( nucleusImpact );
                embeddedNuclearModelElements.add( nucleus );
            }
        }

        private void detectAndDoCollision( Neutron neutron ) {
            if( area.contains( neutron.getPosition()) && neutron.getVelocity().getMagnitudeSq() > 0 ) {
                handleCollision( neutron );
                recordImpact( neutronImpact );
                embeddedNuclearModelElements.add( neutron );
            }
        }

        /**
         * The neutron is either absorbed or reflected in a somewhat random direction. The likelihood of
         * the neutron being absorbed is controlled by a field (absorptionProbability).
         *
         * @param neutron
         */
        private void handleCollision( Neutron neutron ) {
            if( random.nextDouble() <= absorptionProbability ) {
                model.removeModelElement( neutron );
            }
            else {
                Vector2D v = neutron.getVelocity();
                double theta = Math.atan2( area.getBounds().getCenterY() - neutron.getPositionPrev().getY(),
                                           area.getBounds().getCenterX() - neutron.getPositionPrev().getX() );
                double gamma = theta + random.nextDouble() * reflectionSpreadAngle * MathUtil.nextRandomSign();
                double delta = gamma - v.getAngle();
                v.rotate( delta );
                neutron.setVelocity( v );
            }
        }
    }
}
