/**
 * Class: Kaboom
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Mar 9, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Kaboom implements Graphic, Runnable {
    private static long waitTime = 100;
    private static Font kaboomFont = new Font( "Lucinda Sans", Font.BOLD, 18 );
    private static String kaboomStr = "Kaboom!!!";
    private static AffineTransform kaboomStrTx;

    private static Color[] colors = new Color[PhysicalPanel.backgroundColor.getBlue()];

    static {
        for( int i = 0; i < colors.length; i++ ) {
            colors[i] = new Color( 255, 255, i );
        }
    }

    private Point2D location;
    private double radius;
    private Ellipse2D.Double shape = new Ellipse2D.Double();
    private double maxRadius;
    private ApparatusPanel apparatusPanel;
    private double radiusIncr;
    private double kaboomAlpha = 0.4;
    private Color color;


    public Kaboom( Point2D location,
                   double radiusIncr,
                   double maxRadius,
                   ApparatusPanel apparatusPanel ) {
        this.radiusIncr = radiusIncr;
        this.maxRadius = maxRadius;
        this.apparatusPanel = apparatusPanel;
        this.radius = 20;
        this.location = location;

        double theta = Math.random() * Math.PI - ( Math.PI / 2 );
        kaboomStrTx = AffineTransform.getRotateInstance( theta );

        //        Thread thread = new Thread( this );
        //        thread.start();
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        update();
        shape.setFrameFromCenter( location.getX(), location.getY(),
                                  location.getX() + radius,
                                  location.getY() + radius );
        g.setColor( color );
        //        g.setColor( Color.yellow );
        //        GraphicsUtil.setAlpha( g, kaboomAlpha );
        g.fill( shape );
        GraphicsUtil.setAlpha( g, 1 );

        //        g.setFont( kaboomFont );
        //        g.setColor( Color.black );
        //        g.transform( kaboomStrTx );
        //        g.drawString( kaboomStr, 200, 200 );
        //

        gs.restoreGraphics();
    }

    private void update() {
        if( kaboomAlpha > 0 ) {
            kaboomAlpha = Math.max( kaboomAlpha - 0.03, 0 );
            color = colors[( colors.length - 1 ) - (int)( kaboomAlpha * ( colors.length - 1 ) )];
            radius += radiusIncr;
        }
        else {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    apparatusPanel.removeGraphic( Kaboom.this );
                }
            } );
        }
    }

    public void run() {
        while( kaboomAlpha > 0 ) {
            try {
                Thread.sleep( waitTime );
                kaboomAlpha = Math.max( kaboomAlpha - 0.03, 0 );
                radius += radiusIncr;
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                apparatusPanel.removeGraphic( Kaboom.this );
            }
        } );
    }
}
