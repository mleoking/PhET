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

public class LightSimulationPanel extends WaveInterferenceCanvas implements ModelElement {
    private LightModule waterModule;
    private RotationWaveGraphic rotationWaveGraphic;
    private IntensityReaderSet intensityReaderSet;
    private SlitPotentialGraphic slitPotentialGraphic;
    private MeasurementToolSet measurementToolSet;
    private LaserGraphic primaryLaserGraphic;
    private LaserGraphic secondaryLaserGraphic;
    private MultiOscillator multiOscillator;
    private WaveSideView waveSideView;
    private WaveModelGraphic waveModelGraphic;
    private RotationGlyph rotationGlyph;
    private ScreenNode screenNode;
    private LaserControlPanelPNode laserControlPanelPNode;

    public LightSimulationPanel( LightModule waterModule ) {
        this.waterModule = waterModule;

        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 8, 8, new IndexColorMap( getLattice() ) );
        waveSideView = new WaveSideView( getLattice(), waveModelGraphic.getLatticeScreenCoordinates() );

        waveSideView.setStroke( new BasicStroke( 5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        waveModelGraphic.addListener( new WaveModelGraphic.Listener() {
            public void colorMapChanged() {
                colorChanged();
            }
        } );
        rotationGlyph = new RotationGlyph();
        rotationGlyph.setDepthVisible( false );
        rotationWaveGraphic = new RotationWaveGraphic( waveModelGraphic, waveSideView, rotationGlyph );
        rotationWaveGraphic.setOffset( 150, 2 );
        rotationWaveGraphic.addListener( new RotationWaveGraphic.Listener() {
            public void rotationChanged() {
                angleChanged();
            }
        } );

        screenNode = new ScreenNode( getWaveModel(), getLatticeScreenCoordinates(), waveModelGraphic );
        addScreenChild( screenNode );

        addScreenChild( rotationWaveGraphic );

        primaryLaserGraphic = new LaserGraphic( waterModule.getPrimaryOscillator(), getLatticeScreenCoordinates() );
        addScreenChild( primaryLaserGraphic );

        secondaryLaserGraphic = new LaserGraphic( waterModule.getSecondaryOscillator(), getLatticeScreenCoordinates() );
        addScreenChild( secondaryLaserGraphic );

        slitPotentialGraphic = new SlitPotentialGraphic( waterModule.getSlitPotential(), getLatticeScreenCoordinates() );
        addScreenChild( slitPotentialGraphic );

        intensityReaderSet = new IntensityReaderSet();
        addScreenChild( intensityReaderSet );

        measurementToolSet = new MeasurementToolSet( this, waterModule.getClock() );
        addScreenChild( measurementToolSet );

        multiOscillator = new MultiOscillator( getWaveModel(), primaryLaserGraphic, waterModule.getPrimaryOscillator(), secondaryLaserGraphic, waterModule.getSecondaryOscillator() );
        laserControlPanelPNode = new LaserControlPanelPNode( this, waveModelGraphic, waterModule.getPrimaryOscillator(), waterModule.getSecondaryOscillator() );
        addScreenChild( laserControlPanelPNode );
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

    private void updateWaveSize() {
        int insetTop = 2;
        int insetBottom = 2;
        double availableHeight = getHeight() - laserControlPanelPNode.getFullBounds().getHeight() - insetTop - insetBottom;
        double latticeModelHeight = getWaveModel().getHeight();
        int pixelsPerCell = (int)( availableHeight / latticeModelHeight );
        waveModelGraphic.setCellDimensions( pixelsPerCell - 1, pixelsPerCell - 1 );
        double usedHeight = waveModelGraphic.getFullBounds().getHeight() + laserControlPanelPNode.getFullBounds().getHeight() + insetTop + insetBottom;
        System.out.println( "availableHeight = " + availableHeight + ", used height=" + usedHeight );
    }

    private void colorChanged() {
        waveSideView.setColor( waveModelGraphic.getColorMap().getRootColor() );
        rotationGlyph.setColor( waveModelGraphic.getColorMap().getRootColor() );
    }

    private void angleChanged() {
        if( rotationWaveGraphic.isTopView() ) {
            slitPotentialGraphic.setVisible( true );
        }
        else {
            slitPotentialGraphic.setVisible( false );
        }
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

    public void stepInTime( double dt ) {
        rotationWaveGraphic.update();
        intensityReaderSet.update();
        screenNode.updateScreen();
    }

    public MultiOscillator getMultiOscillator() {
        return multiOscillator;
    }

    public ScreenNode getScreenNode() {
        return screenNode;
    }
}
