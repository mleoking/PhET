/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.phetcommon.ShinyPanel;
import edu.colorado.phet.waveinterference.phetcommon.VerticalConnector;
import edu.colorado.phet.waveinterference.tests.ExpandableWaveChart;
import edu.colorado.phet.waveinterference.util.WIStrings;
import edu.colorado.phet.waveinterference.view.*;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private PressureWaveGraphic pressureWaveGraphic;
    private RotationWaveGraphic rotationWaveGraphic;
    private RotationGlyph rotationGlyph;
    private CrossSectionGraphic crossSectionGraphic;
    private WaveInterferenceScreenUnits screenUnits;

    public SoundSimulationPanel( SoundModule soundModule ) {
        this.soundModule = soundModule;

        MutableColor latticeColor = new MutableColor( Color.white );
        IndexColorMap colorMap = new IndexColorMap( getLattice(), latticeColor );
        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 8, 8, colorMap );

        pressureWaveGraphic = new PressureWaveGraphic( getLattice(), waveModelGraphic.getLatticeScreenCoordinates(), soundModule.getSlitPotential() );
        pressureWaveGraphic.setMaxVelocity( 3 );
        pressureWaveGraphic.setOffsetDX( -super.getWaveModelGraphicOffset().getX(), -super.getWaveModelGraphicOffset().getY() );//todo this accounts for the difference between the offset in the rotationWaveGraphic and the waveModelGraphic
        soundWaveGraphic = new SoundWaveGraphic( waveModelGraphic, pressureWaveGraphic );

        soundWaveGraphic.addListener( new SoundWaveGraphic.Listener() {
            public void viewChanged() {
                updateRotationGlyphColor();
            }

            public void viewTypeChanged() {
            }
        } );

//        addScreenChild( soundWaveGraphic );//todo unnecessary when rotation graphic is in place.

        rotationGlyph = new RotationGlyph();
        rotationGlyph.synchronizeDepthSize( waveModelGraphic );
        updateRotationGlyphColor();
        rotationWaveGraphic = new RotationWaveGraphic3D( soundWaveGraphic.getWaveModelGraphic(), soundWaveGraphic, rotationGlyph );
        rotationWaveGraphic.setOffset( super.getWaveModelGraphicOffset() );
        rotationWaveGraphic.addListener( new RotationWaveGraphic.Listener() {
            public void rotationChanged() {
                angleChanged();
            }
        } );
//
        addScreenChild( rotationWaveGraphic );

        primarySpeaker = new OscillatingSpeakerGraphic( this, soundModule.getPrimaryOscillator(), getLatticeScreenCoordinates() );
        secondarySpeaker = new OscillatingSpeakerGraphic( this, soundModule.getSecondaryOscillator(), getLatticeScreenCoordinates() );

        addScreenChild( primarySpeaker );
        addScreenChild( secondarySpeaker );
        multiOscillator = new MultiOscillator( getWaveModel(), primarySpeaker, soundModule.getPrimaryOscillator(), secondarySpeaker, soundModule.getSecondaryOscillator() );

        slitPotentialGraphic = new SlitPotentialGraphic( soundModule.getSlitPotential(), waveModelGraphic.getLatticeScreenCoordinates() );
        addScreenChild( slitPotentialGraphic );

        intensityReaderSet = new IntensityReaderSet();


        measurementToolSet = new MeasurementToolSet( this, soundModule.getClock(), getLatticeScreenCoordinates(), soundModule.getWaveInterferenceModel() );

        speakerControlPanelPNode = new SpeakerControlPanelPNode( this, soundModule.getPrimaryOscillator(), waveModelGraphic );
        addScreenChild( speakerControlPanelPNode );

        verticalConnector = new SpeakerConnectorLeftSide( speakerControlPanelPNode, primarySpeaker );
        addScreenChild( 0, verticalConnector );

        waveChartGraphic = new WaveChartGraphic( WIStrings.getString( "pressure" ), getLatticeScreenCoordinates(), getWaveModel(), new MutableColor( Color.black ), getWaveInterferenceModel().getDistanceUnits(), 0, getWaveInterferenceModel().getPhysicalWidth() );
        expandableWaveChart = new ExpandableWaveChart( this, waveChartGraphic, getLatticeScreenCoordinates() );
        addScreenChild( expandableWaveChart );

        crossSectionGraphic = new CrossSectionGraphic( getWaveModel(), getLatticeScreenCoordinates() );
        soundWaveGraphic.addListener( new SoundWaveGraphic.Listener() {
            public void viewChanged() {
                crossSectionGraphic.setColor( soundWaveGraphic.isParticleVisible() ? Color.white : Color.black );
            }

            public void viewTypeChanged() {
            }
        } );
        addScreenChild( crossSectionGraphic );

        addScreenChild( measurementToolSet );

        expandableWaveChart.addListener( new ExpandableWaveChart.Listener() {
            public void expansionStateChanged() {
                crossSectionGraphic.setVisible( expandableWaveChart.isExpanded() );
            }
        } );
        crossSectionGraphic.setVisible( expandableWaveChart.isExpanded() );
        addScreenChild( intensityReaderSet );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateWaveSize();
            }

            public void componentShown( ComponentEvent e ) {
                updateWaveSize();
            }
        } );
        screenUnits = new WaveInterferenceScreenUnits( getWaveInterferenceModel().getUnits(), getLatticeScreenCoordinates() );
        updateWaveSize();

        ThisSideUpWrapper thisSideUpWrapper = new ThisSideUpWrapper( rotationGlyph, getLatticeScreenCoordinates(), getLattice() );
        addScreenChild( thisSideUpWrapper );
