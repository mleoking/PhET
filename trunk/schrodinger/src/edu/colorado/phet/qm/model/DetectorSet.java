/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.qm.model.operators.PxValue;

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
    private Random random = new Random();
    private DiscreteModel discreteModel;
    private DiscreteModel.Listener listener;

    public DetectorSet( DiscreteModel discreteModel ) {
        this.discreteModel = discreteModel;
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
            detector.updateProbability( discreteModel.getWavefunction() );
        }
    }

    public void handleCollapse() {
        Wavefunction wavefunction = discreteModel.getWavefunction();
        double norm = wavefunction.getMagnitude();
        if( norm >= 0.2 ) {//ensure there's a particle.
//                System.out.println( "detectorNorm=" + norm);
            for( int i = 0; i < detectors.size(); i++ ) {
                Detector detector = (Detector)detectors.get( i );
                detector.fire( wavefunction, norm );
            }
        }
    }

    public Point getCollapsePoint( Rectangle bounds ) {
        //compute a probability model for each dA
        Wavefunction copy = discreteModel.getWavefunction().copy();
        for( int i = 0; i < copy.getWidth(); i++ ) {
            for( int j = 0; j < copy.getHeight(); j++ ) {
                if( !bounds.contains( i, j ) ) {
                    copy.valueAt( i, j ).zero();
                }
            }
        }

        copy.normalize();//in case we care
        //todo could work without a normalize, just choose random.nextDouble between 0 and totalprob.
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
        throw new RuntimeException( "No collapse point." );
//        return new Point( 0, 0 );
    }

    public Point getCollapsePoint() {//todo call getCollapsePoint with the internal bounds.
        return getCollapsePoint( getWavefunction().getBounds() );
    }

    public void collapse( Point collapsePoint, int collapseLatticeDX ) {
        double px = new PxValue().compute( getWavefunction() );
        new GaussianWave( collapsePoint, new Vector2D.Double( px, 0 ), collapseLatticeDX ).initialize( discreteModel.getWavefunction() );
    }

    private Wavefunction getWavefunction() {
        return discreteModel.getWavefunction();
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

    public class MyListener implements DiscreteModel.Listener {
        public void finishedTimeStep( DiscreteModel model ) {
            notifyTimeStepped();
            updateDetectorProbabilities();
            if( model.isDetectionCausesCollapse() ) {
                handleCollapse();
            }
        }

        public void sizeChanged() {
        }

        public void potentialChanged() {
        }

        public void beforeTimeStep( DiscreteModel discreteModel ) {
        }
    }
}
