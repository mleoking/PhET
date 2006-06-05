/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.event.PDebugKeyHandler;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.ParticleUnits;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.colorgrid.ColorMap;
import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;
import edu.colorado.phet.qm.view.colormaps.WaveValueAccessor;
import edu.colorado.phet.qm.view.complexcolormaps.ComplexColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.ComplexColorMapAdapter;
import edu.colorado.phet.qm.view.complexcolormaps.MagnitudeColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.VisualColorMap3;
import edu.colorado.phet.qm.view.gun.AbstractGunGraphic;
import edu.colorado.phet.qm.view.gun.GunControlPanel;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.piccolo.DetectorGraphic;
import edu.colorado.phet.qm.view.piccolo.RectangularPotentialGraphic;
import edu.colorado.phet.qm.view.piccolo.SchrodingerScreenNode;
import edu.colorado.phet.qm.view.piccolo.WavefunctionGraphic;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.DetectorSheetPNode;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityManager;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:55:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerPanel extends PhetPCanvas {
    private DiscreteModel discreteModel;
    private SchrodingerModule module;
    private Photon photon;
    private SchrodingerScreenNode schrodingerScreenNode;
    private boolean fadeEnabled = true;
    private ArrayList listeners = new ArrayList();
    private boolean inverseSlits = false;
    private ComplexColorMap complexColorMap = new MagnitudeColorMap();
    private WaveValueAccessor waveValueAccessor = new WaveValueAccessor.Magnitude();

    public SchrodingerPanel( SchrodingerModule module ) {
        setLayout( null );
        this.module = module;
        this.discreteModel = module.getDiscreteModel();

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                setRenderingSize( 600, 600 );
                schrodingerScreenNode.relayout();
            }
        } );
        addKeyListener( new PDebugKeyHandler() );
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
        getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                updateScreen();
            }
        } );
        schrodingerScreenNode = createSchrodingerScreenNode( module );
        getPhetRootNode().addScreenChild( schrodingerScreenNode );
//        setZoomEventHandler( new PZoomEventHandler() );
        setBackground( new Color( 170, 210, 255 ) );
//        setDoubleSlitControlPanelVisible( false );
        addListener( new Adapter() {
            public void inverseSlitsChanged() {
                synchronizeSlitInverse();
            }
        } );
        synchronizeSlitInverse();
        setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
