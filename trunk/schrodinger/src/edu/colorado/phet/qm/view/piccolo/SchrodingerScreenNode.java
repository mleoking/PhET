/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.phetcommon.RulerGraphic;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.gun.AbstractGunGraphic;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.DetectorSheetPNode;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityManager;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 17, 2005
 * Time: 7:52:28 PM
 * Copyright (c) Sep 17, 2005 by Sam Reid
 */

public class SchrodingerScreenNode extends PNode {
    private SchrodingerModule module;
    private SchrodingerPanel schrodingerPanel;

    private WavefunctionGraphic wavefunctionGraphic;
    private ArrayList rectanglePotentialGraphics = new ArrayList();
    private ArrayList detectorGraphics = new ArrayList();

    private AbstractGunGraphic abstractGunGraphic;
    private IntensityManager intensityManager;
    private RulerGraphic rulerGraphic;

    private Dimension lastLayoutSize = null;
    private static final int WAVE_AREA_LAYOUT_INSET_X = 20;
    private static final int WAVE_AREA_LAYOUT_INSET_Y = 20;
    public static int numIterationsBetwenScreenUpdate = 2;
    private DetectorSheetPNode detectorSheetPNode;

    public SchrodingerScreenNode( SchrodingerModule module, final SchrodingerPanel schrodingerPanel ) {
        this.module = module;
        this.schrodingerPanel = schrodingerPanel;
        wavefunctionGraphic = new WavefunctionGraphic( getDiscreteModel(), module.getDiscreteModel().getWavefunction() );
        getDiscreteModel().addListener( new DiscreteModel.Adapter() {
            public void finishedTimeStep( DiscreteModel model ) {
                if( model.getTimeStep() % numIterationsBetwenScreenUpdate == 0 ) {
                    wavefunctionGraphic.update();
                }
            }
        } );
        rulerGraphic = new RulerGraphic( schrodingerPanel );
        rulerGraphic.setOffset( 20, 20 );
        rulerGraphic.setVisible( false );

        detectorSheetPNode = new DetectorSheetPNode( schrodingerPanel, wavefunctionGraphic, 60 );
        detectorSheetPNode.setOffset( wavefunctionGraphic.getX(), 0 );
        intensityManager = new IntensityManager( getSchrodingerModule(), schrodingerPanel, detectorSheetPNode );
        addChild( detectorSheetPNode );
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

        layoutChildren();
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

    public void setGunGraphic( AbstractGunGraphic abstractGunGraphic ) {
        if( abstractGunGraphic != null ) {
            if( getChildrenReference().contains( abstractGunGraphic ) ) {
                removeChild( abstractGunGraphic );
            }
        }
        this.abstractGunGraphic = abstractGunGraphic;
        addChild( abstractGunGraphic );

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
        detectorSheetPNode.reset();
        intensityManager.reset();
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
        while( rectanglePotentialGraphics.size() > 0 ) {
            removePotentialGraphic( (RectangularPotentialGraphic)rectanglePotentialGraphics.get( 0 ) );
        }
    }

    public IntensityManager getIntensityDisplay() {
        return intensityManager;
    }

    public RulerGraphic getRulerGraphic() {
        return rulerGraphic;
    }

    public AbstractGunGraphic getGunGraphic() {
        return abstractGunGraphic;
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
        wavefunctionGraphic.setGridDimensions( width, height );
        relayout();
    }

    public void relayout() {
        layoutChildren( true );
    }

    protected void layoutChildren() {
        layoutChildren( false );
    }

    protected void layoutChildren( boolean forceLayout ) {
        boolean sizeChanged = lastLayoutSize == null || !lastLayoutSize.equals( schrodingerPanel.getSize() );
        if( sizeChanged || forceLayout ) {


            lastLayoutSize = new Dimension( schrodingerPanel.getSize() );

//            System.out.println( "System.currentTimeMillis() = " + System.currentTimeMillis() );
            super.layoutChildren();
//            double waveAreaX = 50;

            if( schrodingerPanel.getWidth() > 0 && schrodingerPanel.getHeight() > 0 ) {
                wavefunctionGraphic.setCellDimensions( getCellDimensions() );
                double minX = Math.min( detectorSheetPNode.getFullBounds().getMinX(), abstractGunGraphic.getFullBounds().getMinX() );
                double maxX = Math.max( detectorSheetPNode.getFullBounds().getMaxX(), abstractGunGraphic.getFullBounds().getMaxX() );
                double mainWidth = maxX - minX;
                double availableWidth = schrodingerPanel.getWidth() - mainWidth;
                wavefunctionGraphic.setOffset( availableWidth / 2, detectorSheetPNode.getDetectorHeight() );

                detectorSheetPNode.setOffset( wavefunctionGraphic.getFullBounds().getX(),
                                              wavefunctionGraphic.getFullBounds().getY() - detectorSheetPNode.getFullBounds().getHeight() / 2 );
                abstractGunGraphic.setOffset( wavefunctionGraphic.getFullBounds().getCenterX() - abstractGunGraphic.getGunWidth() / 2 + 10,
                                              wavefunctionGraphic.getFullBounds().getMaxY() - getGunGraphicOffsetY() );
            }
        }
    }

    private Dimension getCellDimensions() {
        Dimension availableSize = schrodingerPanel.getSize();
        availableSize.width -= getDetectorSheetControlPanelNode().getFullBounds().getWidth();
        availableSize.width -= WAVE_AREA_LAYOUT_INSET_X;

        availableSize.height -= abstractGunGraphic.getFullBounds().getHeight();
        availableSize.height -= WAVE_AREA_LAYOUT_INSET_Y;

        Dimension availableAreaForWaveform = new Dimension( availableSize.width, availableSize.height );
        int nx = schrodingerPanel.getDiscreteModel().getGridWidth();
        int ny = schrodingerPanel.getDiscreteModel().getGridHeight();
        int cellWidth = availableAreaForWaveform.width / nx;
        int cellHeight = availableAreaForWaveform.height / ny;
        int min = Math.min( cellWidth, cellHeight );
        return new Dimension( min, min );
    }

    private PNode getDetectorSheetControlPanelNode() {
        return detectorSheetPNode.getDetectorSheetControlPanelPNode();
    }

    public void removePotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        removeChild( rectangularPotentialGraphic );
        rectanglePotentialGraphics.remove( rectangularPotentialGraphic );
    }

    public DetectorSheetPNode getDetectorSheetPNode() {
        return detectorSheetPNode;
    }
}
