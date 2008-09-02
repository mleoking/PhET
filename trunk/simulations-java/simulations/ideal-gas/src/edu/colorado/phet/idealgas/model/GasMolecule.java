/*
 * Class: GasMolecule
 * Package: edu.colorado.phet.physics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.idealgas.collision.SolidSphere;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GasMolecule extends SolidSphere {

    //
    // Static fields and methods
    //
    public static float s_radius = 5.0f;

    public static void enableParticleParticleInteractions( boolean interactionsEnabled ) {
        if( interactionsEnabled ) {
            s_radius = 5.0f;
        }
        else {
            s_radius = 0.0f;
        }
    }

    public static Point2D getCm( List instances ) {
        Point2D cm = new Point2D.Double();
        for( int i = 0; i < instances.size(); i++ ) {
            GasMolecule molecule = (GasMolecule)instances.get( i );
            cm.setLocation( cm.getX() + molecule.getPosition().getX(),
                            cm.getY() + molecule.getPosition().getY() );
        }
        if( instances.size() != 0 ) {
            cm.setLocation( cm.getX() / instances.size(),
                            cm.getY() / instances.size() );
        }
        return cm;
    }


    // List of GasMolecule.Observers
    private ArrayList observers = new ArrayList();
    private boolean isInBox = false;

    public interface Observer extends SimpleObserver {
        void removedFromSystem();
    }

    public GasMolecule( Point2D position, Vector2D velocity, Vector2D acceleration, float mass, double radius ) {
        super( position, velocity, acceleration, mass, radius );
    }

    public Vector2D getVelocity() {
        return super.getVelocity();
    }

    public void addObserver( GasMolecule.Observer observer ) {
        observers.add( observer );
        this.addObserver( (SimpleObserver)observer );
    }

    public void removeObserver( GasMolecule.Observer observer ) {
        observers.remove( observer );
    }

    public void setVelocity( Vector2D velocity ) {
        super.setVelocity( velocity );
    }

    public void setVelocity( double vX, double vY ) {
        super.setVelocity( vX, vY );
    }

    public void setVelocityX( double vX ) {
        super.setVelocity( vX, getVelocity().getY() );
    }

    public void setVelocityY( double vY ) {
        super.setVelocity( getVelocity().getX(), vY );
    }

    public double getRadius() {
        return s_radius;
    }

    public void setPosition( double x, double y ) {
        super.setPosition( x, y );
    }

    public void setPosition( Point2D position ) {
        super.setPosition( position );
    }

    public void removeYourselfFromSystem() {
        // Work with a copy of the list of observers, in case any of them remove
        // themselves as observers in their implementations of removeFromSystem()
        List observerCopies = new ArrayList( observers );
        for( int i = 0; i < observerCopies.size(); i++ ) {
            Observer observer = (Observer)observerCopies.get( i );
            observer.removedFromSystem();
        }
    }
}
