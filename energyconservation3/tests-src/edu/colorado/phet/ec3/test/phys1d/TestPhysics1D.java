package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.ec3.model.spline.SplineSurface;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;
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
public class TestPhysics1D extends JFrame {
    private JFrame controlFrame;
    private double cumulativeEnergyError = 0;
    private double time = 0;

    public TestPhysics1D() {
        PSwingCanvas pSwingCanvas = new PSwingCanvas();
        pSwingCanvas.setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setContentPane( pSwingCanvas );

        CubicSpline2D cubicSpline = CubicSpline2D.interpolate( new Point2D[]{
                new Point2D.Double( 100, 50 ),
                new Point2D.Double( 200, 100 ),
                new Point2D.Double( 300, 50 ),
                new Point2D.Double( 450, 200 ),
                new Point2D.Double( 525, 50 )
        } );
        MyCubicCurve2DGraphic mySplineGraphic = new MyCubicCurve2DGraphic( cubicSpline );
        pSwingCanvas.getLayer().addChild( mySplineGraphic );
        setSize( 800, 600 );

        final Particle1D particle1d = new Particle1D( cubicSpline );
        particle1d.setUpdateStrategy( particle1d.createVerletOffset() );
        ParticleGraphic particleGraphic = new ParticleGraphic( particle1d );
        pSwingCanvas.getLayer().addChild( particleGraphic );

        Timer timer = new Timer( 30, new ActionListener() {
            //        Timer timer = new Timer( 300, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                double origEnergy = particle1d.getEnergy();
                Point2D origLoc = particle1d.getLocation();
                double origA = particle1d.alpha;
                double dt = 0.001;
                particle1d.stepInTime( dt );

                Point2D newLoc = particle1d.getLocation();
                double dist = newLoc.distance( origLoc );
                double da = particle1d.alpha - origA;
                double energy = particle1d.getEnergy();
                double dE = energy - origEnergy;
                cumulativeEnergyError += Math.abs( dE );
                time += dt;
//                System.out.println( "dA = " + da + " root(dx^2+dy^2)=" + dist + ", dE(inst)=" + dE + ", dE(cumulative)=" + cumulativeEnergyError + ", dE(avg)=" + cumulativeEnergyError / time );
            }
        } );
        timer.start();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        controlFrame = new JFrame();
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        JRadioButton verlet = new JRadioButton( "Verlet", particle1d.getUpdateStrategy() instanceof Particle1D.Verlet );
        verlet.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle1d.setVelocity( 0 );
                particle1d.setUpdateStrategy( particle1d.createVerlet() );
            }
        } );
        JRadioButton constantVel = new JRadioButton( "Constant Velocity", particle1d.getUpdateStrategy() instanceof Particle1D.ConstantVelocity );
        constantVel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle1d.setVelocity( 1000 * 5 );
                particle1d.setUpdateStrategy( particle1d.createConstantVelocity() );
            }
        } );
        JRadioButton euler = new JRadioButton( "Euler", particle1d.getUpdateStrategy() instanceof Particle1D.Euler );
        euler.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle1d.setVelocity( 0 );
                particle1d.setUpdateStrategy( particle1d.createEuler() );
            }
        } );

        JRadioButton verletOffset = new JRadioButton( "Verlet Offset", particle1d.getUpdateStrategy() instanceof Particle1D.VerletOffset );
        euler.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle1d.setVelocity( 0 );
                particle1d.setUpdateStrategy( particle1d.createVerletOffset() );
            }
        } );
        controlPanel.add( verlet, gridBagConstraints );
        controlPanel.add( constantVel, gridBagConstraints );
        controlPanel.add( euler, gridBagConstraints );
        controlPanel.add( verletOffset, gridBagConstraints );


        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( verlet );
        buttonGroup.add( constantVel );
        buttonGroup.add( euler );

        controlFrame.setContentPane( controlPanel );

        JButton resetEnergyError = new JButton( "Reset Energy Error" );
        resetEnergyError.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                cumulativeEnergyError = 0;
                time = 0;
                particle1d.totalDE = 0;
            }
        } );
        controlPanel.add( resetEnergyError, gridBagConstraints );

        controlFrame.pack();
        controlFrame.setLocation( this.getX() + this.getWidth(), this.getY() );
        controlFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
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

    public static class Particle1D {

        private double alpha = 0.25;
        private CubicSpline2D cubicSpline;
        private double velocity = 0;
        private ArrayList listeners = new ArrayList();
        private UpdateStrategy updateStrategy = new Verlet();
        private double g = 9.8 * 100000;//in pixels per time squared
        private double mass = 1.0;
        private double totalDE = 0;

        public Particle1D( CubicSpline2D cubicSpline ) {
            this.cubicSpline = cubicSpline;
        }

        public double getX() {
            return cubicSpline.evaluate( alpha ).getX();
        }

        public double getY() {
            return cubicSpline.evaluate( alpha ).getY();
        }

        public void stepInTime( double dt ) {
            double initEnergy = getEnergy();
            int N = 10;
//            int N=4;
            for( int i = 0; i < N; i++ ) {
                updateStrategy.stepInTime( dt / N );
            }

            double dEUpdate = getNormalizedEnergyDiff( initEnergy );
            totalDE += dEUpdate;

            fixEnergy( initEnergy );
            double dEFix = getNormalizedEnergyDiff( initEnergy );
            System.out.println( "dEUpdate = " + dEUpdate + "\tdEFix=" + dEFix + ", totalDE=" + totalDE + ", RC=" + getRadiusOfCurvature() );
//            System.out.println( "dEAfter = " + ( getEnergy() - initEnergy ) / initEnergy );
            //look for an adjacent location that will give the correct energy

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

        public UpdateStrategy getUpdateStrategy() {
            return updateStrategy;
        }

        public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
            this.updateStrategy = updateStrategy;
        }

        public UpdateStrategy createVerlet() {
            return new Verlet();
        }

        public UpdateStrategy createConstantVelocity() {
            return new ConstantVelocity();
        }

        public void setVelocity( double v ) {
            this.velocity = v;
        }

        public UpdateStrategy createEuler() {
            return new Euler();
        }

        public double getEnergy() {
            return 0.5 * mass * velocity * velocity - mass * g * getY();
        }

        public UpdateStrategy createVerletOffset() {
            return new VerletOffset();
        }

        public interface UpdateStrategy {
            void stepInTime( double dt );
        }

        private void clampAndBounce() {
            alpha = MathUtil.clamp( 0, alpha, 1.0 );

            if( alpha <= 0 ) {
                velocity *= -1;
            }
            if( alpha >= 1 ) {
                velocity *= -1;
            }
        }

        private void fixEnergy( final double initEnergy ) {
//            int count = 0;
//            while( getNormalizedEnergyDiff( initEnergy ) < 0 ) {
//                velocity *= 1.1;
//                System.out.println( "getNormalizedEnergyDiff( ) = " + getNormalizedEnergyDiff( initEnergy ) );
//                count++;
//                if( count > 10 ) {
//                    break;
//                }
//            }
//
//            while( getNormalizedEnergyDiff( initEnergy ) > 0 ) {
//                velocity *= 0.9;
//                System.out.println( "reducing energy...getNormalizedEnergyDiff( ) = " + getNormalizedEnergyDiff( initEnergy ) );
//                count++;
//                if( count > 10 ) {
//                    break;
//                }
//            }
//            double dE=finalEnergy-initEnergy;
//            System.out.println( "dE = " + dE );
//            double arg = 2.0 / mass * ( initEnergy + mass * g * getY() );
//            if( arg > 0 ) {
//                double deltaV = Math.abs( Math.sqrt( arg ) - velocity );
//                velocity+=deltaV;
////                if( finalEnergy > initEnergy ) {
////                    velocity = ( Math.abs( velocity ) + deltaV ) * MathUtil.getSign( velocity );
////                }
////                else {
////                    velocity = ( Math.abs( velocity ) - deltaV ) * MathUtil.getSign( velocity );
////                }
//            }
        }

        private double getNormalizedEnergyDiff( double initEnergy ) {
            return ( getEnergy() - initEnergy ) / Math.abs( initEnergy );
        }

        double getRadiusOfCurvature() {
            double epsilon = 0.001;
            double dtds = ( cubicSpline.getAngle( alpha - epsilon ) - cubicSpline.getAngle( alpha + epsilon ) ) / epsilon;
            return 1.0 / dtds;
        }

        public class VerletOffset implements UpdateStrategy {

            public void stepInTime( double dt ) {
                double origAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
                double ds = velocity * dt - 0.5 * g * Math.cos( origAngle ) * dt * dt;

                alpha += cubicSpline.getFractionalDistance( alpha, ds );
                double newAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
                velocity = velocity + g * ( Math.cos( origAngle ) + Math.cos( newAngle ) ) / 2.0 * dt;

                clampAndBounce();
            }
        }

        public class Verlet implements UpdateStrategy {

            public void stepInTime( double dt ) {
                double origAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
                double ds = velocity * dt - 0.5 * g * Math.cos( origAngle ) * dt * dt;

                alpha += cubicSpline.getFractionalDistance( alpha, ds );
                double newAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
                velocity = velocity + g * ( Math.cos( origAngle ) + Math.cos( newAngle ) ) / 2.0 * dt;

                clampAndBounce();
            }
        }

        public class ConstantVelocity implements UpdateStrategy {

            public void stepInTime( double dt ) {
                alpha += cubicSpline.getFractionalDistance( alpha, velocity * dt );

                clampAndBounce();
            }
        }

        public class Euler implements UpdateStrategy {

            public void stepInTime( double dt ) {
                Vector2D.Double gVector = new Vector2D.Double( 0, g );
                double a = cubicSpline.getUnitParallelVector( alpha ).dot( gVector );
                alpha += cubicSpline.getFractionalDistance( alpha, velocity * dt + 1 / 2 * a * dt * dt );
                velocity += a * dt;

                clampAndBounce();
            }
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
        new TestPhysics1D().start();
    }

    private void start() {
        //To change body of created methods use File | Settings | File Templates.
        setVisible( true );
        controlFrame.setVisible( true );
    }
}
