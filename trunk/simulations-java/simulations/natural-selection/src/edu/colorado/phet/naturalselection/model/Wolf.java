package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.view.SpriteHandler;

public class Wolf extends ClockAdapter {
    private double x;
    private double y;
    private double z;

    private boolean movingRight;
    private ArrayList<Listener> listeners;

    private NaturalSelectionModel model;

    private boolean enabled = true;
    private static final double HORIZONTAL_STEP = 15.0;

    // random number generator
    private static final Random random = new Random( System.currentTimeMillis() );

    public Wolf( NaturalSelectionModel model, Frenzy frenzy ) {
        this.model = model;

        movingRight = true;
        if ( random.nextInt( 2 ) == 0 ) {
            movingRight = false;
        }

        listeners = new ArrayList<Listener>();

        setInitialPosition();

        model.getClock().addClockListener( this );
    }

    public void disable() {
        if ( enabled ) {
            model.getClock().removeClockListener( this );
        }

        enabled = false;
    }

    private void setInitialPosition() {
        // TODO: refactor initial random positions into SpriteHandler
        x = Math.random() * ( SpriteHandler.MAX_X - SpriteHandler.MIN_X ) + SpriteHandler.MIN_X;

        // start on the ground
        y = SpriteHandler.MIN_Y;

        z = Math.random() * ( SpriteHandler.MAX_Z - SpriteHandler.MIN_Z ) + SpriteHandler.MIN_Z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setX( double x ) {
        this.x = x;
    }

    public void setY( double y ) {
        this.y = y;
    }

    public void setZ( double z ) {
        this.z = z;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    private void moveAround() {

        if ( movingRight ) {
            setX( getX() + HORIZONTAL_STEP );
            if ( getX() >= SpriteHandler.MAX_X ) {
                movingRight = false;
            }
        }
        else {
            setX( getX() - HORIZONTAL_STEP );
            if ( getX() <= SpriteHandler.MIN_X ) {
                movingRight = true;
            }
        }

        notifyPositionChanged();

    }


    public void simulationTimeChanged( ClockEvent clockEvent ) {
        moveAround();

    }


    private void notifyPositionChanged() {
        notifyListenersOfEvent( new Event( this, Event.TYPE_POSITION_CHANGED ) );
    }

    private void notifyListenersOfEvent( Event event ) {
        for ( Iterator<Listener> iterator = listeners.iterator(); iterator.hasNext(); ) {
            Listener listener = iterator.next();
            listener.onEvent( event );
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public interface Listener {
        public void onEvent( Event event );
    }

    public class Event {
        public static final int TYPE_POSITION_CHANGED = 0;

        public final int type;
        public final Wolf wolf;

        public Event( Wolf wolf, int type ) {
            this.type = type;
            this.wolf = wolf;
        }

        public int getType() {
            return type;
        }

        public Wolf getWolf() {
            return wolf;
        }
    }

}
