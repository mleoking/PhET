package edu.colorado.phet.movingmanii.model;

/**
 * The ManState records the state of the man character for purposes of record and playback.
 * It is immutable since you should not be able to modify a recorded sample.
 *
 * @author Sam Reid
 */
public class ManState {
    private final double position;
    private final double velocity;
    private final double acceleration;
    private final MovingMan.MotionStrategy motionStrategy;

    public ManState(double position, double velocity, double acceleration, MovingMan.MotionStrategy motionStrategy) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.motionStrategy = motionStrategy;
    }

    public double getPosition() {
        return position;
    }

    public double getVelocity() {
        return velocity;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public MovingMan.MotionStrategy getMotionStrategy() {
        return motionStrategy;
    }
}
