// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.gui.vectorChooser;

import java.awt.*;

// Referenced classes of package edu.colorado.phet.efield.gui.vectorChooser:
//            VectorPainter

public class DefaultVectorPainter
        implements VectorPainter {

    public DefaultVectorPainter( Color color, Stroke stroke1, double d, double d1 ) {
        c = color;
        stroke = stroke1;
        theta = d;
        tipLength = d1;
    }

    public void paint( Graphics2D graphics2d, int i, int j, int k, int l ) {
        graphics2d.setStroke( stroke );
        graphics2d.setColor( c );
        graphics2d.drawLine( i, j, i + k, j + l );
        if ( k == 0 && l == 0 ) {
            return;
        }
        else {
            double d = Math.atan2( k, l );
            double d1 = d + theta;
            double d2 = d - theta;
            double d3 = (double) ( i + k ) - tipLength * Math.sin( d1 );
            double d4 = (double) ( j + l ) - tipLength * Math.cos( d1 );
            double d5 = (double) ( i + k ) - tipLength * Math.sin( d2 );
            double d6 = (double) ( j + l ) - tipLength * Math.cos( d2 );
            graphics2d.drawLine( i + k, j + l, (int) d3, (int) d4 );
            graphics2d.drawLine( i + k, j + l, (int) d5, (int) d6 );
            return;
        }
    }

    Color c;
    Stroke stroke;
    double theta;
    double tipLength;
}
