/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.swing;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.phetcommon.RulerGraphic;
import edu.colorado.phet.qm.view.gun.AbstractGun;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.piccolo.*;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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

    public DoubleSlitPanel getDoubleSlitPanel() {
        return schrodingerScreenNode.getDoubleSlitPanel();
    }

    public SchrodingerPanel( SchrodingerModule module ) {
        setLayout( null );
        this.module = module;
        this.discreteModel = module.getDiscreteModel();

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                setRenderingSize( 600, 600 );
                //todo piccolo
//                abstractGun.componentResized( e );
            }
        } );

        getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                updateScreen();
            }
        } );
        schrodingerScreenNode = createScreenNode( module );
        setScreenNode( schrodingerScreenNode );
        setZoomEventHandler( new PZoomEventHandler() );
    }

    protected SchrodingerScreenNode createScreenNode( SchrodingerModule module ) {
        return new SchrodingerScreenNode( this );
    }

//    private void addWorldGraphic( PNode graphic, double layer ) {
//        addWorldGraphic( graphic );//todo layering
//    }

//    private void addWorldGraphic( PNode graphic ) {
//        super.addWorldChild( graphic );
//    }

    public PSwing getDoubleSlitPanelGraphic() {
        return schrodingerScreenNode.getDoubleSlitPanelGraphic();
    }

    protected void updateScreen() {
        getIntensityDisplay().tryDetecting();
    }

    protected void setGunGraphic( AbstractGun abstractGun ) {
        schrodingerScreenNode.setGunGraphic( abstractGun );
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

//    private void removeGraphic( PNode graphic ) {
//        removeWorldChild( graphic );
//    }

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

    public AbstractGun getGunGraphic() {
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
        getWavefunctionGraphic().setPhoton( photon );
//        getWavefunctionGraphic().getMagnitudeColorMap().setPhoton( photon );
        getIntensityDisplay().setDisplayPhotonColor( photon );
    }

    public void clearWavefunction() {
        getDiscreteModel().clearWavefunction();
    }

    public void setFadeEnabled( boolean selected ) {
        getIntensityDisplay().setFadeEnabled( selected );
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
}
