/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.*;
import edu.colorado.phet.waveinterference.view.MultiOscillator;
import edu.colorado.phet.waveinterference.view.PhotonEmissionColorMap;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 21, 2006
 * Time: 10:14:51 PM
 * Copyright (c) May 21, 2006 by Sam Reid
 * <p/>
 * Dark wave should propagate through barrier because otherwise for a solid barrier, the light will never disappear.
 */

public class DarkWave {
    private LightSimulationPanel lightSimulationPanel;
    private MultiOscillator multiOscillator;
    private boolean primaryLast;
    private boolean secondaryLast;

    public DarkWave( LightSimulationPanel lightSimulationPanel ) {
        this.lightSimulationPanel = lightSimulationPanel;
        multiOscillator = lightSimulationPanel.getMultiOscillator();
        getLightModule().getWaveInterferenceModel().getPrimaryOscillator().addListener( new Oscillator.Listener() {
            public void enabledStateChanged() {
                maybeFireDarkWave();
            }

            public void locationChanged() {
            }

            public void frequencyChanged() {
            }

            public void amplitudeChanged() {
            }
        } );
        getLightModule().getWaveInterferenceModel().getSecondaryOscillator().addListener( new Oscillator.Listener() {
            public void enabledStateChanged() {
                maybeFireDarkWave();
            }

            public void locationChanged() {
            }

            public void frequencyChanged() {
            }

            public void amplitudeChanged() {
            }
        } );
        setLastValues();
    }

    private void setLastValues() {
        primaryLast = lightSimulationPanel.getLightModule().getPrimaryOscillator().isEnabled();
        secondaryLast = lightSimulationPanel.getLightModule().getSecondaryOscillator().isEnabled();
    }

    private LightModule getLightModule() {
        return lightSimulationPanel.getLightModule();
    }

    private void maybeFireDarkWave() {
        if( multiOscillator.isOneSource() ) {
            if( !getWaveInterferenceModel().getPrimaryOscillator().isEnabled() ) {
                fireDarkWave( getWaveInterferenceModel().getPrimaryOscillator() );
            }
        }
        else {//2 source
            if( getWaveInterferenceModel().getPrimaryOscillator().isEnabled() == false && primaryLast == true && !getWaveInterferenceModel().getSecondaryOscillator().isEnabled() )
            {
                fireDarkWave( getWaveInterferenceModel().getPrimaryOscillator() );
            }
            else
            if( getWaveInterferenceModel().getSecondaryOscillator().isEnabled() == false && secondaryLast == true && !getWaveInterferenceModel().getPrimaryOscillator().isEnabled() )
            {
                fireDarkWave( getWaveInterferenceModel().getSecondaryOscillator() );
            }
        }
        setLastValues();
    }

    private WaveInterferenceModel getWaveInterferenceModel() {
        return getLightModule().getWaveInterferenceModel();
    }

    private void fireDarkWave( Oscillator oscillator ) {
        if( getWaveModelGraphic().getColorMap() instanceof PhotonEmissionColorMap ) {
            PhotonEmissionColorMap colorMap = (PhotonEmissionColorMap)getWaveModelGraphic().getColorMap();
            darkWaves.add( new DarkPropagator( oscillator, colorMap, getLightModule().getWaveModel() ) );
        }
    }

    ArrayList darkWaves = new ArrayList();

    public void reset() {
        while( darkWaves.size() > 0 ) {
            DarkPropagator darkPropagator = (DarkPropagator)darkWaves.get( 0 );
            darkWaves.remove( darkPropagator );
        }
    }

    static class DarkPropagator {
        private Oscillator source;
        private PhotonEmissionColorMap colorMap;
        private int numSteps = 0;
        private WaveModel tmpWaveModel;
        private DampedClassicalWavePropagator dampedClassicalWavePropagator;

        public DarkPropagator( Oscillator source, PhotonEmissionColorMap colorMap, WaveModel waveModel ) {
            this.source = source;
            this.colorMap = colorMap;

            ClassicalWavePropagator classicalWavePropagator = waveModel.getClassicalWavePropagator();
            if( classicalWavePropagator instanceof DampedClassicalWavePropagator ) {
                dampedClassicalWavePropagator = (DampedClassicalWavePropagator)classicalWavePropagator;

            }
            this.tmpWaveModel = new WaveModel( dampedClassicalWavePropagator.getLargeLattice().getWidth(), dampedClassicalWavePropagator.getLargeLattice().getHeight() );
        }

        public void update() {
            //todo this is an awkward dependency on details of damped wave propagator.
            tmpWaveModel.setSourceValue( source.getCenterX() + dampedClassicalWavePropagator.getDampX(), source.getCenterY() + dampedClassicalWavePropagator.getDampY(), (float)( 10 * Math.sin( numSteps / 10.0 ) ) );
            tmpWaveModel.propagate();
            numSteps++;

            Lattice2D largeLattice = tmpWaveModel.getLattice();
            for( int i = 0; i < largeLattice.getWidth(); i++ ) {
                for( int k = 0; k < largeLattice.getHeight(); k++ ) {
                    int i2 = i - dampedClassicalWavePropagator.getDampX();
                    int k2 = k - dampedClassicalWavePropagator.getDampY();
                    if(
                            isWavefront( i, k ) ) {
                        dampedClassicalWavePropagator.clearOffscreenLatticeValue( i, k );
                        if( i2 >= 0 && i2 < colorMap.getWidth() && k2 >= 0 && k2 < colorMap.getHeight() ) {
                            colorMap.setDark( i2, k2 );
                        }
                    }
                }
            }
        }

        private boolean isWavefront( int i, int k ) {
            int num = 0;
            int den = 0;
            int a = 1;
            for( int m = -a; m <= a; m++ ) {
                for( int n = -a; n <= a; n++ ) {
                    if( tmpWaveModel.containsLocation( i + m, k + n ) ) {
                        if( Math.abs( tmpWaveModel.getValue( i + m, k + n ) ) > 1E-6 ) {
                            num++;
                        }
                        den++;
                    }
                }
            }
            double fraction = ( (double)num ) / den;
            return fraction > 0 && fraction < 1;
        }

        public boolean isFinished() {
            return numSteps > Math.sqrt( tmpWaveModel.getHeight() * tmpWaveModel.getHeight() + tmpWaveModel.getWidth() * tmpWaveModel.getWidth() ) * 1.2;
        }
    }

    private WaveModelGraphic getWaveModelGraphic() {
        return lightSimulationPanel.getWaveModelGraphic();
    }

    public void update() {
        for( int i = 0; i < darkWaves.size(); i++ ) {
            DarkPropagator darkPropagator = (DarkPropagator)darkWaves.get( i );
            darkPropagator.update();
        }
        for( int i = 0; i < darkWaves.size(); i++ ) {
            DarkPropagator darkPropagator = (DarkPropagator)darkWaves.get( i );
            if( darkPropagator.isFinished() ) {
                darkWaves.remove( darkPropagator );
                i--;
            }
        }
    }
}
