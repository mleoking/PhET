/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.phetcommon.RulerGraphic;
import edu.colorado.phet.qm.view.gun.AbstractGun;
import edu.umd.cs.piccolo.PNode;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 17, 2005
 * Time: 7:52:28 PM
 * Copyright (c) Sep 17, 2005 by Sam Reid
 */

public class SchrodingerScreenNode extends PNode {
    private SchrodingerPanel schrodingerPanel;

    private WavefunctionGraphic wavefunctionGraphic;
    private ArrayList rectanglePotentialGraphics = new ArrayList();
    private AbstractGun abstractGun;
    private IntensityDisplay intensityDisplay;
    private RulerGraphic rulerGraphic;
    private ArrayList detectorGraphics = new ArrayList();
    private PSwing doubleSlitPanelGraphic;
    private DoubleSlitPanel doubleSlitPanel;

    public SchrodingerScreenNode( SchrodingerPanel schrodingerPanel ) {
        this.schrodingerPanel = schrodingerPanel;
        wavefunctionGraphic = new WavefunctionGraphic( schrodingerPanel );
        wavefunctionGraphic.setOffset( 100, 50 );

        rulerGraphic = new RulerGraphic( schrodingerPanel );
        rulerGraphic.setOffset( 20, 20 );
        rulerGraphic.setVisible( false );

        intensityDisplay = new IntensityDisplay( getSchrodingerModule(), schrodingerPanel, 60, wavefunctionGraphic );

        doubleSlitPanel = new DoubleSlitPanel( getDiscreteModel() );
        doubleSlitPanelGraphic = new PSwing( schrodingerPanel, doubleSlitPanel );
        doubleSlitPanelGraphic.setOffset( getWavefunctionGraphic().getX() + getWavefunctionGraphic().getWidth() - 40, getWavefunctionGraphic().getY() + getWavefunctionGraphic().getHeight() / 2 - doubleSlitPanelGraphic.getHeight() / 2 + 35 );

        addChild( intensityDisplay );
        addChild( doubleSlitPanelGraphic );
        addChild( wavefunctionGraphic );
        addChild( rulerGraphic );
        schrodingerPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                invalidateFullBounds();
                repaint();
            }

            public void componentShown( ComponentEvent e ) {
                invalidateFullBounds();
                repaint();
            }
        } );
    }

    public WavefunctionGraphic getWavefunctionGraphic() {
        return wavefunctionGraphic;
    }

    private DiscreteModel getDiscreteModel() {
        return getSchrodingerModule().getDiscreteModel();
    }

    private SchrodingerModule getSchrodingerModule() {
        return schrodingerPanel.getSchrodingerModule();
    }

    public DoubleSlitPanel getDoubleSlitPanel() {
        return doubleSlitPanel;
    }

    public PSwing getDoubleSlitPanelGraphic() {
        return doubleSlitPanelGraphic;
    }

    public void setGunGraphic( AbstractGun abstractGun ) {
        if( abstractGun != null ) {
            if( getChildrenReference().contains( abstractGun ) ) {
                removeChild( abstractGun );
            }
        }
        this.abstractGun = abstractGun;
        addChild( abstractGun );

        invalidateLayout();
        repaint();
    }

    private int getGunGraphicOffsetY() {
        return 50;
    }

    public void setRulerVisible( boolean rulerVisible ) {
        rulerGraphic.setVisible( rulerVisible );
    }

    public void reset() {
        intensityDisplay.reset();
    }

    public void addDetectorGraphic( DetectorGraphic detectorGraphic ) {
        detectorGraphics.add( detectorGraphic );
        addChild( detectorGraphic );
    }

    public void addRectangularPotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        rectanglePotentialGraphics.add( rectangularPotentialGraphic );
        addChild( rectangularPotentialGraphic );
    }

    public void clearPotential() {
        for( int i = 0; i < rectanglePotentialGraphics.size(); i++ ) {
            RectangularPotentialGraphic rectangularPotentialGraphic = (RectangularPotentialGraphic)rectanglePotentialGraphics.get( i );
            removeChild( rectangularPotentialGraphic );
        }
        rectanglePotentialGraphics.clear();
    }

    public IntensityDisplay getIntensityDisplay() {
        return intensityDisplay;
    }

    public RulerGraphic getRulerGraphic() {
        return rulerGraphic;
    }

    public AbstractGun getGunGraphic() {
        return abstractGun;
    }

    public void removeDetectorGraphic( DetectorGraphic detectorGraphic ) {
        removeChild( detectorGraphic );
        getDiscreteModel().removeDetector( detectorGraphic.getDetector() );
        detectorGraphics.remove( detectorGraphic );
    }

    public DetectorGraphic getDetectorGraphic( Detector detector ) {
        for( int i = 0; i < detectorGraphics.size(); i++ ) {
            DetectorGraphic detectorGraphic = (DetectorGraphic)detectorGraphics.get( i );
            if( detectorGraphic.getDetector() == detector ) {
                return detectorGraphic;
            }
        }
        return null;
    }

    public void setWaveSize( int width, int height ) {
        wavefunctionGraphic.setWaveSize( width, height );
        intensityDisplay.setWaveSize( width, height );
    }

    protected void layoutChildren() {
        super.layoutChildren();

        double screenWidth = schrodingerPanel.getWidth();
        double screenHeight = schrodingerPanel.getHeight();

        wavefunctionGraphic.setTransform( new AffineTransform() );
        wavefunctionGraphic.setOffset( 100, 100 );
        double origWidth = wavefunctionGraphic.getFullBounds().getWidth();
        double fracSize = 0.5;
        wavefunctionGraphic.setScale( screenWidth / origWidth * fracSize );

        abstractGun.setOffset( wavefunctionGraphic.getFullBounds().getCenterX() - abstractGun.getGunWidth() / 2 + 50,
                               wavefunctionGraphic.getFullBounds().getMaxY() - getGunGraphicOffsetY() );
        doubleSlitPanelGraphic.setOffset( wavefunctionGraphic.getFullBounds().getMaxX(),
                                          wavefunctionGraphic.getFullBounds().getCenterY() );
    }

    public void removePotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        removeChild( rectangularPotentialGraphic );
    }
}
