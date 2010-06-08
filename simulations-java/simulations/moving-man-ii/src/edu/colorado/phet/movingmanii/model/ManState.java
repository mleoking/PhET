package edu.colorado.phet.movingmanii.model;

/**
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
