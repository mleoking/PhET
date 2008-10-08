//<pre>

/*
 * Created on Apr 7, 2004
 * Example code demonstrating use of the renderer. 
 * using RenderPanel vs. RenderPanelGL is the choice between hardware
 * and software rendering. 
 * 
 * @author zster
 * 
 */
package srr;

import render.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.*;

public class Demo2 extends JFrame implements Renderable {

    RenderPanel render;

    public Demo2() {
        super( "simple example" );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.setSize( 600, 600 );

        render = new RenderPanel();
//		render = new RenderPanelGL(600,600);

        render.setRenderable( this );

        this.setBounds( 0, 0, 600, 600 );
        this.getContentPane().setBounds( 0, 0, 600, 600 );
        this.getContentPane().add(  render ); 
        setVisible( true );
    }

    public void initialize() {
        render.addLight( 1, 0, 1, 0, 0, 1 ); // LIGHTS
        render.addLight( 1, 1, 1, 1, 1, 1 ); // LIGHTS
        render.addLight( 0, 1, 0, 1, 1, 1 );
        render.setBgColor( .2, .2, .8 ); // BACKGROUND COLOR
        render.push();
        render.transform( render.getWorld() ); // INITIAL VIEW ANGLE
        render.pop();
        Material red = new Material();
        red.setDiffuse( 1, 0, 0 );

        Material blue= new Material();
        blue.setDiffuse( 0, 0, 1 );

        render.getWorld().add().cube().setMaterial( red );    // GOLD BALL
        render.getWorld().add().cube().setMaterial( blue );    // GOLD BALL
        render.getWorld().child[1].getMatrix().translate( 2.5, 0, 0 );

        render.addMouseMotionListener( new MouseMotionListener() {
            Point2D lastPoint;

            public void mouseDragged( MouseEvent e ) {
                if ( lastPoint != null ) {
                    final double dx = e.getX() - lastPoint.getX();
                    final double dy = e.getY() - lastPoint.getY();

                    Geometry geometry = render.getGeometry( e.getX(), e.getY() );
                    if ( geometry != null ) {
                        geometry.getMatrix().translate( dx / 50, -dy / 50, 0 );
                        repaint();
                    }
                }
                lastPoint = e.getPoint();
            }

            public void mouseMoved( MouseEvent e ) {
                lastPoint = e.getPoint();
                if ( render.getGeometry( e.getX(), e.getY() ) != null ) {
                    setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                }
                else {
                    setCursor( Cursor.getDefaultCursor() );
                }
            }
        } );
    }

    public void animate( double time ) {
        render.pause();
        //no need to keep re-rendering if nothing is changing
    }


    public static void main( String[] args ) {
        Demo2 pointApp = new Demo2();
        pointApp.render.init();
        pointApp.render.start();
    }

    /**
     * @see render.Renderable#drawOverlay(java.awt.Graphics)
     */
    public void drawOverlay( Graphics g ) {
    }
}