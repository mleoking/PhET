//package edu.colorado.phet.ec3.model;
//
///**
// * User: Sam Reid
// * Date: Oct 17, 2006
// * Time: 12:01:23 AM
// * Copyright (c) Oct 17, 2006 by Sam Reid
// */
//
//public class ImmutablePotentialEnergyMetric implements PotentialEnergyMetric {
//    private double zeroPointPotentialY;
//    private double gravity;
//
//    public ImmutablePotentialEnergyMetric( double zeroPointPotentialY, double gravity ) {
//        this.zeroPointPotentialY = zeroPointPotentialY;
//        this.gravity = gravity;
//    }
//
//    public double getPotentialEnergy( Body body ) {
//        double h = zeroPointPotentialY - body.getCenterOfMass().getY();
//        return body.getMass() * gravity * h;
//    }
//
//    public double getGravity() {
//        return gravity;
//    }
//
//    public PotentialEnergyMetric copy() {
//        return this;//we are immutable!
//    }
//}
