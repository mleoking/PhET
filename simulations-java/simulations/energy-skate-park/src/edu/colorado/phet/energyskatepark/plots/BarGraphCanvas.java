// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.plots.bargraphs.EnergySkateParkBarGraph;
import edu.colorado.phet.energyskatepark.view.swing.ClearHeatButton;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;

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
    private VerticalZoomControl verticalZoomControl;

    public BarGraphCanvas( final EnergySkateParkModule module ) {
        this.module = module;
        final EnergySkateParkBarGraph energyBarGraphSet = new EnergySkateParkBarGraph( module.getEnergySkateParkSimulationPanel(), module.getEnergySkateParkModel(),
                                                                                       400 / 5000.0 );
        getLayer().addChild( energyBarGraphSet );
        energyBarGraphSet.translate( 45, 45 );

        ClearHeatButton clear = new ClearHeatButton( module.getEnergySkateParkModel() );
        clearHeatButton = new PSwing( clear );
        getLayer().addChild( clearHeatButton );

        setPanEventHandler( null );
        setZoomEventHandler( null );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateLayout();
            }
        } );

        final NumberAxis axis = new NumberAxis();
        axis.addChangeListener( new AxisChangeListener() {
            public void axisChanged( AxisChangeEvent event ) {
                updateZoom( energyBarGraphSet, axis );
            }
        } );
        updateZoom( energyBarGraphSet, axis );
        verticalZoomControl = new VerticalZoomControl( axis );
        getLayer().addChild( verticalZoomControl );
        updateLayout();
    }

    private void updateZoom( EnergySkateParkBarGraph energyBarGraphSet, NumberAxis axis ) {
        double range = Math.abs( axis.getLowerBound() - axis.getUpperBound() );
        energyBarGraphSet.setBarScale( 500 / range );
    }

    private void updateLayout() {
        int insetY = 2;
        int insetX = 2;
        clearHeatButton.setOffset( insetX, getHeight() - clearHeatButton.getFullBounds().getHeight() - insetY );
        verticalZoomControl.setOffset( getWidth() - verticalZoomControl.getFullBounds().getWidth() - insetX, getHeight() - verticalZoomControl.getFullBounds().getHeight() - insetY );
    }

    public void reset() {
        verticalZoomControl.setZoom( 0 );
    }
}