//        addScaleTest();
        SoundWaveGraphicRadioControl soundWaveGraphicRadioControl = new SoundWaveGraphicRadioControl( soundWaveGraphic );
        final PSwing pSwing = new PSwing( this, new ShinyPanel( soundWaveGraphicRadioControl ) );
        addScreenChild( pSwing );

        getLatticeScreenCoordinates().addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
//                updateRadioButtons();
                pSwing.setOffset( getLatticeScreenCoordinates().getScreenRect().getMaxX(),
                                  getLatticeScreenCoordinates().getScreenRect().getCenterY() - pSwing.getFullBounds().getHeight() / 2 );
            }
        } );
    }

    private void updateRotationGlyphColor() {
//        rotationGlyph.setSideColor( soundWaveGraphic.isParticleVisible() ? Color.darkGray : Color.gray );
        rotationGlyph.setColors( soundWaveGraphic.isParticleVisible() ? new Color( 40, 40, 40 ) : Color.darkGray,
                                 soundWaveGraphic.isParticleVisible() ? Color.black : Color.gray );
    }

    private void addScaleTest() {
        Timer timer = new Timer( 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "getLayer().getScale() = " + getLayer().getScale() );
                System.out.println( "getLayer().getGlobalScale() = " + getLayer().getGlobalScale() );
                if( pressureWaveGraphic.numParticles() > 0 ) {
                    PressureWaveGraphic.Particle p = pressureWaveGraphic.getParticle( 0 );
                    System.out.println( "p.getScale() = " + p.getScale() );
                    System.out.println( "p.getGlobalScale() = " + p.getGlobalScale() );
                }
            }
        } );
        timer.start();
    }

    private void angleChanged() {
        if( rotationWaveGraphic.isTopView() ) {
            slitPotentialGraphic.setVisible( true );
            setAsymmetricFeaturesEnabled( true );
        }
        else {
            slitPotentialGraphic.setVisible( false );
            setAsymmetricFeaturesEnabled( false );
        }
    }

    private void setAsymmetricFeaturesEnabled( boolean asymmetricFeaturesEnabled ) {
        soundModule.setAsymmetricFeaturesEnabled( asymmetricFeaturesEnabled );
    }

    private WaveInterferenceModel getWaveInterferenceModel() {
        return soundModule.getWaveInterferenceModel();
    }

    private void updateWaveSize() {
        if( getHeight() > 0 ) {
            double insetTop = super.getWaveModelGraphicOffset().getY();
            double insetBottom = waveChartGraphic.getChartHeight();
            double availableHeight = getLayoutHeight() - insetTop - insetBottom;
            int pixelsPerCell = (int)( availableHeight / getWaveModel().getHeight() );
            waveModelGraphic.setCellDimensions( pixelsPerCell, pixelsPerCell );
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

    public RotationWaveGraphic getRotationWaveGraphic() {
        return rotationWaveGraphic;
    }

    public WaveInterferenceScreenUnits getScreenUnits() {
        return screenUnits;
    }

    public void reset() {
        rotationWaveGraphic.reset();
        primarySpeaker.reset();
        secondarySpeaker.reset();
        intensityReaderSet.reset();
        expandableWaveChart.reset();
        multiOscillator.reset();
        measurementToolSet.reset();
        soundWaveGraphic.reset();
    }
}
