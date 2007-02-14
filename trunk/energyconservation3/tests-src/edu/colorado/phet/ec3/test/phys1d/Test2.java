package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Feb 13, 2007
 * Time: 11:12:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class Test2 extends JFrame {

    public Test2() {
        PSwingCanvas pSwingCanvas = new PSwingCanvas();
        setContentPane( pSwingCanvas );

        CubicSpline2D cubicSpline = CubicSpline2D.interpolate( new Point2D[]{
                new Point2D.Double( 100, 50 ),
                new Point2D.Double( 200, 100 ),
                new Point2D.Double( 300, 50 )
        } );

//        CubicSpline cubicSpline = new CubicSpline();
//        cubicSpline.addControlPoint(10, 10);
//        cubicSpline.addControlPoint(200, 200);
//        cubicSpline.addControlPoint(400, 10);

//        cubicSpline.addControlPoint(50, 50);
//        cubicSpline.addControlPoint(225, 50);
//        cubicSpline.addControlPoint(250, 50);

//        SplineSurface splineSurface = new SplineSurface(cubicSpline);
        MyCubicCurve2DGraphic mySplineGraphic = new MyCubicCurve2DGraphic( cubicSpline );
        pSwingCanvas.getLayer().addChild( mySplineGraphic );
        setSize( 800, 600 );

        final Particle1D particle1d = new Particle1D( cubicSpline );
        ParticleGraphic particleGraphic = new ParticleGraphic( particle1d );
        pSwingCanvas.getLayer().addChild( particleGraphic );

        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Point2D origLoc = particle1d.getLocation();
                double origS = particle1d.s;
                particle1d.stepInTime( 1.0 );


                Point2D newLoc = particle1d.getLocation();
                double dist = newLoc.distance( origLoc );
                double ds = particle1d.s - origS;
                System.out.println( "ds = " + ds + " root(dx^2+dy^2)=" + dist );
            }
        } );
        timer.start();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    static class ParticleGraphic extends PNode {
        private Particle1D particle1d;
        private PhetPPath phetPPath;

        public ParticleGraphic( Particle1D particle1d ) {
            //To change body of created methods use File | Settings | File Templates.
            this.particle1d = particle1d;
            phetPPath = new PhetPPath( new BasicStroke( 1 ), Color.red );
            phetPPath.setPathTo( new Ellipse2D.Double( 0, 0, 10, 10 ) );
            addChild( phetPPath );
            particle1d.addListener( this );
            update();
        }

        private void update() {
            phetPPath.setOffset( particle1d.getX() - phetPPath.getWidth() / 2, particle1d.getY() - phetPPath.getHeight() / 2 );
        }
    }

    static class Particle1D {

        double s = 0;
        private CubicSpline2D cubicSpline;
        private double velocity = 0.005;
        private ArrayList listeners = new ArrayList();

        public Particle1D( CubicSpline2D cubicSpline ) {
            //To change body of created methods use File | Settings | File Templates.
            this.cubicSpline = cubicSpline;
        }

        public double getX() {
            return cubicSpline.evaluate( s ).getX();
//            return 0;  //To change body of created methods use File | Settings | File Templates.
        }

        public double getY() {
            return cubicSpline.evaluate( s ).getY();
//            return 0;  //To change body of created methods use File | Settings | File Templates.
        }

        public void stepInTime( double dt ) {
            s += velocity * dt;
            
            if (s<=0){
                velocity*=-1;
                stepInTime( dt );
            }
            if (s>=1){
                velocity*=-1;
                stepInTime( dt );
            }
            
            for( int i = 0; i < listeners.size(); i++ ) {
                ParticleGraphic particleGraphic = (ParticleGraphic)listeners.get( i );
                particleGraphic.update();
            }
        }

        public void addListener( ParticleGraphic particleGraphic ) {
            listeners.add( particleGraphic );
        }

        public Point2D getLocation() {
            return new Point2D.Double( getX(), getY() );
        }
    }

    static class MyCubicCurve2DGraphic extends PNode {
        private CubicSpline2D cubicSpline2D;
        private PhetPPath phetPPath;

        public MyCubicCurve2DGraphic( CubicSpline2D splineSurface ) {
            this.cubicSpline2D = splineSurface;
            //To change body of created methods use File | Settings | File Templates.
            phetPPath = new PhetPPath( new BasicStroke( 1 ), Color.blue );
            addChild( phetPPath );
            update();
        }

        private void update() {
            DoubleGeneralPath doubleGeneralPath = new DoubleGeneralPath( cubicSpline2D.evaluate( 0 ) );
            double ds = 0.01;
            for( double s = ds; s <= 1.0; s += ds ) {
                doubleGeneralPath.lineTo( cubicSpline2D.evaluate( s ) );
            }
            phetPPath.setPathTo( doubleGeneralPath.getGeneralPath() );
        }
    }

    static class MySplineGraphic extends PNode {
        private SplineSurface splineSurface;
        private PhetPPath phetPPath;

        public MySplineGraphic( SplineSurface splineSurface ) {
            this.splineSurface = splineSurface;
            //To change body of created methods use File | Settings | File Templates.
            phetPPath = new PhetPPath( new BasicStroke( 1 ), Color.blue );
            addChild( phetPPath );
            update();
        }

        private void update() {
            phetPPath.setPathTo( splineSurface.getSpline().getInterpolationPath() );
        }
    }

    public static void main( String[] args ) {
        new Test2().start();
    }

    private void start() {
        //To change body of created methods use File | Settings | File Templates.
        setVisible( true );
    }
}
