// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Jul 2, 2006
 * Time: 12:31:08 AM
 */

public class SparkManager implements ModelElement {
    int[] numElectrons = new int[]{10, 15, 20, 35, 30, 35, 40, 50, 60, 70};
    double[] dist = new double[]{20, 30, 40, 40, 60, 70, 80, 100, 120, 140};
    private ArmNode armNode;
    private DoorknobNode doorknobNode;
    JadeElectronSet jadeElectronSet;
    private TravoltageModule travoltageModule;

    public SparkManager( ArmNode armNode, DoorknobNode doorknobNode, JadeElectronSet jadeElectronSet, TravoltageModule travoltageModule ) {
        this.armNode = armNode;
        this.doorknobNode = doorknobNode;
        this.jadeElectronSet = jadeElectronSet;
        this.travoltageModule = travoltageModule;
    }

    private boolean shouldFire() {
        //low number of electrons requires really close
        double distToKnob = getFingerKnobDistance();
        int n = (int)( jadeElectronSet.getNumElectrons() / 2.0 );
//        System.out.println( "distToKnob = " + distToKnob + ", n=" + n );

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
        travoltageModule.fireSpark();
//        System.out.println( "SparkManager.fireSpark" );
    }
}
