/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * User: Sam Reid
 * Date: May 24, 2006
 * Time: 1:27:08 PM
 * Copyright (c) May 24, 2006 by Sam Reid
 */

public class ResizeHandler {
    private static ResizeHandler instance;

    public static ResizeHandler getInstance() {
        if( instance == null ) {
            instance = new ResizeHandler();
        }
        return instance;
    }

    public void setResizable( final PSwingCanvas pcanvas, final PNode node ) {
        setResizable( pcanvas, node, 0.75 );
    }

    public void setResizable( final PSwingCanvas pcanvas, final PNode node, final double scale ) {
        pcanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateLaserControlPSwingScale( pcanvas, node, scale );
            }
        } );
        updateLaserControlPSwingScale( pcanvas, node, scale );
    }

    private void updateLaserControlPSwingScale( PSwingCanvas lightSimulationPanel, PNode laserControlPSwing, double scale ) {
        if( lightSimulationPanel.getWidth() < 900 ) {
            laserControlPSwing.setScale( scale );
        }
        else {
            laserControlPSwing.setScale( 1.0 );
        }
    }
}