//        IntensityReader intensityReader = new IntensityReader( getWavefunctionGraphic() );
//        schrodingerScreenNode.addChild( intensityReader );
    }

    private AbstractGunGraphic.Listener fireListener = new AbstractGunGraphic.Listener() {
        public void gunFired() {
            doGunFired();
        }

    };

    protected Photon getPhoton() {
        return photon;
    }

    protected SchrodingerScreenNode createSchrodingerScreenNode( SchrodingerModule module ) {
        return new SchrodingerScreenNode( module, this );
    }

    private void doGunFired() {
        discreteModel.gunFired();
    }

    private void synchronizeSlitInverse() {
        discreteModel.getDoubleSlitPotential().setInverseSlits( inverseSlits );
    }

    private void setRenderingSize( int width, int height ) {
        super.setTransformStrategy( new RenderingSizeStrategy( this, new Dimension( width, height ) ) );
    }

    protected void updateScreen() {
        getIntensityDisplay().tryDetecting();
    }

    protected void setGunGraphic( AbstractGunGraphic abstractGunGraphic ) {
        schrodingerScreenNode.setGunGraphic( abstractGunGraphic );
        if( !abstractGunGraphic.containsListener( fireListener ) ) {
            abstractGunGraphic.addListener( fireListener );
        }
    }

    public void setRulerVisible( boolean rulerVisible ) {
        schrodingerScreenNode.setRulerVisible( rulerVisible );
    }

    public void reset() {
        schrodingerScreenNode.reset();
    }

    public void updateGraphics() {
    }

    public DiscreteModel getDiscreteModel() {
        return discreteModel;
    }

    public void addDetectorGraphic( DetectorGraphic detectorGraphic ) {
        schrodingerScreenNode.addDetectorGraphic( detectorGraphic );
    }

    public void addRectangularPotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        schrodingerScreenNode.addRectangularPotentialGraphic( rectangularPotentialGraphic );
    }

    public void clearPotential() {
        schrodingerScreenNode.clearPotential();
    }

    public WavefunctionGraphic getWavefunctionGraphic() {
        return schrodingerScreenNode.getWavefunctionGraphic();
    }

    public SchrodingerModule getSchrodingerModule() {
        return module;
    }

    public IntensityManager getIntensityDisplay() {
        return schrodingerScreenNode.getIntensityDisplay();
    }

    public DetectorSheetPNode getDetectorSheetPNode() {
        return schrodingerScreenNode.getDetectorSheetPNode();
    }

    public AbstractGunGraphic getGunGraphic() {
        return schrodingerScreenNode.getGunGraphic();
    }

    public void removeDetectorGraphic( DetectorGraphic detectorGraphic ) {
        schrodingerScreenNode.removeDetectorGraphic( detectorGraphic );
    }

    public void addDetectorGraphic( Detector detector ) {
        DetectorGraphic detectorGraphic = new DetectorGraphic( this, detector );
        addDetectorGraphic( detectorGraphic );
    }

    public DetectorGraphic getDetectorGraphic( Detector detector ) {
        return schrodingerScreenNode.getDetectorGraphic( detector );
    }

    public void removeDetectorGraphic( Detector detector ) {
        DetectorGraphic detectorGraphic = getDetectorGraphic( detector );
        removeDetectorGraphic( detectorGraphic );
    }

    public void setPhoton( Photon photon ) {
        this.photon = photon;

        getDetectorSheetPNode().setDisplayPhotonColor( photon == null ? null : new ColorData( photon.getWavelengthNM() ) );
        updateWavefunctionGraphic();
    }

    private void updateWavefunctionGraphic() {
        updateWavefunctionColorMap();
        updateWaveGraphic();
    }

    protected void updateWavefunctionColorMap() {
        getWavefunctionGraphic().setColorMap( createColorMap() );
    }

    public void setVisualizationStyle( ComplexColorMap colorMap, WaveValueAccessor waveValueAccessor ) {
        this.complexColorMap = colorMap;
        this.waveValueAccessor = waveValueAccessor;
        updateWavefunctionGraphic();
    }

    protected WaveValueAccessor getWaveValueAccessor() {
        return waveValueAccessor;
    }

    protected ComplexColorMap getComplexColorMap() {
        return complexColorMap;
    }

    protected ColorMap createColorMap() {
        System.out.println( "waveValueAccessor = " + waveValueAccessor );
        if( photon != null && !( complexColorMap instanceof VisualColorMap3 ) ) {
//            return new PhotonColorMap( this, photon.getWavelengthNM(), getWaveValueAccessor() );
            if( waveValueAccessor instanceof WaveValueAccessor.Imag ) {
                return new PhotonColorMap( this, photon.getWavelengthNM(), new WaveValueAccessor() {
                    public double getValue( Wavefunction wavefunction, int i, int j ) {
                        return 0;
                    }
                } );
            }
            else {
//                return new PhotonColorMap( this, photon.getWavelengthNM(), new WaveValueAccessor.Real() );
//                return new PhotonColorMap( this, photon.getWavelengthNM(), new WaveValueAccessor.Magnitude() );
                return new PhotonColorMap( this, photon.getWavelengthNM(), waveValueAccessor );
            }
//            return new RealPhotonColorMap( this, photon.getWavelengthNM(), getWaveValueAccessor() );
//            return new ComplexColorMapAdapter(getDiscreteModel().getWavefunction(), new GrayscaleColorMap.Real() );
        }
        else {
            return new ComplexColorMapAdapter( getDiscreteModel().getWavefunction(), complexColorMap );
        }
    }

    public void clearWavefunction() {
        getDiscreteModel().clearWavefunction();
    }

    public void setFadeEnabled( boolean selected ) {
        this.fadeEnabled = selected;
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.fadeStateChanged();
        }
    }

    public Photon getDisplayPhotonColor() {
        return photon;
    }

    public void setWaveSize( int width, int height ) {
        schrodingerScreenNode.setWaveGraphicGridSize( width, height );
    }

    public Rectangle waveAreaToScreen( Rectangle gridRect ) {
        getWavefunctionGraphic().localToGlobal( gridRect );
        getLayer().globalToLocal( gridRect );
        return gridRect;
    }

    public void removePotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        schrodingerScreenNode.removePotentialGraphic( rectangularPotentialGraphic );
    }

    public SchrodingerScreenNode getSchrodingerScreenNode() {
        return schrodingerScreenNode;
    }

    public boolean isFadeEnabled() {
        return fadeEnabled;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public boolean isInverseSlits() {
        return inverseSlits;
    }

    public void setInverseSlits( boolean inverseSlits ) {
        this.inverseSlits = inverseSlits;
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.inverseSlitsChanged();
        }
    }

    public void setUnits( ParticleUnits particleUnits ) {
        schrodingerScreenNode.setUnits( particleUnits );
    }

    public void setStopwatchVisible( boolean selected ) {
        schrodingerScreenNode.setStopwatchVisible( selected );
    }

    public boolean isRulerVisible() {
        return schrodingerScreenNode.isRulerVisible();
    }

    public void updateWaveGraphic() {
        schrodingerScreenNode.updateWaveGraphic();
    }

    public void addGunControlPanel() {
        GunControlPanel gunControlPanel = getGunGraphic().getGunControlPanel();
        schrodingerScreenNode.setGunControlPanel( gunControlPanel );
    }

    public boolean isPhotonMode() {
        return getGunGraphic().isPhotonMode();
    }

    public void setCellSize( int size ) {
        schrodingerScreenNode.setCellSize( size );
    }

    public static interface Listener {
        public void fadeStateChanged();

        public void inverseSlitsChanged();
    }

    public static class Adapter implements Listener {
        public void fadeStateChanged() {
        }

        public void inverseSlitsChanged() {
        }
    }
}
