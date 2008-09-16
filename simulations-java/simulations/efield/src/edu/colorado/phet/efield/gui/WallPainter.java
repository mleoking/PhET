// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.gui;

import java.awt.*;

// Referenced classes of package edu.colorado.phet.efield.gui:
//            Painter

public class WallPainter
        implements Painter {

    public WallPainter( int i, int j, int k, int l, Stroke stroke1, Color color ) {
        x = i;
        y = j;
        width = k;
        height = l;
        stroke = stroke1;
        c = color;
    }

    public void paint( Graphics2D graphics2d ) {
        graphics2d.setColor( c );
        graphics2d.setStroke( stroke );
        graphics2d.draw( new Rectangle( x, y, width, height ) );
    }

    int x;
    int y;
    int width;
    int height;
    Color c;
    Stroke stroke;
}
