/**
 * Class: Photon
 * Package: edu.colorado.phet.lasers.model
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.photon;

import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.coreadditions.SubscriptionService;
import edu.colorado.phet.collision.SphericalBody;
import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollidableAdapter;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.mechanics.Body;

import java.util.ArrayList;
import java.util.Observer;
import java.util.LinkedList;
import java.util.List;
import java.awt.geom.Point2D;

/**
 *
 */
public class Photon extends Particle implements Collidable {

    static public double s_speed = 1;
//    static public double s_speed = 500;
    static public double s_radius = 10;
    static public int RED = 680;
    static public int DEEP_RED = 640;
    static public int BLUE = 400;
    static public int GRAY = Integer.MAX_VALUE;

    // Free pool of photons. We do this so we don't have to use the heap
    // at run-time
    static private int freePoolSize = 2000;
    static private ArrayList freePool = new ArrayList( freePoolSize );
    // Populate the free pool
    static {
        for( int i = 0; i < freePoolSize; i++ ){
            freePool.add( new Photon() );
        }
    }

    static public Photon create() {
        Photon newPhoton = null;
        /*if( !freePool.isEmpty() ) {
            newPhoton = (Photon)freePool.remove( 0 );
        }
        else */{
            newPhoton = new Photon();
//            freePool.add( new Photon() );
        }
        return newPhoton;
    }

//    static public Photon create( Photon photon ) {
//        Photon newPhoton = create();
//        newPhoton.setVelocity( new Vector2D.Double( photon.getVelocity() ));
//        newPhoton.setWavelength( photon.getWavelength() );
//        newPhoton.numStimulatedPhotons = photon.numStimulatedPhotons;
//        return newPhoton;
//    }

    static public Photon createStimulated( Photon stimulatingPhoton ) {
        stimulatingPhoton.numStimulatedPhotons++;
        if( stimulatingPhoton.numStimulatedPhotons > 1 ) {
//            System.out.println( "!!!" );
        }

        Photon newPhoton = create();
        newPhoton.setVelocity( new Vector2D.Double( stimulatingPhoton.getVelocity() ));
        newPhoton.setWavelength( stimulatingPhoton.getWavelength() );
        int yOffset = stimulatingPhoton.numStimulatedPhotons * 4;
        newPhoton.setPosition( stimulatingPhoton.getPosition().getX(),
                               stimulatingPhoton.getPosition().getY() - yOffset );
//                               stimulatingPhoton.getPosition().getY() - stimulatingPhoton.getRadius() );

        return newPhoton;
    }

    /**
     * If the photon is created by a CollimatedBeam, it should use this method,
     * so that the photon can tell the CollimatedBeam if it is leaving the system.
     * @param beam
     */
    static public Photon create( CollimatedBeam beam ) {
        Photon newPhoton = create();
        newPhoton.beam = beam;
        newPhoton.setWavelength( beam.getWavelength() );
        return newPhoton;
    }



    private int numObservers;
    private int numStimulatedPhotons;
    // If this photon was produced by the stimulation of another, this
    // is a reference to that photon.
    private Photon parentPhoton;
    // If this photon has stimulated the production of another photon, this
    // is a reference to that photon
    private Photon childPhoton;
    private CollidableAdapter collidableAdapter;
    private boolean isCollidable;

    public synchronized void addObserver( SimpleObserver o ) {
        super.addObserver( o );
        if( numObservers > 0 ) {
            System.out.println( "$$$" );
        }
        numObservers++;
    }


    private double wavelength;
    private CollimatedBeam beam;
    // This list keeps track of atoms that the photon has collided with
    private ArrayList contactedAtoms = new ArrayList();

    /**
     * Constructor is private so that clients of the class must use static create()
     * methods. This allows us to manage a free pool of photons and not hit the
     * heap so hard.
     */
    private Photon() {
//        super( s_radius );
        collidableAdapter = new CollidableAdapter( this );
        setVelocity( s_speed, 0 );
//        setMass( 1 );
    }

//    private List listeners = new LinkedList();
    private SubscriptionService bulletinBoard = new SubscriptionService();

    public interface Listener {
        void leavingSystem( Photon photon );
    }

    public void addListener( Listener listener ) {
        bulletinBoard.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        bulletinBoard.removeListener( listener );
    }

    /**
     * Rather than use the superclass behavior, the receiver
     * puts itself in the class free pool, so it can be used
     * again. This helps prevent us from flogging the heap.
     */
    public void removeFromSystem() {
        bulletinBoard.notifyListeners( new SubscriptionService.Notifier() {
            public void doNotify( Object obj ) {
                ((Listener)obj).leavingSystem( Photon.this );
            }
        } );
        if( beam != null ) {
            beam.removePhoton( this );
        }

//        freePool.add( this );
//        setChanged();
//        notifyObservers( Particle.S_REMOVE_BODY );
    }

    public double getWavelength() {
        return wavelength;
    }

    public void setWavelength( double wavelength ) {
        this.wavelength = wavelength;
    }

    public double getEnergy() {
        // Some function based on wavelength
        return ( 1 / getWavelength() );
    }

    public void collideWithAtom( Atom atom ) {
//        contactedAtoms.add( atom );
    }

    public boolean hasCollidedWithAtom( Atom atom ) {
        return contactedAtoms.contains( atom );
    }

    public Photon getParentPhoton() {
        return parentPhoton;
    }

    public void setParentPhoton( Photon parentPhoton ) {
        this.parentPhoton = parentPhoton;
    }

    public Photon getChildPhoton() {
        return childPhoton;
    }

    public void setChildPhoton( Photon childPhoton ) {
        this.childPhoton = childPhoton;
    }


    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }
}
