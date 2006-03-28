/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.view.*;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:31:39 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class SoundSimulationPanel extends WaveInterferenceCanvas implements ModelElement {
    private SoundModule soundModule;
//    private RotationWaveGraphic rotationWaveGraphic;
    private IntensityReaderSet intensityReaderSet;
    private SlitPotentialGraphic slitPotentialGraphic;
    private MeasurementToolSet measurementToolSet;
    private FaucetGraphic primaryFaucetGraphic;
    private FaucetGraphic secondaryFaucetGraphic;
    private MultiDrip multiDrip;
    private WaveModelGraphic waveModelGraphic;

    public SoundSimulationPanel( SoundModule soundModule ) {
        this.soundModule = soundModule;

        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 8, 8, new IndexColorMap( getLattice() ) );
//        WaveSideViewFull waveSideView = new WaveSideViewFull( getLattice(), waveModelGraphic.getLatticeScreenCoordinates() );
//        RotationGlyph rotationGlyph = new RotationGlyph();
//        rotationWaveGraphic = new RotationWaveGraphic( waveModelGraphic, waveSideView, rotationGlyph );
//        rotationWaveGraphic.setOffset( 300, 50 );
//        rotationWaveGraphic.addListener( new RotationWaveGraphic.Listener() {
//            public void rotationChanged() {
//                angleChanged();
//            }
//        } );
//        addScreenChild( rotationWaveGraphic );

        primaryFaucetGraphic = new FaucetGraphic( getWaveModel(), soundModule.getPrimaryOscillator(), getLatticeScreenCoordinates() );
        addScreenChild( primaryFaucetGraphic );

        secondaryFaucetGraphic = new FaucetGraphic( getWaveModel(), soundModule.getSecondaryOscillator(), getLatticeScreenCoordinates() );
        secondaryFaucetGraphic.setEnabled( false );
        addScreenChild( secondaryFaucetGraphic );

        slitPotentialGraphic = new SlitPotentialGraphic( soundModule.getSlitPotential(), getLatticeScreenCoordinates() );
        addScreenChild( slitPotentialGraphic );

        intensityReaderSet = new IntensityReaderSet();
        addScreenChild( intensityReaderSet );

        measurementToolSet = new MeasurementToolSet( this, soundModule.getClock() );
        addScreenChild( measurementToolSet );

        multiDrip = new MultiDrip( getWaveModel(), primaryFaucetGraphic, secondaryFaucetGraphic );

        FaucetControlPanelPNode faucetControlPanelPNode = new FaucetControlPanelPNode( this, new FaucetControlPanel( soundModule.getPrimaryOscillator(), getPrimaryFaucetGraphic() ), getPrimaryFaucetGraphic(), waveModelGraphic );
        addScreenChild( faucetControlPanelPNode );
    }

//    private void angleChanged() {
//        if( rotationWaveGraphic.isTopView() ) {
//            slitPotentialGraphic.setVisible( true );
//        }
//        else {
//            slitPotentialGraphic.setVisible( false );
//        }
//    }

    public MultiDrip getMultiDrip() {
        return multiDrip;
    }

    private Lattice2D getLattice() {
        return getWaveModel().getLattice();
    }

    private WaveModel getWaveModel() {
        return soundModule.getWaveModel();
    }

//    public RotationWaveGraphic getRotationWaveGraphic() {
//        return rotationWaveGraphic;
//    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return waveModelGraphic.getLatticeScreenCoordinates();
    }

    public IntensityReaderSet getIntensityReaderSet() {
        return intensityReaderSet;
    }

    public MeasurementToolSet getMeasurementToolSet() {
        return measurementToolSet;
    }

    public FaucetGraphic getPrimaryFaucetGraphic() {
        return primaryFaucetGraphic;
    }

    public void stepInTime( double dt ) {
        waveModelGraphic.update();
        primaryFaucetGraphic.step();
        secondaryFaucetGraphic.step();
        intensityReaderSet.update();
    }
}
