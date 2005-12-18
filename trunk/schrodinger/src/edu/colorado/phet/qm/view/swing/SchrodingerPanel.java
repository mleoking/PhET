/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.swing;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.phetcommon.RulerGraphic;
import edu.colorado.phet.qm.view.gun.AbstractGunGraphic;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.piccolo.DetectorGraphic;
import edu.colorado.phet.qm.view.piccolo.RectangularPotentialGraphic;
import edu.colorado.phet.qm.view.piccolo.SchrodingerScreenNode;
import edu.colorado.phet.qm.view.piccolo.WavefunctionGraphic;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityGraphic;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

        getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                updateScreen();
            }
        } );
        schrodingerScreenNode = new SchrodingerScreenNode( module, this );
        getPhetRootNode().setScreenNode( schrodingerScreenNode );
//        setZoomEventHandler( new PZoomEventHandler() );
        setBackground( new Color( 170, 210, 255 ) );
        setDoubleSlitControlPanelVisible( false );
        addListener( new Adapter() {
            public void inverseSlitsChanged() {
                synchronizeSlitInverse();
            }
        } );
        synchronizeSlitInverse();
    }

    private void synchronizeSlitInverse() {
        discreteModel.getDoubleSlitPotential().setInverseSlits( inverseSlits );
    }

    private void setRenderingSize( int width, int height ) {
        super.setTransformStrategy( new RenderingSizeStrategy( this, new Dimension( width, height ) ) );
    }

    public DoubleSlitPanel getDoubleSlitPanel() {
        return schrodingerScreenNode.getDoubleSlitPanel();
    }

    public PSwing getDoubleSlitPanelGraphic() {
        return schrodingerScreenNode.getDoubleSlitPanelGraphic();
    }

    private PNode getDoubleSlitPanelButton() {
        return schrodingerScreenNode.getDoubleSlitPanelButton();
    }

    protected void updateScreen() {
        getIntensityDisplay().tryDetecting();
    }

    protected void setGunGraphic( AbstractGunGraphic abstractGunGraphic ) {
        schrodingerScreenNode.setGunGraphic( abstractGunGraphic );
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

    public IntensityGraphic getIntensityDisplay() {
        return schrodingerScreenNode.getIntensityDisplay();
    }

    public RulerGraphic getRulerGraphic() {
        return schrodingerScreenNode.getRulerGraphic();
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

    public void setDisplayPhotonColor( Photon photon ) {
        this.photon = photon;
//        getWavefunctionGraphic().setPhoton( photon );
//        getWavefunctionGraphic().getMagnitudeColorMap().setPhoton( photon );
        getIntensityDisplay().setDisplayPhotonColor( photon );
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
        schrodingerScreenNode.setWaveSize( width, height );
    }

    public Rectangle waveAreaToScreen( Rectangle gridRect ) {
        //todo piccolo
        getWavefunctionGraphic().localToGlobal( gridRect );
        getLayer().globalToLocal( gridRect );
        return gridRect;
//        Rectangle screenRect = wavefunctionGraphic.getNetTransform().createTransformedShape( gridRect ).getBounds();
//        return screenRect;
    }

    public void removePotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        schrodingerScreenNode.removePotentialGraphic( rectangularPotentialGraphic );
    }

    public SchrodingerScreenNode getSchrodingerScreenNode() {
        return schrodingerScreenNode;
    }

    public void setDoubleSlitControlPanelVisible( boolean selected ) {
        getDoubleSlitPanelGraphic().setVisible( selected );
        getDoubleSlitPanelGraphic().setChildrenPickable( selected );
        getDoubleSlitPanelGraphic().setPickable( selected );
//        getDoubleSlitPanelButton().setVisible( !selected );
//        getDoubleSlitPanelButton().setChildrenPickable( !selected );
//        getDoubleSlitPanelButton().setPickable( !selected );
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
