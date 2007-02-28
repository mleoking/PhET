/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.model;

/**
 * User: Sam Reid
 * Date: Aug 4, 2005
 * Time: 10:01:42 PM
 * Copyright (c) Aug 4, 2005 by Sam Reid
 */

public class Collision {
    private Block copy;
    private Block block;
    private RampPhysicalModel rampPhysicalModel;
    private double dt;

    public Collision( Block copy, Block block, RampPhysicalModel rampPhysicalModel, double dt ) {
        this.copy = copy;
        this.block = block;
        this.rampPhysicalModel = rampPhysicalModel;
        this.dt = dt;
    }

    public Block getCopy() {
        return copy;
    }

    public Block getBlock() {
        return block;
    }

    public RampPhysicalModel getRampPhysicalModel() {
        return rampPhysicalModel;
    }

    public double getAbsoluteMomentumChange() {
        return Math.abs( getMomentumChange() );
    }

    public double getMomentumChange() {
        return block.getMomentum() - copy.getMomentum();
    }

    public double getDt() {
        return dt;
    }
}
