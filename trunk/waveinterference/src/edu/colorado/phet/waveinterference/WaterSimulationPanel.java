/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.view.*;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:31:39 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class WaterSimulationPanel extends WaveInterferenceCanvas implements ModelElement {
    private WaterModule waterModule;
    private RotationWaveGraphic rotationWaveGraphic;
    private IntensityReaderSet intensityReaderSet;
    private SlitPotentialGraphic slitPotentialGraphic;
    private MeasurementToolSet measurementToolSet;
    private FaucetGraphic primaryFaucetGraphic;
    private FaucetGraphic secondaryFaucetGraphic;
    private MultiDrip multiDrip;

    public WaterSimulationPanel( WaterModule waterModule ) {
        this.waterModule = waterModule;

        WaveModelGraphic waveModelGraphic = new WaveModelGraphic( getWaveModel(), 10, 10, new IndexColorMap( getLattice() ) );
        WaveSideViewFull waveSideView = new WaveSideViewFull( getLattice(), waveModelGraphic.getLatticeScreenCoordinates() );
        RotationGlyph rotationGlyph = new RotationGlyph();
        rotationWaveGraphic = new RotationWaveGraphic( waveModelGraphic, waveSideView, rotationGlyph );
        rotationWaveGraphic.setOffset( 300, 50 );
        rotationWaveGraphic.addListener( new RotationWaveGraphic.Listener() {
            public void rotationChanged() {
                angleChanged();
            }
        } );
        addScreenChild( rotationWaveGraphic );

        slitPotentialGraphic = new SlitPotentialGraphic( waterModule.getSlitPotential(), getLatticeScreenCoordinates() );
        addScreenChild( slitPotentialGraphic );

        primaryFaucetGraphic = new FaucetGraphic( getWaveModel(), waterModule.getPrimaryOscillator(), getLatticeScreenCoordinates() );
        addScreenChild( primaryFaucetGraphic );

        secondaryFaucetGraphic = new FaucetGraphic( getWaveModel(), waterModule.getSecondaryOscillator(), getLatticeScreenCoordinates() );
        secondaryFaucetGraphic.setEnabled( false );
        addScreenChild( secondaryFaucetGraphic );

        intensityReaderSet = new IntensityReaderSet();
        addScreenChild( intensityReaderSet );

        measurementToolSet = new MeasurementToolSet( this, waterModule.getClock() );
        addScreenChild( measurementToolSet );

        multiDrip = new MultiDrip( primaryFaucetGraphic, secondaryFaucetGraphic );

        FaucetControlPanel faucetControlPanel = new FaucetControlPanel( waterModule.getPrimaryOscillator(), getPrimaryFaucetGraphic() );
        addScreenChild( new PSwing( this, faucetControlPanel ) );
//        PSwing node = new PSwing( this, new OscillatorControlPanel( waterModule.getPrimaryOscillator() ) );
//        node.computeBounds();
//        addScreenChild( node );
    }

    private void angleChanged() {
        if( rotationWaveGraphic.isTopView() ) {
            slitPotentialGraphic.setVisible( true );
        }
        else {
            slitPotentialGraphic.setVisible( false );
        }
    }

    public MultiDrip getMultiDrip() {
        return multiDrip;
    }

    private Lattice2D getLattice() {
        return getWaveModel().getLattice();
    }

    private WaveModel getWaveModel() {
        return waterModule.getWaveModel();
    }

    public RotationWaveGraphic getRotationWaveGraphic() {
        return rotationWaveGraphic;
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return rotationWaveGraphic.getLatticeScreenCoordinates();
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
        rotationWaveGraphic.update();
        primaryFaucetGraphic.step();
        secondaryFaucetGraphic.step();
        intensityReaderSet.update();
    }
}
