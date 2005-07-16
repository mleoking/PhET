/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.view.colormaps.VisualColorMap;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.Raster;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 4:19:29 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class DefaultPainter implements ColorMap {
    private SchrodingerPanel schrodingerPanel;
    private ColorMap wavefunctionColorMap;

    public DefaultPainter( SchrodingerPanel schrodingerPanel ) {
        this( schrodingerPanel, new VisualColorMap( schrodingerPanel ) );
    }

    public DefaultPainter( SchrodingerPanel schrodingerPanel, ColorMap wavefunctionColorMap ) {
        this.schrodingerPanel = schrodingerPanel;
        this.wavefunctionColorMap = wavefunctionColorMap;
    }

    public void setWavefunctionColorMap( ColorMap wavefunctionColorMap ) {
        this.wavefunctionColorMap = wavefunctionColorMap;
    }

    public Paint getPaint( int i, int k ) {
        Paint paint = wavefunctionColorMap.getPaint( i, k );
        Color color = toColor( paint );
        double potval = getPotential().getPotential( i, k, 0 );
        if( potval > 0 ) {
            double r = ( Math.abs( potval ) / 50000.0 );
//            System.out.println( "r = " + r );
            r = Math.min( r, 1.0 );
            paint = new Color( (int)( r * 255 ), color.getGreen(), color.getBlue() );
        }
//        Damping damping = schrodingerPanel.getDiscreteModel().getDamping();
//        double val = damping.getDamping( schrodingerPanel.getDiscreteModel().getWavefunction(), i, k );
//        if( val > 0 ) {
//            paint = new Color( 255, 255, 255 );
//        }
        return paint;
    }

    private Color toColor( Paint paint ) {
        PaintContext context = paint.createContext( null, new Rectangle( 0, 0, 1, 1 ), new Rectangle2D.Double( 0, 0, 1, 1 ), new AffineTransform(), null );
        Raster raster = context.getRaster( 0, 0, 1, 1 );
        int[] co = raster.getPixel( 0, 0, (int[])null );
        Color color = new Color( co[0], co[1], co[2] );
        return color;
    }

    private Potential getPotential() {
        return schrodingerPanel.getDiscreteModel().getPotential();
    }


}
