package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;

public class Wolf implements NaturalSelectionClock.Listener {

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

        model.getClock().addPhysicalListener( this );

        getNewTarget();
    }

    private void getNewTarget() {
        target = frenzy.getNewWolfTarget( this );
    }

    public void disable() {
        if ( enabled ) {
            model.getClock().removePhysicalListener( this );
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
        if ( !hunting ) {
            movingRight = random.nextBoolean();
        }
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

    public void stopHunting() {
        hunting = false;
        movingRight = getPosition().getX() > 0;
    }

    private void moveAround() {
        double killDistance = NaturalSelectionConstants.getSettings().getWolfKillDistance();
        double doubleBackDistance = NaturalSelectionConstants.getSettings().getWolfDoubleBackDistance();
        double maxStep = NaturalSelectionConstants.getSettings().getWolfMaxStep();

        if ( hunting ) {
            if ( target == null ) {
                stopHunting();
                return;
            }
            if ( !target.isAlive() ) {
                getNewTarget();
            }
            if ( target == null ) {
                stopHunting();
                return;
            }

            double mX = position.getX();
            double mY = position.getY();
            double mZ = position.getZ();
            Point3D targetPosition = target.getPosition();
            double tX = targetPosition.getX();
            double tY = targetPosition.getY();
            double tZ = targetPosition.getZ();
            double tGround = model.getLandscape().getGroundY( tX, tZ );

            double killX;

            boolean rightOfTarget = mX > tX;

            if ( rightOfTarget ) {
                killX = tX + model.getLandscape().landscapeDistanceToModel( wolfScale( position ) * NaturalSelectionConstants.WOLF_WIDTH / 2, mZ );
            }
            else {
                killX = tX - model.getLandscape().landscapeDistanceToModel( wolfScale( position ) * NaturalSelectionConstants.WOLF_WIDTH / 2, mZ );
            }

            boolean rightOfKillpoint = mX > killX;

            double aimX = 0;
            double aimZ = 0;
            boolean movePosition = true;

            if ( ( rightOfTarget == rightOfKillpoint ) && ( rightOfTarget != movingRight ) ) {
                // regular approach, moving towards bunny already
                aimX = tX;
                aimZ = tZ;

                double targetDistance = Point3D.distance( killX, 0, tZ, mX, 0, mZ );
                if ( targetDistance < killDistance ) {
                    target.die();
                    notifyKilledBunny();
                }
            }
            else if ( ( rightOfTarget == rightOfKillpoint ) && ( Math.abs( killX - mX ) >= doubleBackDistance ) && ( rightOfTarget == movingRight ) ) {
                // far from target, but moving the wrong way

                // point towards the target
                movingRight = !rightOfTarget;
                movePosition = false;
            }
            else {
                // too close to the target, either:
                // (a) between target and kill point
                // or (b) outside of kill point but moving wrong direction
                // we need to move farther from the target so that our "mouth" can make an approach to get the bunny

                // turn away from target
                movingRight = rightOfTarget;

                final double buffer = 10;

                if ( rightOfTarget ) {
                    aimX = killX + doubleBackDistance + buffer;
                }
                else {
                    aimX = killX - doubleBackDistance - buffer;
                }
                aimZ = tZ;
            }

            if ( movePosition ) {
                double dX = aimX - mX;
                double dZ = aimZ - mZ;
                double distance = Math.sqrt( dX * dX + dZ * dZ );

                if ( distance > maxStep ) {
                    // if our aim point is too far away, limit our step to WOLF_MAX_STEP
                    dX = maxStep * dX / distance;
                    dZ = maxStep * dZ / distance;
                }

                double x = mX + dX;
                double z = mZ + dZ;

                position = new Point3D.Double( x, model.getLandscape().getGroundY( x, z ), z );
            }

        }
        else {
            if ( movingRight ) {
                setX( position.getX() + maxStep );
            }
            else {
                setX( position.getX() - maxStep );
            }
        }

        notifyPositionChanged();

    }

    public static double wolfScale( Point3D position ) {
        return Landscape.NEARPLANE * 0.25 / position.getZ();
    }

    public void onTick( ClockEvent event ) {
        moveAround();
    }

    public boolean isFlipped() {
        return !movingRight;
    }

    private void notifyPositionChanged() {
        notifyListenersOfEvent( new Event( this, Event.TYPE_POSITION_CHANGED ) );
    }

    private void notifyKilledBunny() {
        notifyListenersOfEvent( new Event( this, Event.TYPE_KILLED_BUNNY ) );
    }

    private void notifyListenersOfEvent( Event event ) {
        for ( Listener listener : listeners ) {
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
        public static final int TYPE_KILLED_BUNNY = 1;

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
