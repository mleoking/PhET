/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Nov 16, 2002
 * Time: 1:14:27 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.ohm1d;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.ohm1d.common.paint.Painter;
import edu.colorado.phet.ohm1d.common.phys2d.DoublePoint;
import edu.colorado.phet.ohm1d.common.wire1d.WireParticle;
import edu.colorado.phet.ohm1d.common.wire1d.WirePatch;
import edu.colorado.phet.ohm1d.common.wire1d.WireSystem;
import edu.colorado.phet.ohm1d.gui.VoltageListener;
import edu.colorado.phet.ohm1d.volt.WireRegion;

public class AngelPaint implements Painter, VoltageListener {
    double v;
    WireRegion region;
    BufferedImage left;
    BufferedImage right;
    WireSystem ws;
    WirePatch converter;
    Point dx;
    JCheckBox jcb;
    Point dx2;

    public AngelPaint( WireRegion region, BufferedImage left, BufferedImage right, WireSystem ws, WirePatch converter, Point dx, Point dx2, JCheckBox jcb ) {
        this.dx2 = dx2;
        this.jcb = jcb;
        this.dx = dx;
        this.converter = converter;
        this.ws = ws;
        this.region = region;
        this.left = left;
        this.right = right;
    }

    public void paint( Graphics2D g ) {
        if ( !jcb.isSelected() ) {
            return;
        }
        for ( int i = 0; i < ws.numParticles(); i++ ) {
            WireParticle wp = ws.particleAt( i );
            if ( region.contains( wp ) ) {
                //Paint a pusher.
                DoublePoint loc = converter.getPosition( wp.getPosition() );
                if ( v > 0 ) {
                    AffineTransform at = AffineTransform.getTranslateInstance( loc.getX() + dx.x, loc.getY() + dx.y );
                    g.drawRenderedImage( right, at );
                }
                else if ( v < 0 ) {
                    AffineTransform at = AffineTransform.getTranslateInstance( loc.getX() + dx2.x, loc.getY() + dx2.y );
                    g.drawRenderedImage( left, at );
                }
            }
        }
    }

    public void valueChanged( double val ) {
        this.v = val;
    }
}
