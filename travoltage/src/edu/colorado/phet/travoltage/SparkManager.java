/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Jul 2, 2006
 * Time: 12:31:08 AM
 * Copyright (c) Jul 2, 2006 by Sam Reid
 */

public class SparkManager implements ModelElement {
    int[] numElectrons = new int[]{10, 15, 20, 35, 30, 35, 40, 50, 60, 70};
    double[] dist = new double[]{20, 30, 40, 40, 60, 70, 80, 100, 120, 140};
    private ArmNode armNode;
    private DoorknobNode doorknobNode;
    JadeElectronSet jadeElectronSet;

    public SparkManager( ArmNode armNode, DoorknobNode doorknobNode, JadeElectronSet jadeElectronSet ) {
        this.armNode = armNode;
        this.doorknobNode = doorknobNode;
        this.jadeElectronSet = jadeElectronSet;
    }

    private boolean shouldFire() {
        //low number of electrons requires really close
        double distToKnob = getFingerKnobDistance();
//        Point finger = Finger.getFingerLocation( arm );
//        double distToKnob = new DoublePoint( doorknobX, doorknobY ).distance( new DoublePoint( finger.x, finger.y ) );
        int n = jadeElectronSet.getNumElectrons();

        //edu.colorado.phet.common.util.Debug.traceln("Distance to knob="+distToKnob+", edu.colorado.phet.common count="+n);
        for( int i = 0; i < numElectrons.length; i++ ) {
            if( n > numElectrons[i] && distToKnob < dist[i] ) {
                return true;
            }
        }
        return false;
    }

    private double getFingerKnobDistance() {
        return armNode.getGlobalFingertipPoint().distance( doorknobNode.getGlobalKnobPoint() );
    }

    public void stepInTime( double dt ) {
        if( shouldFire() ) {
            fireSpark();
        }
    }

    private void fireSpark() {

    }
}
