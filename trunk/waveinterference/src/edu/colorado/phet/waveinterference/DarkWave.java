/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

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
//                if (get)
//                maybeFireDarkWave();
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
        private WaveModel waveModel;
        private WaveModelGraphic waveModelGraphic;
//        private boolean[][] alreadyCleared;

        public DarkPropagator( Oscillator source, PhotonEmissionColorMap colorMap, WaveModel waveModel ) {
            this.waveModel = new WaveModel( waveModel.getWidth(), waveModel.getHeight() );
            this.source = source;
            this.colorMap = colorMap;
            this.realwaveModel = waveModel;
            waveModelGraphic = new WaveModelGraphic( this.waveModel );
//            alreadyCleared = new boolean[waveModel.getWidth()][waveModel.getHeight()];
        }

        public void update() {
            waveModel.setSourceValue( source.getCenterX(), source.getCenterY(), (float)( 10 * Math.sin( numSteps / 10.0 ) ) );
            waveModel.propagate();
            numSteps++;
            for( int i = 0; i < waveModel.getWidth(); i++ ) {
                for( int k = 0; k < waveModel.getHeight(); k++ ) {
                    if( isWavefront( i, k ) ) {
                        realwaveModel.setSourceValue( i, k, 0 );
                        colorMap.setDark( i, k );
                    }
                }
            }
            waveModelGraphic.update();
        }

        private boolean isWavefront( int i, int k ) {
            int num = 0;
            int den = 0;
            for( int m = -2; m <= 2; m++ ) {
                for( int n = -2; n <= 2; n++ ) {
                    if( waveModel.containsLocation( i + m, k + n ) ) {
                        if( Math.abs( waveModel.getValue( i + m, k + n ) ) > 1E-6 ) {
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
            return numSteps > Math.sqrt( waveModel.getHeight() * waveModel.getHeight() + waveModel.getWidth() * waveModel.getWidth() ) * 2.1;
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
