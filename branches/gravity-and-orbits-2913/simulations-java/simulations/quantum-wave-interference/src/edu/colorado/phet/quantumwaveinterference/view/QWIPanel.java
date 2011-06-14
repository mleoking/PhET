// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.view;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.model.Detector;
import edu.colorado.phet.quantumwaveinterference.model.ParticleUnits;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.view.colorgrid.ColorMap;
import edu.colorado.phet.quantumwaveinterference.view.colormaps.ColorData;
import edu.colorado.phet.quantumwaveinterference.view.colormaps.PhotonColorMap;
import edu.colorado.phet.quantumwaveinterference.view.colormaps.WaveValueAccessor;
import edu.colorado.phet.quantumwaveinterference.view.complexcolormaps.ComplexColorMap;
import edu.colorado.phet.quantumwaveinterference.view.complexcolormaps.ComplexColorMapAdapter;
import edu.colorado.phet.quantumwaveinterference.view.complexcolormaps.MagnitudeColorMap;
import edu.colorado.phet.quantumwaveinterference.view.complexcolormaps.VisualColorMap3;
import edu.colorado.phet.quantumwaveinterference.view.gun.AbstractGunNode;
import edu.colorado.phet.quantumwaveinterference.view.gun.GunControlPanel;
import edu.colorado.phet.quantumwaveinterference.view.gun.Photon;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.DetectorGraphic;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.QWIScreenNode;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.RectangularPotentialGraphic;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.WavefunctionGraphic;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.detectorscreen.DetectorSheetPNode;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.detectorscreen.IntensityManager;
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
 */

public class QWIPanel extends BufferedPhetPCanvas {
    //    public class QWIPanel extends PhetPCanvas {
    private QWIModel QWIModel;
    private QWIModule module;
    private Photon photon;
    private QWIScreenNode QWIScreenNode;
    private boolean fadeEnabled = true;
    private ArrayList listeners = new ArrayList();
    private boolean inverseSlits = false;
    private ComplexColorMap complexColorMap = new MagnitudeColorMap();
    private WaveValueAccessor waveValueAccessor = new WaveValueAccessor.Magnitude();

    public QWIPanel( QWIModule module ) {
        setLayout( null );
        this.module = module;
        this.QWIModel = module.getQWIModel();

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                setRenderingSize( 600, 600 );
                QWIScreenNode.relayout();
            }
        } );
        addKeyListener( new PDebugKeyHandler() );
        addKeyListener( new DebugSymmetry( module ) );
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
        QWIScreenNode = createSchrodingerScreenNode( module );
        getPhetRootNode().addScreenChild( QWIScreenNode );
//        setZoomEventHandler( new PZoomEventHandler() );
        setBackground( new Color( 170, 210, 255 ) );
//        setDoubleSlitControlPanelVisible( false );
        addListener( new Adapter() {
            public void inverseSlitsChanged() {
                synchronizeSlitInverse();
            }
        } );
        synchronizeSlitInverse();

