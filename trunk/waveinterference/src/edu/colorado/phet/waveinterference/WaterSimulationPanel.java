/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
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

public class WaterSimulationPanel extends WaveInterferenceCanvas implements ModelElement {
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

    public WaterSimulationPanel( WaterModule waterModule ) {
        this.waterModule = waterModule;

        WaveModelGraphic waveModelGraphic = new WaveModelGraphic( getWaveModel(), 8, 8, new IndexColorMap( getLattice(), waterColor ) );
        WaveSideViewFull waveSideView = new WaveSideViewFull( getWaveModel(), waveModelGraphic.getLatticeScreenCoordinates() );
        waveSideView.setStrokeColor( waterColor.getColor().darker() );//todo make it mutable
        waveSideView.setBodyColor( waterColor.getColor() );//todo make it mutable
        RotationGlyph rotationGlyph = new RotationGlyph( waterColor );
        rotationWaveGraphic = new RotationWaveGraphic( waveModelGraphic, waveSideView, rotationGlyph );
        rotationWaveGraphic.setOffset( 150, 50 );
        rotationWaveGraphic.addListener( new RotationWaveGraphic.Listener() {
            public void rotationChanged() {
                angleChanged();
            }
        } );
        addScreenChild( rotationWaveGraphic );

        primaryFaucetGraphic = new RotationFaucetGraphic( getWaveModel(), waterModule.getPrimaryOscillator(), getLatticeScreenCoordinates(), rotationWaveGraphic );
        addScreenChild( primaryFaucetGraphic );

        secondaryFaucetGraphic = new FaucetGraphic( getWaveModel(), waterModule.getSecondaryOscillator(), getLatticeScreenCoordinates() );
        secondaryFaucetGraphic.setEnabled( false );
        addScreenChild( secondaryFaucetGraphic );

        slitPotentialGraphic = new SlitPotentialGraphic( waterModule.getSlitPotential(), getLatticeScreenCoordinates() );
        addScreenChild( slitPotentialGraphic );

        intensityReaderSet = new IntensityReaderSet();
        addScreenChild( intensityReaderSet );

        measurementToolSet = new MeasurementToolSet( this, waterModule.getClock() );
        addScreenChild( measurementToolSet );

        multiFaucetDrip = new MultiFaucetDrip( getWaveModel(), primaryFaucetGraphic, secondaryFaucetGraphic );

        faucetControlPanelPNode = new FaucetControlPanelPNode( this, new FaucetControlPanel( waterModule.getPrimaryOscillator(), getPrimaryFaucetGraphic() ), getPrimaryFaucetGraphic(), waveModelGraphic );
        addScreenChild( faucetControlPanelPNode );


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
        double availableHeight = getHeight() - faucetControlPanelPNode.getFullBounds().getHeight() - insetTop - insetBottom;
        double latticeModelHeight = getWaveModel().getHeight();
        int pixelsPerCell = (int)( availableHeight / latticeModelHeight );
        rotationWaveGraphic.setCellSize( pixelsPerCell - 1 );
        double usedHeight = rotationWaveGraphic.getFullBounds().getHeight() + faucetControlPanelPNode.getFullBounds().getHeight() + insetTop + insetBottom;
//        System.out.println( "availableHeight = " + availableHeight + ", used height=" + usedHeight );
    }

    private void angleChanged() {
        if( rotationWaveGraphic.isTopView() ) {
            slitPotentialGraphic.setVisible( true );
            intensityReaderSet.setToMiddle( true );
        }
        else {
            slitPotentialGraphic.setVisible( false );
            intensityReaderSet.setToMiddle( false );
        }

    }

    public MultiFaucetDrip getMultiDrip() {
        return multiFaucetDrip;
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
