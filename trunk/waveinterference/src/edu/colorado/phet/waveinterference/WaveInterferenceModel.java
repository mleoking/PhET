/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.model.WaveModel;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:34:05 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class WaveInterferenceModel implements ModelElement {
    private WaveModel waveModel;
    private SlitPotential slitPotential;
    private Oscillator primaryOscillator;
    private Oscillator secondaryOscillator;

//    private static final double startTime = System.currentTimeMillis() / 1000.0;
    private double time = 0.0;
    private ArrayList listeners = new ArrayList();

    private WaveInterferenceModelUnits modelUnits = new WaveInterferenceModelUnits();

    public WaveInterferenceModel() {
        waveModel = new WaveModel( 60, 60 );
//        waveModel = new WaveModel( 120,120);
        slitPotential = new SlitPotential( waveModel );
        primaryOscillator = new Oscillator( waveModel );
        secondaryOscillator = new Oscillator( waveModel );
        secondaryOscillator.setEnabled( false );
        secondaryOscillator.setLocation( 10, 10 );
        waveModel.setPotential( slitPotential );
        slitPotential.addListener( new SlitPotential.Listener() {
            public void slitsChanged() {
                notifySymmetryChanged();
            }
        } );
        secondaryOscillator.addListener( new Oscillator.Listener() {
            public void enabledStateChanged() {
                notifySymmetryChanged();
            }

            public void locationChanged() {
            }

            public void frequencyChanged() {
            }

            public void amplitudeChanged() {
            }
        } );
    }

    public void setDistanceUnits( String distanceUnits ) {
        modelUnits.setDistanceUnits( distanceUnits );
    }

    public void setTimeUnits( String timeUnits ) {
        modelUnits.setTimeUnits( timeUnits );
    }

    public void setTimeScale( double timeScale ) {
        modelUnits.setTimeScale( timeScale );
    }

    public void setPhysicalSize( double physicalWidth, double physicalHeight ) {
        modelUnits.setPhysicalSize( physicalWidth, physicalHeight );
    }

    public String getDistanceUnits() {
        return modelUnits.getDistanceUnits();
    }

    public double getPhysicalWidth() {
        return modelUnits.getPhysicalWidth();
    }

    public double getPhysicalHeight() {
        return modelUnits.getPhysicalHeight();
    }

    public String getTimeUnits() {
        return modelUnits.getTimeUnits();
    }

    public double getTimeScale() {
        return modelUnits.getTimeScale();
    }

    public WaveInterferenceModelUnits getUnits() {
        return modelUnits;
    }

    public static interface Listener {
        public void symmetryChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    protected void notifySymmetryChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.symmetryChanged();
        }
    }

    public WaveModel getWaveModel() {
        return waveModel;
    }

    public SlitPotential getSlitPotential() {
        return slitPotential;
    }

    public Oscillator getPrimaryOscillator() {
        return primaryOscillator;
    }

    public Oscillator getSecondaryOscillator() {
        return secondaryOscillator;
    }

    public void stepInTime( double dt ) {
        time += dt;
        waveModel.propagate();
        primaryOscillator.setTime( getTime() );
        secondaryOscillator.setTime( getTime() );
    }

    private double getTime() {
        return time;
//        return System.currentTimeMillis() / 1000.0 - startTime;
    }

    public boolean isSymmetric() {
        return !slitPotential.isEnabled() && !getSecondaryOscillator().isEnabled();
    }
}
