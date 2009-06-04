package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

public class Wolf extends ClockAdapter {
    private static final double MAX_STEP = 7.0;

    private Point3D position;

    private boolean movingRight;

    private ArrayList<Listener> listeners;

    private NaturalSelectionModel model;
    private boolean enabled = true;
    private boolean hunting = true;

    private Bunny target;

    private Frenzy frenzy;

    // random number generator
    private static final Random random = new Random( System.currentTimeMillis() );

    public Wolf( NaturalSelectionModel model, Frenzy frenzy ) {
        this.model = model;
        this.frenzy = frenzy;

        movingRight = true;
        if ( random.nextInt( 2 ) == 0 ) {
            movingRight = false;
        }

        listeners = new ArrayList<Listener>();

        position = model.getLandscape().getRandomGroundPosition();

        model.getClock().addClockListener( this );

        getNewTarget();
    }

    private void getNewTarget() {
        target = frenzy.getNewWolfTarget( this );
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

    public boolean isHunting() {
        return hunting;
    }

    public void setHunting( boolean hunting ) {
        this.hunting = hunting;
    }

    public Bunny getTarget() {
        return target;
    }

    private void setX( double x ) {
        position = new Point3D.Double( x, position.getY(), position.getZ() );
    }

    private double groundDistance( Point3D a, Point3D b ) {
        double x = a.getX() - b.getX();
        double z = a.getZ() - b.getZ();
        return Math.sqrt( x * x + z * z );
    }

    private void moveAround() {
        if ( hunting ) {
            if ( target == null ) {
                hunting = false;
                return;
            }
            if ( !target.isAlive() ) {
                getNewTarget();
            }
            if ( target == null ) {
                hunting = false;
                return;
            }

            Point3D targetPosition = target.getPosition();
            double distance = groundDistance( targetPosition, position );
            if ( distance < MAX_STEP ) {
                target.die();
            }
            else {
                Point3D diff = new Point3D.Double( targetPosition.getX() - position.getX(), 0, targetPosition.getZ() - position.getZ() );
                diff.setLocation( diff.getX() * MAX_STEP / distance, 0, diff.getZ() * MAX_STEP / distance );

                position = new Point3D.Double( position.getX() + diff.getX(), position.getY(), position.getZ() + diff.getZ() );
                movingRight = diff.getX() >= 0;
                /*
                if ( movingRight ) {
                    setX( position.getX() + MAX_STEP );
                    if ( position.getX() >= model.getLandscape().getMaximumX( position.getZ() ) ) {
                        movingRight = false;
                    }
                }
                else {
                    setX( position.getX() - MAX_STEP );
                    if ( position.getX() <= -model.getLandscape().getMaximumX( position.getZ() ) ) {
                        movingRight = true;
                    }
                }
                */
            }
        }
        else {
            if ( movingRight ) {
                setX( position.getX() + MAX_STEP );
            }
            else {
                setX( position.getX() - MAX_STEP );
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
