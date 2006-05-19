/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.phetcommon.VerticalConnector;
import edu.colorado.phet.waveinterference.tests.ExpandableWaveChart;
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
    private WaveChartGraphic waveChartGraphic;
    private ExpandableWaveChart expandableWaveChart;

    public SoundSimulationPanel( SoundModule soundModule ) {
        this.soundModule = soundModule;

        MutableColor latticeColor = new MutableColor( Color.white );
        IndexColorMap colorMap = new IndexColorMap( getLattice(), latticeColor );
        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 8, 8, colorMap );
        waveModelGraphic.setOffset( super.getWaveModelGraphicOffset() );

        PressureWaveGraphic pressureWaveGraphic = new PressureWaveGraphic( getLattice(), waveModelGraphic.getLatticeScreenCoordinates(), soundModule.getSlitPotential() );
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

        measurementToolSet = new MeasurementToolSet( this, soundModule.getClock(), getLatticeScreenCoordinates(), soundModule.getWaveInterferenceModel() );

        speakerControlPanelPNode = new SpeakerControlPanelPNode( this, soundModule.getPrimaryOscillator(), waveModelGraphic );
        addScreenChild( speakerControlPanelPNode );

        verticalConnector = new SpeakerConnectorLeftSide( speakerControlPanelPNode, primarySpeaker );
        addScreenChild( 0, verticalConnector );

        waveChartGraphic = new WaveChartGraphic( "Pressure", getLatticeScreenCoordinates(), getWaveModel(), new MutableColor( Color.black ), getWaveInterferenceModel().getDistanceUnits(), 0, getWaveInterferenceModel().getPhysicalWidth() );
        expandableWaveChart = new ExpandableWaveChart( this, waveChartGraphic, getLatticeScreenCoordinates() );
        addScreenChild( expandableWaveChart );

        final CrossSectionGraphic crossSectionGraphic = new CrossSectionGraphic( getWaveModel(), getLatticeScreenCoordinates() );
        addScreenChild( crossSectionGraphic );
        addScreenChild( measurementToolSet );

        expandableWaveChart.addListener( new ExpandableWaveChart.Listener() {
            public void expansionStateChanged() {
                crossSectionGraphic.setVisible( expandableWaveChart.isExpanded() );
            }
        } );
        crossSectionGraphic.setVisible( expandableWaveChart.isExpanded() );

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

    private WaveInterferenceModel getWaveInterferenceModel() {
        return soundModule.getWaveInterferenceModel();
    }

    private void updateWaveSize() {
        if( getHeight() > 0 ) {
//            System.out.println( "<WaterSimulationPanel.updateWaveSize>" );
            double insetTop = super.getWaveModelGraphicOffset().getY();
//            System.out.println( "insetTop = " + insetTop );
            double insetBottom = waveChartGraphic.getChartHeight();
//            if (waveChartGraphic.getFullBounds().getHeight()>300){
//                System.out.println( "WaterSimulationPanel.updateWaveSize" );
//            }
//            System.out.println( "insetBottom = " + insetBottom );
            double availableHeight = getHeight() - insetTop - insetBottom;
            int pixelsPerCell = (int)( availableHeight / getWaveModel().getHeight() );
//            System.out.println( "pixelsPerCell = " + pixelsPerCell );
            waveModelGraphic.setCellDimensions( pixelsPerCell, pixelsPerCell );
//            double usedHeight = rotationWaveGraphic.getFullBounds().getHeight() + faucetControlPanelPNode.getFullBounds().getHeight() + insetTop + insetBottom;
//            System.out.println( "availableHeight = " + availableHeight + ", used height=" + usedHeight );
//            System.out.println( "</WaterSimulationPanel.updateWaveSize>" );
        }
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
        expandableWaveChart.updateChart();
    }

    public SoundWaveGraphic getSoundWaveGraphic() {
        return soundWaveGraphic;
    }
}
