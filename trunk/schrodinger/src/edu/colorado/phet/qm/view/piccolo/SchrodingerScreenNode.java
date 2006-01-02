/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.phetcommon.RulerGraphic;
import edu.colorado.phet.qm.view.gun.AbstractGunGraphic;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.DetectorSheetPNode;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityManager;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

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
    private AbstractGunGraphic abstractGunGraphic;
    private IntensityManager intensityManager;
    private RulerGraphic rulerGraphic;
    private ArrayList detectorGraphics = new ArrayList();
//    private PSwing doubleSlitPanelGraphic;
//    private DoubleSlitPanel doubleSlitPanel;
//    private PSwing clearButtonNode;
    private Dimension lastLayoutSize = null;
    private static final int WAVE_AREA_LAYOUT_INSET_X = 20;
    private static final int WAVE_AREA_LAYOUT_INSET_Y = 20;
//    private PSwing doubleSlitPanelButton;
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

//        JButton clear = new JButton( "<html>Clear<br>Wave</html>" ) {
//            protected void paintComponent( Graphics g ) {
//                ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
//                super.paintComponent( g );
//            }
//        };
//        clear.setMargin( new Insets( 2, 2, 2, 2 ) );
////        clearButtonNode = new PSwing( schrodingerPanel, clear );
////        addChild( clearButtonNode );
//        clear.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
//        clear.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                schrodingerPanel.clearWavefunction();
//            }
//        } );
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

//    public DoubleSlitPanel getDoubleSlitPanel() {
//        return doubleSlitPanel;
//    }
//
//    public PSwing getDoubleSlitPanelGraphic() {
//        return doubleSlitPanelGraphic;
//    }

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

            System.out.println( "System.currentTimeMillis() = " + System.currentTimeMillis() );
            super.layoutChildren();

            double slitPanelInsetX = 5;
//            double slitPanelWidth = doubleSlitPanelGraphic.getFullBounds().getWidth();
            double slitPanelWidth = 0;
            double waveAreaX = slitPanelInsetX * 2 + slitPanelWidth;

            if( schrodingerPanel.getWidth() > 0 && schrodingerPanel.getHeight() > 0 ) {
                Dimension dim = getCellDimensions();
                wavefunctionGraphic.setCellDimensions( dim.width, dim.height );
                wavefunctionGraphic.setOffset( waveAreaX, 50 );
//                wavefunctionGraphic.setOffset( 0, 0 );
                detectorSheetPNode.setOffset( wavefunctionGraphic.getFullBounds().getX(),
                                              wavefunctionGraphic.getFullBounds().getY() - detectorSheetPNode.getFullBounds().getHeight() / 2 );
                abstractGunGraphic.setOffset( wavefunctionGraphic.getFullBounds().getCenterX() - abstractGunGraphic.getGunWidth() / 2 + 10,
                                              wavefunctionGraphic.getFullBounds().getMaxY() - getGunGraphicOffsetY() );

                PBounds detectorSheetBounds = detectorSheetPNode.getDetectorSheetPanel().getGlobalFullBounds();
                globalToLocal( detectorSheetBounds );
//                doubleSlitPanelGraphic.setOffset( slitPanelInsetX, wavefunctionGraphic.getFullBounds().getCenterY() - doubleSlitPanelGraphic.getFullBounds().getHeight() / 2 );
//                doubleSlitPanelButton.setOffset( doubleSlitPanelGraphic.getXOffset(), doubleSlitPanelGraphic.getYOffset() - doubleSlitPanelButton.getFullBounds().getHeight() );
//                clearButtonNode.setOffset( wavefunctionGraphic.getFullBounds().getX() - clearButtonNode.getFullBounds().getWidth(),
//                                           wavefunctionGraphic.getFullBounds().getMaxY() - clearButtonNode.getHeight() );
            }
        }
    }

    private Dimension getCellDimensions() {
        Dimension availableSize = schrodingerPanel.getSize();
        availableSize.width -= getDetectorSheetControlPanelNode().getFullBounds().getWidth();
//        availableSize.width -= doubleSlitPanelGraphic.getFullBounds().getWidth();
        int doubleSlitPanelGraphicWidth = 0;
        availableSize.width -= doubleSlitPanelGraphicWidth;
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
        return detectorSheetPNode.getDetectorSheetPanel();
    }

    public void removePotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        removeChild( rectangularPotentialGraphic );
        rectanglePotentialGraphics.remove( rectangularPotentialGraphic );
    }

//    public PNode getDoubleSlitPanelButton() {
//        return doubleSlitPanelButton;
//    }

    public DetectorSheetPNode getDetectorSheetPNode() {
        return detectorSheetPNode;
    }
}
