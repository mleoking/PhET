/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.ClassicalWavePropagator;
import edu.colorado.phet.waveinterference.model.DampedClassicalWavePropagator;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.view.PhotonEmissionColorMap;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 21, 2006
 * Time: 10:14:51 PM
 * Copyright (c) May 21, 2006 by Sam Reid
 */

public class DarkWave {
    private LightSimulationPanel lightSimulationPanel;

    public DarkWave( LightSimulationPanel lightSimulationPanel ) {
        this.lightSimulationPanel = lightSimulationPanel;

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
    }

    private LightModule getLightModule() {
        return lightSimulationPanel.getLightModule();
    }

    private void maybeFireDarkWave() {
        if( !getWaveInterferenceModel().getPrimaryOscillator().isEnabled() && !getWaveInterferenceModel().getPrimaryOscillator().isEnabled() )
        {
            fireDarkWave();
        }
    }

    private WaveInterferenceModel getWaveInterferenceModel() {
        return getLightModule().getWaveInterferenceModel();
    }

    private void fireDarkWave() {
        if( getWaveModelGraphic().getColorMap() instanceof PhotonEmissionColorMap ) {
            PhotonEmissionColorMap colorMap = (PhotonEmissionColorMap)getWaveModelGraphic().getColorMap();
//            Lattice2D newLattice = getLightModule().getWaveModel().getLattice().createEmptyWavefunction();
            darkWaves.add( new DarkPropagator( getLightModule().getPrimaryOscillator(), colorMap, getLightModule().getWaveModel() ) );
        }
    }

    ArrayList darkWaves = new ArrayList();

    static class DarkPropagator {
        private Oscillator source;
        private PhotonEmissionColorMap colorMap;
        private WaveModel realwaveModel;
        private int numSteps = 0;
        private WaveModel tmpWaveModel;
        private DampedClassicalWavePropagator dampedClassicalWavePropagator;

        public DarkPropagator( Oscillator source, PhotonEmissionColorMap colorMap, WaveModel waveModel ) {
            this.source = source;
            this.colorMap = colorMap;
            this.realwaveModel = waveModel;

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

            for( int i = 0; i < tmpWaveModel.getWidth(); i++ ) {
                for( int k = 0; k < tmpWaveModel.getHeight(); k++ ) {
                    int i2 = i - dampedClassicalWavePropagator.getDampX();
                    int k2 = k - dampedClassicalWavePropagator.getDampY();
                    if( realwaveModel.containsLocation( i2, k2 ) &&
                        isWavefront( i, k ) ) {
                        realwaveModel.setSourceValue( i2, k2, 0 );
                        colorMap.setDark( i2, k2 );
                    }
                }
            }
//            waveModelGraphic.update();
        }

        private boolean isWavefront( int i, int k ) {
            int num = 0;
            int den = 0;
            int a = 4;
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
            return numSteps > Math.sqrt( tmpWaveModel.getHeight() * tmpWaveModel.getHeight() + tmpWaveModel.getWidth() * tmpWaveModel.getWidth() ) * 2;
        }
    }

    private WaveModelGraphic getWaveModelGraphic() {
        return lightSimulationPanel.getWaveModelGraphic();
    }

    public void update() {
//        System.out.println( "darkWaves.size() = " + darkWaves.size() );
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
