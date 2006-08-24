/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.phetcommon.ShinyPanel;
import edu.colorado.phet.waveinterference.tests.ExpandableWaveChart;
import edu.colorado.phet.waveinterference.util.WIStrings;
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

public class WaterSimulationPanel extends WaveInterferenceCanvas implements ModelElement, TopViewBarrierVisibility {
    private WaterModule waterModule;
    private RotationWaveGraphic rotationWaveGraphic;
    private IntensityReaderSet intensityReaderSet;
    private SlitPotentialGraphic slitPotentialGraphic;
    private MeasurementToolSet measurementToolSet;
    private FaucetGraphic primaryFaucetGraphic;
    private FaucetGraphic secondaryFaucetGraphic;
    private MultiFaucetDrip multiFaucetDrip;
    private FaucetControlPanelPNode faucetControlPanelPNode;
    private MutableColor waterColor = new MutableColor( new Color( 37, 179, 255 ) );
    private FaucetConnector faucetConnector;
    private WaveModel viewableModel;
    private ExpandableWaveChart expandableWaveChart;
    private WaveChartGraphic waveChartGraphic;
    private CrossSectionGraphic crossSectionGraphic;
    private WaveInterferenceScreenUnits screenUnits;
//    private ThisSideUpGraphic thisSideUpGraphic;

    public WaterSimulationPanel( WaterModule waterModule ) {
        this.waterModule = waterModule;

        viewableModel = waterModule.getWaveModel();

        WaveModelGraphic waveModelGraphic = new WaveModelGraphic( getWaveModel(), 6, 6, new IndexColorMap( getLattice(), waterColor ) );
        WaveSideViewFull waveSideView = new WaveSideViewFull( getWaveModel(), waveModelGraphic.getLatticeScreenCoordinates() );

        waveSideView.setStrokeColor( waterColor.getColor().darker() );//todo make it mutable
        waveSideView.setBodyColor( waterColor.getColor() );//todo make it mutable
        final RotationGlyph rotationGlyph = new RotationGlyph( waterColor );
        rotationWaveGraphic = new RotationWaveGraphic( waveModelGraphic, waveSideView, rotationGlyph );
        rotationWaveGraphic.setOffset( super.getWaveModelGraphicOffset() );
        rotationWaveGraphic.addListener( new RotationWaveGraphic.Listener() {
            public void rotationChanged() {
                angleChanged();
            }
        } );

        addScreenChild( new BarrierSideView( waterModule.getSlitPotential(), getLatticeScreenCoordinates(), waveSideView ) );//not a part of the wavesideview so coordinates will be easier, and z-order will be easier
        addScreenChild( rotationWaveGraphic );
        rotationWaveGraphic.setPickable( false );
        rotationWaveGraphic.setChildrenPickable( false );

        primaryFaucetGraphic = new RotationFaucetGraphic( this, getWaveModel(), waterModule.getPrimaryOscillator(), getLatticeScreenCoordinates(), rotationWaveGraphic );
        addScreenChild( primaryFaucetGraphic );

        secondaryFaucetGraphic = new RotationFaucetGraphic( this, getWaveModel(), waterModule.getSecondaryOscillator(), getLatticeScreenCoordinates(), rotationWaveGraphic );
        secondaryFaucetGraphic.setEnabled( false );
        addScreenChild( secondaryFaucetGraphic );

        slitPotentialGraphic = new SlitPotentialGraphic( this, waterModule.getSlitPotential(), getLatticeScreenCoordinates() );
        addScreenChild( slitPotentialGraphic );

        intensityReaderSet = new IntensityReaderSet();


        measurementToolSet = new MeasurementToolSet( this, waterModule.getClock(), getLatticeScreenCoordinates(), getWaveInterferenceModel() );
//        measurementToolSet.getMeasuringTape().setLocation( new Point2D.Double( 0,0),new Point2D.Double( 0,0) );

        multiFaucetDrip = new MultiFaucetDrip( getWaveModel(), primaryFaucetGraphic, secondaryFaucetGraphic );

        faucetControlPanelPNode = new FaucetControlPanelPNode( this, new ShinyPanel( new FaucetControlPanel( waterModule.getPrimaryOscillator(), getPrimaryFaucetGraphic() ) ), getPrimaryFaucetGraphic(), waveModelGraphic );
        ResizeHandler.getInstance().setResizable( this, faucetControlPanelPNode, 0.9 );
        addScreenChild( faucetControlPanelPNode );

        faucetConnector = new FaucetConnector( faucetControlPanelPNode, primaryFaucetGraphic );
        addScreenChild( 0, faucetConnector );

        waveChartGraphic = new WaveChartGraphic( WIStrings.getString( "water.level" ), getLatticeScreenCoordinates(), getWaveModel(), waterColor, getWaveInterferenceModel().getDistanceUnits(), 0, getWaveInterferenceModel().getPhysicalWidth() );
        expandableWaveChart = new ExpandableWaveChart( this, waveChartGraphic, getLatticeScreenCoordinates() );
        addScreenChild( expandableWaveChart );

        crossSectionGraphic = new CrossSectionGraphic( getWaveModel(), getLatticeScreenCoordinates() );
        addScreenChild( crossSectionGraphic );
        addScreenChild( measurementToolSet );
        addScreenChild( intensityReaderSet );
        expandableWaveChart.addListener( new ExpandableWaveChart.Listener() {
            public void expansionStateChanged() {
                updateCrossSectionGraphicVisible();
            }
        } );
        updateCrossSectionGraphicVisible();

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
    }

    private WaveInterferenceModel getWaveInterferenceModel() {
        return waterModule.getWaveInterferenceModel();
    }

    private void updateCrossSectionGraphicVisible() {
        crossSectionGraphic.setVisible( expandableWaveChart.isExpanded() && rotationWaveGraphic.isTopView() );
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
            double availableHeight = super.getLayoutHeight() - insetTop - insetBottom;
            int pixelsPerCell = (int)( availableHeight / getWaveModel().getHeight() );
//            System.out.println( "pixelsPerCell = " + pixelsPerCell );
            rotationWaveGraphic.setCellSize( pixelsPerCell );
            double usedHeight = rotationWaveGraphic.getFullBounds().getHeight() + faucetControlPanelPNode.getFullBounds().getHeight() + insetTop + insetBottom;
//            System.out.println( "availableHeight = " + availableHeight + ", used height=" + usedHeight );
//            System.out.println( "</WaterSimulationPanel.updateWaveSize>" );
        }
    }


    public boolean isTopVisible() {
        return rotationWaveGraphic.isTopView();
    }

    private void angleChanged() {
        if( isTopVisible() ) {
            slitPotentialGraphic.setVisible( true );
            slitPotentialGraphic.update();
        }
        else {
            slitPotentialGraphic.setVisible( false );
        }
        updateCrossSectionGraphicVisible();
        intensityReaderSet.setConstrainedToMidline( rotationWaveGraphic.isSideView() );
    }

    public MultiFaucetDrip getMultiDrip() {
        return multiFaucetDrip;
    }

    private Lattice2D getLattice() {
        return getWaveModel().getLattice();
    }

    private WaveModel getWaveModel() {
        return viewableModel;
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
        expandableWaveChart.updateChart();
    }

    public WaveInterferenceScreenUnits getScreenUnits() {
        return screenUnits;
    }

    public void reset() {
        rotationWaveGraphic.reset();
        primaryFaucetGraphic.reset();
        secondaryFaucetGraphic.reset();
        intensityReaderSet.reset();
        expandableWaveChart.reset();
        multiFaucetDrip.reset();
        measurementToolSet.reset();
    }
}
