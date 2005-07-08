/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jun 21, 2005
 * Time: 9:41:09 PM
 * Copyright (c) Jun 21, 2005 by Sam Reid
 */

public class DetectorSet {
    private ArrayList detectors = new ArrayList();
    private static final Random random = new Random();
    private DiscreteModel.Listener listener;
    private Wavefunction wavefunction;
    private boolean autodetect = false;

    public DetectorSet( Wavefunction wavefunction ) {
        this.wavefunction = wavefunction;
        listener = new DetectorSet.MyListener();
    }

    public void reset() {
        for( int i = 0; i < detectors.size(); i++ ) {
            Detector detector = (Detector)detectors.get( i );
            detector.reset();
        }
    }

    public void addDetector( Detector detector ) {
        detectors.add( detector );
    }

    public void updateDetectorProbabilities() {
        for( int i = 0; i < detectors.size(); i++ ) {
            Detector detector = (Detector)detectors.get( i );
            detector.updateProbability( getWavefunction() );
        }
    }

    public void fireAllEnabledDetectors() {
        fireAllEnabledDetectors( new FireEnabled() );
    }

    public boolean containsDetector( Detector detector ) {
        return detectors.contains( detector );
    }

    private static interface FireStrategy {

        void fire( Detector detector, Wavefunction wavefunction, double norm );
    }

    private static class FireEnabled implements FireStrategy {

        public void fire( Detector detector, Wavefunction wavefunction, double norm ) {

            detector.fire( wavefunction, norm );
        }
    }

    private static class FireWhenReady implements FireStrategy {

        public void fire( Detector detector, Wavefunction wavefunction, double norm ) {
            if( detector.timeToFire() ) {
                detector.fire( wavefunction, norm );
            }
        }
    }

    public void fireAllEnabledDetectors( FireStrategy fireStrategy ) {
        double norm = 1.0;
        if( wavefunction.getMagnitude() > 0 ) {
            for( int i = 0; i < detectors.size(); i++ ) {
                Detector detector = (Detector)detectors.get( i );
                if( detector.isEnabled() ) {
                    fireStrategy.fire( detector, getWavefunction(), norm );
                }
            }
        }
    }

    public Point getCollapsePoint( Rectangle bounds ) {
        //compute a probability model for each dA
        Wavefunction copy = getWavefunction().copy();
        for( int i = 0; i < copy.getWidth(); i++ ) {
            for( int j = 0; j < copy.getHeight(); j++ ) {
                if( !bounds.contains( i, j ) ) {
                    copy.valueAt( i, j ).zero();
                }
            }
        }

        copy.normalize();//in case we care
        Complex runningSum = new Complex();
        double rnd = random.nextDouble();

        for( int i = 0; i < copy.getWidth(); i++ ) {
            for( int j = 0; j < copy.getHeight(); j++ ) {
                Complex psiStar = copy.valueAt( i, j ).complexConjugate();
                Complex psi = copy.valueAt( i, j );
                Complex term = psiStar.times( psi );
                double pre = runningSum.abs();
                runningSum = runningSum.plus( term );
                double post = runningSum.abs();
                if( pre <= rnd && rnd <= post ) {
                    return new Point( i, j );
                }
            }
        }
//        new RuntimeException( "No collapse point." ).printStackTrace();
        new RuntimeException( "No collapse point." ).printStackTrace();
        return new Point( 0, 0 );
//        return new Point( 0, 0 );
    }

    public Point getCollapsePoint() {//todo call getCollapsePoint with the internal bounds.
        return getCollapsePoint( getWavefunction().getBounds() );
    }

    private Wavefunction getWavefunction() {
        return wavefunction;
    }

    public DiscreteModel.Listener getListener() {
        return listener;
    }

    private void notifyTimeStepped() {
        for( int i = 0; i < detectors.size(); i++ ) {
            Detector detector = (Detector)detectors.get( i );
            detector.notifyTimeStepped();
        }
    }

    public void setAutoDetect( boolean selected ) {
        this.autodetect = selected;
    }

    public void enableAll() {
        for( int i = 0; i < detectors.size(); i++ ) {
            Detector detector = (Detector)detectors.get( i );
            detector.setEnabled( true );
        }
    }

    public void removeDetector( Detector detector ) {
        detectors.remove( detector );
    }

    public class MyListener extends DiscreteModel.Adapter {
        public void finishedTimeStep( DiscreteModel model ) {
            notifyTimeStepped();
            updateDetectorProbabilities();
            if( autodetect && model.isDetectionCausesCollapse() ) {
                fireAllEnabledDetectors( new FireWhenReady() );
            }
        }
    }
}
