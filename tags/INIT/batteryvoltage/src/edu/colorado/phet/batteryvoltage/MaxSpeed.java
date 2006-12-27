package edu.colorado.phet.batteryvoltage;

import phys2d.Particle;
import phys2d.propagators.VelocityUpdate;

public class MaxSpeed implements ParticleMoveListener {
    VelocityUpdate left;
    VelocityUpdate right;

    public MaxSpeed( VelocityUpdate left, VelocityUpdate right ) {
        this.left = left;
        this.right = right;
    }

    public void particleMoved( Battery b, Particle p ) {
        //225 for 20 on that side
        //Speed should increase as the number of particles increases.
        int lhs = b.numLeft();
        int rhs = b.numRight();

        int leftSpeed = toSpeed( lhs );
        left.setMaxSpeed( leftSpeed );

        int rightSpeed = toSpeed( rhs );
        right.setMaxSpeed( rightSpeed );
        //o.O.p("lhs="+lhs+", rhs="+rhs+", leftSpeed="+leftSpeed+", rightSpeed="+rightSpeed);
    }

    private int toSpeed( int numElectrons ) {
        int speed = 40 + 10 * numElectrons;
        //int speed=40+6*numElectrons;
        return speed;
    }
}


