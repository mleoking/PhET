// Copyright 2002-2012, University of Colorado

/**
 * Class: Gravity
 * Package: edu.colorado.phet.idealgas.model
 * Author: Another Guy
 * Date: Jul 19, 2004
 */
package edu.colorado.phet.idealgas.model;

import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import edu.colorado.phet.common.mechanics.Body;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.EventChannel;

public class Gravity implements ModelElement {

    private MutableVector2D acceleration = new MutableVector2D();
    private IdealGasModel model;

    public Gravity( IdealGasModel model ) {
        this.model = model;
        this.setAmt( 0 );
    }

    public void stepInTime( double dt ) {
        List bodies = model.getBodies();
        for ( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body) bodies.get( i );
            body.setAcceleration( body.getAcceleration().add( acceleration ) );
        }
    }

    public double getAmt() {
        return acceleration.getY();
    }

    public void setAmt( double amt ) {
        double oldAmt = acceleration.getMagnitude();
        this.acceleration = new MutableVector2D( 0, amt );
        double change = acceleration.getMagnitude() - oldAmt;
//        fireEvent( new ChangeEvent( this, change ) );

        try {
            listenerProxy.gravityChanged( new ChangeEvent( this, change ) );
        }
        catch ( java.lang.reflect.UndeclaredThrowableException e ) {
            System.out.println( "e = " + e );
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Inner classes
    private EventChannel channel = new EventChannel( ChangeListener.class );
    private ChangeListener listenerProxy = (ChangeListener) channel.getListenerProxy();

    public void addListener( ChangeListener listener ) {
        channel.addListener( listener );
    }

    public void removeListener( ChangeListener listener ) {
        channel.removeListener( listener );
    }

    public interface ChangeListener extends EventListener {
        void gravityChanged( Gravity.ChangeEvent event );
    }

    public class ChangeEvent extends EventObject {
        private double change;

        public ChangeEvent( Object source, double change ) {
            super( source );
            this.change = change;
        }

        public double getChange() {
            return change;
        }

        public Gravity getGravity() {
            return (Gravity) getSource();
        }
    }

}
