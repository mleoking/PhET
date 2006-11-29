/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.plots;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.ec3.EnergySkateParkModule;
import edu.colorado.phet.ec3.EnergySkateParkStrings;
import edu.colorado.phet.ec3.view.bargraphs.EnergyBarGraphSet;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 2:01:22 PM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class BarGraphCanvas extends PSwingCanvas {
    private EnergySkateParkModule module;
    private PSwing clearHeatButton;

    public BarGraphCanvas( final EnergySkateParkModule module ) {
        this.module = module;
        EnergyBarGraphSet energyBarGraphSet = new EnergyBarGraphSet( module.getEnergyConservationCanvas(), module.getEnergySkateParkModel(),
                                                                     new ModelViewTransform1D( 0, 7000, 0, 500 ) );
        getLayer().addChild( energyBarGraphSet );
        energyBarGraphSet.translate( 45, 45 );
        JButton removeHeatButton = new JButton( EnergySkateParkStrings.getString( "clear.heat" ) );
        removeHeatButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clearHeat();
            }
        } );
        clearHeatButton = new PSwing( this, removeHeatButton );
        getLayer().addChild( clearHeatButton );

        setPanEventHandler( null );
        setZoomEventHandler( null );
        updateLayout();
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateLayout();
            }
        } );
    }

    private void updateLayout() {
        clearHeatButton.setOffset( getWidth() / 2 - clearHeatButton.getFullBounds().getWidth() / 2, getHeight() - clearHeatButton.getFullBounds().getHeight() - 5 );
    }
}
