/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.piccolo;

import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.view.ColorGrid;
import edu.colorado.phet.qm.view.ColorMap;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 28, 2005
 * Time: 9:18:24 AM
 * Copyright (c) Jul 28, 2005 by Sam Reid
 */

public class WavefunctionPGraphic extends PNode {
    private SchrodingerCanvas schrodingerCanvas;
    private int nx;
    private int ny;
    private PImage wavefunctionGraphic;

    public WavefunctionPGraphic( SchrodingerCanvas schrodingerCanvas,int nx,int ny ) {
        this.schrodingerCanvas = schrodingerCanvas;
        this.nx = nx;
        this.ny = ny;
        wavefunctionGraphic = new PImage();
        schrodingerCanvas.getDiscreteModel().addListener( new DiscreteModel.Adapter() {
            public void finishedTimeStep( DiscreteModel model ) {
                update();
            }
        } );
        update();
        addChild( wavefunctionGraphic );
        wavefunctionGraphic.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        wavefunctionGraphic.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        wavefunctionGraphic.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );

        JButton clearWavefunctionButton = new JButton( "Clear Wavefunction" );
        clearWavefunctionButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().clearWavefunction();
            }
        } );
        PSwing clearGraphic = new PSwing( schrodingerCanvas, clearWavefunctionButton );
        addChild( clearGraphic );
    }

    private void update() {
        ColorMap colorMap = new PiccoloMagnitudeInGrayscale( getDiscreteModel() );
        ColorGrid colorGrid = new ColorGrid( 600, 600, nx,ny );
        colorGrid.colorize( colorMap );
        wavefunctionGraphic.setImage( colorGrid.getBufferedImage() );
        repaint();
    }

    private DiscreteModel getDiscreteModel() {
        return schrodingerCanvas.getDiscreteModel();
    }
}