//        IntensityReader intensityReader = new IntensityReader( getWavefunctionGraphic() );
//        schrodingerScreenNode.addChild( intensityReader );
    }

    private AbstractGunNode.Listener fireListener = new AbstractGunNode.Listener() {
        public void gunFired() {
            doGunFired();
        }

    };

    protected Photon getPhoton() {
        return photon;
    }

    protected QWIScreenNode createSchrodingerScreenNode( QWIModule module ) {
        return new QWIScreenNode( module, this );
    }

    private void doGunFired() {
        QWIModel.gunFired();
    }

    private void synchronizeSlitInverse() {
        QWIModel.getDoubleSlitPotential().setInverseSlits( inverseSlits );
    }

    private void setRenderingSize( int width, int height ) {
        super.setWorldTransformStrategy( new RenderingSizeStrategy( this, new Dimension( width, height ) ) );
    }

    protected void updateScreen() {
        getIntensityDisplay().tryDetecting();
    }

    protected void setGunGraphic( AbstractGunNode abstractGunNode ) {
        QWIScreenNode.setGunGraphic( abstractGunNode );
        if( !abstractGunNode.containsListener( fireListener ) ) {
            abstractGunNode.addListener( fireListener );
        }
    }

    public void setRulerVisible( boolean rulerVisible ) {
        QWIScreenNode.setRulerVisible( rulerVisible );
    }

    public void reset() {
        QWIScreenNode.reset();
    }

    public QWIModel getDiscreteModel() {
        return QWIModel;
    }

    public void addDetectorGraphic( DetectorGraphic detectorGraphic ) {
        QWIScreenNode.addDetectorGraphic( detectorGraphic );
    }

    public void addRectangularPotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        QWIScreenNode.addRectangularPotentialGraphic( rectangularPotentialGraphic );
    }

    public void clearPotential() {
        QWIScreenNode.clearPotential();
    }

    public WavefunctionGraphic getWavefunctionGraphic() {
        return QWIScreenNode.getWavefunctionGraphic();
    }

    public QWIModule getSchrodingerModule() {
        return module;
    }

    public IntensityManager getIntensityDisplay() {
        return QWIScreenNode.getIntensityDisplay();
    }

    public DetectorSheetPNode getDetectorSheetPNode() {
        return QWIScreenNode.getDetectorSheetPNode();
    }

    public AbstractGunNode getGunGraphic() {
        return QWIScreenNode.getGunGraphic();
    }

    public void removeDetectorGraphic( DetectorGraphic detectorGraphic ) {
        QWIScreenNode.removeDetectorGraphic( detectorGraphic );
    }

    public void addDetectorGraphic( Detector detector ) {
        DetectorGraphic detectorGraphic = new DetectorGraphic( this, detector );
        addDetectorGraphic( detectorGraphic );
    }

    public DetectorGraphic getDetectorGraphic( Detector detector ) {
        return QWIScreenNode.getDetectorGraphic( detector );
    }

    public void removeDetectorGraphic( Detector detector ) {
        DetectorGraphic detectorGraphic = getDetectorGraphic( detector );
        removeDetectorGraphic( detectorGraphic );
    }

    public void setPhoton( Photon photon ) {
        this.photon = photon;

        getDetectorSheetPNode().setDisplayPhotonColor( photon == null ? null : new ColorData( photon.getWavelengthNM() ) );
        update();
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.particleTypeChanged();
        }
    }

    public void update() {
        updateWavefunctionColorMap();
        updateWaveGraphic();
    }

    protected void updateWavefunctionColorMap() {
        ColorMap colorMap = createColorMap();
//        System.out.println( "colorMap = " + colorMap );
        getWavefunctionGraphic().setColorMap( colorMap );
    }

    public void setVisualizationStyle( ComplexColorMap colorMap, WaveValueAccessor waveValueAccessor ) {
        if( this.complexColorMap != colorMap || this.waveValueAccessor != waveValueAccessor ) {
            this.complexColorMap = colorMap;
            this.waveValueAccessor = waveValueAccessor;
            update();
            notifyVisualizationStyleChanged();
        }
    }

    private void notifyVisualizationStyleChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.visualizationStyleChanged();
        }
    }

    public WaveValueAccessor getWaveValueAccessor() {
        return waveValueAccessor;
    }

    public ComplexColorMap getComplexColorMap() {
        return complexColorMap;
    }

    protected ColorMap createColorMap() {
        if( photon != null && !( complexColorMap instanceof VisualColorMap3 ) ) {
            if( waveValueAccessor instanceof WaveValueAccessor.Imag ) {
                return new PhotonColorMap( this, photon.getWavelengthNM(), new WaveValueAccessor.Empty() );
            }
            else {
                return new PhotonColorMap( this, photon.getWavelengthNM(), waveValueAccessor );
//                return new PhotonColorMap( this, photon.getWavelengthNM(), new WaveValueAccessor.Real() );
            }
        }
        else {
            return new ComplexColorMapAdapter( getDiscreteModel().getWavefunction(), complexColorMap );
        }
    }

    public void clearWavefunction() {
        getDiscreteModel().clearWavefunction();
        updateWaveGraphic();
        repaint();
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
        QWIScreenNode.setWaveGraphicGridSize( width, height );
    }

    public Rectangle waveAreaToScreen( Rectangle gridRect ) {
        getWavefunctionGraphic().localToGlobal( gridRect );
        getLayer().globalToLocal( gridRect );
        return gridRect;
    }

    public void removePotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        QWIScreenNode.removePotentialGraphic( rectangularPotentialGraphic );
    }

    public QWIScreenNode getSchrodingerScreenNode() {
        return QWIScreenNode;
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
        QWIScreenNode.setUnits( particleUnits );
    }

    public void setStopwatchVisible( boolean selected ) {
        QWIScreenNode.setStopwatchVisible( selected );
    }

    public boolean isRulerVisible() {
        return QWIScreenNode.isRulerVisible();
    }

    public void updateWaveGraphic() {
        QWIScreenNode.updateWaveGraphic();
    }

    public void addGunControlPanel() {
        GunControlPanel gunControlPanel = getGunGraphic().getGunControlPanel();
        QWIScreenNode.setGunControlPanel( gunControlPanel );
    }

    public boolean isPhotonMode() {
        return getGunGraphic().isPhotonMode();
    }

    public void setCellSize( int size ) {
        QWIScreenNode.setCellSize( size );
    }

    public void updateDetectorReadouts() {
        module.getQWIModel().getDetectorSet().updateDetectorProbabilities();
    }

    public static interface Listener {
        public void fadeStateChanged();

        public void inverseSlitsChanged();

        void visualizationStyleChanged();

        void particleTypeChanged();
    }

    public static class Adapter implements Listener {
        public void fadeStateChanged() {
        }

        public void inverseSlitsChanged() {
        }

        public void visualizationStyleChanged() {
        }

        public void particleTypeChanged() {
        }
    }
}
