package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 28, 2004
 * Time: 7:56:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class WiggleMe extends CompositePhetGraphic {
    private Font font = new Font( "Lucida Sans", Font.BOLD, 22 );
    private long t0 = System.currentTimeMillis();
    private double frequency = 0.0025;//in Hertz.
    private double amplitude = 30;//In pixels.
    private Target target;
    private PhetGraphic textGraphic;
    private PhetShapeGraphic phetShapeGraphic;
    private Timer timer;

    public static interface Target {

        Point getLocation();

        int getHeight();
    }

    public static class PhetGraphicTarget implements Target {
        private PhetGraphic target;


        public PhetGraphicTarget( PhetGraphic t ) {
            this.target = t;
        }

        public Point getLocation() {
            Point targetLoc = target.getLocation();
            return targetLoc;
        }

        public int getHeight() {
            return target.getHeight();
        }
    }

    public WiggleMe( Component component, String text, PhetGraphic phetGraphic ) {
        this( component, text, new PhetGraphicTarget( phetGraphic ) );
    }

    public WiggleMe( Component component, String text, Target t ) {
        super( component );
        this.target = t;
        textGraphic = new PhetShadowTextGraphic( component, text, font, 0, 0, Color.black, 2, 2, Color.red );
        addGraphic( textGraphic );
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                tick();
            }
        } );
        textGraphic.setLocation( 0, 0 );
        timer.start();
        Arrow arrow = new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( 50, 0 ), 20, 20, 10 );
        phetShapeGraphic = new PhetShapeGraphic( component, arrow.getShape(), Color.blue, new BasicStroke( 2 ), Color.black );

        addGraphic( phetShapeGraphic );
        phetShapeGraphic.setLocation( 0, textGraphic.getHeight() + 5 );
        setRegistrationPoint( getBounds().x, getBounds().y );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if( visible ) {
            timer.start();
        }
        else {
            timer.stop();
        }
    }

    private void tick() {
        double time = System.currentTimeMillis() - t0;
        Point targetLoc = target.getLocation();
        int y0 = targetLoc.y + target.getHeight() / 2 - getHeight();
        int y = (int)( Math.sin( frequency * time ) * amplitude + y0 );
        int x = targetLoc.x - getWidth() - 50;
        setLocation( x, y );
    }
}
