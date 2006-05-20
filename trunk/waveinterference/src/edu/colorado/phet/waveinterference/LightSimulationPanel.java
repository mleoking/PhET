/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.phetcommon.VerticalConnector;
import edu.colorado.phet.waveinterference.tests.ExpandableScreenChartGraphic;
import edu.colorado.phet.waveinterference.tests.ExpandableWaveChart;
import edu.colorado.phet.waveinterference.view.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:31:39 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class LightSimulationPanel extends WaveInterferenceCanvas implements ModelElement {
    private LightModule lightModule;
    private RotationWaveGraphic rotationWaveGraphic;
    private IntensityReaderSet intensityReaderSet;
    private SlitPotentialGraphic slitPotentialGraphic;
    private MeasurementToolSet measurementToolSet;
    private LightSourceGraphic primaryLaserGraphic;
    private LightSourceGraphic secondaryLaserGraphic;
    private MultiOscillator multiOscillator;
    private WaveSideView waveSideView;
    private WaveModelGraphic waveModelGraphic;
    private RotationGlyph rotationGlyph;
    private ScreenNode screenNode;
    private LaserControlPanelPNode laserControlPanelPNode;
    private WaveModel visibleWaveModel;
    private WaveChartGraphic waveChartGraphic;
    private ExpandableWaveChart expandableWaveChart;
    private ScreenChartGraphic screenChart;
    private ExpandableScreenChartGraphic expandableScreenChartGraphic;

    public LightSimulationPanel( LightModule lightModule ) {
        this.lightModule = lightModule;

        this.visibleWaveModel = lightModule.getWaveModel();

        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 8, 8, new IndexColorMap( getLattice() ) );
        waveSideView = new WaveSideViewPhoton( getWaveModel(), waveModelGraphic.getLatticeScreenCoordinates() );

        waveSideView.setStroke( new BasicStroke( 5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        waveModelGraphic.addListener( new WaveModelGraphic.Listener() {
            public void colorMapChanged() {
                colorChanged();
            }
        } );
        rotationGlyph = new RotationGlyph();
        rotationGlyph.synchronizeDepthSize( waveModelGraphic );
        rotationWaveGraphic = new RotationWaveGraphic3D( waveModelGraphic, waveModelGraphic, rotationGlyph );
        rotationWaveGraphic.setOffset( super.getWaveModelGraphicOffset() );
        rotationWaveGraphic.addListener( new RotationWaveGraphic.Listener() {
            public void rotationChanged() {
                angleChanged();
            }
        } );

        screenNode = new ScreenNode( getWaveModel(), getLatticeScreenCoordinates(), waveModelGraphic );
        addScreenChild( screenNode );

        addScreenChild( rotationWaveGraphic );

        primaryLaserGraphic = new LightSourceGraphic( this, lightModule.getPrimaryOscillator(), getLatticeScreenCoordinates() );
        addScreenChild( primaryLaserGraphic );

        secondaryLaserGraphic = new LightSourceGraphic( this, lightModule.getSecondaryOscillator(), getLatticeScreenCoordinates() );
        addScreenChild( secondaryLaserGraphic );

        slitPotentialGraphic = new SlitPotentialGraphic( lightModule.getSlitPotential(), getLatticeScreenCoordinates() );
        addScreenChild( slitPotentialGraphic );

        intensityReaderSet = new IntensityReaderSet();
        addScreenChild( intensityReaderSet );

        measurementToolSet = new MeasurementToolSet( this, lightModule.getClock(), getLatticeScreenCoordinates(), getWaveInterferenceModel() );

        multiOscillator = new MultiOscillator( getWaveModel(), primaryLaserGraphic, lightModule.getPrimaryOscillator(), secondaryLaserGraphic, lightModule.getSecondaryOscillator() );
        laserControlPanelPNode = new LaserControlPanelPNode( this, waveModelGraphic, lightModule.getPrimaryOscillator(), lightModule.getSecondaryOscillator() );
        addScreenChild( laserControlPanelPNode );

        VerticalConnector verticalConnector = new VerticalConnector( laserControlPanelPNode, primaryLaserGraphic );
        try {
            verticalConnector.setTexture( ImageLoader.loadBufferedImage( "images/silverwire.png" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        addScreenChild( 0, verticalConnector );

        waveChartGraphic = new LaserWaveChartGraphic( this, "Electric Field", getLatticeScreenCoordinates(), getWaveModel(), new MutableColor( waveModelGraphic.getColorMap().getRootColor() ),
                                                      getWaveInterferenceModel().getDistanceUnits(), 0, getWaveInterferenceModel().getPhysicalWidth() );

        expandableWaveChart = new ExpandableWaveChart( this, waveChartGraphic, getLatticeScreenCoordinates() );
        addScreenChild( expandableWaveChart );

        final CrossSectionGraphic crossSectionGraphic = new CrossSectionGraphic( getWaveModel(), getLatticeScreenCoordinates() );
        addScreenChild( crossSectionGraphic );

        expandableWaveChart.addListener( new ExpandableWaveChart.Listener() {
            public void expansionStateChanged() {
                crossSectionGraphic.setVisible( expandableWaveChart.isExpanded() );
            }
        } );
        crossSectionGraphic.setVisible( expandableWaveChart.isExpanded() );

        screenChart = new ScreenChartGraphic( "Screen Chart", getLatticeScreenCoordinates(), getWaveModel(), new MutableColor( Color.black ), screenNode.getBrightnessScreenGraphic() );

        expandableScreenChartGraphic = new ExpandableScreenChartGraphic( this, screenChart );
        addScreenChild( expandableScreenChartGraphic );
        addScreenChild( measurementToolSet );
        screenNode.addListener( new ScreenNode.Listener() {
            public void enabledStateChanged() {
                updateExpandableGraphicVisibility();
            }
        } );
        updateExpandableGraphicVisibility();

        colorChanged();
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
        return lightModule.getWaveInterferenceModel();
    }

    private void updateExpandableGraphicVisibility() {
        expandableScreenChartGraphic.setVisible( screenNode.isEnabled() );
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

    private void colorChanged() {
        waveSideView.setStrokeColor( waveModelGraphic.getColorMap().getRootColor() );
        rotationGlyph.setColor( waveModelGraphic.getColorMap().getRootColor() );
        if( expandableWaveChart != null ) {
            expandableWaveChart.setColor( waveModelGraphic.getColorMap().getRootColor() );
        }
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

    private void setAsymmetricFeaturesEnabled( boolean b ) {
        lightModule.setAsymmetricFeaturesEnabled( b );
    }

    private Lattice2D getLattice() {
        return getWaveModel().getLattice();
    }

    private WaveModel getWaveModel() {
        return visibleWaveModel;
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

    public void stepInTime( double dt ) {
        rotationWaveGraphic.update();
        intensityReaderSet.update();
        screenNode.updateScreen();
        expandableWaveChart.updateChart();
        expandableScreenChartGraphic.updateChart();
    }

    public MultiOscillator getMultiOscillator() {
        return multiOscillator;
    }

    public ScreenNode getScreenNode() {
        return screenNode;
    }
}
