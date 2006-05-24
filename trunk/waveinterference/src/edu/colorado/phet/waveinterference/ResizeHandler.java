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

    public void setResizable( final PSwingCanvas lightSimulationPanel, final PNode node ) {
        lightSimulationPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateLaserControlPSwingScale( lightSimulationPanel, node );
            }
        } );
        updateLaserControlPSwingScale( lightSimulationPanel, node );
    }

    private void updateLaserControlPSwingScale( PSwingCanvas lightSimulationPanel, PNode laserControlPSwing ) {
        if( lightSimulationPanel.getWidth() < 900 ) {
            laserControlPSwing.setScale( 0.75 );
        }
        else {
            laserControlPSwing.setScale( 1.0 );
        }
    }
}
