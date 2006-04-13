/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.phetcommon.VerticalConnector;
import edu.colorado.phet.waveinterference.phetcommon.VerticalConnectorLeftSide;
import edu.colorado.phet.waveinterference.view.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
    private SpeakerControlPanelPNode speakerControlPanelPNode;
    private WaveModelGraphic waveModelGraphic;
    private VerticalConnector verticalConnector;
    private ImageOscillatorPNode primarySpeaker;
    private ImageOscillatorPNode secondarySpeaker;

    public SoundSimulationPanel( SoundModule soundModule ) {
        this.soundModule = soundModule;

        IndexColorMap colorMap = new IndexColorMap( getLattice(), Color.white );
        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 8, 8, colorMap );
        waveModelGraphic.setOffset( 100, 10 );

        PressureWaveGraphic pressureWaveGraphic = new PressureWaveGraphic( getLattice(), waveModelGraphic.getLatticeScreenCoordinates() );
        pressureWaveGraphic.setMaxVelocity( 3 );
        soundWaveGraphic = new SoundWaveGraphic( waveModelGraphic, pressureWaveGraphic );
        addScreenChild( soundWaveGraphic );

        primarySpeaker = new OscillatingSpeakerGraphic( this, soundModule.getPrimaryOscillator(), getLatticeScreenCoordinates() );
        secondarySpeaker = new OscillatingSpeakerGraphic( this, soundModule.getSecondaryOscillator(), getLatticeScreenCoordinates() );

        addScreenChild( primarySpeaker );
        addScreenChild( secondarySpeaker );
        multiOscillator = new MultiOscillator( getWaveModel(), primarySpeaker, soundModule.getPrimaryOscillator(), secondarySpeaker, soundModule.getSecondaryOscillator() );

        slitPotentialGraphic = new SlitPotentialGraphic( soundModule.getSlitPotential(), waveModelGraphic.getLatticeScreenCoordinates() );
        addScreenChild( slitPotentialGraphic );

        intensityReaderSet = new IntensityReaderSet();
        addScreenChild( intensityReaderSet );

        measurementToolSet = new MeasurementToolSet( this, soundModule.getClock() );
        addScreenChild( measurementToolSet );

        speakerControlPanelPNode = new SpeakerControlPanelPNode( this, soundModule.getPrimaryOscillator(), waveModelGraphic );
        addScreenChild( speakerControlPanelPNode );

        verticalConnector = new VerticalConnectorLeftSide( speakerControlPanelPNode, primarySpeaker );
        addScreenChild( 0, verticalConnector );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateWaveSize();
            }

            public void componentShown( ComponentEvent e ) {
                updateWaveSize();
            }
        } );
        updateWaveSize();
    }

    private void updateWaveSize() {
        int insetTop = 2;
        int insetBottom = 2;
        double availableHeight = getHeight() - speakerControlPanelPNode.getFullBounds().getHeight() - insetTop - insetBottom;
        double latticeModelHeight = getWaveModel().getHeight();
        int pixelsPerCell = (int)( availableHeight / latticeModelHeight );
        waveModelGraphic.setCellDimensions( pixelsPerCell - 1, pixelsPerCell - 1 );
//        double usedHeight = waveModelGraphic.getFullBounds().getHeight() + speakerControlPanelPNode.getFullBounds().getHeight() + insetTop + insetBottom;
//        System.out.println( "availableHeight = " + availableHeight + ", used height=" + usedHeight );
    }

    public MultiOscillator getMultiOscillator() {
        return multiOscillator;
    }

    private Lattice2D getLattice() {
        return getWaveModel().getLattice();
    }

    private WaveModel getWaveModel() {
        return soundModule.getWaveModel();
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return soundWaveGraphic.getLatticeScreenCoordinates();
    }

    public IntensityReaderSet getIntensityReaderSet() {
        return intensityReaderSet;
    }

    public MeasurementToolSet getMeasurementToolSet() {
        return measurementToolSet;
    }

    public void stepInTime( double dt ) {
        soundWaveGraphic.update();
        intensityReaderSet.update();
        primarySpeaker.update();
        secondarySpeaker.update();
    }

    public SoundWaveGraphic getSoundWaveGraphic() {
        return soundWaveGraphic;
    }
}
