/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.piccolo;

import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.view.ColorGrid;
import edu.colorado.phet.qm.view.ColorMap;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 28, 2005
 * Time: 9:18:24 AM
 * Copyright (c) Jul 28, 2005 by Sam Reid
 */

public class WavefunctionPGraphic extends PNode {
    private SchrodingerCanvas schrodingerCanvas;
    private PImage wavefunctionGraphic;

    public WavefunctionPGraphic( SchrodingerCanvas schrodingerCanvas ) {
        this.schrodingerCanvas = schrodingerCanvas;
        wavefunctionGraphic = new PImage();
        schrodingerCanvas.getDiscreteModel().addListener( new DiscreteModel.Adapter() {
            public void finishedTimeStep( DiscreteModel model ) {
                update();
            }
        } );
        update();
        addChild( wavefunctionGraphic );
        setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
    }

    private void update() {
        ColorMap colorMap = new PiccoloMagnitudeInGrayscale( getDiscreteModel() );
        ColorGrid colorGrid = new ColorGrid( 600, 600, 100, 100 );
        colorGrid.colorize( colorMap );
        wavefunctionGraphic.setImage( colorGrid.getBufferedImage() );
        repaint();
    }

    private DiscreteModel getDiscreteModel() {
        return schrodingerCanvas.getDiscreteModel();
    }
}
