// Copyright 2002-2011, University of Colorado

/**
 * Class: DiskGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.greenhouse.common.graphics.Graphic;
import edu.colorado.phet.greenhouse.model.Disk;

public class DiskGraphic implements Graphic {
    private Disk disk;
    private Paint paint;
    private Ellipse2D.Double circle = new Ellipse2D.Double();

    public DiskGraphic( Disk disk, Paint paint ) {
        this.disk = disk;
        this.paint = paint;
        this.update();
    }

    public void setPaint( Paint paint ) {
        this.paint = paint;
    }

    public void paint( Graphics2D g2 ) {
        RenderingHints orgHints = g2.getRenderingHints();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setPaint( paint );

        g2.fill( circle );
        g2.setRenderingHints( orgHints );
    }

    public void update() {
        circle.setFrameFromCenter( disk.getCM().getX(),
                                   disk.getCM().getY(),
                                   disk.getCM().getX() + disk.getRadius(),
                                   disk.getCM().getY() - disk.getRadius() );
//        circle.setFrame(disk.getCM().getX(),);
    }
}
