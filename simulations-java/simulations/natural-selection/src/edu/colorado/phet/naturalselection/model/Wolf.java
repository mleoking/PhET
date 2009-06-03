package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

public class Wolf extends ClockAdapter {
    private Point3D position;

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

        position = model.getLandscape().getRandomGroundPosition();

        model.getClock().addClockListener( this );
    }

    public void disable() {
        if ( enabled ) {
            model.getClock().removeClockListener( this );
        }

        enabled = false;
    }

    public void setPosition( Point3D position ) {
        this.position = position;
    }

    public Point3D getPosition() {
        return position;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    private void setX( double x ) {
        position = new Point3D.Double( x, position.getY(), position.getZ() );
    }

    private void moveAround() {

        if ( movingRight ) {
            setX( position.getX() + HORIZONTAL_STEP );
            if ( position.getX() >= model.getLandscape().getMaximumX( position.getZ() ) ) {
                movingRight = false;
            }
        }
        else {
            setX( position.getX() - HORIZONTAL_STEP );
            if ( position.getX() <= -model.getLandscape().getMaximumX( position.getZ() ) ) {
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
