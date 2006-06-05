/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 21, 2005
 * Time: 9:41:09 PM
 * Copyright (c) Jun 21, 2005 by Sam Reid
 */

public class DetectorSet {
    private ArrayList detectors = new ArrayList();
    private QWIModel.Listener listener;
    private Wavefunction wavefunction;
    private boolean autodetect = true;
    private boolean repeats = false;//forAutoDetect

    public DetectorSet( Wavefunction wavefunction ) {
        this.wavefunction = wavefunction;
        listener = new DetectorSet.AutoDetectAdapter();
    }

    public void setRepeats( boolean notRepeats ) {
        this.repeats = notRepeats;
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

    public boolean isAutoDetect() {
        return autodetect;
    }

    public void gunFired() {
        enableAll();
    }

    public int numDetectors() {
        return detectors.size();
    }

    public Detector detectorAt( int i ) {
        return (Detector)detectors.get( i );
    }

    private static interface FireStrategy {
        boolean tryToGrab( Detector detector, Wavefunction wavefunction, double norm );
    }

    private static class FireEnabled implements FireStrategy {
        public boolean tryToGrab( Detector detector, Wavefunction wavefunction, double norm ) {
            return detector.tryToGrab( wavefunction, norm );
        }
    }

    private static class AutoDetect implements FireStrategy {
        private boolean detectionDisablesDetectorForThisParticle = true;

        public AutoDetect( boolean detectionDisablesDetectorForThisParticle ) {
            this.detectionDisablesDetectorForThisParticle = detectionDisablesDetectorForThisParticle;
        }

        public boolean isDetectionDisablesDetectorForThisParticle() {
            return detectionDisablesDetectorForThisParticle;
        }

        public void setDetectionDisablesDetectorForThisParticle( boolean detectionDisablesDetectorForThisParticle ) {
            this.detectionDisablesDetectorForThisParticle = detectionDisablesDetectorForThisParticle;
        }

        public boolean tryToGrab( Detector detector, Wavefunction wavefunction, double norm ) {
            if( detector.timeToFire() ) {
                boolean grabbed = detector.tryToGrab( wavefunction, norm );
                if( grabbed && detectionDisablesDetectorForThisParticle ) {
                    detector.setEnabled( false );
                }
                return grabbed;
            }
            else {
                return false;
            }
        }
    }

    private void fireAllEnabledDetectors( FireStrategy fireStrategy ) {
        double norm = 1.0;
        if( getWavefunction().getMagnitude() > 0 ) {
            for( int i = 0; i < detectors.size(); i++ ) {
                Detector detector = (Detector)detectors.get( i );
                if( detector.isEnabled() ) {
                    boolean grabbedIt = fireStrategy.tryToGrab( detector, getWavefunction(), norm );
                    if( grabbedIt ) {
                        break;
                    }
                }
            }
        }
    }

    private Wavefunction getWavefunction() {
        return wavefunction;
    }

    public QWIModel.Listener getListener() {
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

    public class AutoDetectAdapter extends QWIModel.Adapter {
        public void finishedTimeStep( QWIModel model ) {
            notifyTimeStepped();
            updateDetectorProbabilities();
            if( autodetect && model.isDetectionCausesCollapse() ) {
                fireAllEnabledDetectors( new AutoDetect( !repeats ) );
            }
        }
    }

    public boolean isRepeats() {
        return repeats;
    }
}
