/**
 * Class: EmfSensingElectron
 * Package: edu.colorado.phet.emf.model
 * Author: Another Guy
 * Date: May 29, 2003
 */
package edu.colorado.phet.emf.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.emf.model.movement.ManualMovement;

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

public class EmfSensingElectron extends Electron implements Observer {
    private Point2D location;
    RetardedFieldElement retardedFieldElement;
    private Electron sourceElectron;
    private Point2D prevPosition = new Point2D.Double();
    private Vector2D fieldStrength;

    public EmfSensingElectron( Point2D.Double location, Electron sourceElectron ) {
        super( location );
        this.location = location;
        this.sourceElectron = sourceElectron;
        retardedFieldElement = new RetardedFieldElement( location, sourceElectron );
        this.setMovementStrategy( new ManualMovement() );
    }

    public synchronized void stepInTime( double dt ) {
        super.stepInTime( dt );
        fieldStrength = sourceElectron.getFieldAtLocation( location );
        // The field strength is a force on the electron, so we must compute an
        // acceleration
        Vector2D v = this.getVelocity();
        float m = getMass();
        Vector2D a = fieldStrength;
//        Vector2D a = new Vector2D( fieldStrength.multiply( m ));
        v.setX( v.getX() + a.getX() * (float)dt );
        v.setY( v.getY() + a.getY() * (float)dt );

//        System.out.println( "ay = " + a.getY() );
        double x = this.getCurrentPosition().getX();
        double y = this.getCurrentPosition().getY();

        location = this.getCurrentPosition();
        x += v.getX() * dt;
        y += v.getY() * dt;
        location.setLocation( x, y );
        this.setCurrentPosition( location );
        updateObservers();

    }

    public void update( Observable o, Object arg ) {

//        fieldStrength = retardedFieldElement.getFieldStrength();
/*
        double x = this.getCurrentPosition().getX();
        double y = this.getCurrentPosition().getY();

        fieldStrength = sourceElectron.getFieldAtLocation( location );
        // The field strength is a force on the electron, so we must compute an
        // acceleration
        Vector2D v = this.getVelocity();
        double m = getMass();
        Vector2D a = new Vector2D( fieldStrength.multiply( m ));

        // TODO: the / 2 in the next two lines is a very rough intial approximation
        // in the fall-off of the field at distance
        y += fieldStrength.getY() * EmfModel.instance().getClock().getDt() / 2;
        location = this.getCurrentPosition();
        location.setLocation( x, y );
        this.setCurrentPosition( location );
        updateObservers();
*/
    }
}
