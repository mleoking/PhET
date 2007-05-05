/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.phetcommon.math.ModelViewTransform1D;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.view.ClearHeatButton;
import edu.colorado.phet.energyskatepark.view.bargraphs.EnergyBarGraphSet;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 2:01:22 PM
 */

public class BarGraphCanvas extends PSwingCanvas {
    private EnergySkateParkModule module;
    private PSwing clearHeatButton;
    private BarGraphZoomPanel barGraphZoomPanel;
    private PSwing barGraphPSwing;

    public BarGraphCanvas( final EnergySkateParkModule module ) {
        this.module = module;
        EnergyBarGraphSet energyBarGraphSet = new EnergyBarGraphSet( module.getEnergyConservationCanvas(), module.getEnergySkateParkModel(),
                                                                     new ModelViewTransform1D( 0, 7000, 0, 500 ) );
        getLayer().addChild( energyBarGraphSet );
        energyBarGraphSet.translate( 45, 45 );

        ClearHeatButton clear = new ClearHeatButton( module );
        clearHeatButton = new PSwing( clear );
        getLayer().addChild( clearHeatButton );

        setPanEventHandler( null );
        setZoomEventHandler( null );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateLayout();
            }
        } );

        barGraphZoomPanel = new BarGraphZoomPanel( energyBarGraphSet );
        barGraphPSwing = new PSwing( barGraphZoomPanel );
        getLayer().addChild( barGraphPSwing );
        updateLayout();
    }

    private void updateLayout() {
        int insetY = 2;
        int insetX = 2;
        clearHeatButton.setOffset( insetX, getHeight() - clearHeatButton.getFullBounds().getHeight() - insetY );
        barGraphPSwing.setOffset( getWidth() - barGraphPSwing.getFullBounds().getWidth() - insetX, getHeight() - barGraphPSwing.getFullBounds().getHeight() - insetY );
    }

    public void reset() {
        barGraphZoomPanel.reset();
    }
}
