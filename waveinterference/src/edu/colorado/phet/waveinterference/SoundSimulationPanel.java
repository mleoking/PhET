/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.view.*;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:31:39 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class SoundSimulationPanel extends WaveInterferenceCanvas implements ModelElement {
    private SoundModule soundModule;
    private IntensityReaderSet intensityReaderSet;
    private SlitPotentialGraphic slitPotentialGraphic;
    private MeasurementToolSet measurementToolSet;
    private SoundWaveGraphic soundWaveGraphic;
    private MultiOscillator multiOscillator;

    public SoundSimulationPanel( SoundModule soundModule ) {
        this.soundModule = soundModule;

        IndexColorMap colorMap = new IndexColorMap( getLattice(), Color.white );
        WaveModelGraphic waveModelGraphic = new WaveModelGraphic( getWaveModel(), 8, 8, colorMap );
        waveModelGraphic.setOffset( 100, 10 );
//        SoundWaveGraphic soundWaveGraphic = new SoundWaveGraphic( waveModelGraphic );
//        addScreenChild( soundWaveGraphic );

        PressureWaveGraphic pressureWaveGraphic = new PressureWaveGraphic( getLattice(), waveModelGraphic.getLatticeScreenCoordinates() );
        pressureWaveGraphic.setMaxVelocity( 3 );
        soundWaveGraphic = new SoundWaveGraphic( waveModelGraphic, pressureWaveGraphic );
        addScreenChild( soundWaveGraphic );
//        addScreenChild( pressureWaveGraphic );
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

//        primaryFaucetGraphic = new FaucetGraphic( getWaveModel(), soundModule.getPrimaryOscillator(), getLatticeScreenCoordinates() );
//        addScreenChild( primaryFaucetGraphic );
//
//        secondaryFaucetGraphic = new FaucetGraphic( getWaveModel(), soundModule.getSecondaryOscillator(), getLatticeScreenCoordinates() );
//        secondaryFaucetGraphic.setEnabled( false );
        ImageOscillatorPNode primarySpeaker = new SpeakerGraphic( soundModule.getPrimaryOscillator(), getLatticeScreenCoordinates() );
        ImageOscillatorPNode secondarySpeaker = new SpeakerGraphic( soundModule.getSecondaryOscillator(), getLatticeScreenCoordinates() );

        addScreenChild( primarySpeaker );
        addScreenChild( secondarySpeaker );
        multiOscillator = new MultiOscillator( getWaveModel(), primarySpeaker, soundModule.getPrimaryOscillator(), secondarySpeaker, soundModule.getSecondaryOscillator() );
//        addScreenChild( mu);
//        addScreenChild( secondaryFaucetGraphic );

        slitPotentialGraphic = new SlitPotentialGraphic( soundModule.getSlitPotential(), waveModelGraphic.getLatticeScreenCoordinates() );
        addScreenChild( slitPotentialGraphic );

        intensityReaderSet = new IntensityReaderSet();
        addScreenChild( intensityReaderSet );

        measurementToolSet = new MeasurementToolSet( this, soundModule.getClock() );
        addScreenChild( measurementToolSet );

//        multiFaucetDrip = new MultiFaucetDrip( getWaveModel(), primaryFaucetGraphic, secondaryFaucetGraphic );

//        FaucetControlPanelPNode faucetControlPanelPNode = new FaucetControlPanelPNode( this, new FaucetControlPanel( soundModule.getPrimaryOscillator(), getPrimaryFaucetGraphic() ), getPrimaryFaucetGraphic(), waveModelGraphic );
//        addScreenChild( faucetControlPanelPNode );
        SpeakerControlPanelPNode speakerControlPanelPNode = new SpeakerControlPanelPNode( this, soundModule.getPrimaryOscillator(), waveModelGraphic );
        addScreenChild( speakerControlPanelPNode );
    }

    public MultiOscillator getMultiOscillator() {
        return multiOscillator;
    }
//    private void angleChanged() {
//        if( rotationWaveGraphic.isTopView() ) {
//            slitPotentialGraphic.setVisible( true );
//        }
//        else {
//            slitPotentialGraphic.setVisible( false );
//        }
//    }

//    public MultiFaucetDrip getMultiDrip() {
//        return multiFaucetDrip;
//    }

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
        return soundWaveGraphic.getLatticeScreenCoordinates();
    }

    public IntensityReaderSet getIntensityReaderSet() {
        return intensityReaderSet;
    }

    public MeasurementToolSet getMeasurementToolSet() {
        return measurementToolSet;
    }

//    public FaucetGraphic getPrimaryFaucetGraphic() {
//        return primaryFaucetGraphic;
//    }

    public void stepInTime( double dt ) {
//        w.update();
        soundWaveGraphic.update();
//        pressureWaveGraphic.update();
//        primaryFaucetGraphic.step();
//        secondaryFaucetGraphic.step();
        intensityReaderSet.update();
    }

//    public SoundWaveGraphic getSoundWaveGraphic() {
//        return soundWaveGraphic;
//    }

    public SoundWaveGraphic getSoundWaveGraphic() {
        return soundWaveGraphic;
    }
}
