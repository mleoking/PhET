/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import java.util.ArrayList;
import java.util.Collections;

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
    private boolean oneShot = true;
    private ArrayList listeners = new ArrayList();

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

    public void setOneShot( boolean oneShot ) {
        this.oneShot = oneShot;
    }

    public boolean isOneShot() {
        return oneShot;
    }

    private static interface FireStrategy {
        boolean tryToGrab( Detector detector, Wavefunction wavefunction, double norm );
    }

    private class FireEnabled implements FireStrategy {
        public boolean tryToGrab( Detector detector, Wavefunction wavefunction, double norm ) {
            return detector.tryToGrab( wavefunction, norm );
        }
    }

    private class AutoDetect implements FireStrategy {
        public boolean tryToGrab( Detector detector, Wavefunction wavefunction, double norm ) {
//            System.out.println( "oneShot = " + oneShot );
            boolean grabbed = false;
            if( detector.timeToFire() ) {
                grabbed = detector.tryToGrab( wavefunction, norm );
            }
            return grabbed;
        }
    }

    private void fireAllEnabledDetectors( FireStrategy fireStrategy ) {
        double norm = 1.0;
        if( getWavefunction().getMagnitude() > 0 ) {
            ArrayList detectors = new ArrayList();
            detectors.addAll( this.detectors );
            Collections.shuffle( detectors );
            for( int i = 0; i < detectors.size(); i++ ) {
                Detector detector = (Detector)detectors.get( i );
                if( detector.isEnabled() ) {
                    boolean grabbedIt = fireStrategy.tryToGrab( detector, getWavefunction(), norm );
                    if( grabbedIt ) {
                        if( oneShot ) {
                            detector.setEnabled( false );
                        }
                        break;
                    }
                }
            }
        }
        if( detectors.size() > 0 ) {
            notifyDetectionFinished();
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
                fireAllEnabledDetectors( new AutoDetect() );
            }
        }
    }

    public boolean isRepeats() {
        return repeats;
    }

    public static interface Listener {
        void detectionAttempted();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyDetectionFinished() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.detectionAttempted();
        }
    }

}
