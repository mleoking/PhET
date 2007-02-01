/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.plots;

import edu.umd.cs.piccolox.pswing.PSwing;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * User: Sam Reid
 * Date: Jun 2, 2006
 * Time: 12:01:22 AM
 * Copyright (c) Jun 2, 2006 by Sam Reid
 */

public class ZoomPanelPSwing extends PSwing {
    private EnergyPositionPlotCanvas canvas;
    private ZoomPanel zoomPanel;

    public ZoomPanelPSwing( EnergyPositionPlotCanvas canvas, ZoomPanel zoomPanel ) {
        super( canvas, zoomPanel );
        this.canvas = canvas;
        this.zoomPanel = zoomPanel;

        canvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                update();
            }

            public void componentShown( ComponentEvent e ) {
                update();
            }

        } );
    }

    private void update() {
        setOffset( 0, canvas.getHeight() - getFullBounds().getHeight() - canvas.getSouthPanel().getHeight() );
    }
}
