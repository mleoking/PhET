package srr;//<pre>

import render.Geometry;
import render.Material;
import render.RenderJPanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.*;

public class TestDragBlocks extends RenderJPanel {
    public void initialize() {
        setBgColor( .7, .7, 1 ); // SKY COLORED BACKGROUND
        setFOV( .6 ); // SMALLER FIELD OF VIEW

        Material red = new Material(), gold = new Material();
        red.setColor( .5, 0, 0, 1, 1, 1, 20 ); // WHITE HILITE MAKES PLASTIC LOOK
        double r = .4, g = .3, b = .12, S = 3.3;
        gold.setColor( r, g, b, S * r, S * g, S * b, 10 );//.setGlow(G*r,G*g,G*b);


        getWorld().add().cube().setMaterial( red );    // GOLD BALL
        getWorld().add().cube().setMaterial( gold );    // GOLD BALL
        getWorld().child[1].getMatrix().translate( 2.5, 0, 0 );

        addLight( 1, 1, -1, .5, .4, .5 ); // USE MULTI-TINTED SOFT LIGHTING
        addLight( 1, -1, 1, .6, .4, .4 );
        addLight( -1, 1, 1, .4, .4, .6 );
        addLight( -1, -1, 1, .35, .2, .2 );
        addLight( -1, 1, -1, .2, .25, .3 );
        addLight( 1, -1, -1, .3, .2, .25 );

        addMouseMotionListener( new MouseMotionListener() {
            Point2D lastPoint;

            public void mouseDragged( MouseEvent e ) {
                if ( lastPoint != null ) {
                    final double dx = e.getX() - lastPoint.getX();
                    final double dy = e.getY() - lastPoint.getY();

                    Geometry geometry = getGeometry( e.getX(), e.getY() );
                    if ( geometry != null ) {
                        geometry.getMatrix().translate( dx / 50, -dy / 50, 0 );
                        repaint();
                    }
                }
                lastPoint = e.getPoint();
            }

            public void mouseMoved( MouseEvent e ) {
                lastPoint = e.getPoint();
                if ( getGeometry( e.getX(), e.getY() ) != null ) {
                    setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                }
                else {
                    setCursor( Cursor.getDefaultCursor() );
                }
            }
        } );
    }

    int lastX = -1;

    public void animate( final double time ) {
    }

    public static void main( String[] args ) {
        JFrame frame=new JFrame( );
        frame.setContentPane( new TestDragBlocks() );
        frame.setSize( 400,400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}