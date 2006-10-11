/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.test;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;

/**
 * User: Sam Reid
 * Date: May 27, 2006
 * Time: 11:28:18 AM
 * Copyright (c) May 27, 2006 by Sam Reid
 */
public class WorldNode extends PNode {
    PCanvas pCanvas;
    private double minWidth;
    private double minHeight;

    public WorldNode( final PCanvas pCanvas, final double minWidth, final double minHeight ) {
        this.pCanvas = pCanvas;
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        pCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                //choose a scale based on aspect ratio.
                updateScale();
            }
        } );
        updateScale();
    }

    public Dimension2D getMinDimension() {
        return new PDimension( minWidth, minHeight );
    }

    public void setMinDimension( double minWidth, double minHeight ) {
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        updateScale();
    }

    protected void updateScale() {
        double minSX = pCanvas.getWidth() / minWidth;
        double minSY = pCanvas.getHeight() / minHeight;
        double scale = Math.min( minSX, minSY );
        System.out.println( "scale = " + scale );
        if( scale > 0 ) {
            setScale( scale );
        }
    }
    //todo: override setscale?  Or have a private hidden inner instance?  Or just assume this interface won't be abused.
    //todo: these nodes should be stackable.

}
